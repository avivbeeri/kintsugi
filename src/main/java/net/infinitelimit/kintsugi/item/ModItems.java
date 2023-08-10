package net.infinitelimit.kintsugi.item;

import net.infinitelimit.kintsugi.Kintsugi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Kintsugi.MOD_ID);

    public static final RegistryObject<Item> POWER_BOOK =
            ITEMS.register("power_book", () -> new PowerBookItem(new Item.Properties().rarity(Rarity.RARE)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
