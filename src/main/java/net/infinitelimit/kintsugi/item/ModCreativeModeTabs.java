package net.infinitelimit.kintsugi.item;

import net.infinitelimit.kintsugi.Kintsugi;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Kintsugi.MOD_ID);

    public static final RegistryObject<CreativeModeTab> KINTSUGI_TAB = CREATIVE_MODE_TABS.register("book_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.SPELL_BOOK.get())).title(Component.translatable("creativetab.kintsugi"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ModItems.SPELL_BOOK.get());
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
