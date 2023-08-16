package net.infinitelimit.kintsugi.datagen;

import net.infinitelimit.kintsugi.Kintsugi;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

import static net.infinitelimit.kintsugi.Kintsugi.MOD_ID;

@Mod.EventBusSubscriber(modid = Kintsugi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    // On the MOD event bus
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new ModLootTableProvider(output, MOD_ID));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, MOD_ID, existingFileHelper));

        BlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(), new BlockTagGenerator(output, lookupProvider,  MOD_ID, existingFileHelper));
        generator.addProvider(event.includeServer(), new ItemTagGenerator(output, lookupProvider, blockTagGenerator.contentsGetter(), MOD_ID, existingFileHelper));
    }
}
