package com.magicextra;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// 文件名: ModConfig.java
@Mod.EventBusSubscriber(modid = "magicextra", bus = Mod.EventBusSubscriber.Bus.MOD)
@Config(modid = "magicextra")
public class ModConfig {
    public static final Common COMMON = new Common();

    public static class Common {
        @Config.Comment("示例数值配置 (范围: 0-100)")
        @Config.RangeInt(min = 0, max = 100)
        public int exampleNumber = 42;

        @Config.Comment("是否启用特性")
        public boolean enableFeature = true;
    }

    // 配置重载事件
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals("magicextra")) {
            // 在此处执行配置更新后的逻辑
            System.out.println("配置已热重载！新值: " + COMMON.exampleNumber);
        }
    }
}