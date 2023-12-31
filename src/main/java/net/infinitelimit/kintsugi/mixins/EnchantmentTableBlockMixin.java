package net.infinitelimit.kintsugi.mixins;

import net.infinitelimit.kintsugi.menus.RemixEnchantmentMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentTableBlock.class)
public abstract class EnchantmentTableBlockMixin {

    @Final
    @Shadow
    public static final List<BlockPos> BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-3, -2, -3, 3, 2, 3).filter((p_207914_) -> {
        return Math.abs(p_207914_.getX()) > 1 || Math.abs(p_207914_.getZ()) > 1;
    }).map(BlockPos::immutable).toList();

    @Inject(at = @At("HEAD"), method = "getMenuProvider(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/MenuProvider;", cancellable = true)
    private void getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos, CallbackInfoReturnable<SimpleMenuProvider> callback) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof EnchantmentTableBlockEntity) {
            Component component = ((Nameable)blockentity).getDisplayName();
            SimpleMenuProvider menuProvider = new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) ->
                    new RemixEnchantmentMenu(pContainerId, pPlayerInventory, ContainerLevelAccess.create(pLevel, pPos)), component);
            callback.setReturnValue(menuProvider);
        } else {
            callback.setReturnValue(null);
        }
    }
}
