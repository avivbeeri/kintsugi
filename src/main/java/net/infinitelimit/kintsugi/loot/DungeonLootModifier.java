package net.infinitelimit.kintsugi.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.infinitelimit.kintsugi.item.PowerBookItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class DungeonLootModifier extends LootModifier {

    private final Item item;
    private final Integer rate;
    private final Map<String, Float> enchantments;

    public static final Supplier<Codec<DungeonLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(
                inst -> codecStart(inst).and(
                    inst.group(
                        ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item),
                        Codec.INT.fieldOf("rate").forGetter(m -> m.rate),
                        Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("enchantments").forGetter(m -> m.enchantments)
                    )
                ).apply(inst, DungeonLootModifier::new)
              )
          );

    protected DungeonLootModifier(LootItemCondition[] in, Item additionIn, int rate, Map<String, Float> enchantments) {
        super(in);
        this.item = additionIn;
        this.rate = rate;
        this.enchantments = enchantments;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < rate) {
            List<Tuple<Enchantment, Float>> distribution = enchantments.entrySet().stream().map(it ->
                    new Tuple<>(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(it.getKey()))), it.getValue())).toList();

            Enchantment enchantment = null;
            float roll = context.getRandom().nextFloat();
            float total = 0;
            for (Tuple<Enchantment, Float> tuple: distribution) {
                total += tuple.getB();
                if (roll <= total) {
                    enchantment = tuple.getA();
                }
            }

            if (enchantment != null) {
                ItemStack itemstack = PowerBookItem.createForEnchantment(enchantment);
                generatedLoot.add(itemstack);
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
