package net.infinitelimit.kintsugi.datagen;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.KnowledgeHelper;
import net.infinitelimit.kintsugi.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

import static net.infinitelimit.kintsugi.item.KnowledgeBookItem.TAG_RITUAL_ENCHANTMENT;

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
                .addCriterion("trade", tradedBookWithVillager())
                .save(saver, new ResourceLocation(Kintsugi.MOD_ID, "knowledge/trade"), existingFileHelper);

        Advancement advancement3 = Advancement.Builder.advancement().parent(advancement1)
                .display(Items.CHISELED_BOOKSHELF, Component.translatable("advancements.kintsugi.shelve.title"), Component.translatable("advancements.kintsugi.shelve.description"), null, FrameType.TASK, true, true, false)
                .addCriterion("shelve", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.CHISELED_BOOKSHELF).build()), ItemPredicate.Builder.item().of(ModItems.KNOWLEDGE_BOOK.get())))
                .save(saver, new ResourceLocation(Kintsugi.MOD_ID, "knowledge/shelve"), existingFileHelper);


        Advancement.Builder builder = Advancement.Builder.advancement().parent(advancement3)
                .display(Items.BOOKSHELF, Component.translatable("advancements.kintsugi.shelve_all.title"), Component.translatable("advancements.kintsugi.shelve_all.description"), null, FrameType.CHALLENGE, true, true, true)
                .requirements(RequirementsStrategy.AND);

       for (Enchantment enchantment: KnowledgeHelper.KNOWLEDGE_ENCHANTMENTS) {
           CompoundTag tag = new CompoundTag();
           String id = String.valueOf(EnchantmentHelper.getEnchantmentId(enchantment));
           tag.putString(TAG_RITUAL_ENCHANTMENT, id);
           builder.addCriterion(id, ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                   LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.CHISELED_BOOKSHELF).build()),
                   ItemPredicate.Builder.item().of(ModItems.KNOWLEDGE_BOOK.get()).hasNbt(tag)
           ));
       }

       Advancement advancement4 =  builder.save(saver, new ResourceLocation(Kintsugi.MOD_ID, "knowledge/shelve_all"), existingFileHelper);;
    }

    public static TradeTrigger.TriggerInstance tradedBookWithVillager() {
        return new TradeTrigger.TriggerInstance(ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ItemPredicate.Builder.item().of(ModItems.KNOWLEDGE_BOOK.get()).build());
    }
}
