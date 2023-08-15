package net.infinitelimit.kintsugi.menus;

import java.util.*;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.infinitelimit.kintsugi.item.PowerBookItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class RemixEnchantmentMenu extends AbstractContainerMenu {
    private final ContainerData enchantmentAvailability;

    private final ContainerLevelAccess access;

    private final DataSlot nearbyEnchantmentCount = DataSlot.standalone();
    private final DataSlot fuelCost = DataSlot.standalone();
    private final DataSlot levelCost = DataSlot.standalone();
    private final DataSlot selectedEnchantment = DataSlot.standalone();
    private final DataSlot maxPower = DataSlot.standalone();
    private final DataSlot valid = DataSlot.standalone();

    private final Map<ResourceLocation, Integer> enchantmentIndexMap;
    public final Set<Enchantment> enchantmentsNearby = new HashSet<>();

    private Item lastItem = Items.AIR;
    private final Set<Enchantment> DEFAULT_ENCHANTMENTS = Set.of(
            Enchantments.UNBREAKING,
            Enchantments.BLOCK_EFFICIENCY,
            Enchantments.SILK_TOUCH,
            Enchantments.POWER_ARROWS,
            Enchantments.ALL_DAMAGE_PROTECTION);

    private final ResultContainer resultSlot = new ResultContainer();
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


        int index = 0;
        for (ResourceLocation location: enchantmentKeys) {
            this.enchantmentAvailability.set(index, 0);
            enchantmentIndexMap.put(location, index);
            index++;
        }

        // Set the default enchantments
        for (Enchantment enchantment: DEFAULT_ENCHANTMENTS) {
            this.enchantmentAvailability.set(enchantmentIndexMap.get(EnchantmentHelper.getEnchantmentId(enchantment)), 1);
        }

        this.addDataSlots(enchantmentAvailability);
        this.addDataSlot(selectedEnchantment).set(-1);
        this.addDataSlot(maxPower).set(0);
        this.addDataSlot(valid).set(1);

        this.addSlot(new Slot(this.enchantSlots, 0, 178, 25) {
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
        this.addSlot(new Slot(this.enchantSlots, 1, 178, 48) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean mayPlace(ItemStack pStack) {
                return pStack.is(net.minecraftforge.common.Tags.Items.ENCHANTING_FUELS);
            }
        });

        this.addSlot(new Slot(this.resultSlot, 1, 248, 25) {
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
        this.addDataSlot(this.nearbyEnchantmentCount).set(0);

        this.access.execute((pLevel, pBlockPos) -> {
            if (pLevel.isClientSide()) {
                this.refreshNearbyEnchantments();
                return;
            }
            enchantmentsNearby.addAll(calculateNearbyEnchantments(pLevel, pBlockPos));
            int power = this.enchantmentsNearby.size();
            this.nearbyEnchantmentCount.set(enchantmentsNearby.size());
            this.broadcastChanges();

            pPlayerInventory.player.sendSystemMessage(Component.literal("Books nearby: " + power));
            pPlayerInventory.player.sendSystemMessage(Component.literal("Unique enchantments nearby: " + getNearbyEnchantmentCount()));
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

        pPlayer.onEnchantmentPerformed(pStack, cost);
        pPlayer.awardStat(Stats.ENCHANT_ITEM);
        if (pPlayer instanceof ServerPlayer) {
            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) pPlayer, pStack, cost);
        }
        this.access.execute((pLevel, pBlockPos) -> {
            pLevel.playSound(null, pBlockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, pLevel.random.nextFloat() * 0.1F + 0.9F);
        });

        this.broadcastChanges();
    }

    private Set<Enchantment> calculateNearbyEnchantments(Level pLevel, BlockPos pBlockPos) {
        Set<Enchantment> enchantments = new HashSet<>(DEFAULT_ENCHANTMENTS);
        int count = 0;

        for (int i = 0; i < this.enchantmentAvailability.getCount(); i++) {
            this.enchantmentAvailability.set(i, 0);
        }
        // Set the default enchantments
        for (Enchantment enchantment: DEFAULT_ENCHANTMENTS) {
            this.enchantmentAvailability.set(enchantmentIndexMap.get(EnchantmentHelper.getEnchantmentId(enchantment)), 1);
        }

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
                            if (!DEFAULT_ENCHANTMENTS.contains(enchantment)) {
                                count = count + 1;
                            }
                            enchantments.add(enchantment);
                        }
                    }
                }
            }
        }
        this.nearbyEnchantmentCount.set(count);
        this.broadcastChanges();

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

    private boolean calculateCompatibility(Collection<Enchantment> enchantments) {
        for(Enchantment enchantment1 : enchantments) {
            for (Enchantment enchantment2 : enchantments) {
                if (enchantment1 != enchantment2 && !enchantment1.isCompatibleWith(enchantment2)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void calculateResultItem(ItemStack itemStack, ItemStack fuelStack) {
        if (!itemStack.isEmpty()) {
            this.access.execute((pLevel, pBlockPos) -> {
                this.enchantmentsNearby.clear();
                this.enchantmentsNearby.addAll(this.calculateNearbyEnchantments(pLevel, pBlockPos));
                if (this.lastItem != itemStack.getItem()) {
                    this.selectedEnchantment.set(-1);
                }
                this.lastItem = itemStack.getItem();

                if (!this.enchantmentsNearby.isEmpty() && this.selectedEnchantment.get() >= 0) {
                    ResourceLocation enchantmentId = EnchantmentHelper.getEnchantmentId(this.getAvailableEnchantments().get(this.selectedEnchantment.get()));
                    Enchantment selection = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
                    assert selection != null;

                    Set<Enchantment> currentEnchantments = new HashSet<>(itemStack.getAllEnchantments().keySet());
                    currentEnchantments.add(selection);
                    if (!calculateCompatibility(currentEnchantments)) {
                        this.valid.set(0);
                    } else {
                        this.valid.set(1);
                    }

                    int maxLevel = getMaxEnchantmentLevel(itemStack);
                    maxLevel = Math.min(maxLevel, selection.getMaxLevel());
                    this.maxPower.set(maxLevel);
                } else {
                    this.maxPower.set(0);
                    this.valid.set(1);
                }


                this.broadcastChanges();
            });
        } else {
            this.enchantmentsNearby.clear();
            this.maxPower.set(0);
            this.valid.set(1);
        }

        if (!itemStack.isEmpty() && !fuelStack.isEmpty()) {
            this.access.execute((pLevel, pBlockPos) -> {
                if (!this.enchantmentsNearby.isEmpty() && this.selectedEnchantment.get() >= 0 && this.getValidity()) {
                    ResourceLocation enchantmentId = EnchantmentHelper.getEnchantmentId(this.getAvailableEnchantments().get(this.selectedEnchantment.get()));
                    Enchantment selection = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
                    assert selection != null;

                    int targetLevel = Mth.clamp(fuelStack.getCount(), 1, this.maxPower.get());
                    this.levelCost.set(1);
                    this.fuelCost.set(targetLevel);
                    if (selection.canEnchant(itemStack) || itemStack.is(Items.ENCHANTED_BOOK)) {
                        ItemStack copy = itemStack.copyWithCount(1);
                        Map<Enchantment, Integer> allEnchantments = copy.getAllEnchantments();
                        allEnchantments.put(selection, targetLevel);
                        EnchantmentHelper.setEnchantments(allEnchantments, copy);
                        this.resultSlot.setItem(0, copy);
                    } else if (itemStack.is(Items.BOOK)) {
                        this.resultSlot.setItem(0, new ItemStack(Items.ENCHANTED_BOOK));
                        EnchantedBookItem.addEnchantment(this.resultSlot.getItem(0), new EnchantmentInstance(selection, targetLevel));
                    }
                } else {
                    this.resultSlot.setItem(0, ItemStack.EMPTY);
                }

                this.broadcastChanges();
            });
        } else {
            this.levelCost.set(-1);
            this.fuelCost.set(-1);
            this.resultSlot.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
        }
    }

    private int getMaxEnchantmentLevel(ItemStack itemStack) {
        int maxLevel = 1;
        int count = this.getNearbyEnchantmentCount();

        if (count >= 3) {
            maxLevel++;
        }
        if (count >= 3 + 6) {
            maxLevel++;
        }
        if (count >= 3 + 6 + 9) {
            maxLevel++;
        }
        if (count >= 3 + 6 + 9 + 12) {
            maxLevel++;
        }

        Item item = itemStack.getItem();
        if (item instanceof TieredItem tieredItem) {
            if (tieredItem.getTier().equals(Tiers.GOLD)) {
                 maxLevel++;
            }
        } else if (item instanceof ArmorItem armorItem) {
            if (armorItem.getMaterial().equals(ArmorMaterials.GOLD)) {
                maxLevel++;
            }
        }
        return maxLevel;
    }

    public void refreshNearbyEnchantments() {
        this.enchantmentsNearby.clear();
        this.enchantmentsNearby.addAll(DEFAULT_ENCHANTMENTS);
        for (ResourceLocation location: enchantmentIndexMap.keySet()) {
            if (this.enchantmentAvailability.get(enchantmentIndexMap.get(location)) == 1) {
                this.enchantmentsNearby.add(ForgeRegistries.ENCHANTMENTS.getValue(location));
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
        this.refreshNearbyEnchantments();
        ItemStack itemStack = this.getSlot(0).getItem();
        boolean isBook = itemStack.is(Items.BOOK);

        return this.enchantmentsNearby
                .stream()
                .filter(enchantment -> isBook || enchantment.canEnchant(itemStack))
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

    public boolean getValidity() {
        return this.valid.get() == 1;
    }

    public int getNearbyEnchantmentCount() {
        return this.nearbyEnchantmentCount.get();
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

    public int getMaxPower() {
        return this.maxPower.get();
    }
}

