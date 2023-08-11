package net.infinitelimit.kintsugi.menus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.infinitelimit.kintsugi.item.PowerBookItem;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

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
    private final RandomSource random = RandomSource.create();
    private final DataSlot enchantmentSeed = DataSlot.standalone();
    private final DataSlot enchantmentPower = DataSlot.standalone();
    public final int[] costs = new int[3];
    public final int[] enchantClue = new int[]{-1, -1, -1};
    public final int[] levelClue = new int[]{-1, -1, -1};
    private final Map<ResourceLocation, Integer> enchantmentIndexMap;


    public RemixEnchantmentMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    public RemixEnchantmentMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(ModMenuTypes.ENCHANTMENT.get(), pContainerId);
        this.access = pAccess;
        enchantmentAvailability = new SimpleContainerData(ForgeRegistries.ENCHANTMENTS.getValues().size());

        List<ResourceLocation> enchantmentKeys = ForgeRegistries.ENCHANTMENTS.getKeys().stream()
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .toList();
        enchantmentIndexMap = new Object2IntArrayMap<>();

        int index = 0;
        for (ResourceLocation location: enchantmentKeys) {
            this.enchantmentAvailability.set(index, 0);
            enchantmentIndexMap.put(location, index++);
        }

        this.addDataSlots(enchantmentAvailability);

        this.addSlot(new Slot(this.enchantSlots, 0, 15, 47) {
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
        this.addSlot(new Slot(this.enchantSlots, 1, 35, 47) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean mayPlace(ItemStack pStack) {
                return pStack.is(net.minecraftforge.common.Tags.Items.ENCHANTING_FUELS);
            }
        });

        addPlayerInventory(pPlayerInventory);

        this.addDataSlot(DataSlot.shared(this.costs, 0));
        this.addDataSlot(DataSlot.shared(this.costs, 1));
        this.addDataSlot(DataSlot.shared(this.costs, 2));

        this.addDataSlot(this.enchantmentSeed).set(pPlayerInventory.player.getEnchantmentSeed());
        this.addDataSlot(this.enchantmentPower).set(0);
        this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
        this.addDataSlot(DataSlot.shared(this.levelClue, 0));
        this.addDataSlot(DataSlot.shared(this.levelClue, 1));
        this.addDataSlot(DataSlot.shared(this.levelClue, 2));

        this.access.execute((pLevel, pBlockPos) -> {
            if (pLevel.isClientSide()) {
                return;
            }

            int power = 0;
            Set<Enchantment> enchantmentsFound = new HashSet<>();

            for (BlockPos offsetPos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
                if (EnchantmentTableBlock.isValidBookShelf(pLevel, pBlockPos, offsetPos)) {
                    BlockEntity entity = pLevel.getBlockEntity(pBlockPos.offset(offsetPos));
                    if (entity instanceof ChiseledBookShelfBlockEntity bookshelf) {
                        for (int i = 0; i < ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); i++) {
                            if (entity.getBlockState().getValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i))) {
                                ItemStack itemStack = bookshelf.getItem(i);
                                if (!itemStack.isEmpty()) {
                                    CompoundTag tag = itemStack.getOrCreateTag();
                                    if (tag.contains(PowerBookItem.TAG_RITUAL_ENCHANTMENT)) {
                                        power++;
                                        ResourceLocation enchantmentId = PowerBookItem.getEnchantmentId(tag);
                                        this.enchantmentAvailability.set(enchantmentIndexMap.get(enchantmentId), 1);

                                        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
                                        enchantmentsFound.add(enchantment);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.enchantmentPower.set(enchantmentsFound.size());
            this.broadcastChanges();
            pPlayerInventory.player.sendSystemMessage(Component.literal("Books found: " + power));
            pPlayerInventory.player.sendSystemMessage(Component.literal("Unique enchantments found: " + enchantmentsFound.size()));
            for (ResourceLocation location: enchantmentKeys) {
                if (this.enchantmentAvailability.get(enchantmentIndexMap.get(location)) == 1) {
                    pPlayerInventory.player.sendSystemMessage(
                            Component.literal("Found: " + Component.translatable(ForgeRegistries.ENCHANTMENTS.getValue(location).getDescriptionId())
                                    .getString())
                    );
                }

            }
        });
    }

    private void addPlayerInventory(Inventory pPlayerInventory) {
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 108 + k * 18, 142));
        }
    }

    public RemixEnchantmentMenu(int id, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(id, inventory);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void slotsChanged(Container pInventory) {
        if (pInventory == this.enchantSlots) {
            ItemStack itemstack = pInventory.getItem(0);
            if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                this.access.execute((pLevel, pBlockPos) -> {
                    float j = 0;

                    for (BlockPos blockpos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
                        if (EnchantmentTableBlock.isValidBookShelf(pLevel, pBlockPos, blockpos)) {
                            j += pLevel.getBlockState(pBlockPos.offset(blockpos)).getEnchantPowerBonus(pLevel, pBlockPos.offset(blockpos));
                        }
                    }

                    this.random.setSeed((long) this.enchantmentSeed.get());

                    for (int k = 0; k < 3; ++k) {
                        this.costs[k] = EnchantmentHelper.getEnchantmentCost(this.random, k, (int) j, itemstack);
                        this.enchantClue[k] = -1;
                        this.levelClue[k] = -1;
                        if (this.costs[k] < k + 1) {
                            this.costs[k] = 0;
                        }
                        this.costs[k] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(pLevel, pBlockPos, k, (int) j, itemstack, costs[k]);
                    }

                    for (int l = 0; l < 3; ++l) {
                        if (this.costs[l] > 0) {
                            List<EnchantmentInstance> list = this.getEnchantmentList(itemstack, l, this.costs[l]);
                            if (list != null && !list.isEmpty()) {
                                EnchantmentInstance enchantmentinstance = list.get(this.random.nextInt(list.size()));
                                this.enchantClue[l] = BuiltInRegistries.ENCHANTMENT.getId(enchantmentinstance.enchantment);
                                this.levelClue[l] = enchantmentinstance.level;
                            }
                        }
                    }

                    this.broadcastChanges();
                });
            } else {
                for (int i = 0; i < 3; ++i) {
                    this.costs[i] = 0;
                    this.enchantClue[i] = -1;
                    this.levelClue[i] = -1;
                }
            }
        }

    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (pId >= 0 && pId < this.costs.length) {
            ItemStack itemstack = this.enchantSlots.getItem(0);
            ItemStack itemstack1 = this.enchantSlots.getItem(1);
            int i = pId + 1;
            if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !pPlayer.getAbilities().instabuild) {
                return false;
            } else if (this.costs[pId] <= 0 || itemstack.isEmpty() || (pPlayer.experienceLevel < i || pPlayer.experienceLevel < this.costs[pId]) && !pPlayer.getAbilities().instabuild) {
                return false;
            } else {
                this.access.execute((pLevel, pBlockPos) -> {
                    ItemStack itemstack2 = itemstack;
                    List<EnchantmentInstance> list = this.getEnchantmentList(itemstack, pId, this.costs[pId]);
                    if (!list.isEmpty()) {
                        pPlayer.onEnchantmentPerformed(itemstack, i);
                        boolean flag = itemstack.is(Items.BOOK);
                        if (flag) {
                            itemstack2 = new ItemStack(Items.ENCHANTED_BOOK);
                            CompoundTag compoundtag = itemstack.getTag();
                            if (compoundtag != null) {
                                itemstack2.setTag(compoundtag.copy());
                            }

                            this.enchantSlots.setItem(0, itemstack2);
                        }

                        for (int j = 0; j < list.size(); ++j) {
                            EnchantmentInstance enchantmentinstance = list.get(j);
                            if (flag) {
                                EnchantedBookItem.addEnchantment(itemstack2, enchantmentinstance);
                            } else {
                                itemstack2.enchant(enchantmentinstance.enchantment, enchantmentinstance.level);
                            }
                        }

                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack1.shrink(i);
                            if (itemstack1.isEmpty()) {
                                this.enchantSlots.setItem(1, ItemStack.EMPTY);
                            }
                        }

                        pPlayer.awardStat(Stats.ENCHANT_ITEM);
                        if (pPlayer instanceof ServerPlayer) {
                            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) pPlayer, itemstack2, i);
                        }

                        this.enchantSlots.setChanged();
                        this.enchantmentSeed.set(pPlayer.getEnchantmentSeed());
                        this.slotsChanged(this.enchantSlots);
                        pLevel.playSound((Player) null, pBlockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, pLevel.random.nextFloat() * 0.1F + 0.9F);
                    }

                });
                return true;
            }
        } else {
            Util.logAndPauseIfInIde(pPlayer.getName() + " pressed invalid button id: " + pId);
            return false;
        }
    }

    private List<EnchantmentInstance> getEnchantmentList(ItemStack pStack, int pEnchantSlot, int pLevel) {
        this.random.setSeed((long) (this.enchantmentSeed.get() + pEnchantSlot));
        List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, pStack, pLevel, false);
        if (pStack.is(Items.BOOK) && list.size() > 1) {
            list.remove(this.random.nextInt(list.size()));
        }

        return list;
    }

    public int getGoldCount() {
        ItemStack itemstack = this.enchantSlots.getItem(1);
        return itemstack.isEmpty() ? 0 : itemstack.getCount();
    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed.get();
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

    /**
     * Called when the container is closed.
     */
    public void removed(Player pPlayer) {
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

