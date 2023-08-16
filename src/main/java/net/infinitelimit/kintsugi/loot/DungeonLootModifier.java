package net.infinitelimit.kintsugi.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.infinitelimit.kintsugi.item.PowerBookItem;
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
import java.util.function.Supplier;

public class DungeonLootModifier extends LootModifier {

    private final Item item;
    private final Integer rate;
    private final Map<String, Float> enchantments;
    /*
    public static final Supplier<Codec<DungeonLootModifier>> CODEC = Suppliers.memoize(() ->
                    RecordCodecBuilder.create(inst -> codecStart(inst).and(ForgeRegistries.ITEMS.getCodec()
                                    .fieldOf("item").forGetter(m -> m.item)).apply(inst, DungeonLootModifier::new)));
*/
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
       // if (context.getRandom().nextFloat() > 0.05) {
        List<Enchantment> list = ForgeRegistries.ENCHANTMENTS.getValues().stream().filter(Enchantment::isTradeable).toList();
        Enchantment enchantment = list.get(context.getRandom().nextInt(list.size()));
        ItemStack itemstack = PowerBookItem.createForEnchantment(enchantment);
        generatedLoot.add(itemstack);
        //}

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
