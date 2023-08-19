package net.infinitelimit.kintsugi.offers;

import net.infinitelimit.kintsugi.KnowledgeHelper;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

public class ModTradeOffers {

    public static class KnowledgeBookForEmeralds implements VillagerTrades.ItemListing {
        private final int villagerXp;

        public KnowledgeBookForEmeralds(int pVillagerXp) {
            this.villagerXp = pVillagerXp;
        }

        public MerchantOffer getOffer(@NotNull Entity pTrader, RandomSource pRandom) {
            Enchantment enchantment = KnowledgeHelper.getEnchantmentByVillagerType(((Villager)pTrader).getVariant(), pRandom);
            ItemStack itemstack = KnowledgeBookItem.createForEnchantment(enchantment);
            int cost = calculateEnchantmentCost(pRandom, enchantment);

            return new MerchantOffer(new ItemStack(Items.EMERALD, cost), itemstack, 1, this.villagerXp, 0.2F);
        }
    }

    public static class RandomKnowledgeBookForEmeralds implements VillagerTrades.ItemListing {
        private final int villagerXp;

        public RandomKnowledgeBookForEmeralds(int pVillagerXp) {
            this.villagerXp = pVillagerXp;
        }

        public MerchantOffer getOffer(@NotNull Entity pTrader, RandomSource pRandom) {
            Enchantment enchantment = KnowledgeHelper.getRandomVillagerEnchantment(((Villager)pTrader).getVariant(), pRandom);
            ItemStack itemstack = KnowledgeBookItem.createForEnchantment(enchantment);
            int cost = calculateEnchantmentCost(pRandom, enchantment);

            return new MerchantOffer(new ItemStack(Items.EMERALD, cost), itemstack, 1, this.villagerXp, 0.2F);
        }
    }

    public static class EmeraldsForRandomKnowledgeBook implements VillagerTrades.ItemListing {
        private final int villagerXp;

        public EmeraldsForRandomKnowledgeBook(int pVillagerXp) {
            this.villagerXp = pVillagerXp;
        }

        public MerchantOffer getOffer(@NotNull Entity pTrader, RandomSource pRandom) {
            Enchantment enchantment = KnowledgeHelper.getRandomEnchantment(pRandom);
            ItemStack itemstack = KnowledgeBookItem.createForEnchantment(enchantment);
            int cost = calculateEnchantmentCost(pRandom, enchantment);

            return new MerchantOffer(itemstack, new ItemStack(Items.EMERALD, cost) , 1, this.villagerXp, 0.2F);
        }
    }

    public static class CopyKnowledgeBookForEmeralds implements VillagerTrades.ItemListing {
        private final int villagerXp;

        public CopyKnowledgeBookForEmeralds(int pVillagerXp) {
            this.villagerXp = pVillagerXp;
        }

        public MerchantOffer getOffer(@NotNull Entity pTrader, RandomSource pRandom) {
            Enchantment enchantment = KnowledgeHelper.getRandomEnchantment(pRandom);
            ItemStack itemstack = KnowledgeBookItem.createForEnchantment(enchantment);
            int cost = calculateEnchantmentCost(pRandom, enchantment);

            return new MerchantOffer(new ItemStack(Items.EMERALD, cost), itemstack, itemstack.copyWithCount(2), 1, this.villagerXp, 0.2F);
        }
    }

    private static int calculateEnchantmentCost(RandomSource pRandom, Enchantment enchantment) {
        int category = KnowledgeHelper.getEnchantmentCategory(enchantment) + 1;

        int j = 7 + 3 * category + pRandom.nextInt(7 + category * 7);
        // cat 1 = 7 + 3 + rand(14) = 10 - 24
        // cat 2 = 7 + 6 + rand(21) = 13 - 34
        // cat 3 = 7 + 9 + rand(28) = 16 - 44
        // cat 4 = 7 + 12 + rand(35) = 19 - 54
        // cat 5 = 7 + 15 + rand(42) = 22 - 64
        if (enchantment.isTreasureOnly() || !KnowledgeHelper.ANY_VILLAGER_TRADES.contains(enchantment)) {
            j *= 2;
        }

        if (j > 64) {
            j = 64;
        }
        return j;
    }
}
