package net.infinitelimit.kintsugi.loot;

import com.mojang.serialization.Codec;
import net.infinitelimit.kintsugi.Kintsugi;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Kintsugi.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> CHEST_ITEM_MODIFIER =
            LOOT_MODIFIER_SERIALIZERS.register("knowledge_book_chest", ChestLootModifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> FISHING_ITEM_MODIFIER =
            LOOT_MODIFIER_SERIALIZERS.register("knowledge_book_fishing", FishingLootModifier.CODEC);


    public static void register(IEventBus bus) {
        LOOT_MODIFIER_SERIALIZERS.register(bus);
    }
}
