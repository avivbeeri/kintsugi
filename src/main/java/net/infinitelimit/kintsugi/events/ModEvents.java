package net.infinitelimit.kintsugi.events;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.offers.ModTradeOffers;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.Items.NAME_TAG;

@Mod.EventBusSubscriber(modid = Kintsugi.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.LIBRARIAN) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.put(1, List.of(new VillagerTrades.EmeraldForItems(Items.PAPER, 24, 16, 2), new ModTradeOffers.RandomKnowledgeBookForEmeralds(2), new VillagerTrades.ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)));
            trades.put(2, List.of(new VillagerTrades.EmeraldForItems(Items.BOOK, 4, 12, 10), new ModTradeOffers.RandomKnowledgeBookForEmeralds(5), new VillagerTrades.ItemsForEmeralds(Items.LANTERN, 1, 1, 5)));
            trades.put(3, List.of(new VillagerTrades.EmeraldForItems(Items.INK_SAC, 5, 12, 20), new ModTradeOffers.RandomKnowledgeBookForEmeralds(10), new VillagerTrades.ItemsForEmeralds(Items.GLASS, 1, 4, 10)));
            trades.put(4, List.of(new VillagerTrades.EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30), new ModTradeOffers.RandomKnowledgeBookForEmeralds(15), new VillagerTrades.ItemsForEmeralds(Items.CLOCK, 5, 1, 15), new VillagerTrades.ItemsForEmeralds(Items.COMPASS, 4, 1, 15)));
            trades.put(5, List.of(new VillagerTrades.ItemsForEmeralds(NAME_TAG, 20, 1, 30), new ModTradeOffers.KnowledgeBookForEmeralds(30)));
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack item = event.getLeft();
        ItemStack sacrifice = event.getRight();
        ItemStack result = item.copy();

        boolean createResult = false;
        int xpCost = 0;
        int materialCost = 0;
        int damage = item.getDamageValue();
        boolean addUse = false;

        if (item.isEmpty()) {
            event.setOutput(ItemStack.EMPTY);
            return;
        }

        if (event.getName() != null && !Util.isBlank(event.getName())) {
            if (!event.getName().equals(item.getHoverName().getString())) {
                result.setHoverName(Component.literal(event.getName()));
                xpCost += 1;
                createResult = true;
            }
        } else if (item.hasCustomHoverName()) {
            result.resetHoverName();
        }

        Map<Enchantment, Integer> itemEnchantments = EnchantmentHelper.getEnchantments(item);

        if (!sacrifice.isEmpty()) {
            Map<Enchantment, Integer> sacrificeEnchantments = EnchantmentHelper.getEnchantments(sacrifice);
            if (item.isDamageableItem() && item.isDamaged() && item.getItem().isValidRepairItem(item, sacrifice)) {
                // repair
                createResult = true;
                addUse = true;
                int expense = Math.min(4, sacrifice.getCount());
                damage -= (result.getMaxDamage() / 4) * expense;
                materialCost += expense;
            } else if (item.is(sacrifice.getItem())) {
                // do item merging
                createResult = true;
                addUse = true;
                int itemDamage = item.getMaxDamage() - item.getDamageValue();
                int sacrificeDamage = sacrifice.getMaxDamage() - sacrifice.getDamageValue();
                int bonusDamage = Math.round(item.getMaxDamage() * 0.12f);
                damage = Math.max(0, item.getMaxDamage() - (itemDamage + sacrificeDamage + bonusDamage));
            }

            if ((item.is(sacrifice.getItem())) || (sacrifice.is(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantments(sacrifice).isEmpty() && item.isBookEnchantable(sacrifice))) {
                // apply enchantment to item
                createResult = true;
                addUse = true;
                for (Enchantment enchantment: sacrificeEnchantments.keySet()) {
                    if (!enchantment.canEnchant(item)) {
                        continue;
                    }
                    if (!itemEnchantments.containsKey(enchantment) && !EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), enchantment)) {
                        continue;
                    }
                    int sacrificeLevel = sacrificeEnchantments.getOrDefault(enchantment, 0);
                    int existingLevel = itemEnchantments.getOrDefault(enchantment, 0);
                    int level = Math.max(sacrificeLevel, existingLevel);
                    itemEnchantments.put(enchantment, level);
                }
                EnchantmentHelper.setEnchantments(itemEnchantments, result);
            }
        }

        if (createResult) {
            if (addUse) {
                int repairCost = Math.max(item.getBaseRepairCost(), sacrifice.getBaseRepairCost());
                repairCost = AnvilMenu.calculateIncreasedRepairCost(repairCost);
                result.setRepairCost(repairCost);
            }

            result.setDamageValue(damage);

            event.setOutput(result);
            // how many levels to extract from player
            event.setCost(xpCost);
            // how many repair items in stack to consume
            event.setMaterialCost(materialCost);
        } else {
            event.setOutput(ItemStack.EMPTY);
        }
    }

}
