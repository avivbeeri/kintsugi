package net.infinitelimit.kintsugi.datagen;

import net.infinitelimit.kintsugi.item.ModItems;
import net.infinitelimit.kintsugi.loot.ChestLootModifier;
import net.infinitelimit.kintsugi.loot.FishingLootModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

import java.util.Map;
import java.util.Objects;

import static net.minecraft.world.item.enchantment.Enchantments.*;

public class ModLootTableProvider extends GlobalLootModifierProvider {

    public ModLootTableProvider(PackOutput output, String modid) {
        super(output, modid);
    }

    @Override
    public void start() {

        this.addChestEntry(BuiltInLootTables.SIMPLE_DUNGEON, 0.07, Map.ofEntries(
            Map.entry(eId(PROJECTILE_PROTECTION), 0.333),
            Map.entry(eId(SMITE), 0.333),
            Map.entry(eId(BANE_OF_ARTHROPODS), 0.334)
        ));

        this.addChestEntry(BuiltInLootTables.NETHER_BRIDGE, 0.03, Map.ofEntries(
            Map.entry(eId(FIRE_PROTECTION), 0.333),
            Map.entry(eId(FIRE_ASPECT), 0.333),
            Map.entry(eId(FLAMING_ARROWS), 0.333)
        ));
        this.addChestEntry(BuiltInLootTables.RUINED_PORTAL, 0.015, Map.ofEntries(
                Map.entry(eId(FIRE_PROTECTION), 0.333),
                Map.entry(eId(FIRE_ASPECT), 0.333),
                Map.entry(eId(FLAMING_ARROWS), 0.334)
        ));
        this.addChestEntry(BuiltInLootTables.ABANDONED_MINESHAFT, 0.07, Map.ofEntries(
                Map.entry(eId(BANE_OF_ARTHROPODS), 1.0)
        ));
        this.addChestEntry(BuiltInLootTables.STRONGHOLD_LIBRARY, 0.33, Map.ofEntries(
                Map.entry(eId(BANE_OF_ARTHROPODS), 0.5),
                Map.entry(eId(INFINITY_ARROWS), 0.5)
        ));
        this.addChestEntry(BuiltInLootTables.ANCIENT_CITY, 0.025, Map.ofEntries(
                Map.entry(eId(MENDING), 0.5),
                Map.entry(eId(SWIFT_SNEAK), 0.5)
        ));
        this.addChestEntry(BuiltInLootTables.SHIPWRECK_TREASURE, 0.08, Map.ofEntries(
                Map.entry(eId(AQUA_AFFINITY), 0.333),
                Map.entry(eId(RESPIRATION), 0.333),
                Map.entry(eId(DEPTH_STRIDER), 0.334)
        ));

        this.addChestEntry(BuiltInLootTables.WOODLAND_MANSION, 0.25, Map.ofEntries(
                Map.entry(eId(INFINITY_ARROWS), 0.5),
                Map.entry(eId(MOB_LOOTING), 0.5)
        ));

        this.addChestEntry(BuiltInLootTables.UNDERWATER_RUIN_SMALL, 0.025, Map.ofEntries(
                Map.entry(eId(IMPALING), 0.25),
                Map.entry(eId(CHANNELING), 0.25),
                Map.entry(eId(RIPTIDE), 0.25),
                Map.entry(eId(LOYALTY), 0.25)
        ));

        this.addChestEntry(BuiltInLootTables.UNDERWATER_RUIN_BIG, 0.10, Map.ofEntries(
                Map.entry(eId(IMPALING), 0.25),
                Map.entry(eId(CHANNELING), 0.25),
                Map.entry(eId(RIPTIDE), 0.25),
                Map.entry(eId(LOYALTY), 0.25)
        ));

        this.addChestEntry(BuiltInLootTables.BASTION_TREASURE, 0.05, Map.ofEntries(
                Map.entry(eId(PIERCING), 0.333),
                Map.entry(eId(QUICK_CHARGE), 0.333),
                Map.entry(eId(MULTISHOT), 0.334)
        ));

        this.addChestEntry(BuiltInLootTables.BASTION_BRIDGE, 0.02, Map.ofEntries(
                Map.entry(eId(PIERCING), 0.333),
                Map.entry(eId(QUICK_CHARGE), 0.333),
                Map.entry(eId(MULTISHOT), 0.334)
        ));

        this.addChestEntry(BuiltInLootTables.BASTION_HOGLIN_STABLE, 0.01, Map.ofEntries(
                Map.entry(eId(PIERCING), 0.667),
                Map.entry(eId(QUICK_CHARGE), 0.166),
                Map.entry(eId(MULTISHOT), 0.166)
        ));

        this.addChestEntry(BuiltInLootTables.PILLAGER_OUTPOST, 0.06, Map.ofEntries(
                Map.entry(eId(PIERCING), 0.333),
                Map.entry(eId(QUICK_CHARGE), 0.333),
                Map.entry(eId(MULTISHOT), 0.334)
        ));

        this.addFishingEntry(0.3334, Map.ofEntries(
                Map.entry(eId(FISHING_SPEED), 0.48),
                Map.entry(eId(FISHING_LUCK), 0.48),
                Map.entry(eId(BINDING_CURSE), 0.02),
                Map.entry(eId(VANISHING_CURSE), 0.02)
        ));
    }

    private String eId(Enchantment enchantment) {
        return Objects.requireNonNull(EnchantmentHelper.getEnchantmentId(enchantment)).toString();
    }

    private void addFishingEntry(double rate, Map<String, Double> enchantments) {
        this.add(toFileName(BuiltInLootTables.FISHING), new FishingLootModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(BuiltInLootTables.FISHING).build()
                },
                ModItems.KNOWLEDGE_BOOK.get(),
                rate,
                enchantments
        ));
    }

    private void addChestEntry(ResourceLocation location, double rate, Map<String, Double> enchantments) {
        this.add(toFileName(location), new ChestLootModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(location).build()
                },
                ModItems.KNOWLEDGE_BOOK.get(),
                rate,
                enchantments
        ));
    }

    private static String toFileName(ResourceLocation location) {
        return location.getPath().replace('/', '_').replace(':', '_');
    }



}
