package net.minecraft.data.loot.packs;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public interface LootData {
   Map<DyeColor, ItemLike> WOOL_ITEM_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), var0 -> {
      var0.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
      var0.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
      var0.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
      var0.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
      var0.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
      var0.put(DyeColor.LIME, Blocks.LIME_WOOL);
      var0.put(DyeColor.PINK, Blocks.PINK_WOOL);
      var0.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
      var0.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
      var0.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
      var0.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
      var0.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
      var0.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
      var0.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
      var0.put(DyeColor.RED, Blocks.RED_WOOL);
      var0.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
   });
}
