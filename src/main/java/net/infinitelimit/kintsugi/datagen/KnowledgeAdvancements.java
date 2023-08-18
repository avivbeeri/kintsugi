package net.infinitelimit.kintsugi.datagen;

import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class KnowledgeAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        Advancement advancementRoot = Advancement.Builder.advancement()
                .display(ModItems.KNOWLEDGE_BOOK.get(), Component.translatable("advancements.kintsugi.root.title"), Component.translatable("advancements.kintsugi.root.description"), new ResourceLocation(Kintsugi.MOD_ID,"textures/gui/advancements/backgrounds/book.png"), FrameType.TASK, false, false, false)
                .addCriterion("book", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.KNOWLEDGE_BOOK.get()))
                .save(saver, new ResourceLocation(Kintsugi.MOD_ID, "knowledge/root"), existingFileHelper);

        Advancement advancement1 = Advancement.Builder.advancement().parent(advancementRoot)
                .display(ModItems.KNOWLEDGE_BOOK.get(), Component.translatable("advancements.kintsugi.acquire.title"), Component.translatable("advancements.kintsugi.acquire.description"), null, FrameType.TASK, true, true, false)
                .addCriterion("acquired_book", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.KNOWLEDGE_BOOK.get()))
                .save(saver, new ResourceLocation(Kintsugi.MOD_ID, "knowledge/acquire"), existingFileHelper);
        Advancement advancement2 = Advancement.Builder.advancement().parent(advancementRoot)
                .display(Items.EMERALD, Component.translatable("advancements.kintsugi.trade.title"), Component.translatable("advancements.kintsugi.trade.description"), null, FrameType.TASK, true, true, false)
                .requirements(RequirementsStrategy.AND)
                .addCriterion("trade", TradeTrigger.TriggerInstance.tradedWithVillager()).addCriterion("acquired_book", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.KNOWLEDGE_BOOK.get()))
                .save(saver, new ResourceLocation(Kintsugi.MOD_ID, "knowledge/trade"), existingFileHelper);

        Advancement advancement3 = Advancement.Builder.advancement().parent(advancement1)
                .display(Items.CHISELED_BOOKSHELF, Component.translatable("advancements.kintsugi.shelve.title"), Component.translatable("advancements.kintsugi.shelve.description"), null, FrameType.TASK, true, true, false)
                .addCriterion("shelve", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.CHISELED_BOOKSHELF).build()), ItemPredicate.Builder.item().of(ModItems.KNOWLEDGE_BOOK.get())))
                .save(saver, new ResourceLocation(Kintsugi.MOD_ID, "knowledge/shelve"), existingFileHelper);

    }
}
