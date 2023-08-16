package net.infinitelimit.kintsugi.datagen;

import net.infinitelimit.kintsugi.item.ModItems;
import net.infinitelimit.kintsugi.loot.DungeonLootModifier;
import net.minecraft.client.particle.FlameParticle;
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

        this.addChestEntry(BuiltInLootTables.SIMPLE_DUNGEON, 0.05, Map.ofEntries(
            Map.entry(eId(PROJECTILE_PROTECTION), 0.333),
            Map.entry(eId(SMITE), 0.333),
            Map.entry(eId(BANE_OF_ARTHROPODS), 0.333)
        ));

        this.addChestEntry(BuiltInLootTables.NETHER_BRIDGE, 0.05, Map.ofEntries(
            Map.entry(eId(FIRE_PROTECTION), 0.333),
            Map.entry(eId(FIRE_ASPECT), 0.333),
            Map.entry(eId(FLAMING_ARROWS), 0.333)
        ));
        this.addChestEntry(BuiltInLootTables.RUINED_PORTAL, 0.05, Map.ofEntries(
                Map.entry(eId(FIRE_PROTECTION), 0.333),
                Map.entry(eId(FIRE_ASPECT), 0.333),
                Map.entry(eId(FLAMING_ARROWS), 0.333)
        ));
        this.addChestEntry(BuiltInLootTables.ABANDONED_MINESHAFT, 0.05, Map.ofEntries(
                Map.entry(eId(BANE_OF_ARTHROPODS), 1.0)
        ));
        this.addChestEntry(BuiltInLootTables.STRONGHOLD_LIBRARY, 0.05, Map.ofEntries(
                Map.entry(eId(BANE_OF_ARTHROPODS), 0.5),
                Map.entry(eId(INFINITY_ARROWS), 0.5)
        ));
        this.addChestEntry(BuiltInLootTables.ANCIENT_CITY, 0.05, Map.ofEntries(
                Map.entry(eId(MENDING), 0.5),
                Map.entry(eId(SWIFT_SNEAK), 0.5)
        ));
        this.addChestEntry(BuiltInLootTables.NETHER_BRIDGE, 0.05, Map.ofEntries(
                Map.entry(eId(FIRE_PROTECTION), 0.333),
                Map.entry(eId(FIRE_ASPECT), 0.333),
                Map.entry(eId(FLAMING_ARROWS), 0.333)
        ));



        this.addChestEntry(BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY, 0.05, Map.ofEntries(
                Map.entry(eId(PUNCH_ARROWS), 0.5),
                Map.entry(eId(KNOCKBACK), 0.5))
        );

        this.addChestEntry(BuiltInLootTables.UNDERWATER_RUIN_BIG, 0.06, Map.ofEntries(
                Map.entry(eId(PUNCH_ARROWS), 0.5),
                Map.entry(eId(KNOCKBACK), 0.5))
        );
    }

    private String eId(Enchantment enchantment) {
        return Objects.requireNonNull(EnchantmentHelper.getEnchantmentId(enchantment)).toString();
    }

    private void addChestEntry(ResourceLocation location, double rate, Map<String, Double> enchantments) {
        this.add(toFileName(location), new DungeonLootModifier(
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
