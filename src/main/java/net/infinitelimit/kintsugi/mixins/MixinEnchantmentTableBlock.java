package net.infinitelimit.kintsugi.mixins;

import net.infinitelimit.kintsugi.menus.RemixEnchantmentMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentTableBlock.class)
public abstract class MixinEnchantmentTableBlock {
    @Inject(at = @At("HEAD"), method = "getMenuProvider(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/MenuProvider;", cancellable = true)
    private void getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos, CallbackInfoReturnable<SimpleMenuProvider> callback) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof EnchantmentTableBlockEntity) {
            Component component = ((Nameable)blockentity).getDisplayName();
            SimpleMenuProvider menu = new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) ->
                    new RemixEnchantmentMenu(pContainerId, pPlayerInventory, ContainerLevelAccess.create(pLevel, pPos)), component);
            callback.setReturnValue(menu);
        } else {
            callback.setReturnValue(null);
        }
    }
}
