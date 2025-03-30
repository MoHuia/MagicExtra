package com.magicextra.mixin;

import com.magicextra.Config;
import com.magicextra.item.wrench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Shadow public abstract ServerPlayer getPlayer();

    @Inject(method = "handlePlayerAction", at = @At("HEAD"), cancellable = true)
    private void stop(ServerboundPlayerActionPacket pPacket, CallbackInfo cir) {
    //拦截F更换物品的行为
        ServerboundPlayerActionPacket.Action serverboundplayeractionpacket$action = pPacket.getAction();

        Item item=player.getInventory().getSelected().getItem();
        int Slot = Config.SLOT.get();

        switch (serverboundplayeractionpacket$action) {
            case SWAP_ITEM_WITH_OFFHAND:
                ResourceLocation ITEM = new ResourceLocation(Config.ITEM.get());
                Item Eitem = ForgeRegistries.ITEMS.getValue(ITEM);

                if(item == Eitem && player.getInventory().selected == Slot) {
                    Minecraft.getInstance().player.sendSystemMessage(
                            Component.literal("发包了"+player.getInventory().selected)
                    );
                    cir.cancel();
                }
            default:
                return;
        }

    }
//    @Inject(method = "handleContainerClick", at = @At("HEAD"), cancellable = true)
//    public void ContainerClick(ServerboundContainerClickPacket pPacket ,CallbackInfo cir) {
//        if(pPacket.getButtonNum()==0){
//            Minecraft.getInstance().player.sendSystemMessage(
//                    Component.literal("不能丢"+player.getInventory().selected)
//            );
//            cir.cancel();
//        }
//            if(pPacket.getCarriedItem().getItem() == wrench.WRENCH.get()){
//                Minecraft.getInstance().player.sendSystemMessage(
//                        Component.literal("看见了"+player.getInventory().selected)
//                );
//                cir.cancel();
//            }
//        }
}
