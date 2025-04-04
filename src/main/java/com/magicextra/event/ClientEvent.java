package com.magicextra.event;


import com.magicextra.Config;
import com.magicextra.MagicExtra;
import com.magicextra.net.DefusePack;
import com.magicextra.net.ModMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = MagicExtra.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)//注解，告诉编译器底下是事件注册
public class ClientEvent {//以上是监听注解，，这是静态注册事件的办法
    private static boolean wasKeyPressed = false;
    @SubscribeEvent
    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        if (KeyBindings.DEFUSE_KEY.isDown()) {
            // 当按键被按下时执行
//            Minecraft.getInstance().player.sendSystemMessage(
//                  //  Component.literal("R键被按下!")
//                    //处理逻辑
//            );
            wasKeyPressed = true;
            DefusePack pack = new DefusePack(1);
            ModMessage.sendToServer(pack);
        } else if (wasKeyPressed != KeyBindings.DEFUSE_KEY.isDown()) {
            wasKeyPressed = false;
            Minecraft.getInstance().player.sendSystemMessage(
                    Component.literal("玩家不按了"+ Config.VALUE.get())
            );
        }
    }

}