package net.infinitelimit.kintsugi.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DungeonLootModifier extends LootModifier {

    private final Item item;
    public static final Supplier<Codec<DungeonLootModifier>> CODEC = Suppliers.memoize(() ->
                    RecordCodecBuilder.create(inst -> codecStart(inst).and(ForgeRegistries.ITEMS.getCodec()
                                    .fieldOf("item").forGetter(m -> m.item)).apply(inst, DungeonLootModifier::new)));

    protected DungeonLootModifier(LootItemCondition[] in, Item additionIn) {
        super(in);
        this.item = additionIn;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
       // if (context.getRandom().nextFloat() > 0.05) {
            generatedLoot.add(new ItemStack(item, 1));
        //}

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
