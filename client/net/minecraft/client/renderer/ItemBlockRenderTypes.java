package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class ItemBlockRenderTypes {
   private static final Map<Block, RenderType> TYPE_BY_BLOCK = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      RenderType var1 = RenderType.tripwire();
      var0.put(Blocks.TRIPWIRE, var1);
      RenderType var2 = RenderType.cutoutMipped();
      var0.put(Blocks.GRASS_BLOCK, var2);
      var0.put(Blocks.IRON_BARS, var2);
      var0.put(Blocks.GLASS_PANE, var2);
      var0.put(Blocks.TRIPWIRE_HOOK, var2);
      var0.put(Blocks.HOPPER, var2);
      var0.put(Blocks.CHAIN, var2);
      var0.put(Blocks.JUNGLE_LEAVES, var2);
      var0.put(Blocks.OAK_LEAVES, var2);
      var0.put(Blocks.SPRUCE_LEAVES, var2);
      var0.put(Blocks.ACACIA_LEAVES, var2);
      var0.put(Blocks.CHERRY_LEAVES, var2);
      var0.put(Blocks.BIRCH_LEAVES, var2);
      var0.put(Blocks.DARK_OAK_LEAVES, var2);
      var0.put(Blocks.AZALEA_LEAVES, var2);
      var0.put(Blocks.FLOWERING_AZALEA_LEAVES, var2);
      var0.put(Blocks.MANGROVE_ROOTS, var2);
      var0.put(Blocks.MANGROVE_LEAVES, var2);
      RenderType var3 = RenderType.cutout();
      var0.put(Blocks.OAK_SAPLING, var3);
      var0.put(Blocks.SPRUCE_SAPLING, var3);
      var0.put(Blocks.BIRCH_SAPLING, var3);
      var0.put(Blocks.JUNGLE_SAPLING, var3);
      var0.put(Blocks.ACACIA_SAPLING, var3);
      var0.put(Blocks.CHERRY_SAPLING, var3);
      var0.put(Blocks.DARK_OAK_SAPLING, var3);
      var0.put(Blocks.GLASS, var3);
      var0.put(Blocks.WHITE_BED, var3);
      var0.put(Blocks.ORANGE_BED, var3);
      var0.put(Blocks.MAGENTA_BED, var3);
      var0.put(Blocks.LIGHT_BLUE_BED, var3);
      var0.put(Blocks.YELLOW_BED, var3);
      var0.put(Blocks.LIME_BED, var3);
      var0.put(Blocks.PINK_BED, var3);
      var0.put(Blocks.GRAY_BED, var3);
      var0.put(Blocks.LIGHT_GRAY_BED, var3);
      var0.put(Blocks.CYAN_BED, var3);
      var0.put(Blocks.PURPLE_BED, var3);
      var0.put(Blocks.BLUE_BED, var3);
      var0.put(Blocks.BROWN_BED, var3);
      var0.put(Blocks.GREEN_BED, var3);
      var0.put(Blocks.RED_BED, var3);
      var0.put(Blocks.BLACK_BED, var3);
      var0.put(Blocks.POWERED_RAIL, var3);
      var0.put(Blocks.DETECTOR_RAIL, var3);
      var0.put(Blocks.COBWEB, var3);
      var0.put(Blocks.SHORT_GRASS, var3);
      var0.put(Blocks.FERN, var3);
      var0.put(Blocks.DEAD_BUSH, var3);
      var0.put(Blocks.SEAGRASS, var3);
      var0.put(Blocks.TALL_SEAGRASS, var3);
      var0.put(Blocks.DANDELION, var3);
      var0.put(Blocks.POPPY, var3);
      var0.put(Blocks.BLUE_ORCHID, var3);
      var0.put(Blocks.ALLIUM, var3);
      var0.put(Blocks.AZURE_BLUET, var3);
      var0.put(Blocks.RED_TULIP, var3);
      var0.put(Blocks.ORANGE_TULIP, var3);
      var0.put(Blocks.WHITE_TULIP, var3);
      var0.put(Blocks.PINK_TULIP, var3);
      var0.put(Blocks.OXEYE_DAISY, var3);
      var0.put(Blocks.CORNFLOWER, var3);
      var0.put(Blocks.WITHER_ROSE, var3);
      var0.put(Blocks.LILY_OF_THE_VALLEY, var3);
      var0.put(Blocks.BROWN_MUSHROOM, var3);
      var0.put(Blocks.RED_MUSHROOM, var3);
      var0.put(Blocks.TORCH, var3);
      var0.put(Blocks.WALL_TORCH, var3);
      var0.put(Blocks.SOUL_TORCH, var3);
      var0.put(Blocks.SOUL_WALL_TORCH, var3);
      var0.put(Blocks.FIRE, var3);
      var0.put(Blocks.SOUL_FIRE, var3);
      var0.put(Blocks.SPAWNER, var3);
      var0.put(Blocks.TRIAL_SPAWNER, var3);
      var0.put(Blocks.VAULT, var3);
      var0.put(Blocks.REDSTONE_WIRE, var3);
      var0.put(Blocks.WHEAT, var3);
      var0.put(Blocks.OAK_DOOR, var3);
      var0.put(Blocks.LADDER, var3);
      var0.put(Blocks.RAIL, var3);
      var0.put(Blocks.IRON_DOOR, var3);
      var0.put(Blocks.REDSTONE_TORCH, var3);
      var0.put(Blocks.REDSTONE_WALL_TORCH, var3);
      var0.put(Blocks.CACTUS, var3);
      var0.put(Blocks.SUGAR_CANE, var3);
      var0.put(Blocks.REPEATER, var3);
      var0.put(Blocks.OAK_TRAPDOOR, var3);
      var0.put(Blocks.SPRUCE_TRAPDOOR, var3);
      var0.put(Blocks.BIRCH_TRAPDOOR, var3);
      var0.put(Blocks.JUNGLE_TRAPDOOR, var3);
      var0.put(Blocks.ACACIA_TRAPDOOR, var3);
      var0.put(Blocks.CHERRY_TRAPDOOR, var3);
      var0.put(Blocks.DARK_OAK_TRAPDOOR, var3);
      var0.put(Blocks.CRIMSON_TRAPDOOR, var3);
      var0.put(Blocks.WARPED_TRAPDOOR, var3);
      var0.put(Blocks.MANGROVE_TRAPDOOR, var3);
      var0.put(Blocks.BAMBOO_TRAPDOOR, var3);
      var0.put(Blocks.COPPER_TRAPDOOR, var3);
      var0.put(Blocks.EXPOSED_COPPER_TRAPDOOR, var3);
      var0.put(Blocks.WEATHERED_COPPER_TRAPDOOR, var3);
      var0.put(Blocks.OXIDIZED_COPPER_TRAPDOOR, var3);
      var0.put(Blocks.WAXED_COPPER_TRAPDOOR, var3);
      var0.put(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, var3);
      var0.put(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, var3);
      var0.put(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, var3);
      var0.put(Blocks.ATTACHED_PUMPKIN_STEM, var3);
      var0.put(Blocks.ATTACHED_MELON_STEM, var3);
      var0.put(Blocks.PUMPKIN_STEM, var3);
      var0.put(Blocks.MELON_STEM, var3);
      var0.put(Blocks.VINE, var3);
      var0.put(Blocks.GLOW_LICHEN, var3);
      var0.put(Blocks.LILY_PAD, var3);
      var0.put(Blocks.NETHER_WART, var3);
      var0.put(Blocks.BREWING_STAND, var3);
      var0.put(Blocks.COCOA, var3);
      var0.put(Blocks.BEACON, var3);
      var0.put(Blocks.FLOWER_POT, var3);
      var0.put(Blocks.POTTED_OAK_SAPLING, var3);
      var0.put(Blocks.POTTED_SPRUCE_SAPLING, var3);
      var0.put(Blocks.POTTED_BIRCH_SAPLING, var3);
      var0.put(Blocks.POTTED_JUNGLE_SAPLING, var3);
      var0.put(Blocks.POTTED_ACACIA_SAPLING, var3);
      var0.put(Blocks.POTTED_CHERRY_SAPLING, var3);
      var0.put(Blocks.POTTED_DARK_OAK_SAPLING, var3);
      var0.put(Blocks.POTTED_MANGROVE_PROPAGULE, var3);
      var0.put(Blocks.POTTED_FERN, var3);
      var0.put(Blocks.POTTED_DANDELION, var3);
      var0.put(Blocks.POTTED_POPPY, var3);
      var0.put(Blocks.POTTED_BLUE_ORCHID, var3);
      var0.put(Blocks.POTTED_ALLIUM, var3);
      var0.put(Blocks.POTTED_AZURE_BLUET, var3);
      var0.put(Blocks.POTTED_RED_TULIP, var3);
      var0.put(Blocks.POTTED_ORANGE_TULIP, var3);
      var0.put(Blocks.POTTED_WHITE_TULIP, var3);
      var0.put(Blocks.POTTED_PINK_TULIP, var3);
      var0.put(Blocks.POTTED_OXEYE_DAISY, var3);
      var0.put(Blocks.POTTED_CORNFLOWER, var3);
      var0.put(Blocks.POTTED_LILY_OF_THE_VALLEY, var3);
      var0.put(Blocks.POTTED_WITHER_ROSE, var3);
      var0.put(Blocks.POTTED_RED_MUSHROOM, var3);
      var0.put(Blocks.POTTED_BROWN_MUSHROOM, var3);
      var0.put(Blocks.POTTED_DEAD_BUSH, var3);
      var0.put(Blocks.POTTED_CACTUS, var3);
      var0.put(Blocks.POTTED_AZALEA, var3);
      var0.put(Blocks.POTTED_FLOWERING_AZALEA, var3);
      var0.put(Blocks.POTTED_TORCHFLOWER, var3);
      var0.put(Blocks.CARROTS, var3);
      var0.put(Blocks.POTATOES, var3);
      var0.put(Blocks.COMPARATOR, var3);
      var0.put(Blocks.ACTIVATOR_RAIL, var3);
      var0.put(Blocks.IRON_TRAPDOOR, var3);
      var0.put(Blocks.SUNFLOWER, var3);
      var0.put(Blocks.LILAC, var3);
      var0.put(Blocks.ROSE_BUSH, var3);
      var0.put(Blocks.PEONY, var3);
      var0.put(Blocks.TALL_GRASS, var3);
      var0.put(Blocks.LARGE_FERN, var3);
      var0.put(Blocks.SPRUCE_DOOR, var3);
      var0.put(Blocks.BIRCH_DOOR, var3);
      var0.put(Blocks.JUNGLE_DOOR, var3);
      var0.put(Blocks.ACACIA_DOOR, var3);
      var0.put(Blocks.CHERRY_DOOR, var3);
      var0.put(Blocks.DARK_OAK_DOOR, var3);
      var0.put(Blocks.MANGROVE_DOOR, var3);
      var0.put(Blocks.BAMBOO_DOOR, var3);
      var0.put(Blocks.COPPER_DOOR, var3);
      var0.put(Blocks.EXPOSED_COPPER_DOOR, var3);
      var0.put(Blocks.WEATHERED_COPPER_DOOR, var3);
      var0.put(Blocks.OXIDIZED_COPPER_DOOR, var3);
      var0.put(Blocks.WAXED_COPPER_DOOR, var3);
      var0.put(Blocks.WAXED_EXPOSED_COPPER_DOOR, var3);
      var0.put(Blocks.WAXED_WEATHERED_COPPER_DOOR, var3);
      var0.put(Blocks.WAXED_OXIDIZED_COPPER_DOOR, var3);
      var0.put(Blocks.END_ROD, var3);
      var0.put(Blocks.CHORUS_PLANT, var3);
      var0.put(Blocks.CHORUS_FLOWER, var3);
      var0.put(Blocks.TORCHFLOWER, var3);
      var0.put(Blocks.TORCHFLOWER_CROP, var3);
      var0.put(Blocks.PITCHER_PLANT, var3);
      var0.put(Blocks.PITCHER_CROP, var3);
      var0.put(Blocks.BEETROOTS, var3);
      var0.put(Blocks.KELP, var3);
      var0.put(Blocks.KELP_PLANT, var3);
      var0.put(Blocks.TURTLE_EGG, var3);
      var0.put(Blocks.DEAD_TUBE_CORAL, var3);
      var0.put(Blocks.DEAD_BRAIN_CORAL, var3);
      var0.put(Blocks.DEAD_BUBBLE_CORAL, var3);
      var0.put(Blocks.DEAD_FIRE_CORAL, var3);
      var0.put(Blocks.DEAD_HORN_CORAL, var3);
      var0.put(Blocks.TUBE_CORAL, var3);
      var0.put(Blocks.BRAIN_CORAL, var3);
      var0.put(Blocks.BUBBLE_CORAL, var3);
      var0.put(Blocks.FIRE_CORAL, var3);
      var0.put(Blocks.HORN_CORAL, var3);
      var0.put(Blocks.DEAD_TUBE_CORAL_FAN, var3);
      var0.put(Blocks.DEAD_BRAIN_CORAL_FAN, var3);
      var0.put(Blocks.DEAD_BUBBLE_CORAL_FAN, var3);
      var0.put(Blocks.DEAD_FIRE_CORAL_FAN, var3);
      var0.put(Blocks.DEAD_HORN_CORAL_FAN, var3);
      var0.put(Blocks.TUBE_CORAL_FAN, var3);
      var0.put(Blocks.BRAIN_CORAL_FAN, var3);
      var0.put(Blocks.BUBBLE_CORAL_FAN, var3);
      var0.put(Blocks.FIRE_CORAL_FAN, var3);
      var0.put(Blocks.HORN_CORAL_FAN, var3);
      var0.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, var3);
      var0.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, var3);
      var0.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, var3);
      var0.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, var3);
      var0.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, var3);
      var0.put(Blocks.TUBE_CORAL_WALL_FAN, var3);
      var0.put(Blocks.BRAIN_CORAL_WALL_FAN, var3);
      var0.put(Blocks.BUBBLE_CORAL_WALL_FAN, var3);
      var0.put(Blocks.FIRE_CORAL_WALL_FAN, var3);
      var0.put(Blocks.HORN_CORAL_WALL_FAN, var3);
      var0.put(Blocks.SEA_PICKLE, var3);
      var0.put(Blocks.CONDUIT, var3);
      var0.put(Blocks.BAMBOO_SAPLING, var3);
      var0.put(Blocks.BAMBOO, var3);
      var0.put(Blocks.POTTED_BAMBOO, var3);
      var0.put(Blocks.SCAFFOLDING, var3);
      var0.put(Blocks.STONECUTTER, var3);
      var0.put(Blocks.LANTERN, var3);
      var0.put(Blocks.SOUL_LANTERN, var3);
      var0.put(Blocks.CAMPFIRE, var3);
      var0.put(Blocks.SOUL_CAMPFIRE, var3);
      var0.put(Blocks.SWEET_BERRY_BUSH, var3);
      var0.put(Blocks.WEEPING_VINES, var3);
      var0.put(Blocks.WEEPING_VINES_PLANT, var3);
      var0.put(Blocks.TWISTING_VINES, var3);
      var0.put(Blocks.TWISTING_VINES_PLANT, var3);
      var0.put(Blocks.NETHER_SPROUTS, var3);
      var0.put(Blocks.CRIMSON_FUNGUS, var3);
      var0.put(Blocks.WARPED_FUNGUS, var3);
      var0.put(Blocks.CRIMSON_ROOTS, var3);
      var0.put(Blocks.WARPED_ROOTS, var3);
      var0.put(Blocks.POTTED_CRIMSON_FUNGUS, var3);
      var0.put(Blocks.POTTED_WARPED_FUNGUS, var3);
      var0.put(Blocks.POTTED_CRIMSON_ROOTS, var3);
      var0.put(Blocks.POTTED_WARPED_ROOTS, var3);
      var0.put(Blocks.CRIMSON_DOOR, var3);
      var0.put(Blocks.WARPED_DOOR, var3);
      var0.put(Blocks.POINTED_DRIPSTONE, var3);
      var0.put(Blocks.SMALL_AMETHYST_BUD, var3);
      var0.put(Blocks.MEDIUM_AMETHYST_BUD, var3);
      var0.put(Blocks.LARGE_AMETHYST_BUD, var3);
      var0.put(Blocks.AMETHYST_CLUSTER, var3);
      var0.put(Blocks.LIGHTNING_ROD, var3);
      var0.put(Blocks.CAVE_VINES, var3);
      var0.put(Blocks.CAVE_VINES_PLANT, var3);
      var0.put(Blocks.SPORE_BLOSSOM, var3);
      var0.put(Blocks.FLOWERING_AZALEA, var3);
      var0.put(Blocks.AZALEA, var3);
      var0.put(Blocks.PINK_PETALS, var3);
      var0.put(Blocks.BIG_DRIPLEAF, var3);
      var0.put(Blocks.BIG_DRIPLEAF_STEM, var3);
      var0.put(Blocks.SMALL_DRIPLEAF, var3);
      var0.put(Blocks.HANGING_ROOTS, var3);
      var0.put(Blocks.SCULK_SENSOR, var3);
      var0.put(Blocks.CALIBRATED_SCULK_SENSOR, var3);
      var0.put(Blocks.SCULK_VEIN, var3);
      var0.put(Blocks.SCULK_SHRIEKER, var3);
      var0.put(Blocks.MANGROVE_PROPAGULE, var3);
      var0.put(Blocks.FROGSPAWN, var3);
      var0.put(Blocks.COPPER_GRATE, var3);
      var0.put(Blocks.EXPOSED_COPPER_GRATE, var3);
      var0.put(Blocks.WEATHERED_COPPER_GRATE, var3);
      var0.put(Blocks.OXIDIZED_COPPER_GRATE, var3);
      var0.put(Blocks.WAXED_COPPER_GRATE, var3);
      var0.put(Blocks.WAXED_EXPOSED_COPPER_GRATE, var3);
      var0.put(Blocks.WAXED_WEATHERED_COPPER_GRATE, var3);
      var0.put(Blocks.WAXED_OXIDIZED_COPPER_GRATE, var3);
      RenderType var4 = RenderType.translucent();
      var0.put(Blocks.ICE, var4);
      var0.put(Blocks.NETHER_PORTAL, var4);
      var0.put(Blocks.WHITE_STAINED_GLASS, var4);
      var0.put(Blocks.ORANGE_STAINED_GLASS, var4);
      var0.put(Blocks.MAGENTA_STAINED_GLASS, var4);
      var0.put(Blocks.LIGHT_BLUE_STAINED_GLASS, var4);
      var0.put(Blocks.YELLOW_STAINED_GLASS, var4);
      var0.put(Blocks.LIME_STAINED_GLASS, var4);
      var0.put(Blocks.PINK_STAINED_GLASS, var4);
      var0.put(Blocks.GRAY_STAINED_GLASS, var4);
      var0.put(Blocks.LIGHT_GRAY_STAINED_GLASS, var4);
      var0.put(Blocks.CYAN_STAINED_GLASS, var4);
      var0.put(Blocks.PURPLE_STAINED_GLASS, var4);
      var0.put(Blocks.BLUE_STAINED_GLASS, var4);
      var0.put(Blocks.BROWN_STAINED_GLASS, var4);
      var0.put(Blocks.GREEN_STAINED_GLASS, var4);
      var0.put(Blocks.RED_STAINED_GLASS, var4);
      var0.put(Blocks.BLACK_STAINED_GLASS, var4);
      var0.put(Blocks.WHITE_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.ORANGE_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.MAGENTA_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.YELLOW_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.LIME_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.PINK_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.GRAY_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.CYAN_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.PURPLE_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.BLUE_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.BROWN_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.GREEN_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.RED_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.BLACK_STAINED_GLASS_PANE, var4);
      var0.put(Blocks.SLIME_BLOCK, var4);
      var0.put(Blocks.HONEY_BLOCK, var4);
      var0.put(Blocks.FROSTED_ICE, var4);
      var0.put(Blocks.BUBBLE_COLUMN, var4);
      var0.put(Blocks.TINTED_GLASS, var4);
   });
   private static final Map<Fluid, RenderType> TYPE_BY_FLUID = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      RenderType var1 = RenderType.translucent();
      var0.put(Fluids.FLOWING_WATER, var1);
      var0.put(Fluids.WATER, var1);
   });
   private static boolean renderCutout;

   public ItemBlockRenderTypes() {
      super();
   }

   public static RenderType getChunkRenderType(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 instanceof LeavesBlock) {
         return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
      } else {
         RenderType var2 = (RenderType)TYPE_BY_BLOCK.get(var1);
         return var2 != null ? var2 : RenderType.solid();
      }
   }

   public static RenderType getMovingBlockRenderType(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 instanceof LeavesBlock) {
         return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
      } else {
         RenderType var2 = (RenderType)TYPE_BY_BLOCK.get(var1);
         if (var2 != null) {
            return var2 == RenderType.translucent() ? RenderType.translucentMovingBlock() : var2;
         } else {
            return RenderType.solid();
         }
      }
   }

   public static RenderType getRenderType(BlockState var0, boolean var1) {
      RenderType var2 = getChunkRenderType(var0);
      if (var2 == RenderType.translucent()) {
         if (!Minecraft.useShaderTransparency()) {
            return Sheets.translucentCullBlockSheet();
         } else {
            return var1 ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
         }
      } else {
         return Sheets.cutoutBlockSheet();
      }
   }

   public static RenderType getRenderType(ItemStack var0, boolean var1) {
      Item var2 = var0.getItem();
      if (var2 instanceof BlockItem) {
         Block var3 = ((BlockItem)var2).getBlock();
         return getRenderType(var3.defaultBlockState(), var1);
      } else {
         return var1 ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
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
