package net.infinitelimit.kintsugi.mixins;

import net.infinitelimit.kintsugi.offers.ModTradeOffers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {

    private static final int NUMBER_OF_TRADE_OFFERS = 5;

    public WanderingTraderMixin(EntityType<? extends AbstractVillager> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /**
     * @author Aviv Beeri
     * @reason The wandering trader needed an extra trade for books, which should always be present.
     */

    @Overwrite
    protected void updateTrades() {
        VillagerTrades.ItemListing bookCopyTrade = new ModTradeOffers.CopyKnowledgeBookForEmeralds(1);
        VillagerTrades.ItemListing bookBuyTrade = new ModTradeOffers.EmeraldsForRandomKnowledgeBook(1);
        VillagerTrades.ItemListing[] commonTrades = VillagerTrades.WANDERING_TRADER_TRADES.get(1);
        VillagerTrades.ItemListing[] rareTrades = VillagerTrades.WANDERING_TRADER_TRADES.get(2);
        if (commonTrades != null && rareTrades != null) {
            MerchantOffers merchantOffers = this.getOffers();
            this.addOffersFromItemListings(merchantOffers, commonTrades, NUMBER_OF_TRADE_OFFERS);
            int i = this.random.nextInt(rareTrades.length);
            VillagerTrades.ItemListing rareTrade = rareTrades[i];
            MerchantOffer rareOffer = rareTrade.getOffer(this, this.random);
            if (rareOffer != null) {
                merchantOffers.add(rareOffer);
            }
            merchantOffers.add(0, bookCopyTrade.getOffer(this, this.random));
            merchantOffers.add(1, bookBuyTrade.getOffer(this, this.random));
        }
    }
}
