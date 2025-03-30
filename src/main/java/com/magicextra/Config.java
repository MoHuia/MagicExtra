package com.magicextra;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.IntValue VALUE;
    public static ForgeConfigSpec.ConfigValue<String> ITEM;
    public static ForgeConfigSpec.IntValue SLOT;
    static {
        //创建配置类
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        //
        COMMON_BUILDER.comment("General settings").push("general");
        VALUE = COMMON_BUILDER
                .comment("Test config value")
                .defineInRange("value", 10, 0, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();
        //
        COMMON_BUILDER.comment("item settings").push("load");
        ITEM = COMMON_BUILDER
                .comment("要锁定的物品id")
                .define("id","magicextra:wrench");
        //
        SLOT = COMMON_BUILDER
                .comment("锁定的槽位")
                .defineInRange("slot", 0, 0, 36);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
//#General settings
//[general]
//        #Test config value
//    #Range: > 0
//value = 10