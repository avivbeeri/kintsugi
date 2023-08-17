package net.infinitelimit.kintsugi.loot;

import com.google.common.base.Suppliers;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.infinitelimit.kintsugi.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;



public class AddRitualFunction extends LootItemConditionalFunction {
    static LootItemFunctionType ritualFunctionType = new LootItemFunctionType(new AddRitualFunction.Serializer());

    private final NumberProvider enchantmentNo;

    protected AddRitualFunction(LootItemCondition[] pPredicates, NumberProvider enchantmentNo) {
        super(pPredicates);
        this.enchantmentNo = enchantmentNo;
    }

    public static LootItemConditionalFunction.Builder<?> addEnchantment(NumberProvider pDamageValue) {
        return simpleBuilder((predicates) -> new AddRitualFunction(predicates, pDamageValue));
    }

    @Override
    protected ItemStack run(ItemStack pStack, LootContext pContext) {
        if (enchantmentNo.getFloat(pContext) < 0.5) {
            return KnowledgeBookItem.createForEnchantment(Enchantments.FISHING_LUCK);
        } else {
            return KnowledgeBookItem.createForEnchantment(Enchantments.FISHING_SPEED);
        }
    }

    @Override
    public LootItemFunctionType getType() {
        return ritualFunctionType;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<AddRitualFunction> {
        /**
         * Serialize the {@link CopyNbtFunction} by putting its data into the JsonObject.
         */
        public void serialize(JsonObject pJson, AddRitualFunction pLootingEnchantFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pLootingEnchantFunction, pSerializationContext);
            pJson.add("count", pSerializationContext.serialize(pLootingEnchantFunction.enchantmentNo));
        }

        public AddRitualFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            int i = GsonHelper.getAsInt(pObject, "limit", 0);
            return new AddRitualFunction(pConditions, GsonHelper.getAsObject(pObject, "count", pDeserializationContext, NumberProvider.class));
        }
    }
}
