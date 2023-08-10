package net.infinitelimit.kintsugi.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
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

import javax.annotation.Nullable;
import java.util.List;

public class PowerBookItem extends Item {
    public static final String TAG_RITUAL_ENCHANTMENT = "RitualEnchantment";
    public PowerBookItem(Properties pProperties) {
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

    /**
     * Allows items to add custom lines of information to the mouseover description.
     */
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        ResourceLocation enchantmentId = getEnchantment(pStack);
        if (enchantmentId != null) {
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
            assert enchantment != null;
            MutableComponent name = Component.translatable(enchantment.getDescriptionId());
            if (enchantment.isCurse()) {
                name.withStyle(ChatFormatting.RED);
            } else {
                name.withStyle(ChatFormatting.GRAY);
            }
            pTooltip.add(name);
        }
    }

    /**
     * Returns the ItemStack of an enchanted version of this item.
     */
    public static ItemStack createForEnchantment(Enchantment pEnchantment) {
        ItemStack itemstack = new ItemStack(ModItems.POWER_BOOK.get());
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
