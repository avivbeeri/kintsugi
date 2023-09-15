package net.infinitelimit.kintsugi.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.KnowledgeHelper;
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
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
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

                intiterator = intarraylist.intIterator();
                while(intiterator.hasNext()) {
                    int current = intiterator.nextInt();

                    StructureTemplate.StructureBlockInfo originalBlockInfo = originalBlockInfoList.get(current);
                    StructureTemplate.StructureBlockInfo processedBlockInfoBefore = processedBlockInfoList.get(current);
                    if (!processedBlockInfoBefore.state().is(Blocks.ENCHANTING_TABLE)) {
                        continue;
                    }
                    StructureTemplate.StructureBlockInfo processedBlockInfoAfter = processEnchantingTable(pServerLevel, pOffset, blockPosition, originalBlockInfo, processedBlockInfoBefore, pSettings);
                    if (processedBlockInfoAfter != null && !processedBlockInfoAfter.equals(processedBlockInfoBefore)) {
                        processedBlockInfoList.set(current, processedBlockInfoAfter);
                        break;
                    }
                }
            }
        }
        return processedBlockInfoList;
    }

    private StructureTemplate.StructureBlockInfo processEnchantingTable(ServerLevelAccessor pServerLevel, BlockPos pOffset, BlockPos blockPosition, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo pRelativeBlockInfo, StructurePlaceSettings pSettings) {
        RandomSource randomsource = RandomSource.create(Mth.getSeed(pRelativeBlockInfo.pos()));
        if (randomsource.nextDouble() < 0.2) {
            return new StructureTemplate.StructureBlockInfo(pRelativeBlockInfo.pos(), Blocks.OBSIDIAN.defaultBlockState(), pRelativeBlockInfo.nbt());
        }

        return pRelativeBlockInfo;
    }

    public StructureTemplate.StructureBlockInfo processBookshelf(LevelReader pLevel, BlockPos p_74300_, BlockPos pPos, StructureTemplate.StructureBlockInfo pBlockInfo, StructureTemplate.StructureBlockInfo pRelativeBlockInfo, StructurePlaceSettings pSettings) {
        RandomSource randomsource = RandomSource.create(Mth.getSeed(pRelativeBlockInfo.pos()));
        BlockState blockstate = pRelativeBlockInfo.state();
        if (blockstate.is(BlockTags.ENCHANTMENT_POWER_PROVIDER)) {
            int slot = randomsource.nextInt(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size());
            blockstate = blockstate.setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(slot), Boolean.valueOf(true));
            Enchantment enchantment;
            VillagerType villagerType = VillagerType.byBiome(pLevel.getBiome(pRelativeBlockInfo.pos()));
            if (randomsource.nextDouble() < 0.90) {
                enchantment = KnowledgeHelper.getRandomVillagerEnchantment(villagerType, randomsource);
            } else {
                enchantment = KnowledgeHelper.getEnchantmentByVillagerType(villagerType, randomsource);
            }
            return new StructureTemplate.StructureBlockInfo(pRelativeBlockInfo.pos(), blockstate, applyTag(randomsource, pRelativeBlockInfo.nbt(), slot, enchantment));
        }

        return pRelativeBlockInfo;
    }

    private CompoundTag applyTag(RandomSource pRandom, @Nullable CompoundTag pTag, int slot, Enchantment enchantment) {
        CompoundTag itemTag = new CompoundTag();
        itemTag.putByte("Slot", (byte) slot);

        ItemStack stack = KnowledgeBookItem.createForEnchantment(enchantment);
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
