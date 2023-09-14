package net.infinitelimit.kintsugi;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.infinitelimit.kintsugi.item.ModCreativeModeTabs;
import net.infinitelimit.kintsugi.item.ModItems;
import net.infinitelimit.kintsugi.loot.ModLootModifiers;
import net.infinitelimit.kintsugi.menus.ModMenuTypes;
import net.infinitelimit.kintsugi.screens.RemixEnchantmentScreen;
import net.infinitelimit.kintsugi.worldgen.LibraryStructureProcessor;
import net.infinitelimit.kintsugi.worldgen.ModifyBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Kintsugi.MOD_ID)
public class Kintsugi
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "kintsugi";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Kintsugi()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        // Register the item to a creative tab
        ModCreativeModeTabs.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        registerRule("modify_block", ModifyBlockState.CODEC);
        register("library_structure", LibraryStructureProcessor.CODEC);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            MenuScreens.register(ModMenuTypes.ENCHANTMENT.get(), RemixEnchantmentScreen::new);
        }
    }


    private static <P extends RuleBlockEntityModifier> RuleBlockEntityModifierType<P> registerRule(String pName, Codec<P> pCodec) {
        return Registry.register(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, pName, () -> {
            return pCodec;
        });
    }
    private static <P extends StructureProcessor> StructureProcessorType<P> register(String pName, Codec<P> pCodec) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, pName, () -> {
            return pCodec;
        });
    }
}
