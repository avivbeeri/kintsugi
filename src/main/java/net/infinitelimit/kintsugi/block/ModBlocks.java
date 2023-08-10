package net.infinitelimit.kintsugi.block;

import net.infinitelimit.kintsugi.Kintsugi;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Kintsugi.MOD_ID);

    /*
    public static final RegistryObject<Item> REMIX_ENCHANTMENT_TABLE_BLOCK =
            BLOCKS.register("enchantment_block", () -> new RemixEnchantmentTableBlock(new Blo));
    */

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
