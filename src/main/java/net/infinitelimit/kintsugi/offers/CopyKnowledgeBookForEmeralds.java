package net.infinitelimit.kintsugi.offers;

import net.infinitelimit.kintsugi.KnowledgeHelper;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

public class CopyKnowledgeBookForEmeralds implements VillagerTrades.ItemListing {
    private final int villagerXp;

    public CopyKnowledgeBookForEmeralds(int pVillagerXp) {
        this.villagerXp = pVillagerXp;
    }

    public MerchantOffer getOffer(@NotNull Entity pTrader, RandomSource pRandom) {
        Enchantment enchantment = KnowledgeHelper.getRandomEnchantment(pRandom);
        int i = Mth.nextInt(pRandom, enchantment.getMinLevel(), enchantment.getMaxLevel());
        ItemStack itemstack = KnowledgeBookItem.createForEnchantment(enchantment);
        int j = 2 + pRandom.nextInt(5 + i * 10) + 3 * i;
        if (enchantment.isTreasureOnly()) {
            j *= 2;
        }

        if (j > 64) {
            j = 64;
        }

        return new MerchantOffer(new ItemStack(Items.EMERALD, j), itemstack, itemstack.copyWithCount(2), 1, this.villagerXp, 0.2F);
    }
}
