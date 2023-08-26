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
        ItemStack target = event.getLeft();
        ItemStack sacrifice = event.getRight();
        ItemStack result = target.copy();

        boolean createResult = false;
        int xpCost = 0;
        int materialCost = 0;
        int damage = target.getDamageValue();
        boolean addUse = false;

        if (target.isEmpty()) {
            event.setOutput(ItemStack.EMPTY);
            return;
        }

        if (event.getName() != null && !Util.isBlank(event.getName())) {
            if (!event.getName().equals(target.getHoverName().getString())) {
                result.setHoverName(Component.literal(event.getName()));
                xpCost += 1;
                createResult = true;
            }
        } else if (target.hasCustomHoverName()) {
            result.resetHoverName();
        }

        Map<Enchantment, Integer> targetEnchantments = EnchantmentHelper.getEnchantments(target);

        if (!sacrifice.isEmpty()) {
            Map<Enchantment, Integer> sacrificeEnchantments = EnchantmentHelper.getEnchantments(sacrifice);
            if (target.isDamageableItem() && target.isDamaged() && target.getItem().isValidRepairItem(target, sacrifice)) {
                // repair
                createResult = true;
                addUse = true;
                int expense = Math.min(4, sacrifice.getCount());
                damage -= (result.getMaxDamage() / 4) * expense;
                materialCost += expense;
                xpCost += expense;
            } else if (target.is(sacrifice.getItem())) {
                // do item merging
                createResult = true;
                addUse = true;
                if (target.isDamaged()) {
                    xpCost += 1;
                }
                int targetDamage = target.getMaxDamage() - target.getDamageValue();
                int sacrificeDamage = sacrifice.getMaxDamage() - sacrifice.getDamageValue();
                int bonusDamage = Math.round(target.getMaxDamage() * 0.12f);
                damage = Math.max(0, target.getMaxDamage() - (targetDamage + sacrificeDamage + bonusDamage));
            }

            if ((target.is(sacrifice.getItem())) || (sacrifice.is(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantments(sacrifice).isEmpty() && target.isBookEnchantable(sacrifice))) {
                // apply enchantment to item
                createResult = true;
                addUse = true;
                // add cost for previous enchantments
                xpCost += targetEnchantments.size();
                for (Enchantment enchantment: sacrificeEnchantments.keySet()) {
                    if (!enchantment.canEnchant(target)) {
                        continue;
                    }
                    if (!targetEnchantments.containsKey(enchantment) && !EnchantmentHelper.isEnchantmentCompatible(targetEnchantments.keySet(), enchantment)) {
                        continue;
                    }
                    int sacrificeLevel = sacrificeEnchantments.getOrDefault(enchantment, 0);
                    int existingLevel = targetEnchantments.getOrDefault(enchantment, 0);
                    if (existingLevel == 0) {
                        // new enchantments cost extra
                        xpCost += sacrificeLevel;
                    } else if (sacrificeLevel > existingLevel) {
                        xpCost += sacrificeLevel - existingLevel;
                    }

                    int level = Math.max(sacrificeLevel, existingLevel);
                    targetEnchantments.put(enchantment, level);
                }
                EnchantmentHelper.setEnchantments(targetEnchantments, result);
            }
        }

        if (createResult) {
            if (addUse) {
                int repairCost = Math.max(target.getBaseRepairCost(), sacrifice.getBaseRepairCost());
                xpCost += repairCost;
                result.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(repairCost));
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
