package com.magicextra;

import com.magicextra.item.ModItemRegister;
import com.magicextra.net.ModMessage;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MagicExtra.MODID)
public class MagicExtra {

    public static final String MODID = "magicextra";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MagicExtra() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModMessage.register();
        InitAll(bus);//注册物品
        config();//注册配置
    }
    public void InitAll(IEventBus iEventBus){//正常注册进世界总线
        ModItemRegister.init(iEventBus);
    }
    private void config() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    }
}
