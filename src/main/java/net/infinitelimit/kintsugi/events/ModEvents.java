package net.infinitelimit.kintsugi.events;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.offers.ModTradeOffers;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.Items.NAME_TAG;

@Mod.EventBusSubscriber(modid = Kintsugi.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.LIBRARIAN) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.put(1, List.of(new VillagerTrades.EmeraldForItems(Items.PAPER, 24, 16, 2), new ModTradeOffers.RandomKnowledgeBookForEmeralds(2), new VillagerTrades.ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)));
            trades.put(2, List.of(new VillagerTrades.EmeraldForItems(Items.BOOK, 4, 12, 10), new ModTradeOffers.RandomKnowledgeBookForEmeralds(5), new VillagerTrades.ItemsForEmeralds(Items.LANTERN, 1, 1, 5)));
            trades.put(3, List.of(new VillagerTrades.EmeraldForItems(Items.INK_SAC, 5, 12, 20), new ModTradeOffers.RandomKnowledgeBookForEmeralds(10), new VillagerTrades.ItemsForEmeralds(Items.GLASS, 1, 4, 10)));
            trades.put(4, List.of(new VillagerTrades.EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30), new ModTradeOffers.RandomKnowledgeBookForEmeralds(15), new VillagerTrades.ItemsForEmeralds(Items.CLOCK, 5, 1, 15), new VillagerTrades.ItemsForEmeralds(Items.COMPASS, 4, 1, 15)));
            trades.put(5, List.of(new VillagerTrades.ItemsForEmeralds(NAME_TAG, 20, 1, 30), new ModTradeOffers.KnowledgeBookForEmeralds(30)));
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack item = event.getLeft();
        ItemStack sacrifice = event.getRight();
        ItemStack result = item.copy();

        boolean createResult = false;
        int xpCost = 0;
        int materialCost = 0;
        int damage = item.getDamageValue();
        int recovery = 0;
        boolean addUse = false;

        if (item.isEmpty()) {
            event.setOutput(ItemStack.EMPTY);
            return;
        }

        if (event.getName() != null && !Util.isBlank(event.getName())) {
            result.setHoverName(Component.literal(event.getName()));
            xpCost += 1;
            createResult = true;
        }

        if (!sacrifice.isEmpty()) {
            if (item.isDamageableItem() && item.isDamaged() && item.getItem().isValidRepairItem(item, sacrifice)) {
                createResult = true;
                recovery = Math.round((result.getMaxDamage() * 0.25f) * Math.min(4, item.getCount()));
                materialCost += 1;
                addUse = true;
            }
        }

        if (createResult) {
            if (addUse) {
                int repairCost = Math.max(item.getBaseRepairCost(), sacrifice.getBaseRepairCost());
                repairCost = AnvilMenu.calculateIncreasedRepairCost(repairCost);
                result.setRepairCost(repairCost);
            }
            int maxRepair = Math.round(result.getMaxDamage() * (1.0f - (result.getBaseRepairCost() / 31.0f)));

            result.setDamageValue(damage - Math.min(maxRepair, recovery));

            event.setOutput(result);
            // how many levels to extract from player
            event.setCost(xpCost);
            // how many repair items in stack to consume
            event.setMaterialCost(materialCost);


        } else {
            event.setOutput(ItemStack.EMPTY);
        }
    }
/*
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean DEBUG_COST = false;
    public static final int MAX_NAME_LENGTH = 50;
    public int repairItemCountCost;
    @Nullable
    private String itemName;
    private final DataSlot cost = DataSlot.standalone();
    private static final int COST_FAIL = 0;
    private static final int COST_BASE = 1;
    private static final int COST_ADDED_BASE = 1;
    private static final int COST_REPAIR_MATERIAL = 1;
    private static final int COST_REPAIR_SACRIFICE = 2;
    private static final int COST_INCOMPATIBLE_PENALTY = 1;
    private static final int COST_RENAME = 1;
    private static final int INPUT_SLOT_X_PLACEMENT = 27;
    private static final int ADDITIONAL_SLOT_X_PLACEMENT = 76;
    private static final int RESULT_SLOT_X_PLACEMENT = 134;
    private static final int SLOT_Y_PLACEMENT = 47;
    private static final int INVENTORY_SLOTS_PER_ROW = 9;
    private static final int INVENTORY_SLOTS_PER_COLUMN = 3;
    protected  ContainerLevelAccess access;
    protected Player player;
    protected  Container inputSlots;
    private  List<Integer> inputSlotIndexes;
    protected  ResultContainer resultSlots = new ResultContainer();
    private  int resultSlotIndex;


    public void createResult() {
        ItemStack input1 = this.inputSlots.getItem(0);
        this.cost.set(COST_BASE);
        int i = 0;
        int baseRepairCost = 0;
        int k = 0;
        if (input1.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(COST_FAIL);
        } else {
            ItemStack result = input1.copy();
            ItemStack input2 = this.inputSlots.getItem(1);
            Map<Enchantment, Integer> originalEnchantments = EnchantmentHelper.getEnchantments(result);
            baseRepairCost += input1.getBaseRepairCost() + (input2.isEmpty() ? 0 : input2.getBaseRepairCost());
            this.repairItemCountCost = 0;
            boolean combiningWithBook = false;

            if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(null, input1, input2, resultSlots, itemName, baseRepairCost, this.player)) return;
            if (!input2.isEmpty()) {
                combiningWithBook = input2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(input2).isEmpty();
                if (result.isDamageableItem() && result.getItem().isValidRepairItem(input1, input2)) {
                    int resultDamageValue = Math.min(result.getDamageValue(), result.getMaxDamage() / 4);
                    if (resultDamageValue <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(COST_FAIL);
                        return;
                    }

                    int i3;
                    for(i3 = 0; resultDamageValue > 0 && i3 < input2.getCount(); ++i3) {
                        int j3 = result.getDamageValue() - resultDamageValue;
                        result.setDamageValue(j3);
                        ++i;
                        resultDamageValue = Math.min(result.getDamageValue(), result.getMaxDamage() / 4);
                    }

                    this.repairItemCountCost = i3;
                } else {
                    if (!combiningWithBook && (!result.is(input2.getItem()) || !result.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(COST_FAIL);
                        return;
                    }

                    if (result.isDamageableItem() && !combiningWithBook) {
                        int itemHealth1 = input1.getMaxDamage() - input1.getDamageValue();
                        int itemHealth2 = input2.getMaxDamage() - input2.getDamageValue();
                        int repairedHealth = itemHealth2 + result.getMaxDamage() * 12 / 100;
                        int combinedHealth = itemHealth1 + repairedHealth;
                        int resultHealth = result.getMaxDamage() - combinedHealth;
                        if (resultHealth < 0) {
                            resultHealth = 0;
                        }

                        if (resultHealth < result.getDamageValue()) {
                            result.setDamageValue(resultHealth);
                            i += 2;
                        }
                    }

                    Map<Enchantment, Integer> newEnchantments = EnchantmentHelper.getEnchantments(input2);
                    boolean flag2 = false;
                    boolean isImpossibleCombination = false;

                    for(Enchantment newEnchantment : newEnchantments.keySet()) {
                        if (newEnchantment != null) {
                            int originalLevel = originalEnchantments.getOrDefault(newEnchantment, 0);
                            int enchantmentLevel = newEnchantments.get(newEnchantment);
                            enchantmentLevel = originalLevel == enchantmentLevel ? enchantmentLevel + 1 : Math.max(enchantmentLevel, originalLevel);
                            boolean isEnchantmentCompatible = newEnchantment.canEnchant(input1);
                            if (this.player.getAbilities().instabuild || input1.is(Items.ENCHANTED_BOOK)) {
                                isEnchantmentCompatible = true;
                            }

                            for(Enchantment originalEnchantment : originalEnchantments.keySet()) {
                                if (originalEnchantment != newEnchantment && !newEnchantment.isCompatibleWith(originalEnchantment)) {
                                    isEnchantmentCompatible = false;
                                    ++i;
                                }
                            }

                            if (!isEnchantmentCompatible) {
                                isImpossibleCombination = true;
                            } else {
                                flag2 = true;
                                if (enchantmentLevel > newEnchantment.getMaxLevel()) {
                                    enchantmentLevel = newEnchantment.getMaxLevel();
                                }

                                originalEnchantments.put(newEnchantment, enchantmentLevel);
                            }
                        }
                    }

                    if (isImpossibleCombination && !flag2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }

            if (this.itemName != null && !Util.isBlank(this.itemName)) {
                if (!this.itemName.equals(input1.getHoverName().getString())) {
                    k = 1;
                    i += k;
                    result.setHoverName(Component.literal(this.itemName));
                }
            } else if (input1.hasCustomHoverName()) {
                k = 1;
                i += k;
                result.resetHoverName();
            }
            if (combiningWithBook && !result.isBookEnchantable(input2)) result = ItemStack.EMPTY;

            this.cost.set(baseRepairCost + i);
            if (i <= 0) {
                result = ItemStack.EMPTY;
            }

            if (k == i && k > 0 && this.cost.get() >= 40) {
                this.cost.set(39); // maximum?
            }

            if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
                result = ItemStack.EMPTY;
            }

            if (!result.isEmpty()) {
                int k2 = result.getBaseRepairCost();
                if (!input2.isEmpty() && k2 < input2.getBaseRepairCost()) {
                    k2 = input2.getBaseRepairCost();
                }

                if (k != i || k == 0) {
                   // k2 = calculateIncreasedRepairCost(k2);
                }

                result.setRepairCost(k2);
                EnchantmentHelper.setEnchantments(originalEnchantments, result);
            }

            this.resultSlots.setItem(0, result);
            //this.broadcastChanges();
        }
    }

 */

}
