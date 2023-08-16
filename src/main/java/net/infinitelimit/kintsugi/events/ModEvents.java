package net.infinitelimit.kintsugi.events;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.datagen.ModLootTableProvider;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.infinitelimit.kintsugi.Kintsugi.MOD_ID;

@Mod.EventBusSubscriber(modid = Kintsugi.MOD_ID)
public class ModEvents {

    // On the MOD event bus
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeServer(),
                (DataProvider.Factory<ModLootTableProvider>) pOutput ->
                        new ModLootTableProvider(pOutput, MOD_ID));
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {

        if (event.getType() == VillagerProfession.LIBRARIAN) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.put(1, List.of(new VillagerTrades.EmeraldForItems(Items.PAPER, 24, 16, 2), new KnowledgeBookForEmeralds(1), new VillagerTrades.ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)));
            trades.put(2, List.of(new VillagerTrades.EmeraldForItems(Items.BOOK, 4, 12, 10), new KnowledgeBookForEmeralds(5), new VillagerTrades.ItemsForEmeralds(Items.LANTERN, 1, 1, 5)));
            trades.put(3, List.of(new VillagerTrades.EmeraldForItems(Items.INK_SAC, 5, 12, 20), new KnowledgeBookForEmeralds(10), new VillagerTrades.ItemsForEmeralds(Items.GLASS, 1, 4, 10)));
            trades.put(4, List.of(new VillagerTrades.EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30), new KnowledgeBookForEmeralds(15), new VillagerTrades.ItemsForEmeralds(Items.CLOCK, 5, 1, 15), new VillagerTrades.ItemsForEmeralds(Items.COMPASS, 4, 1, 15)));
            trades.put(5, List.of(new VillagerTrades.ItemsForEmeralds(Items.NAME_TAG, 20, 1, 30)));
        }
    }

    // toIntMap(ImmutableMap.of(
    // 1, new VillagerTrades.ItemListing[]{new VillagerTrades.ItemsForEmeralds(Items.SEA_PICKLE, 2, 1, 5, 1), new VillagerTrades.ItemsForEmeralds(Items.SLIME_BALL, 4, 1, 5, 1), new VillagerTrades.ItemsForEmeralds(Items.GLOWSTONE, 2, 1, 5, 1), new VillagerTrades.ItemsForEmeralds(Items.NAUTILUS_SHELL, 5, 1, 5, 1), new VillagerTrades.ItemsForEmeralds(Items.FERN, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.SUGAR_CANE, 1, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.PUMPKIN, 1, 1, 4, 1), new VillagerTrades.ItemsForEmeralds(Items.KELP, 3, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.CACTUS, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.DANDELION, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.POPPY, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.BLUE_ORCHID, 1, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.ALLIUM, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.AZURE_BLUET, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.RED_TULIP, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.ORANGE_TULIP, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.WHITE_TULIP, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.PINK_TULIP, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.OXEYE_DAISY, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.CORNFLOWER, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1), new VillagerTrades.ItemsForEmeralds(Items.WHEAT_SEEDS, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.BEETROOT_SEEDS, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.PUMPKIN_SEEDS, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.MELON_SEEDS, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.ACACIA_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.BIRCH_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.DARK_OAK_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.JUNGLE_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.OAK_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.SPRUCE_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.CHERRY_SAPLING, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.MANGROVE_PROPAGULE, 5, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.RED_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.WHITE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.BLUE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.PINK_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.BLACK_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.GREEN_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.LIGHT_GRAY_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.MAGENTA_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.YELLOW_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.GRAY_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.PURPLE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.LIGHT_BLUE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.LIME_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.ORANGE_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.BROWN_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.CYAN_DYE, 1, 3, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.VINE, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.BROWN_MUSHROOM, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.RED_MUSHROOM, 1, 1, 12, 1), new VillagerTrades.ItemsForEmeralds(Items.LILY_PAD, 1, 2, 5, 1), new VillagerTrades.ItemsForEmeralds(Items.SMALL_DRIPLEAF, 1, 2, 5, 1), new VillagerTrades.ItemsForEmeralds(Items.SAND, 1, 8, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.RED_SAND, 1, 4, 6, 1), new VillagerTrades.ItemsForEmeralds(Items.POINTED_DRIPSTONE, 1, 2, 5, 1), new VillagerTrades.ItemsForEmeralds(Items.ROOTED_DIRT, 1, 2, 5, 1), new VillagerTrades.ItemsForEmeralds(Items.MOSS_BLOCK, 1, 2, 5, 1)},
    // 2, new VillagerTrades.ItemListing[]{new VillagerTrades.ItemsForEmeralds(Items.TROPICAL_FISH_BUCKET, 5, 1, 4, 1), new VillagerTrades.ItemsForEmeralds(Items.PUFFERFISH_BUCKET, 5, 1, 4, 1), new VillagerTrades.ItemsForEmeralds(Items.PACKED_ICE, 3, 1, 6, 1), new VillagerTrades.ItemsForEmeralds(Items.BLUE_ICE, 6, 1, 6, 1), new VillagerTrades.ItemsForEmeralds(Items.GUNPOWDER, 1, 1, 8, 1), new VillagerTrades.ItemsForEmeralds(Items.PODZOL, 3, 3, 6, 1)}));

    static class KnowledgeBookForEmeralds implements VillagerTrades.ItemListing {
        private final int villagerXp;

        public KnowledgeBookForEmeralds(int pVillagerXp) {
            this.villagerXp = pVillagerXp;
        }

        public MerchantOffer getOffer(@NotNull Entity pTrader, RandomSource pRandom) {
            List<Enchantment> list = ForgeRegistries.ENCHANTMENTS.getValues().stream().filter(Enchantment::isTradeable).toList();
            Enchantment enchantment = list.get(pRandom.nextInt(list.size()));
            int i = Mth.nextInt(pRandom, enchantment.getMinLevel(), enchantment.getMaxLevel());
            ItemStack itemstack = KnowledgeBookItem.createForEnchantment(enchantment);
            int j = 2 + pRandom.nextInt(5 + i * 10) + 3 * i;
            if (enchantment.isTreasureOnly()) {
                j *= 2;
            }

            if (j > 64) {
                j = 64;
            }

            return new MerchantOffer(new ItemStack(Items.EMERALD, j), itemstack, 1, this.villagerXp, 0.2F);
        }
    }


}
