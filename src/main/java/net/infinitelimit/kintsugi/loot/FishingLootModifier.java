package net.infinitelimit.kintsugi.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.infinitelimit.kintsugi.item.KnowledgeBookItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class FishingLootModifier extends LootModifier {

    private final Item item;
    private final Map<String, Double> enchantments;

    public static final Supplier<Codec<FishingLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(
                inst -> codecStart(inst).and(
                    inst.group(
                        ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item),
                        Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("enchantments").forGetter(m -> m.enchantments)
                    )
                ).apply(inst, FishingLootModifier::new)
              )
          );

    public FishingLootModifier(LootItemCondition[] in, Item additionIn, Map<String, Double> enchantments) {
        super(in);
        this.item = additionIn;
        this.enchantments = enchantments;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {

        LootTable table = context.getLevel().getServer().getLootData().getLootTable(context.getQueriedLootTableId());


        if (context.getRandom().nextDouble() < 0.5) {
            List<Tuple<Enchantment, Double>> distribution = enchantments.entrySet().stream().map(it ->
                    new Tuple<>(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(it.getKey()))), it.getValue())).toList();

            Enchantment enchantment = null;
            double roll = context.getRandom().nextDouble();
            double total = 0.0;
            for (Tuple<Enchantment, Double> tuple: distribution) {
                total += tuple.getB();
                if (roll <= total) {
                    enchantment = tuple.getA();
                }
            }

            if (enchantment != null) {
                ItemStack itemstack = KnowledgeBookItem.createForEnchantment(enchantment);
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
