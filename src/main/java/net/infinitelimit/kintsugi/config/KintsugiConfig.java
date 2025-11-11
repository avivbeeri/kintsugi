package net.infinitelimit.kintsugi.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class KintsugiConfig {

    public static final ForgeConfigSpec GENERAL_SPEC;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    public static ForgeConfigSpec.BooleanValue dropLoot;

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Configure the Kintsugi Mod")
                .push("kintsugi");
        dropLoot = builder.define("drop_as_loot", true);
        builder.pop();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, KintsugiConfig.GENERAL_SPEC, "kintsugi-config.toml");
    }
}
