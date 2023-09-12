package net.infinitelimit.kintsugi.item;

import net.infinitelimit.kintsugi.Kintsugi;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class KnowledgeBookItem extends Item {
    public static final String TAG_RITUAL_ENCHANTMENT = "RitualEnchantment";
    public KnowledgeBookItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return false;
    }

    @Override
    public boolean canGrindstoneRepair(ItemStack pStack) { return true; }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    /**
     * Gets the title name of the book
     */
    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        ResourceLocation enchantment = getEnchantment(pStack);
        if (enchantment == null) {
            // Defensive, in case the book is created without a tag.
            return Component.translatable(new ResourceLocation(Kintsugi.MOD_ID, "item.kintsugi.knowledge_book.generic").getPath());
        } else {
            String enchantmentName = Component.translatable(
                    Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getValue(enchantment)).getDescriptionId()
            ).getString();
            return Component.translatable(this.getDescriptionId(pStack), enchantmentName);
        }
    }

    /**
     * Allows items to add custom lines of information to the mouseover description.
     */
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    /**
     * Returns the ItemStack of an enchanted version of this item.
     */
    public static ItemStack createForEnchantment(Enchantment pEnchantment) {
        ItemStack itemstack = new ItemStack(ModItems.KNOWLEDGE_BOOK.get());
        addEnchantment(itemstack, pEnchantment);
        return itemstack;
    }

    public static ResourceLocation getEnchantment(ItemStack pEnchantedBookStack) {
        CompoundTag compoundtag = pEnchantedBookStack.getTag();
        return compoundtag != null ? getEnchantmentId(compoundtag) : null;
    }


    public static void addEnchantment(ItemStack pStack, Enchantment pEnchantment) {
        ResourceLocation enchantmentId = EnchantmentHelper.getEnchantmentId(pEnchantment);
        pStack.getOrCreateTag().putString(TAG_RITUAL_ENCHANTMENT, String.valueOf(enchantmentId));
    }

    @Nullable
    public static ResourceLocation getEnchantmentId(CompoundTag pCompoundTag) {
        return ResourceLocation.tryParse(pCompoundTag.getString(TAG_RITUAL_ENCHANTMENT));
    }
}
