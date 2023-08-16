package net.infinitelimit.kintsugi.events;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.KnowledgeHelper;
import net.infinitelimit.kintsugi.datagen.ModLootTableProvider;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.infinitelimit.kintsugi.offers.ModTradeOffers;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
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

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.LIBRARIAN) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.put(1, List.of(new VillagerTrades.EmeraldForItems(Items.PAPER, 24, 16, 2), new ModTradeOffers.KnowledgeBookForEmeralds(1), new VillagerTrades.ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)));
            trades.put(2, List.of(new VillagerTrades.EmeraldForItems(Items.BOOK, 4, 12, 10), new ModTradeOffers.KnowledgeBookForEmeralds(5), new VillagerTrades.ItemsForEmeralds(Items.LANTERN, 1, 1, 5)));
            trades.put(3, List.of(new VillagerTrades.EmeraldForItems(Items.INK_SAC, 5, 12, 20), new ModTradeOffers.KnowledgeBookForEmeralds(10), new VillagerTrades.ItemsForEmeralds(Items.GLASS, 1, 4, 10)));
            trades.put(4, List.of(new VillagerTrades.EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30), new ModTradeOffers.KnowledgeBookForEmeralds(15), new VillagerTrades.ItemsForEmeralds(Items.CLOCK, 5, 1, 15), new VillagerTrades.ItemsForEmeralds(Items.COMPASS, 4, 1, 15)));
            trades.put(5, List.of(new VillagerTrades.ItemsForEmeralds(Items.NAME_TAG, 20, 1, 30)));
        }
    }

}
