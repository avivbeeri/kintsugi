package net.infinitelimit.kintsugi.datagen;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

import java.util.Optional;

public class ModSpriteSourceProvider extends SpriteSourceProvider {
    public ModSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper, String modid) {
        super(output, fileHelper, modid);
    }

    @Override
    protected void addSources() {
        atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource(new SingleFile(new ResourceLocation("kintsugi:entity/enchanting_table_book"), Optional.empty()));
    }
}
