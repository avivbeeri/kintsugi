package net.infinitelimit.kintsugi.item;

import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.KnowledgeHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;

import static net.infinitelimit.kintsugi.KnowledgeHelper.KNOWLEDGE_ENCHANTMENTS;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Kintsugi.MOD_ID);

    public static final RegistryObject<CreativeModeTab> KINTSUGI_TAB = CREATIVE_MODE_TABS.register("book_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.KNOWLEDGE_BOOK.get())).title(Component.translatable("creativetab.kintsugi"))
            .displayItems((pParameters, pOutput) -> {
                KNOWLEDGE_ENCHANTMENTS
                        .stream()
                        .sorted(Comparator.comparing(a -> Component.translatable(a.getDescriptionId()).getString()))
                        .forEach(enchantment ->
                                pOutput.accept(KnowledgeBookItem.createForEnchantment(enchantment)));
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
