package com.magicextra;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

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
                .comment("锁定的槽位总数")
                .defineInRange("value", 1, 0, 36);
        COMMON_BUILDER.pop();
      //  int value = Config.COMMON_CONFIG.getInt("value");
       // for(int i=0; i<value;i++){
            COMMON_BUILDER.comment("locked_slots").push("load");
            ITEM = COMMON_BUILDER
                    .comment("要锁定的物品id")
                    .define("id","magicextra:wrench");
            //
            SLOT = COMMON_BUILDER
                    .comment("锁定的槽位")
                    .defineInRange("slot", 1, 0, 36);
            COMMON_BUILDER.pop();
            COMMON_CONFIG = COMMON_BUILDER.build();
       // }
    }
}
//#General settings
//[general]
//        #Test config value
//    #Range: > 0
//value = 10