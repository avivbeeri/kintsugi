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
import net.minecraftforge.eventbus.api.Event;
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
        } else if (bottom.is(ModItems.KNOWLEDGE_BOOK.get()) && top.is(ModItems.KNOWLEDGE_BOOK.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onGrindstoneTake(GrindstoneEvent.OnTakeItem event) {
        ItemStack bottom = event.getBottomItem();
        ItemStack top = event.getTopItem();
        if ((bottom.isEmpty() && !top.isEmpty()) || (!bottom.isEmpty() && top.isEmpty())) {
            boolean topFlag = !top.isEmpty();
            ItemStack current = (topFlag ? top: bottom).copy();
            if (!current.is(ModItems.KNOWLEDGE_BOOK.get())) {
                return;
            }
            current.shrink(1);
            if (topFlag) {
                event.setNewTopItem(current);
            } else {
                event.setNewBottomItem(current);
            }
        }
    }

    private static int calculateEnchantmentExperience(ItemStack current) {
        Enchantment enchantment = KnowledgeHelper.getEnchantment(current);
        int category = KnowledgeHelper.getEnchantmentCategory(enchantment);

        return category + enchantment.getMaxLevel();
    }

}
