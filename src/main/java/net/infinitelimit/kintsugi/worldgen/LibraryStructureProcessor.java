package net.infinitelimit.kintsugi.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.infinitelimit.kintsugi.Kintsugi;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.List;
import java.util.stream.IntStream;

public class LibraryStructureProcessor extends StructureProcessor {
    public static final Codec<LibraryStructureProcessor> CODEC = RecordCodecBuilder.create((p_277598_) -> {
        return p_277598_.group(StructureProcessorType.SINGLE_CODEC.fieldOf("delegate").forGetter((p_277456_) -> {
            return p_277456_.delegate;
        }), IntProvider.POSITIVE_CODEC.fieldOf("limit").forGetter((p_277680_) -> {
            return p_277680_.limit;
        })).apply(p_277598_, LibraryStructureProcessor::new);
    });
    private final StructureProcessor delegate;
    private final IntProvider limit;

    public LibraryStructureProcessor(StructureProcessor delegate, IntProvider limit) {
        this.delegate = delegate;
        this.limit = limit;
    }

    public final List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor pServerLevel, BlockPos pOffset, BlockPos blockPosition, List<StructureTemplate.StructureBlockInfo> originalBlockInfoList, List<StructureTemplate.StructureBlockInfo> processedBlockInfoList, StructurePlaceSettings pSettings) {
        if (this.limit.getMaxValue() != 0 && !processedBlockInfoList.isEmpty()) {
            if (originalBlockInfoList.size() != processedBlockInfoList.size()) {
                Util.logAndPauseIfInIde("Original block info list not in sync with processed list, skipping processing. Original size: " + originalBlockInfoList.size() + ", Processed size: " + processedBlockInfoList.size());
                return processedBlockInfoList;
            } else {
                RandomSource randomsource = RandomSource.create(pServerLevel.getLevel().getSeed()).forkPositional().at(pOffset);
                int limit = Math.min(this.limit.sample(randomsource), processedBlockInfoList.size());
                if (limit < 1) {
                    return processedBlockInfoList;
                } else {
                    IntArrayList intarraylist = Util.toShuffledList(IntStream.range(0, processedBlockInfoList.size()), randomsource);
                    IntIterator intiterator = intarraylist.intIterator();
                    int changeCount = 0;

                    while(intiterator.hasNext() && changeCount < limit) {
                        int current = intiterator.nextInt();
                        StructureTemplate.StructureBlockInfo originalBlockInfo = originalBlockInfoList.get(current);
                        StructureTemplate.StructureBlockInfo processedBlockInfoBefore = processedBlockInfoList.get(current);
                        StructureTemplate.StructureBlockInfo processedBlockInfoAfter = this.delegate.processBlock(pServerLevel, pOffset, blockPosition, originalBlockInfo, processedBlockInfoBefore, pSettings);
                        processedBlockInfoAfter.state().getProperties().addAll(processedBlockInfoBefore.state().getProperties());
                        if (processedBlockInfoAfter != null && !processedBlockInfoBefore.equals(processedBlockInfoAfter)) {
                            ++changeCount;
                            processedBlockInfoList.set(current, processedBlockInfoAfter);
                        }
                    }

                    return processedBlockInfoList;
                }
            }
        } else {
            return processedBlockInfoList;
        }
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return BuiltInRegistries.STRUCTURE_PROCESSOR.get(new ResourceLocation(Kintsugi.MOD_ID, "library_structure"));
    }
}
