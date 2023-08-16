package net.infinitelimit.kintsugi.datagen;

import net.infinitelimit.kintsugi.item.ModItems;
import net.infinitelimit.kintsugi.loot.DungeonLootModifier;
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
