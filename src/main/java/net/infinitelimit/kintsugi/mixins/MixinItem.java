package net.infinitelimit.kintsugi.mixins;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem {
    @Inject(at = @At("HEAD"), method = "isFoil(Lnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    private void isFoil(ItemStack pStack, CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(true);
    }
}
