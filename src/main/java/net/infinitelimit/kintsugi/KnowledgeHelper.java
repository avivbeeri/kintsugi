package net.infinitelimit.kintsugi;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.world.item.enchantment.Enchantments.*;

public class KnowledgeHelper {

    public static final Map<Integer, Set<Enchantment>> ENCHANTMENT_CATEGORIES = Map.of(
            0, Set.of(SHARPNESS, BLAST_PROTECTION, PROJECTILE_PROTECTION,
                    FIRE_PROTECTION, BLOCK_EFFICIENCY, SMITE, BANE_OF_ARTHROPODS,
                    FROST_WALKER, FALL_PROTECTION, AQUA_AFFINITY, POWER_ARROWS, IMPALING,
                    MULTISHOT, FISHING_LUCK, FISHING_SPEED, BINDING_CURSE, VANISHING_CURSE),
            1, Set.of(ALL_DAMAGE_PROTECTION, UNBREAKING, FIRE_ASPECT, KNOCKBACK, SWEEPING_EDGE, FLAMING_ARROWS,
                    PIERCING, QUICK_CHARGE, THORNS, PUNCH_ARROWS),
            2, Set.of(BLOCK_FORTUNE, DEPTH_STRIDER, SOUL_SPEED, SWIFT_SNEAK, RESPIRATION, CHANNELING,
                    LOYALTY, MOB_LOOTING),
            3, Set.of(SILK_TOUCH),
            4, Set.of(MENDING, INFINITY_ARROWS, RIPTIDE)
    );

    public static final Set<Enchantment> DEFAULT_ENCHANTMENTS = Set.of(
            Enchantments.UNBREAKING,
            Enchantments.BLOCK_EFFICIENCY,
            Enchantments.SILK_TOUCH,
            Enchantments.POWER_ARROWS,
            Enchantments.SHARPNESS,
            Enchantments.ALL_DAMAGE_PROTECTION);

    public static final Set<Enchantment> KNOWLEDGE_ENCHANTMENTS = getKnowledgeBookEnchantments();

    public static final Set<Enchantment> ANY_VILLAGER_TRADES = Set.of(
            BINDING_CURSE, VANISHING_CURSE, SWEEPING_EDGE, PUNCH_ARROWS
    );

    public static final Map<VillagerType, Enchantment> UNIQUE_VILLAGER_ENCHANTS =
            Map.ofEntries(
                    Map.entry(VillagerType.DESERT, BLAST_PROTECTION),
                    Map.entry(VillagerType.JUNGLE, THORNS),
                    Map.entry(VillagerType.PLAINS, FROST_WALKER),
                    Map.entry(VillagerType.SNOW, FROST_WALKER),
                    Map.entry(VillagerType.SAVANNA, FALL_PROTECTION),
                    Map.entry(VillagerType.SWAMP, BLOCK_FORTUNE),
                    Map.entry(VillagerType.TAIGA, FROST_WALKER)
            );

    public static Enchantment getRandomEnchantment(RandomSource pRandom) {
        List<Enchantment> validEnchantments = List.copyOf(KNOWLEDGE_ENCHANTMENTS);
        int i = pRandom.nextInt(validEnchantments.size());
        return validEnchantments.get(i);
    }

    public static Enchantment getEnchantmentByVillagerType(VillagerType type, RandomSource pRandom) {
        return UNIQUE_VILLAGER_ENCHANTS.get(type);
    }

    public static Enchantment getRandomVillagerEnchantment(VillagerType type, RandomSource pRandom) {
        List<Enchantment> villagerEnchants = new ArrayList<>(ANY_VILLAGER_TRADES);
        villagerEnchants.add(getEnchantmentByVillagerType(type, pRandom));
        return villagerEnchants.get(pRandom.nextInt(villagerEnchants.size()));
    }

    private static Set<Enchantment> getKnowledgeBookEnchantments() {
        return ForgeRegistries.ENCHANTMENTS.getValues()
                .stream()
                .filter(enchantment -> !KnowledgeHelper.DEFAULT_ENCHANTMENTS.contains(enchantment))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}

