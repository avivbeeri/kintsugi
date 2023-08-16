package net.infinitelimit.kintsugi;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;
import java.util.Set;

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
}
