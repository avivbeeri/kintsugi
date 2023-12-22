package net.infinitelimit.kintsugi.worldgen;

import net.infinitelimit.kintsugi.Kintsugi;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStructures {
    public static final ResourceKey<Structure> LIBRARY_KEY = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(Kintsugi.MOD_ID, "library"));
    public static final TagKey<Structure> LIBRARY = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Kintsugi.MOD_ID, "library"));

    private static final DeferredRegister<StructureProcessorType<?>> REGISTER = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Kintsugi.MOD_ID);
    //public static final RegistryObject<StructureProcessorType<LibraryStructureProcessor>> LIBRARY_PROCESSOR = REGISTER.register("library_structure", () -> () -> LibraryStructureProcessor.CODEC);

    public static void register(IEventBus eventBus) {
       REGISTER.register(eventBus);
    }
}
