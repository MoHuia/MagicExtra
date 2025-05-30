package com.magicextra.registry;


import com.magicextra.MagicExtra;
import com.magicextra.client.DefuseOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
;
import static com.magicextra.client.DefuseOverlay.WATER_LEVEL_HUD;

@Mod.EventBusSubscriber(modid = MagicExtra.MODID,bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)//注解，告诉编译器底下是事件注册
public class OverlayRegistry {
    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("water_level_hud", WATER_LEVEL_HUD);
        //event.registerAboveAll(VanillaGuiOverlay.HOTBAR.id(), "defuse_bar", DefuseOverlay.instance);
        event.registerAboveAll("defuse_bar", DefuseOverlay.instance);

    }

}
