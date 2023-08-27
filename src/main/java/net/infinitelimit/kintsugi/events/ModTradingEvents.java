package net.infinitelimit.kintsugi.events;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.offers.ModTradeOffers;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static net.minecraft.world.item.Items.NAME_TAG;

@Mod.EventBusSubscriber(modid = Kintsugi.MOD_ID)
public class ModTradingEvents {

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
    public static void addWanderingTrades(WandererTradesEvent event) {
        event.getGenericTrades().clear();
        event.getRareTrades().clear();

        List<VillagerTrades.ItemListing> commonTrades =List.of(
                new VillagerTrades.ItemsForEmeralds(Items.SEA_PICKLE, 2, 1, 5, 1),
                new VillagerTrades.ItemsForEmeralds(Items.SLIME_BALL, 4, 1, 5, 1),
                new VillagerTrades.ItemsForEmeralds(Items.GLOWSTONE, 2, 1, 5, 1),
                new VillagerTrades.ItemsForEmeralds(Items.NAUTILUS_SHELL, 5, 1, 5, 1),
                new VillagerTrades.ItemsForEmeralds(Items.FERN, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.SUGAR_CANE, 1, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.PUMPKIN, 1, 1, 4, 1),
                new VillagerTrades.ItemsForEmeralds(Items.KELP, 3, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.CACTUS, 3, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.DANDELION, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.POPPY, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.BLUE_ORCHID, 1, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.ALLIUM, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.AZURE_BLUET, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.RED_TULIP, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.ORANGE_TULIP, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.WHITE_TULIP, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.PINK_TULIP, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.OXEYE_DAISY, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.CORNFLOWER, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1),
                new VillagerTrades.ItemsForEmeralds(Items.WHEAT_SEEDS, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.BEETROOT_SEEDS, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.PUMPKIN_SEEDS, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.MELON_SEEDS, 1, 1, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.ACACIA_SAPLING, 5, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.BIRCH_SAPLING, 5, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.DARK_OAK_SAPLING, 5, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.JUNGLE_SAPLING, 5, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.OAK_SAPLING, 5, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.SPRUCE_SAPLING, 5, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.CHERRY_SAPLING, 5, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.MANGROVE_PROPAGULE, 5, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.RED_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.WHITE_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.BLUE_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.PINK_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.BLACK_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.GREEN_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.LIGHT_GRAY_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.MAGENTA_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.YELLOW_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.GRAY_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.PURPLE_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.LIGHT_BLUE_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.LIME_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.ORANGE_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.BROWN_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.CYAN_DYE, 1, 3, 12, 1),
                new VillagerTrades.ItemsForEmeralds(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.VINE, 1, 3, 4, 1),
                new VillagerTrades.ItemsForEmeralds(Items.BROWN_MUSHROOM, 1, 3, 4, 1),
                new VillagerTrades.ItemsForEmeralds(Items.RED_MUSHROOM, 1, 3, 4, 1),
                new VillagerTrades.ItemsForEmeralds(Items.LILY_PAD, 1, 5, 2, 1),
                new VillagerTrades.ItemsForEmeralds(Items.SMALL_DRIPLEAF, 1, 2, 5, 1),
                new VillagerTrades.ItemsForEmeralds(Items.SAND, 1, 8, 8, 1),
                new VillagerTrades.ItemsForEmeralds(Items.RED_SAND, 1, 4, 6, 1),
                new VillagerTrades.ItemsForEmeralds(Items.POINTED_DRIPSTONE, 1, 2, 5, 1),
                new VillagerTrades.ItemsForEmeralds(Items.ROOTED_DIRT, 1, 2, 5, 1),
                new VillagerTrades.ItemsForEmeralds(Items.MOSS_BLOCK, 1, 2, 5, 1),
                new VillagerTrades.ItemsForEmeralds(Items.PUFFERFISH_BUCKET, 3, 1, 4, 1),
                new VillagerTrades.ItemsForEmeralds(Items.TROPICAL_FISH_BUCKET, 3, 1, 4, 1),
                new VillagerTrades.ItemsForEmeralds(Items.GUNPOWDER, 1, 4, 2, 1)
        );
        List<VillagerTrades.ItemListing> rareTrades = List.of(
               new VillagerTrades.ItemsForEmeralds(Items.PACKED_ICE, 1, 1, 6, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BLUE_ICE, 6, 1, 6, 1),
               new VillagerTrades.ItemsForEmeralds(Items.PODZOL, 3, 3, 6, 1),
               new VillagerTrades.ItemsForEmeralds(Items.ACACIA_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BIRCH_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.CHERRY_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.OAK_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.JUNGLE_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.DARK_OAK_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.SPRUCE_LOG, 1, 8, 4, 1),
               new ModTradeOffers.PotionItemForEmeralds(Potions.LONG_INVISIBILITY, 5, 1, 1),
               new VillagerTrades.EnchantedItemForEmeralds(Items.IRON_PICKAXE, 1, 1, 1, 1)
        );

        event.getGenericTrades().addAll(commonTrades);
        event.getRareTrades().addAll(rareTrades);
    }

}
