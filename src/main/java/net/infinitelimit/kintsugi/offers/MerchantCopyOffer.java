package net.infinitelimit.kintsugi.offers;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantCopyOffer extends MerchantOffer {
    public MerchantCopyOffer(ItemStack pBaseCostA, ItemStack pCostB, ItemStack pResult, int pMaxUses, int pXp, float pPriceMultiplier) {
        super(pBaseCostA, pCostB, pResult, pMaxUses, pXp, pPriceMultiplier);
    }

    @Override
    public boolean take(ItemStack pPlayerOfferA, ItemStack pPlayerOfferB) {
        if (!this.satisfiedBy(pPlayerOfferA, pPlayerOfferB)) {
            return this.take(pPlayerOfferA, pPlayerOfferB);
        } else {
            pPlayerOfferA.shrink(this.getCostA().getCount());

            return true;
        }
    }
}
