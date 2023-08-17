package net.infinitelimit.kintsugi.events;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.infinitelimit.kintsugi.item.ModItems;
import net.infinitelimit.kintsugi.loot.AddRitualFunction;
import net.infinitelimit.kintsugi.offers.ModTradeOffers;
import net.minecraft.core.NonNullList;
import net.minecraft.data.loot.packs.VanillaFishingLoot;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.world.item.Items.BOOK;
import static net.minecraft.world.item.Items.BOW;
import static net.minecraft.world.item.Items.ENCHANTED_BOOK;
import static net.minecraft.world.item.Items.FISHING_ROD;
import static net.minecraft.world.item.Items.NAME_TAG;
import static net.minecraft.world.item.Items.NAUTILUS_SHELL;
import static net.minecraft.world.item.Items.SADDLE;
import static net.minecraft.world.item.enchantment.Enchantments.FISHING_LUCK;
import static net.minecraft.world.item.enchantment.Enchantments.FISHING_SPEED;

@Mod.EventBusSubscriber(modid = Kintsugi.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        ItemStack stack = event.getDrops().get(0);
        if (stack.is(ENCHANTED_BOOK)) {
            float i = event.getEntity().getRandom().nextFloat();
            if (i < 0.333) {
                event.getDrops().set(0, KnowledgeBookItem.createForEnchantment(FISHING_LUCK));
            } else if (i < 0.666) {
                event.getDrops().set(0, KnowledgeBookItem.createForEnchantment(FISHING_SPEED));
            }  /* else Book is unchanged */
        }
    }

    /* // WORKING
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(BuiltInLootTables.FISHING_TREASURE)) {
            LootTable table = LootTable.lootTable().withPool(
                    LootPool.lootPool()
                            .add(LootItem.lootTableItem(NAME_TAG))
                            .add(LootItem.lootTableItem(SADDLE))
                            .add(LootItem.lootTableItem(Items.BOW).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.25F))).apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(30.0F)).allowTreasure()))
                            .add(LootItem.lootTableItem(Items.FISHING_ROD).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.25F))).apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(30.0F)).allowTreasure()))
                            .add(LootItem.lootTableItem(Items.BOOK).apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(30.0F)).allowTreasure()))
                            .add(LootItem.lootTableItem(Items.NAUTILUS_SHELL))
                            .add(LootItem.lootTableItem(ModItems.KNOWLEDGE_BOOK.get()).apply(AddRitualFunction.addEnchantment(UniformGenerator.between(0.0f, 1.0f))))).build();
            event.setTable(table);
        }
    }
    */

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.LIBRARIAN) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.put(1, List.of(new VillagerTrades.EmeraldForItems(Items.PAPER, 24, 16, 2), new ModTradeOffers.KnowledgeBookForEmeralds(1), new VillagerTrades.ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)));
            trades.put(2, List.of(new VillagerTrades.EmeraldForItems(Items.BOOK, 4, 12, 10), new ModTradeOffers.RandomKnowledgeBookForEmeralds(5), new VillagerTrades.ItemsForEmeralds(Items.LANTERN, 1, 1, 5)));
            trades.put(3, List.of(new VillagerTrades.EmeraldForItems(Items.INK_SAC, 5, 12, 20), new ModTradeOffers.RandomKnowledgeBookForEmeralds(10), new VillagerTrades.ItemsForEmeralds(Items.GLASS, 1, 4, 10)));
            trades.put(4, List.of(new VillagerTrades.EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30), new ModTradeOffers.RandomKnowledgeBookForEmeralds(15), new VillagerTrades.ItemsForEmeralds(Items.CLOCK, 5, 1, 15), new VillagerTrades.ItemsForEmeralds(Items.COMPASS, 4, 1, 15)));
            trades.put(5, List.of(new VillagerTrades.ItemsForEmeralds(NAME_TAG, 20, 1, 30)));
        }
    }

}
