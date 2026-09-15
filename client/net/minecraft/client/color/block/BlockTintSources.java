package net.minecraft.client.color.block;

import java.util.Set;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RedstoneWireBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockTintSources {
   public BlockTintSources() {
      super();
   }

   public static BlockTintSource constant(final int color) {
      return (var1) -> color;
   }

   public static BlockTintSource constant(final int colorInHand, final int colorInWorld) {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return colorInHand;
         }

         public int colorInWorld(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return colorInWorld;
         }
      };
   }

   public static BlockTintSource doubleTallGrass() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return GrassColor.getDefaultColor();
         }

         public int colorInWorld(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return BiomeColors.getAverageGrassColor(level, state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos);
         }

         public Set<Property<?>> relevantProperties() {
            return Set.of(DoublePlantBlock.HALF);
         }
      };
   }

   public static BlockTintSource grass() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return GrassColor.getDefaultColor();
         }

         public int colorInWorld(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return BiomeColors.getAverageGrassColor(level, pos);
         }
      };
   }

   public static BlockTintSource grassBlock() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return GrassColor.getDefaultColor();
         }

         public int colorInWorld(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return BiomeColors.getAverageGrassColor(level, pos);
         }

         public int colorAsTerrainParticle(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return -1;
         }
      };
   }

   public static BlockTintSource sugarCane() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return -1;
         }

         public int colorInWorld(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return BiomeColors.getAverageGrassColor(level, pos);
         }
      };
   }

   public static BlockTintSource foliage() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return -12012264;
         }

         public int colorInWorld(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return BiomeColors.getAverageFoliageColor(level, pos);
         }
      };
   }

   public static BlockTintSource dryFoliage() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return -10732494;
         }

         public int colorInWorld(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return BiomeColors.getAverageDryFoliageColor(level, pos);
         }
      };
   }

   public static BlockTintSource water() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return -1;
         }

         public int colorInWorld(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return BiomeColors.getAverageWaterColor(level, pos);
         }
      };
   }

   public static BlockTintSource waterParticles() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return -1;
         }

         public int colorAsTerrainParticle(final BlockState state, final BlockAndTintGetter level, final BlockPos pos) {
            return BiomeColors.getAverageWaterColor(level, pos);
         }
      };
   }

   public static BlockTintSource redstone() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            return RedstoneWireBlock.getColorForPower((Integer)state.getValue(RedstoneWireBlock.POWER));
         }

         public Set<Property<?>> relevantProperties() {
            return Set.of(RedstoneWireBlock.POWER);
         }
      };
   }

   public static BlockTintSource stem() {
      return new BlockTintSource() {
         public int color(final BlockState state) {
            int age = (Integer)state.getValue(StemBlock.AGE);
            return ARGB.color(age * 32, 255 - age * 8, age * 4);
         }

         public Set<Property<?>> relevantProperties() {
            return Set.of(StemBlock.AGE);
         }
      };
   }
}
