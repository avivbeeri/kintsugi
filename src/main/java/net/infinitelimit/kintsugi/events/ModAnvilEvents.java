package net.infinitelimit.kintsugi.events;

import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.KnowledgeHelper;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Kintsugi.MOD_ID)
public class ModAnvilEvents {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack target = event.getLeft();
        ItemStack sacrifice = event.getRight();
        ItemStack result = target.copy();

        boolean createResult = false;
        int xpCost = 0;
        int materialCost = 0;
        int damage = target.getDamageValue();
        boolean addUse = false;

        if (target.isEmpty()) {
            event.setOutput(ItemStack.EMPTY);
            return;
        }

        if (event.getName() != null && !Util.isBlank(event.getName())) {
            if (!event.getName().equals(target.getHoverName().getString())) {
                result.setHoverName(Component.literal(event.getName()));
                xpCost += 1;
                createResult = true;
            }
        } else if (target.hasCustomHoverName()) {
            result.resetHoverName();
        }

        Map<Enchantment, Integer> targetEnchantments = EnchantmentHelper.getEnchantments(target);

        if (!sacrifice.isEmpty()) {
            Map<Enchantment, Integer> sacrificeEnchantments = EnchantmentHelper.getEnchantments(sacrifice);
            if (target.isDamageableItem() && target.isDamaged() && target.getItem().isValidRepairItem(target, sacrifice)) {
                // repair
                createResult = true;
                addUse = true;
                int expense = Math.min(4, sacrifice.getCount());
                damage -= (result.getMaxDamage() / 4) * expense;
                materialCost += expense;
                xpCost += expense;
            } else if (target.is(sacrifice.getItem())) {
                // do item merging
                createResult = true;
                addUse = true;
                if (target.isDamaged()) {
                    xpCost += 1;
                }
                int targetDamage = target.getMaxDamage() - target.getDamageValue();
                int sacrificeDamage = sacrifice.getMaxDamage() - sacrifice.getDamageValue();
                int bonusDamage = Math.round(target.getMaxDamage() * 0.12f);
                damage = Math.max(0, target.getMaxDamage() - (targetDamage + sacrificeDamage + bonusDamage));
            }

            if ((target.is(sacrifice.getItem())) || (sacrifice.is(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantments(sacrifice).isEmpty() && target.isBookEnchantable(sacrifice))) {
                // apply enchantment to item
                createResult = true;
                addUse = true;
                // add cost for previous enchantments
                xpCost += targetEnchantments.size();
                for (Enchantment enchantment: sacrificeEnchantments.keySet()) {
                    if (!enchantment.canEnchant(target)) {
                        continue;
                    }
                    if (!targetEnchantments.containsKey(enchantment) && !EnchantmentHelper.isEnchantmentCompatible(targetEnchantments.keySet(), enchantment)) {
                        continue;
                    }
                    int sacrificeLevel = sacrificeEnchantments.getOrDefault(enchantment, 0);
                    int existingLevel = targetEnchantments.getOrDefault(enchantment, 0);
                    if (existingLevel == 0) {
                        // new enchantments cost extra
                        xpCost += sacrificeLevel;
                    } else if (sacrificeLevel > existingLevel) {
                        xpCost += sacrificeLevel - existingLevel;
                    }

                    int category;
                    for (category = 0; category < 5; category++) {
                        if (KnowledgeHelper.ENCHANTMENT_CATEGORIES.get(category).contains(enchantment)) {
                            break;
                        }
                    }
                    xpCost += category;

                    int level = Math.max(sacrificeLevel, existingLevel);
                    targetEnchantments.put(enchantment, level);
                }
                EnchantmentHelper.setEnchantments(targetEnchantments, result);
            }
        }

        if (createResult) {
            if (addUse) {
                int repairCost = Math.max(target.getBaseRepairCost(), sacrifice.getBaseRepairCost());
                xpCost += repairCost;
                result.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(repairCost));
            }

            result.setDamageValue(damage);

            event.setOutput(result);
            // how many levels to extract from player
            event.setCost(xpCost);
            // how many repair items in stack to consume
            event.setMaterialCost(materialCost);
        } else {
            event.setOutput(ItemStack.EMPTY);
        }
    }

}
