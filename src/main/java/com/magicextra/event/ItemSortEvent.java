package com.magicextra.event;


import com.magicextra.Config;
import com.magicextra.MagicExtra;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static com.magicextra.Config.*;

@Mod.EventBusSubscriber(modid = MagicExtra.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemSortEvent {
        @SubscribeEvent
        public static void onPlayerPickupItem(EntityItemPickupEvent event) {
//              //debug
//            Minecraft.getInstance().player.sendSystemMessage(
//                    Component.literal("捡起物品"+ event.getItem()));
            Player player = event.getEntity();
            ItemStack pickedStack = event.getItem().getItem();
//          //读取配置文件的特定物品和槽位
            //-----------------------------------------
            Item targetItem = pickedStack.getItem();
            int index = getIndexFromItem(targetItem);
            if(index<0) return;
            int targetSlot = getTargetSlot(index);
            if(targetSlot<0) return;
            //-----------------------------------------
            event.setCanceled(true);
            Inventory inventory = player.getInventory();
            ItemStack targetSlotStack = inventory.getItem(targetSlot);
        // 4. 处理目标槽位的不同情况
        if(targetSlotStack.isEmpty()){
            targetSlotStack.grow(1);
            pickedStack.shrink(1);
            inventory.setItem(targetSlot, new ItemStack(pickedStack.getItem(),1));
        }
        if ( ItemStack.isSameItemSameTags(targetSlotStack, pickedStack)) {
            // 槽位有相同物品，合并堆叠
            int maxStackSize = targetSlotStack.getMaxStackSize();
            int transferable = Math.min(pickedStack.getCount(), maxStackSize - targetSlotStack.getCount());

            if (transferable > 0) {
                targetSlotStack.grow(transferable);
                pickedStack.shrink(transferable);
                inventory.setItem(targetSlot, targetSlotStack);
            }
        }
        else if(!targetSlotStack.isEmpty()){
            // 槽位有不同物品，尝试移动原有物品
            ItemStack originalItem = targetSlotStack.copy();
            boolean moved = moveItemToEmptySlot(inventory, originalItem);

            if (moved) {
                // 原物品移动成功，放入新物品
                inventory.setItem(targetSlot, pickedStack.copy());
                pickedStack.setCount(0);
            }

        }

  }

        // 辅助方法：将物品移动到其他空槽位
        private static boolean moveItemToEmptySlot(Inventory inventory, ItemStack stackToMove) {
            for (int i = 0; i < 36; i++) {
                if (inventory.getItem(i).isEmpty()) {
                    inventory.setItem(i, stackToMove);
                    return true;
                }
            }
            return false; // 没有空槽位
        }
    @SubscribeEvent
    public static void onInventoryClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return; // 只在服务端执行
        }
            organizeSpecificItem(player); // 执行整理逻辑
    }

    private static void organizeSpecificItem(Player player) {

        // 遍历背包，查找目标物品
        Inventory inventory = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getItem(i);
            //读取配置文件的特定物品和槽位
            //----------------------------------------
            int index =  getIndexFromItem(stack.getItem());
            if(index<0) continue;
            int targetSlot = getTargetSlot(index);
            if(targetSlot<0) continue;
            //-----------------------------------------

            if (i != targetSlot) { // 跳过目标槽位本身
                // 如果目标槽位已有物品，尝试交换
                ItemStack targetSlotStack = inventory.getItem(targetSlot);

                if (targetSlotStack.isEmpty()) {
                    // 目标槽位为空，直接移动
                    inventory.setItem(targetSlot, stack.copy());
                    inventory.setItem(i, ItemStack.EMPTY);
                }
                else if (ItemStack.isSameItemSameTags(targetSlotStack, stack)) {
                    // 目标槽位有相同物品，尝试合并
                    int maxTransfer = targetSlotStack.getMaxStackSize() - targetSlotStack.getCount();
                    if (maxTransfer > 0) {
                        int transfer = Math.min(stack.getCount(), maxTransfer);
                        targetSlotStack.grow(transfer);
                        stack.shrink(transfer);
                        if (stack.isEmpty()) {
                            inventory.setItem(i, ItemStack.EMPTY);
                             }
                        }
                        //合并完将剩下的物品丢弃
                        ItemStack stackToDrop = stack.copy(); // 复制要丢弃的物品
                        inventory.setItem(i, ItemStack.EMPTY); // 先清空槽位
                        player.drop(stackToDrop, true); // 再丢弃
                    }
                 else {
                    // 目标槽位有不同物品，优先尝试合并到背包中已有的相同物品堆叠
                    boolean moved = tryMergeWithExistingStacks(inventory, targetSlotStack.copy(), targetSlot);

                    if (moved) {
                        // 原有物品已成功移动，现在可以放入新物品
                        inventory.setItem(targetSlot, stack.copy());
                        inventory.setItem(i, ItemStack.EMPTY);
                    }
                    else {
                        inventory.setItem(i, ItemStack.EMPTY);
                        // 没有找到合适的堆叠，尝试移动到空槽位
                        if (moveItemToEmptySlot(inventory, targetSlotStack.copy())) {
                            inventory.setItem(targetSlot, stack.copy());
                        }

                    }
                }
            }
        }
    }
    //尝试移动到背包已有物品并堆叠
    private static boolean tryMergeWithExistingStacks(Inventory inventory, ItemStack stackToMove, int excludeSlot) {
        for (int i = 0; i < 36; i++) {
            if (i == excludeSlot) continue; // 跳过目标槽位

            ItemStack existingStack = inventory.getItem(i);
            if (ItemStack.isSameItemSameTags(existingStack, stackToMove)) {
                int maxTransfer = existingStack.getMaxStackSize() - existingStack.getCount();
                if (maxTransfer > 0) {
                    int transfer = Math.min(stackToMove.getCount(), maxTransfer);
                    existingStack.grow(transfer);
                    stackToMove.shrink(transfer);

                    if (stackToMove.isEmpty()) {
                        return true; // 完全合并
                    }
                }
            }
        }
        return false; // 没有完全合并
    }

}

