package net.infinitelimit.kintsugi;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;
import java.util.Set;

import static net.minecraft.world.item.enchantment.Enchantments.*;

public class KnowledgeHelper {

    public static final Map<Integer, Set<Enchantment>> ENCHANTMENT_CATEGORIES = Map.of(
            1, Set.of(KNOCKBACK, SHARPNESS),
            2, Set.of(),
            3, Set.of(),
            4, Set.of(),
            5, Set.of(MENDING)
    );

    public static final Set<Enchantment> DEFAULT_ENCHANTMENTS = Set.of(
            Enchantments.UNBREAKING,
            Enchantments.BLOCK_EFFICIENCY,
            Enchantments.SILK_TOUCH,
            Enchantments.POWER_ARROWS,
            Enchantments.SHARPNESS,
            Enchantments.ALL_DAMAGE_PROTECTION);
}
