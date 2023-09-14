package net.infinitelimit.kintsugi.worldgen;

import com.mojang.serialization.Codec;
import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.menus.RemixEnchantmentMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModStructures {
    public static final ResourceKey<Structure> LIBRARY_KEY = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(Kintsugi.MOD_ID, "library"));
    public static final TagKey<Structure> LIBRARY = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Kintsugi.MOD_ID, "library"));

    /*

   // public static RuleBlockEntityModifierType<ModifyBlockState> MODIFY_BLOCK_STATE;
    private static final DeferredRegister<RuleBlockEntityModifierType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER.key(), Kintsugi.MOD_ID);

    public static final RegistryObject<RuleBlockEntityModifierType<ModifyBlockState>> MODIFY_BLOCK_STATE =
            REGISTER.register(
            "modify_block",
                    () -> ModifyBlockState.CODEC
    );



    public static void register(IEventBus eventBus) {
        //MODIFY_BLOCK_STATE = register("modify_block", ModifyBlockState.CODEC);
        REGISTER.register(eventBus);
    }

    private static <P extends RuleBlockEntityModifier> RuleBlockEntityModifierType<P> register(String pName, Codec<P> pCodec) {
        return Registry.register(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, pName, () -> {
            return pCodec;
        });
    }



);

 */

}
