package com.magicextra.mixin;


import com.magicextra.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static com.magicextra.Config.*;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Shadow
    private ItemStack carried;
    @Shadow
    private ItemStack remoteCarried;
    @Shadow
    public final NonNullList<Slot> slots = NonNullList.create();
    @Shadow @Nullable
    private MenuType<?> menuType;
    //  @Shadow
    //private final MenuType<?> menuType = null ;
    //  @Inject(method = "setCarried", at = @At("HEAD"), cancellable = true)
    //setCarried(ItemStack pStack)
//    private void stopDrop(ItemStack pStack, CallbackInfo cir){
//        if(pStack.getItem() == wrench.WRENCH.get()){
//            //调试
//                Minecraft.getInstance().player.sendSystemMessage
//                        (Component.literal("你丢你妈呢")
//                        );
//            cir.cancel();
//            carried = remoteCarried;
//            }
//        }
//    @Inject(method = "setCarried", at = @At("HEAD"), cancellable = true)
//    public void Carried(ItemStack pStack,CallbackInfo cir) {
//        if (pStack.getItem() == wrench.WRENCH.get()) {
//            Minecraft.getInstance().player.sendSystemMessage
//                    (Component.literal("不允许更换carried")
//                    );
//
//            cir.cancel();
//        }
//    }
    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void preventWrenchMovement(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {

        // 1. 获取当前交互的槽位和物品
        ItemStack sourceItem = ItemStack.EMPTY;
        ItemStack carriedItem = this.carried;

        // 2. 根据操作类型判断涉及扳手的场景
        if (slotId >= 0 && slotId < this.slots.size()) {
            // 点击容器内的槽位
            sourceItem = slots.get(slotId).getItem();
        } else if (slotId == AbstractContainerMenu.SLOT_CLICKED_OUTSIDE) {
            // 点击容器外（丢弃）
            sourceItem = this.carried;
        }
        int temp=0;
        if(this.menuType == BuiltInRegistries.MENU.get(new ResourceLocation("inventory"))){
            temp=1;
        }
        //读取配置文件的特定物品和槽位
        //-----------------------------------------
        int Slot = slotId - this.slots.size()+9+temp;
        int index =  getIndexFromSlot(Slot);
        if(index<0) return;
        Item item= getTargetItem(index);//获取物品
        if(item == null )  return;
        //---------------------------------------------
        //判断本物品是否具有上面的物品标签
        boolean isItemOperation =
                (sourceItem.getItem() == item ||
                        carriedItem.getItem() == item);

        if (isItemOperation) {
            // 4. 客户端提示
            if (player.level().isClientSide) {
                Minecraft.getInstance().player.sendSystemMessage(
                        Component.literal("你没有权限控制这个物品栏+Slotod:"+slotId+item)
                );
            }
            // 5. 取消所有涉及扳手的操作
            ci.cancel();
        }
    }

//    @Inject(method = "synchronizeCarriedToRemote", at = @At("HEAD"), cancellable = true)
//    private void CarriedToRemote(CallbackInfo cir) {
//        if(carried.getItem() == wrench.WRENCH.get()){
//            Minecraft.getInstance().player.sendSystemMessage
//                    (Component.literal("拒绝你的选择")
//                    );
//            cir.cancel();
//        }
//    }
//    @Inject(method = "synchronizeSlotToRemote", at = @At("HEAD"), cancellable = true)
//    private void CarriedToRemote(int pSlotIndex, ItemStack pStack, Supplier<ItemStack> pSupplier,CallbackInfo cir) {
//        if(pStack.getItem() == wrench.WRENCH.get()){
//            Minecraft.getInstance().player.sendSystemMessage
//                    (Component.literal("拒绝你的选择")
//                    );
//            cir.cancel();
//        }
//    }
//    @Inject(method = "canDragTo", at = @At("HEAD"), cancellable = true)
//    private void stopQuickDrop(Slot pSlot,CallbackInfoReturnable<Boolean> cir){
//       // if(carried.getItem() == wrench.WRENCH.get()){
//            //调试
//            Minecraft.getInstance().player.sendSystemMessage
//                    (Component.literal("你丢你妈呢")
//                    );
//            cir.cancel();
//            cir.setReturnValue(false);
//            carried = remoteCarried;
//        }
//


}