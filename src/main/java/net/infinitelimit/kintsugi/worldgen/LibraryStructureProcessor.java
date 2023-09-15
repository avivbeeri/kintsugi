package net.infinitelimit.kintsugi.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

public class LibraryStructureProcessor extends StructureProcessor {
    public static final Codec<LibraryStructureProcessor> CODEC = RecordCodecBuilder.create((p_277598_) -> {
        return p_277598_.stable(new LibraryStructureProcessor());
    });

    public LibraryStructureProcessor() {
    }

    public final List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor pServerLevel, BlockPos pOffset, BlockPos blockPosition, List<StructureTemplate.StructureBlockInfo> originalBlockInfoList, List<StructureTemplate.StructureBlockInfo> processedBlockInfoList, StructurePlaceSettings pSettings) {
        if (!processedBlockInfoList.isEmpty()) {
            if (originalBlockInfoList.size() != processedBlockInfoList.size()) {
                Util.logAndPauseIfInIde("Original block info list not in sync with processed list, skipping processing. Original size: " + originalBlockInfoList.size() + ", Processed size: " + processedBlockInfoList.size());
            } else {
                RandomSource randomsource = RandomSource.create(pServerLevel.getLevel().getSeed()).forkPositional().at(pOffset);
                IntArrayList intarraylist = Util.toShuffledList(IntStream.range(0, processedBlockInfoList.size()), randomsource);
                IntIterator intiterator = intarraylist.intIterator();
                while(intiterator.hasNext()) {
                    int current = intiterator.nextInt();

                    StructureTemplate.StructureBlockInfo originalBlockInfo = originalBlockInfoList.get(current);
                    StructureTemplate.StructureBlockInfo processedBlockInfoBefore = processedBlockInfoList.get(current);
                    if (!processedBlockInfoBefore.state().is(BlockTags.ENCHANTMENT_POWER_PROVIDER)) {
                        continue;
                    }
                    StructureTemplate.StructureBlockInfo processedBlockInfoAfter = processBookshelf(pServerLevel, pOffset, blockPosition, originalBlockInfo, processedBlockInfoBefore, pSettings);
                    if (processedBlockInfoAfter != null && !processedBlockInfoAfter.equals(processedBlockInfoBefore)) {
                        processedBlockInfoList.set(current, processedBlockInfoAfter);
                        break;
                    }
                }
            }
        }
        return processedBlockInfoList;
    }

    public StructureTemplate.StructureBlockInfo processBookshelf(LevelReader pLevel, BlockPos p_74300_, BlockPos pPos, StructureTemplate.StructureBlockInfo pBlockInfo, StructureTemplate.StructureBlockInfo pRelativeBlockInfo, StructurePlaceSettings pSettings) {
        RandomSource randomsource = RandomSource.create(Mth.getSeed(pRelativeBlockInfo.pos()));
        BlockState blockstate = pRelativeBlockInfo.state();
        if (blockstate.is(BlockTags.ENCHANTMENT_POWER_PROVIDER)) {
            blockstate = blockstate.setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(0), Boolean.valueOf(true));
            return new StructureTemplate.StructureBlockInfo(pRelativeBlockInfo.pos(), blockstate, applyTag(randomsource, pRelativeBlockInfo.nbt()));
        }

        return pRelativeBlockInfo;
    }

    private CompoundTag applyTag(RandomSource pRandom, @Nullable CompoundTag pTag) {
        CompoundTag itemTag = new CompoundTag();
        itemTag.putByte("Slot", (byte) 0);
        ItemStack stack = KnowledgeBookItem.createForEnchantment(Enchantments.VANISHING_CURSE);
        stack.save(itemTag);

        ListTag listTag = new ListTag();
        listTag.add(itemTag);

        CompoundTag tag = new CompoundTag();
        tag.put("Items", listTag);

        if (pTag == null) {
            return tag;
        } else {
            return pTag.merge(tag);
        }
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return BuiltInRegistries.STRUCTURE_PROCESSOR.get(new ResourceLocation(Kintsugi.MOD_ID, "library_structure"));
    }
}
