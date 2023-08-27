package net.infinitelimit.kintsugi.mixins;

import net.infinitelimit.kintsugi.offers.ModTradeOffers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {

    @Unique
    private static final int NUMBER_OF_COMMON_TRADE_OFFERS = 5;
    @Unique
    private static final int NUMBER_OF_RARE_TRADE_OFFERS = 2;
    @Unique
    private static final int NUMBER_OF_BUY_TRADE_OFFERS = 2;

    @Unique
    private static final VillagerTrades.ItemListing[] BUYING_TRADES = new VillagerTrades.ItemListing[] {
        new ModTradeOffers.EmeraldForPotionItems(Potions.WATER, 1, 1, 1),
        new VillagerTrades.EmeraldForItems(Items.HAY_BLOCK, 1, 1, 1),
        new ModTradeOffers.EmeraldsForItems(Items.WATER_BUCKET, 1,2, 1, 1),
        new ModTradeOffers.EmeraldsForItems(Items.MILK_BUCKET, 1,2, 1, 1),
        new ModTradeOffers.EmeraldsForItems(Items.FERMENTED_SPIDER_EYE, 1,3, 1, 1),
        new ModTradeOffers.EmeraldsForItems(Items.BAKED_POTATO, 4,1, 1, 1),
    };

    public WanderingTraderMixin(EntityType<? extends AbstractVillager> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /**
     * @author Aviv Beeri
     * @reason The wandering trader needed an extra trade for books, which should always be present.
     * We also need a third table of trades for buying items, and the experimental rebalancing tweaks
     */

    @Overwrite
    protected void updateTrades() {
        VillagerTrades.ItemListing bookCopyTrade = new ModTradeOffers.CopyKnowledgeBookForEmeralds(1);
        VillagerTrades.ItemListing bookBuyTrade = new ModTradeOffers.EmeraldsForRandomKnowledgeBook(1);
        VillagerTrades.ItemListing[] commonTrades = VillagerTrades.WANDERING_TRADER_TRADES.get(1);
        VillagerTrades.ItemListing[] rareTrades = VillagerTrades.WANDERING_TRADER_TRADES.get(2);
        if (commonTrades != null && rareTrades != null) {
            MerchantOffers merchantOffers = this.getOffers();
            this.addOffersFromItemListings(merchantOffers, BUYING_TRADES, NUMBER_OF_BUY_TRADE_OFFERS);
            this.addOffersFromItemListings(merchantOffers, commonTrades, NUMBER_OF_COMMON_TRADE_OFFERS);
            this.addOffersFromItemListings(merchantOffers, rareTrades, NUMBER_OF_RARE_TRADE_OFFERS);

            merchantOffers.add(0, bookCopyTrade.getOffer(this, this.random));
            merchantOffers.add(1, bookBuyTrade.getOffer(this, this.random));
        }
    }
}
