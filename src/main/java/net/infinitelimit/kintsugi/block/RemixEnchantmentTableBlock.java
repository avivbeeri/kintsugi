package net.infinitelimit.kintsugi.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;

public class RemixEnchantmentTableBlock extends EnchantmentTableBlock {
    public RemixEnchantmentTableBlock(Properties pProperties) {
        super(pProperties);
    }

    public static boolean isValidBookShelf(Level pLevel, BlockPos current, BlockPos target) {
        return pLevel.getBlockState(current.offset(target)).getEnchantPowerBonus(pLevel, current.offset(target)) != 0 && pLevel.getBlockState(current.offset(target.getX() / 2, target.getY(), target.getZ() / 2)).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }
}
