package net.infinitelimit.kintsugi.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.KnowledgeHelper;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.infinitelimit.kintsugi.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

import static net.infinitelimit.kintsugi.item.KnowledgeBookItem.TAG_RITUAL_ENCHANTMENT;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED;

public class ModifyBlockState implements RuleBlockEntityModifier {

   public static final Codec<ModifyBlockState> CODEC = RecordCodecBuilder.create((instance) -> {
      /*
      return instance.group(CompoundTag.CODEC.fieldOf("data").forGetter((data) -> {
         return data.tag;
      })).apply(instance, ModifyBlockState::new);
      */
      return instance.stable(new ModifyBlockState());
   });


   public ModifyBlockState() {
   }

   public CompoundTag apply(RandomSource pRandom, @Nullable CompoundTag pTag) {
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

   public RuleBlockEntityModifierType<?> getType() {
      return BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER.get(new ResourceLocation("modify_block"));
   }
}