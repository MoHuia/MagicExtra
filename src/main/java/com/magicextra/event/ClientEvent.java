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
    static int tick;
    static int enable;
//右键检测的代码
//    @SubscribeEvent//表示,以下是执行代码
//    public static void PlayerHandWrench(PlayerInteractEvent.EntityInteract event) {
//        //获取生物
//        // 由于长按期间经常出现误判，因此应该更换方法，将参数写入玩家内，
//        Entity entity = event.getTarget();
//        Level level = event.getLevel();
//        if (level.isClientSide()) return;
//        if (event.getItemStack().getItem() == wrench.WRENCH.get() && entity instanceof Pig && entity.distanceTo(event.getEntity()) < 2.0) {//当且仅当扳手
//            tick++;
//            enable = 1;
//            System.out.println("level:" + level + tick);
//            //获取世界
//            level.addFreshEntity(new PrimedTnt(level, entity.getX() + 0.5, entity.getY() + 10, entity.getZ() + 0.5, (LivingEntity) entity));
//            //event.setCanceled(true);
//        } else {
//            System.out.println("level:" + level);
//            //tick=0;
//        }
//    }

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
    @SubscribeEvent
    public static void onPlayerPickupItem(PlayerEvent.ItemPickupEvent event) {
        //目前还没生效
        // 1. 获取配置中的物品和槽位
        ResourceLocation targetItemId = new ResourceLocation(Config.ITEM.get());
        Item targetItem = ForgeRegistries.ITEMS.getValue(targetItemId);
        int targetSlot = Config.SLOT.get();

        Player player = event.getEntity();
        ItemStack pickedStack = event.getStack();

        // 2. 检查拾取的物品是否匹配配置
        if (pickedStack.getItem() != targetItem) {
            return; // 不处理非目标物品
        }

        // 3. 验证槽位有效性（0~35）
        if (targetSlot < 0 || targetSlot >= 36) {
            player.displayClientMessage(
                    Component.literal("配置错误：槽位编号必须在0~35之间！"), true
            );
            return;
        }

        Inventory inventory = player.getInventory();
        ItemStack targetSlotStack = inventory.getItem(targetSlot);

        // 4. 处理目标槽位的不同情况
        if (targetSlotStack.isEmpty()) {
            // 槽位为空，直接放入
            inventory.setItem(targetSlot, pickedStack.copy());
            pickedStack.setCount(0); // 清空拾取的物品
        } else if (ItemStack.isSameItemSameTags(targetSlotStack, pickedStack)) {
            // 槽位有相同物品，合并堆叠
            int maxStackSize = targetSlotStack.getMaxStackSize();
            int transferable = Math.min(pickedStack.getCount(), maxStackSize - targetSlotStack.getCount());

            if (transferable > 0) {
                targetSlotStack.grow(transferable);
                pickedStack.shrink(transferable);
                inventory.setItem(targetSlot, targetSlotStack);
            }

            // 如果仍有剩余物品，留在原地（原拾取逻辑）
            if (pickedStack.isEmpty()) {
                event.setCanceled(true); // 完全转移后取消原事件
            }
        } else {
            // 槽位有不同物品，尝试移动原有物品
            ItemStack originalItem = targetSlotStack.copy();
            boolean moved = moveItemToEmptySlot(inventory, originalItem);

            if (moved) {
                // 原物品移动成功，放入新物品
                inventory.setItem(targetSlot, pickedStack.copy());
                pickedStack.setCount(0);
                event.setCanceled(true);
            } else {
                // 无法移动原物品，保留原有逻辑


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
}