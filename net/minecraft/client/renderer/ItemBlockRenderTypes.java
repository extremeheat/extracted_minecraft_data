package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ItemBlockRenderTypes {
   private static final Map TYPE_BY_BLOCK = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      RenderType var1 = RenderType.cutoutMipped();
      var0.put(Blocks.GRASS_BLOCK, var1);
      var0.put(Blocks.IRON_BARS, var1);
      var0.put(Blocks.GLASS_PANE, var1);
      var0.put(Blocks.TRIPWIRE_HOOK, var1);
      var0.put(Blocks.HOPPER, var1);
      var0.put(Blocks.JUNGLE_LEAVES, var1);
      var0.put(Blocks.OAK_LEAVES, var1);
      var0.put(Blocks.SPRUCE_LEAVES, var1);
      var0.put(Blocks.ACACIA_LEAVES, var1);
      var0.put(Blocks.BIRCH_LEAVES, var1);
      var0.put(Blocks.DARK_OAK_LEAVES, var1);
      RenderType var2 = RenderType.cutout();
      var0.put(Blocks.OAK_SAPLING, var2);
      var0.put(Blocks.SPRUCE_SAPLING, var2);
      var0.put(Blocks.BIRCH_SAPLING, var2);
      var0.put(Blocks.JUNGLE_SAPLING, var2);
      var0.put(Blocks.ACACIA_SAPLING, var2);
      var0.put(Blocks.DARK_OAK_SAPLING, var2);
      var0.put(Blocks.GLASS, var2);
      var0.put(Blocks.WHITE_BED, var2);
      var0.put(Blocks.ORANGE_BED, var2);
      var0.put(Blocks.MAGENTA_BED, var2);
      var0.put(Blocks.LIGHT_BLUE_BED, var2);
      var0.put(Blocks.YELLOW_BED, var2);
      var0.put(Blocks.LIME_BED, var2);
      var0.put(Blocks.PINK_BED, var2);
      var0.put(Blocks.GRAY_BED, var2);
      var0.put(Blocks.LIGHT_GRAY_BED, var2);
      var0.put(Blocks.CYAN_BED, var2);
      var0.put(Blocks.PURPLE_BED, var2);
      var0.put(Blocks.BLUE_BED, var2);
      var0.put(Blocks.BROWN_BED, var2);
      var0.put(Blocks.GREEN_BED, var2);
      var0.put(Blocks.RED_BED, var2);
      var0.put(Blocks.BLACK_BED, var2);
      var0.put(Blocks.POWERED_RAIL, var2);
      var0.put(Blocks.DETECTOR_RAIL, var2);
      var0.put(Blocks.COBWEB, var2);
      var0.put(Blocks.GRASS, var2);
      var0.put(Blocks.FERN, var2);
      var0.put(Blocks.DEAD_BUSH, var2);
      var0.put(Blocks.SEAGRASS, var2);
      var0.put(Blocks.TALL_SEAGRASS, var2);
      var0.put(Blocks.DANDELION, var2);
      var0.put(Blocks.POPPY, var2);
      var0.put(Blocks.BLUE_ORCHID, var2);
      var0.put(Blocks.ALLIUM, var2);
      var0.put(Blocks.AZURE_BLUET, var2);
      var0.put(Blocks.RED_TULIP, var2);
      var0.put(Blocks.ORANGE_TULIP, var2);
      var0.put(Blocks.WHITE_TULIP, var2);
      var0.put(Blocks.PINK_TULIP, var2);
      var0.put(Blocks.OXEYE_DAISY, var2);
      var0.put(Blocks.CORNFLOWER, var2);
      var0.put(Blocks.WITHER_ROSE, var2);
      var0.put(Blocks.LILY_OF_THE_VALLEY, var2);
      var0.put(Blocks.BROWN_MUSHROOM, var2);
      var0.put(Blocks.RED_MUSHROOM, var2);
      var0.put(Blocks.TORCH, var2);
      var0.put(Blocks.WALL_TORCH, var2);
      var0.put(Blocks.FIRE, var2);
      var0.put(Blocks.SPAWNER, var2);
      var0.put(Blocks.REDSTONE_WIRE, var2);
      var0.put(Blocks.WHEAT, var2);
      var0.put(Blocks.OAK_DOOR, var2);
      var0.put(Blocks.LADDER, var2);
      var0.put(Blocks.RAIL, var2);
      var0.put(Blocks.IRON_DOOR, var2);
      var0.put(Blocks.REDSTONE_TORCH, var2);
      var0.put(Blocks.REDSTONE_WALL_TORCH, var2);
      var0.put(Blocks.CACTUS, var2);
      var0.put(Blocks.SUGAR_CANE, var2);
      var0.put(Blocks.REPEATER, var2);
      var0.put(Blocks.OAK_TRAPDOOR, var2);
      var0.put(Blocks.SPRUCE_TRAPDOOR, var2);
      var0.put(Blocks.BIRCH_TRAPDOOR, var2);
      var0.put(Blocks.JUNGLE_TRAPDOOR, var2);
      var0.put(Blocks.ACACIA_TRAPDOOR, var2);
      var0.put(Blocks.DARK_OAK_TRAPDOOR, var2);
      var0.put(Blocks.ATTACHED_PUMPKIN_STEM, var2);
      var0.put(Blocks.ATTACHED_MELON_STEM, var2);
      var0.put(Blocks.PUMPKIN_STEM, var2);
      var0.put(Blocks.MELON_STEM, var2);
      var0.put(Blocks.VINE, var2);
      var0.put(Blocks.LILY_PAD, var2);
      var0.put(Blocks.NETHER_WART, var2);
      var0.put(Blocks.BREWING_STAND, var2);
      var0.put(Blocks.COCOA, var2);
      var0.put(Blocks.BEACON, var2);
      var0.put(Blocks.FLOWER_POT, var2);
      var0.put(Blocks.POTTED_OAK_SAPLING, var2);
      var0.put(Blocks.POTTED_SPRUCE_SAPLING, var2);
      var0.put(Blocks.POTTED_BIRCH_SAPLING, var2);
      var0.put(Blocks.POTTED_JUNGLE_SAPLING, var2);
      var0.put(Blocks.POTTED_ACACIA_SAPLING, var2);
      var0.put(Blocks.POTTED_DARK_OAK_SAPLING, var2);
      var0.put(Blocks.POTTED_FERN, var2);
      var0.put(Blocks.POTTED_DANDELION, var2);
      var0.put(Blocks.POTTED_POPPY, var2);
      var0.put(Blocks.POTTED_BLUE_ORCHID, var2);
      var0.put(Blocks.POTTED_ALLIUM, var2);
      var0.put(Blocks.POTTED_AZURE_BLUET, var2);
      var0.put(Blocks.POTTED_RED_TULIP, var2);
      var0.put(Blocks.POTTED_ORANGE_TULIP, var2);
      var0.put(Blocks.POTTED_WHITE_TULIP, var2);
      var0.put(Blocks.POTTED_PINK_TULIP, var2);
      var0.put(Blocks.POTTED_OXEYE_DAISY, var2);
      var0.put(Blocks.POTTED_CORNFLOWER, var2);
      var0.put(Blocks.POTTED_LILY_OF_THE_VALLEY, var2);
      var0.put(Blocks.POTTED_WITHER_ROSE, var2);
      var0.put(Blocks.POTTED_RED_MUSHROOM, var2);
      var0.put(Blocks.POTTED_BROWN_MUSHROOM, var2);
      var0.put(Blocks.POTTED_DEAD_BUSH, var2);
      var0.put(Blocks.POTTED_CACTUS, var2);
      var0.put(Blocks.CARROTS, var2);
      var0.put(Blocks.POTATOES, var2);
      var0.put(Blocks.COMPARATOR, var2);
      var0.put(Blocks.ACTIVATOR_RAIL, var2);
      var0.put(Blocks.IRON_TRAPDOOR, var2);
      var0.put(Blocks.SUNFLOWER, var2);
      var0.put(Blocks.LILAC, var2);
      var0.put(Blocks.ROSE_BUSH, var2);
      var0.put(Blocks.PEONY, var2);
      var0.put(Blocks.TALL_GRASS, var2);
      var0.put(Blocks.LARGE_FERN, var2);
      var0.put(Blocks.SPRUCE_DOOR, var2);
      var0.put(Blocks.BIRCH_DOOR, var2);
      var0.put(Blocks.JUNGLE_DOOR, var2);
      var0.put(Blocks.ACACIA_DOOR, var2);
      var0.put(Blocks.DARK_OAK_DOOR, var2);
      var0.put(Blocks.END_ROD, var2);
      var0.put(Blocks.CHORUS_PLANT, var2);
      var0.put(Blocks.CHORUS_FLOWER, var2);
      var0.put(Blocks.BEETROOTS, var2);
      var0.put(Blocks.KELP, var2);
      var0.put(Blocks.KELP_PLANT, var2);
      var0.put(Blocks.TURTLE_EGG, var2);
      var0.put(Blocks.DEAD_TUBE_CORAL, var2);
      var0.put(Blocks.DEAD_BRAIN_CORAL, var2);
      var0.put(Blocks.DEAD_BUBBLE_CORAL, var2);
      var0.put(Blocks.DEAD_FIRE_CORAL, var2);
      var0.put(Blocks.DEAD_HORN_CORAL, var2);
      var0.put(Blocks.TUBE_CORAL, var2);
      var0.put(Blocks.BRAIN_CORAL, var2);
      var0.put(Blocks.BUBBLE_CORAL, var2);
      var0.put(Blocks.FIRE_CORAL, var2);
      var0.put(Blocks.HORN_CORAL, var2);
      var0.put(Blocks.DEAD_TUBE_CORAL_FAN, var2);
      var0.put(Blocks.DEAD_BRAIN_CORAL_FAN, var2);
      var0.put(Blocks.DEAD_BUBBLE_CORAL_FAN, var2);
      var0.put(Blocks.DEAD_FIRE_CORAL_FAN, var2);
      var0.put(Blocks.DEAD_HORN_CORAL_FAN, var2);
      var0.put(Blocks.TUBE_CORAL_FAN, var2);
      var0.put(Blocks.BRAIN_CORAL_FAN, var2);
      var0.put(Blocks.BUBBLE_CORAL_FAN, var2);
      var0.put(Blocks.FIRE_CORAL_FAN, var2);
      var0.put(Blocks.HORN_CORAL_FAN, var2);
      var0.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, var2);
      var0.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, var2);
      var0.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, var2);
      var0.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, var2);
      var0.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, var2);
      var0.put(Blocks.TUBE_CORAL_WALL_FAN, var2);
      var0.put(Blocks.BRAIN_CORAL_WALL_FAN, var2);
      var0.put(Blocks.BUBBLE_CORAL_WALL_FAN, var2);
      var0.put(Blocks.FIRE_CORAL_WALL_FAN, var2);
      var0.put(Blocks.HORN_CORAL_WALL_FAN, var2);
      var0.put(Blocks.SEA_PICKLE, var2);
      var0.put(Blocks.CONDUIT, var2);
      var0.put(Blocks.BAMBOO_SAPLING, var2);
      var0.put(Blocks.BAMBOO, var2);
      var0.put(Blocks.POTTED_BAMBOO, var2);
      var0.put(Blocks.SCAFFOLDING, var2);
      var0.put(Blocks.STONECUTTER, var2);
      var0.put(Blocks.LANTERN, var2);
      var0.put(Blocks.CAMPFIRE, var2);
      var0.put(Blocks.SWEET_BERRY_BUSH, var2);
      RenderType var3 = RenderType.translucent();
      var0.put(Blocks.ICE, var3);
      var0.put(Blocks.NETHER_PORTAL, var3);
      var0.put(Blocks.WHITE_STAINED_GLASS, var3);
      var0.put(Blocks.ORANGE_STAINED_GLASS, var3);
      var0.put(Blocks.MAGENTA_STAINED_GLASS, var3);
      var0.put(Blocks.LIGHT_BLUE_STAINED_GLASS, var3);
      var0.put(Blocks.YELLOW_STAINED_GLASS, var3);
      var0.put(Blocks.LIME_STAINED_GLASS, var3);
      var0.put(Blocks.PINK_STAINED_GLASS, var3);
      var0.put(Blocks.GRAY_STAINED_GLASS, var3);
      var0.put(Blocks.LIGHT_GRAY_STAINED_GLASS, var3);
      var0.put(Blocks.CYAN_STAINED_GLASS, var3);
      var0.put(Blocks.PURPLE_STAINED_GLASS, var3);
      var0.put(Blocks.BLUE_STAINED_GLASS, var3);
      var0.put(Blocks.BROWN_STAINED_GLASS, var3);
      var0.put(Blocks.GREEN_STAINED_GLASS, var3);
      var0.put(Blocks.RED_STAINED_GLASS, var3);
      var0.put(Blocks.BLACK_STAINED_GLASS, var3);
      var0.put(Blocks.TRIPWIRE, var3);
      var0.put(Blocks.WHITE_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.ORANGE_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.MAGENTA_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.YELLOW_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.LIME_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.PINK_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.GRAY_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.CYAN_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.PURPLE_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.BLUE_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.BROWN_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.GREEN_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.RED_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.BLACK_STAINED_GLASS_PANE, var3);
      var0.put(Blocks.SLIME_BLOCK, var3);
      var0.put(Blocks.HONEY_BLOCK, var3);
      var0.put(Blocks.FROSTED_ICE, var3);
      var0.put(Blocks.BUBBLE_COLUMN, var3);
   });
   private static final Map TYPE_BY_FLUID = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      RenderType var1 = RenderType.translucent();
      var0.put(Fluids.FLOWING_WATER, var1);
      var0.put(Fluids.WATER, var1);
   });
   private static boolean renderCutout;

   public static RenderType getChunkRenderType(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 instanceof LeavesBlock) {
         return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
      } else {
         RenderType var2 = (RenderType)TYPE_BY_BLOCK.get(var1);
         return var2 != null ? var2 : RenderType.solid();
      }
   }

   public static RenderType getRenderType(BlockState var0) {
      RenderType var1 = getChunkRenderType(var0);
      return var1 == RenderType.translucent() ? Sheets.translucentBlockSheet() : Sheets.cutoutBlockSheet();
   }

   public static RenderType getRenderType(ItemStack var0) {
      Item var1 = var0.getItem();
      if (var1 instanceof BlockItem) {
         Block var2 = ((BlockItem)var1).getBlock();
         return getRenderType(var2.defaultBlockState());
      } else {
         return Sheets.translucentBlockSheet();
      }
   }

   public static RenderType getRenderLayer(FluidState var0) {
      RenderType var1 = (RenderType)TYPE_BY_FLUID.get(var0.getType());
      return var1 != null ? var1 : RenderType.solid();
   }

   public static void setFancy(boolean var0) {
      renderCutout = var0;
   }
}
