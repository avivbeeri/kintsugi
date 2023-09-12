package net.infinitelimit.kintsugi.events;

import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.KnowledgeHelper;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.infinitelimit.kintsugi.item.ModItems;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Kintsugi.MOD_ID)
public class ModGrindstoneEvents {

    @SubscribeEvent
    public static void onGrindstoneUpdate(GrindstoneEvent.OnPlaceItem event) {
        ItemStack bottom = event.getBottomItem();
        ItemStack top = event.getTopItem();

        if ((bottom.isEmpty() && !top.isEmpty()) || (!bottom.isEmpty() && top.isEmpty())) {
            ItemStack current = top.isEmpty() ? bottom : top;
            if (!current.is(ModItems.KNOWLEDGE_BOOK.get())) {
                return;
            }
            event.setOutput(new ItemStack(Items.BOOK));
            event.setXp(calculateEnchantmentExperience(current));
        }
    }

    private static int calculateEnchantmentExperience(ItemStack current) {
        int xp = 0;
        for (Map.Entry<Enchantment, Integer> entry: current.getAllEnchantments().entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            int category;
            for (category = 0; category < 5; category++) {
                if (KnowledgeHelper.ENCHANTMENT_CATEGORIES.get(category).contains(enchantment)) {
                    break;
                }
            }

            xp += category + level;
        }

        return xp;
    }

}
