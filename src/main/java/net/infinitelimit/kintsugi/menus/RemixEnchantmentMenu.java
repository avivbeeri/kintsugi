package net.infinitelimit.kintsugi.menus;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.infinitelimit.kintsugi.item.PowerBookItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class RemixEnchantmentMenu extends AbstractContainerMenu {
    private final ContainerData enchantmentAvailability;

    private final Container enchantSlots = new SimpleContainer(2) {
        /**
         * For block entities, ensures the chunk containing the block entity is saved to disk later - the game won't think
         * it hasn't changed and skip it.
         */
        public void setChanged() {
            super.setChanged();
            RemixEnchantmentMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;
    private final DataSlot enchantmentPower = DataSlot.standalone();
    private final DataSlot fuelCost = DataSlot.standalone();
    private final DataSlot levelCost = DataSlot.standalone();
    private final DataSlot selectedEnchantment = DataSlot.standalone();
    private final Map<ResourceLocation, Integer> enchantmentIndexMap;
    private final Map<Integer, ResourceLocation> indexEnchantmentMap;
    public final Set<Enchantment> enchantmentsFound = new HashSet<>();
    private final ResultContainer resultSlot = new ResultContainer();


    public RemixEnchantmentMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    public RemixEnchantmentMenu(int id, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(id, inventory);
    }

    public RemixEnchantmentMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(ModMenuTypes.ENCHANTMENT.get(), pContainerId);
        this.access = pAccess;
        this.levelCost.set(-1);
        this.fuelCost.set(-1);

        enchantmentAvailability = new SimpleContainerData(ForgeRegistries.ENCHANTMENTS.getValues().size());

        List<ResourceLocation> enchantmentKeys = ForgeRegistries.ENCHANTMENTS.getKeys().stream().toList();
        enchantmentIndexMap = new Object2IntArrayMap<>();
        indexEnchantmentMap = new Int2ObjectArrayMap<>();

        int index = 0;
        for (ResourceLocation location: enchantmentKeys) {
            this.enchantmentAvailability.set(index, 0);
            indexEnchantmentMap.put(index, location);
            enchantmentIndexMap.put(location, index);
            index++;
        }

        this.addDataSlots(enchantmentAvailability);
        this.addDataSlot(selectedEnchantment).set(-1);

        this.addSlot(new Slot(this.enchantSlots, 0, 182, 37) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean mayPlace(ItemStack pStack) {
                return true;
            }

            /**
             * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
             * case of armor slots)
             */
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.enchantSlots, 1, 208, 37) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean mayPlace(ItemStack pStack) {
                return pStack.is(net.minecraftforge.common.Tags.Items.ENCHANTING_FUELS);
            }
        });

        this.addSlot(new Slot(this.resultSlot, 1, 250, 37) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean mayPlace(ItemStack pStack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player pPlayer) {
                return RemixEnchantmentMenu.this.mayPickup(pPlayer, this.hasItem());
            }

            @Override
            public void onTake(Player pPlayer, ItemStack pStack) {
                RemixEnchantmentMenu.this.onTake(pPlayer, pStack);
            }
        });

        addPlayerInventory(pPlayerInventory);
        this.addDataSlot(this.enchantmentPower).set(0);

        this.access.execute((pLevel, pBlockPos) -> {
            if (pLevel.isClientSide()) {
                this.refreshFoundEnchantments();
                return;
            }
            enchantmentsFound.addAll(calculateFoundEnchantments(pLevel, pBlockPos));
            int power = this.enchantmentsFound.size();
            this.enchantmentPower.set(enchantmentsFound.size());
            this.broadcastChanges();

            pPlayerInventory.player.sendSystemMessage(Component.literal("Books found: " + power));
            pPlayerInventory.player.sendSystemMessage(Component.literal("Unique enchantments found: " + enchantmentsFound.size()));
        });
    }

    protected boolean mayPickup(Player pPlayer, boolean pHasStack) {
        // calculate costs here
        int cost = this.levelCost.get();
        int lapisCost = this.fuelCost.get();
        return (pHasStack &&
                this.getFuelCount() >= lapisCost &&
                (pPlayer.getAbilities().instabuild || (pPlayer.experienceLevel >= cost)));
    }

    protected void onTake(Player pPlayer, ItemStack pStack) {
        int cost = this.levelCost.get();
        int lapisCost = this.fuelCost.get();
        // Clear the old tool
        // Reduce the lapis by lapis cost
        this.enchantSlots.setItem(0, ItemStack.EMPTY);
        ItemStack fuelStack = this.enchantSlots.getItem(1);
        fuelStack.shrink(lapisCost);
        if (fuelStack.isEmpty()) {
            this.enchantSlots.setItem(1, ItemStack.EMPTY);
        }
        pPlayer.giveExperienceLevels(-cost);
        this.broadcastChanges();
    }

    private Set<Enchantment> calculateFoundEnchantments(Level pLevel, BlockPos pBlockPos) {
        Set<Enchantment> enchantments = new HashSet<>();
        List<ChiseledBookShelfBlockEntity> bookshelves = EnchantmentTableBlock.BOOKSHELF_OFFSETS.stream()
                .filter(offsetPos -> EnchantmentTableBlock.isValidBookShelf(pLevel, pBlockPos, offsetPos))
                .map(offsetPos -> pLevel.getBlockEntity(pBlockPos.offset(offsetPos)))
                .filter(entity -> entity instanceof ChiseledBookShelfBlockEntity)
                .map(entity -> (ChiseledBookShelfBlockEntity) entity)
                .toList();
        for (ChiseledBookShelfBlockEntity bookshelf : bookshelves) {
            for (int i = 0; i < ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); i++) {
                if (bookshelf.getBlockState().getValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i))) {
                    ItemStack itemStack = bookshelf.getItem(i);
                    if (!itemStack.isEmpty()) {
                        CompoundTag tag = itemStack.getOrCreateTag();
                        if (tag.contains(PowerBookItem.TAG_RITUAL_ENCHANTMENT)) {
                            ResourceLocation enchantmentId = PowerBookItem.getEnchantmentId(tag);
                            this.enchantmentAvailability.set(enchantmentIndexMap.get(enchantmentId), 1);

                            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
                            enchantments.add(enchantment);
                        }
                    }
                }
            }
        }
        return enchantments;
    }

    private void addPlayerInventory(Inventory pPlayerInventory) {
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 124 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 124 + k * 18, 142));
        }
    }


    /**
     * Callback for when the crafting matrix is changed.
     */
    public void slotsChanged(Container pInventory) {
        if (pInventory == this.enchantSlots) {
            ItemStack itemstack = pInventory.getItem(0);
            ItemStack fuelStack = pInventory.getItem(1);
            calculateResultItem(itemstack, fuelStack);
        }
    }

    private void calculateResultItem(ItemStack itemStack, ItemStack fuelStack) {
        if (!itemStack.isEmpty() && !fuelStack.isEmpty()) {

            this.access.execute((pLevel, pBlockPos) -> {
                this.enchantmentsFound.clear();
                this.enchantmentsFound.addAll(this.calculateFoundEnchantments(pLevel, pBlockPos));
                int maxLevel = getMaxEnchantmentLevel(itemStack);

                if (!this.enchantmentsFound.isEmpty() && this.selectedEnchantment.get() >= 0) {
                    ResourceLocation enchantmentId = EnchantmentHelper.getEnchantmentId(this.getAvailableEnchantments().get(this.selectedEnchantment.get()));
                    Enchantment selection = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
                    assert selection != null;
                    int targetLevel = Math.min(Math.min(maxLevel, selection.getMaxLevel()), fuelStack.getCount());
                    this.levelCost.set(1);
                    this.fuelCost.set(targetLevel);
                    if (selection.canEnchant(this.enchantSlots.getItem(0))) {
                        ItemStack copy = this.enchantSlots.getItem(0).copyWithCount(1);
                        Map<Enchantment, Integer> allEnchantments = copy.getAllEnchantments();
                        allEnchantments.put(selection, targetLevel);
                        EnchantmentHelper.setEnchantments(allEnchantments, copy);
                        this.resultSlot.setItem(0, copy);
                    }
                } else {
                    this.resultSlot.setItem(0, ItemStack.EMPTY);
                }

                this.broadcastChanges();
            });
        } else {
            this.enchantmentsFound.clear();
            this.selectedEnchantment.set(-1);
            this.levelCost.set(-1);
            this.fuelCost.set(-1);
            this.resultSlot.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
        }
    }

    private int getMaxEnchantmentLevel(ItemStack itemStack) {
        int maxLevel = 1;

        if (this.enchantmentsFound.size() >= 3) {
            maxLevel++;
        }
        if (this.enchantmentsFound.size() >= 3 + 6) {
            maxLevel++;
        }
        if (this.enchantmentsFound.size() >= 3 + 6 + 9) {
            maxLevel++;
        }
        if (this.enchantmentsFound.size() >= 3 + 6 + 9 + 12) {
            maxLevel++;
        }

        Item item = itemStack.getItem();
        if (item instanceof TieredItem tieredItem) {
            if (tieredItem.getTier().equals(Tiers.GOLD)) {
                 maxLevel++;
            }
        }
        return maxLevel;
    }

    public void refreshFoundEnchantments() {
        this.enchantmentsFound.clear();
        for (ResourceLocation location: enchantmentIndexMap.keySet()) {
            if (this.enchantmentAvailability.get(enchantmentIndexMap.get(location)) == 1) {
                this.enchantmentsFound.add(ForgeRegistries.ENCHANTMENTS.getValue(location));
            }
        }
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (pId >= 0 && pId < this.getAvailableEnchantments().size()) {
            this.selectEnchantment(pId);
            return true;
        }
        return super.clickMenuButton(pPlayer, pId);
    }

    public List<Enchantment> getAvailableEnchantments() {
        this.refreshFoundEnchantments();
        ItemStack itemStack = this.getSlot(0).getItem();

        return this.enchantmentsFound
                .stream()
                .filter(enchantment -> enchantment.canEnchant(itemStack))
                .sorted((a, b) -> {
                    String aName = Component.translatable(a.getDescriptionId()).getString();
                    String bName = Component.translatable(b.getDescriptionId()).getString();
                    return aName.compareToIgnoreCase(bName);
                })
                .collect(Collectors.toList());
    }

    public int getFuelCount() {
        ItemStack fuelStack = this.enchantSlots.getItem(1);
        return fuelStack.isEmpty() ? 0 : fuelStack.getCount();
    }

    public int getFuelCost() {
        return this.fuelCost.get();
    }

    public int getLevelCost() {
        return this.levelCost.get();
    }

    public int getEnchantmentTotal() {
        int total = 0;
        for (ResourceLocation location: ForgeRegistries.ENCHANTMENTS.getKeys()) {
            if (this.enchantmentAvailability.get(enchantmentIndexMap.get(location)) == 1) {
                total++;
            }
        }
        return total;
    }

    public void selectEnchantment(int i) {
        this.selectedEnchantment.set(i);
        this.broadcastChanges();
        ItemStack itemstack = this.enchantSlots.getItem(0);
        ItemStack fuelStack = this.enchantSlots.getItem(1);
        calculateResultItem(itemstack, fuelStack);
    }

    public int getSelectedEnchantment() {
      return this.selectedEnchantment.get();
    }

    /**
     * Called when the container is closed.
     */
    public void removed(Player pPlayer) {
        this.resultSlot.setItem(0, ItemStack.EMPTY);
        super.removed(pPlayer);
        this.access.execute((pLevel, pBlockPos) -> {
            this.clearContainer(pPlayer, this.enchantSlots);
        });
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, Blocks.ENCHANTING_TABLE);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex == 1) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.is(net.minecraftforge.common.Tags.Items.ENCHANTING_FUELS)) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(itemstack1)) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemstack2 = itemstack1.copyWithCount(1);
                itemstack1.shrink(1);
                this.slots.get(0).setByPlayer(itemstack2);
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }
}

