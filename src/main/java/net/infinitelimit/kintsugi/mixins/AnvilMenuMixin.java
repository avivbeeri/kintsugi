package net.infinitelimit.kintsugi.mixins;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    public AnvilMenuMixin(@Nullable MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
    }

    @Shadow public abstract int getCost();

    @Shadow public int repairItemCountCost;

    /**
     * @author Aviv
     * @reason We want to allow items to be repaired without an experience cost maybe
     */
    @Overwrite
    protected boolean mayPickup(Player pPlayer, boolean pHasStack) {
        return (pPlayer.getAbilities().instabuild
                || (pPlayer.experienceLevel >= this.getCost()) && this.getCost() > 0
                || this.getSlot(1).getItem().getCount() >= this.repairItemCountCost && this.repairItemCountCost > 0);
    }
}
