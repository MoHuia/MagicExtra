package com.magicextra;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.ObjectInputStream;
import java.util.*;

public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.IntValue VALUE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM;
    public static ForgeConfigSpec.ConfigValue<List<? extends Integer>> SLOT;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> TAG;

    static {
        //创建配置类
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        //
        COMMON_BUILDER.comment("General settings").push("general");
        VALUE = COMMON_BUILDER
                .comment("锁定的槽位总数")
                .defineInRange("value", 1, 0, 36);
        COMMON_BUILDER.pop();
        //-----------------------------------------------------------------
        COMMON_BUILDER.comment("locked_slots").push("load");
        ITEM = COMMON_BUILDER
                .comment("要锁定的物品ID列表")  // 配置项的注释（描述）
                .defineList(
                        "item_ids",  // 配置项的键名（在 TOML 文件里显示为 `item_ids = [...]`）
                        Collections.singletonList("magicextra:wrench"),  // 默认值（一个单元素的列表）
                        obj -> obj instanceof String  // 验证函数，确保列表里的每个元素都是 String 类型
                );
        SLOT = COMMON_BUILDER
                .comment("锁定的槽位列表")
                .defineList("slot_numbers",
                        Collections.singletonList(0),
                        obj -> obj instanceof Integer && (int) obj >= 0 && (int) obj < 36);

        TAG = COMMON_BUILDER
                .comment("要锁定的物品TAG列表（已经弃用）")  // 配置项的注释（描述）
                .defineList(
                        "item_tags",  // 配置项的键名（在 TOML 文件里显示为 `item_ids = [...]`）
                        Collections.singletonList("magicextra:items/master_weapon"),  // 默认值（一个单元素的列表）
                        obj -> obj instanceof String  // 验证函数，确保列表里的每个元素都是 String 类型
                );
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
    //从列表安全获取物品
    public static Optional<String> getItemAt(int index) {
        try {
            List<String> items = (List<String>) Config.ITEM.get();
            if (index >= 0 && index < items.size()) {
                return Optional.of(items.get(index));
            }
        } catch (Exception e) {
            System.err.println("读取物品配置出错: " + e.getMessage());
        }
        return Optional.empty();
    }
    //从列表安全获取获取槽位
    public static Optional<Integer> getSlotAt(int index) {
        try {
            List<Integer> slots = (List<Integer>) Config.SLOT.get();
            if (index >= 0 && index < slots.size()) {
                return Optional.of(slots.get(index));
            }
        } catch (Exception e) {
            System.err.println("读取槽位配置出错: " + e.getMessage());
        }
        return Optional.empty();
    }
    //从列表安全获取标签
    public static Optional<String> getTagAt(int index) {
        try {
            List<String> tag = (List<String>) Config.TAG.get();
            if (index >= 0 && index < tag.size()) {
                return Optional.of(tag.get(index));
            }
        } catch (Exception e) {
            System.err.println("读取标签配置出错: " + e.getMessage());
        }
        return Optional.empty();
    }
    //用索引获取物品
    public static Item getTargetItem(int index) {
        Optional<String> itemOpt = getItemAt(index);
        if (itemOpt.isPresent()) {
            String itemId = itemOpt.get(); // 安全获取值
            System.out.println("物品ID: " + itemId);
            //将string转换为对应物品
            ResourceLocation location = new ResourceLocation(itemId);
            ItemStack stack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(location)));
            return stack.getItem();
        } else {
            System.out.println("配置缺失或索引越界");
            return null;
        }
    }
    //用索引获取槽位
    public static int getTargetSlot(int index) {
        Optional<Integer> slotOpt = getSlotAt(index);
        return slotOpt.orElse(-1);
    }
    //用索引获得标签
    public static TagKey<Item> getTagKey(int index) {
        Optional<String> tagOpt = getTagAt(index);
        if (tagOpt.isPresent()) {
            String tagId = tagOpt.get();
            System.out.println("标签ID: " + tagId);
            //将string转换为对应标签tagkey
            return TagKey.create(
                    BuiltInRegistries.ITEM.key(),
                    new ResourceLocation(tagId)
            );
        } else {
            System.out.println("配置缺失或索引越界");
            return null;
        }
    }

    //用槽位获取索引
    public static int getIndexFromSlot(int slot) {
        try {
            List<Integer> slots = (List<Integer>) SLOT.get();
            for (int i = 0; i < slots.size(); i++) {
                if (slots.get(i) == slot) {
                    return i; // 返回第一个匹配的索引
                }
            }
        } catch (Exception e) {
            System.err.println("读取槽位配置出错: " + e.getMessage());
        }
        return -1; // 未找到或出错
    }
    //用物品id获取索引
    public static int getIndexFromItem(String itemId) {
        try {
            // 验证输入格式
            ResourceLocation location = new ResourceLocation(itemId);

            List<String> items = (List<String>) ITEM.get();
            for (int i = 0; i < items.size(); i++) {
                try {
                    // 比较规范化后的物品ID（忽略大小写）
                    if (new ResourceLocation(items.get(i)).equals(location)) {
                        return i;
                    }
                } catch (Exception e) {
                    System.err.println("跳过无效的物品ID配置: " + items.get(i));
                }
            }
        } catch (Exception e) {
            System.err.println("物品ID格式错误: " + itemId + " | " + e.getMessage());
        }
        return -1;
    }
    //用物品实例获取索引
    public static int getIndexFromItem(Item item) {
        return getIndexFromItem(ForgeRegistries.ITEMS.getKey(item).toString());
    }
    //用物品实例获取标签列表
//    public static int getIndexFromTag(Item item) {
////        Optional<String> tagOpt = getItemAt(index);
////        if (itemOpt.isPresent()) {
////            String itemId = itemOpt.get(); // 安全获取值
////            System.out.println("物品ID: " + itemId);
////            //将string转换为对应物品
////            ResourceLocation location = new ResourceLocation(itemId);
////            ItemStack stack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(location)));
////            return stack.getItem();
////        } else {
////            System.out.println("配置缺失或索引越界");
////            return null;
////        }
////        // 1. 获取物品的Holder（新API方式）
////        Holder<Item> itemHolder = BuiltInRegistries.ITEM.getHolder(BuiltInRegistries.ITEM.getId(item))
////                .orElse(null);
////        item.getShareTag()
////        if (itemHolder == null) return -1;
////
////        // 2. 获取配置的标签列表
////        List<String> tagNames = (List<String>) TAG.get();
////
////        // 3. 遍历检测
////        for (int i = 0; i < tagNames.size(); i++) {
////            try {
////                TagKey<Item> tag = TagKey.create(
////                        BuiltInRegistries.ITEM.key(),
////                        new ResourceLocation(tagNames.get(i))
////                );
////
////                if (itemHolder.is(tag)) {
////                    return i;
////                }
////            } catch (Exception e) {
////                System.err.println("Invalid tag format: " + tagNames.get(i));
////            }
////        }
////        return -1;
//    }
}


//#General settings
//[general]
//        #Test config value
//    #Range: > 0
//value = 10
//
//            // 获取第 1 个槽位（如果存在）
//            Optional<Integer> slotOpt = getSlotAt(0);
//            slotOpt.ifPresent(slot -> {
//                System.out.println("槽位: " + slot);
//            });
////---------------------------------------------------------