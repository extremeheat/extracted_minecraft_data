package net.minecraft.world.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class PickaxeItem extends DiggerItem {
   private static final Set<Block> DIGGABLES;
   private static final Object2IntMap<Block> MIN_LEVEL_FOR_DROPS;

   protected PickaxeItem(Tier var1, int var2, float var3, Item.Properties var4) {
      super((float)var2, var3, var1, DIGGABLES, var4);
   }

   public boolean isCorrectToolForDrops(BlockState var1) {
      int var2 = this.getTier().getLevel();
      int var3 = MIN_LEVEL_FOR_DROPS.getInt(var1.getBlock());
      if (var3 != -1) {
         return var2 >= var3;
      } else {
         Material var4 = var1.getMaterial();
         return var4 == Material.STONE || var4 == Material.METAL || var4 == Material.HEAVY_METAL;
      }
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      Material var3 = var2.getMaterial();
      return var3 != Material.METAL && var3 != Material.HEAVY_METAL && var3 != Material.STONE ? super.getDestroySpeed(var1, var2) : this.speed;
   }

   static {
      DIGGABLES = ImmutableSet.of(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, new Block[]{Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.PISTON_HEAD, Blocks.AMETHYST_CLUSTER, Blocks.SMALL_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD, Blocks.LARGE_AMETHYST_BUD, Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST, Blocks.COPPER_ORE, Blocks.COPPER_BLOCK, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER, Blocks.WEATHERED_COPPER_BLOCK, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.SEMI_WEATHERED_COPPER_BLOCK, Blocks.SEMI_WEATHERED_CUT_COPPER, Blocks.SEMI_WEATHERED_CUT_COPPER_SLAB, Blocks.SEMI_WEATHERED_CUT_COPPER_STAIRS, Blocks.LIGHTLY_WEATHERED_COPPER_BLOCK, Blocks.LIGHTLY_WEATHERED_CUT_COPPER_SLAB, Blocks.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS, Blocks.LIGHTLY_WEATHERED_CUT_COPPER, Blocks.WAXED_COPPER, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_SEMI_WEATHERED_COPPER, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_LIGHTLY_WEATHERED_COPPER, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER, Blocks.LIGHTNING_ROD});
      MIN_LEVEL_FOR_DROPS = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (var0) -> {
         var0.defaultReturnValue(-1);
         UnmodifiableIterator var1 = ImmutableList.of(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK, Blocks.RESPAWN_ANCHOR, Blocks.ANCIENT_DEBRIS).iterator();

         Block var2;
         while(var1.hasNext()) {
            var2 = (Block)var1.next();
            var0.put(var2, 3);
         }

         var1 = ImmutableList.of(Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.REDSTONE_ORE, Blocks.AMETHYST_BLOCK, Blocks.AMETHYST_CLUSTER).iterator();

         while(var1.hasNext()) {
            var2 = (Block)var1.next();
            var0.put(var2, 2);
         }

         var1 = ImmutableList.of(Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.COPPER_BLOCK, Blocks.COPPER_ORE, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER, Blocks.WEATHERED_COPPER_BLOCK, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_STAIRS, new Block[]{Blocks.WEATHERED_CUT_COPPER, Blocks.SEMI_WEATHERED_COPPER_BLOCK, Blocks.SEMI_WEATHERED_CUT_COPPER_SLAB, Blocks.SEMI_WEATHERED_CUT_COPPER_STAIRS, Blocks.SEMI_WEATHERED_CUT_COPPER, Blocks.LIGHTLY_WEATHERED_COPPER_BLOCK, Blocks.LIGHTLY_WEATHERED_CUT_COPPER_SLAB, Blocks.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS, Blocks.LIGHTLY_WEATHERED_CUT_COPPER, Blocks.WAXED_COPPER, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_SEMI_WEATHERED_COPPER, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER, Blocks.WAXED_LIGHTLY_WEATHERED_COPPER, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER}).iterator();

         while(var1.hasNext()) {
            var2 = (Block)var1.next();
            var0.put(var2, 1);
         }

         var1 = ImmutableList.of(Blocks.NETHER_GOLD_ORE).iterator();

         while(var1.hasNext()) {
            var2 = (Block)var1.next();
            var0.put(var2, 0);
         }

      });
   }
}
