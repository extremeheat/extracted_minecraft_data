package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;

public class BlockColors {
   private static final int DEFAULT = -1;
   private final IdMapper<BlockColor> blockColors = new IdMapper(32);
   private final Map<Block, Set<Property<?>>> coloringStates = Maps.newHashMap();

   public BlockColors() {
      super();
   }

   public static BlockColors createDefault() {
      BlockColors var0 = new BlockColors();
      var0.register((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.getAverageGrassColor(var1, var0x.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? var2.below() : var2) : GrassColor.getDefaultColor();
      }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      var0.addColoringState(DoublePlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      var0.register((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.getAverageGrassColor(var1, var2) : GrassColor.getDefaultColor();
      }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.SHORT_GRASS, Blocks.POTTED_FERN);
      var0.register((var0x, var1, var2, var3) -> {
         if (var3 != 0) {
            return var1 != null && var2 != null ? BiomeColors.getAverageGrassColor(var1, var2) : GrassColor.getDefaultColor();
         } else {
            return -1;
         }
      }, Blocks.PINK_PETALS);
      var0.register((var0x, var1, var2, var3) -> {
         return FoliageColor.getEvergreenColor();
      }, Blocks.SPRUCE_LEAVES);
      var0.register((var0x, var1, var2, var3) -> {
         return FoliageColor.getBirchColor();
      }, Blocks.BIRCH_LEAVES);
      var0.register((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.getAverageFoliageColor(var1, var2) : FoliageColor.getDefaultColor();
      }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE, Blocks.MANGROVE_LEAVES);
      var0.register((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.getAverageWaterColor(var1, var2) : -1;
      }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.WATER_CAULDRON);
      var0.register((var0x, var1, var2, var3) -> {
         return RedStoneWireBlock.getColorForPower((Integer)var0x.getValue(RedStoneWireBlock.POWER));
      }, Blocks.REDSTONE_WIRE);
      var0.addColoringState(RedStoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
      var0.register((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.getAverageGrassColor(var1, var2) : -1;
      }, Blocks.SUGAR_CANE);
      var0.register((var0x, var1, var2, var3) -> {
         return -2046180;
      }, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      var0.register((var0x, var1, var2, var3) -> {
         int var4 = (Integer)var0x.getValue(StemBlock.AGE);
         return FastColor.ARGB32.color(var4 * 32, 255 - var4 * 8, var4 * 4);
      }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      var0.addColoringState(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      var0.register((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? -14647248 : -9321636;
      }, Blocks.LILY_PAD);
      return var0;
   }

   public int getColor(BlockState var1, Level var2, BlockPos var3) {
      BlockColor var4 = (BlockColor)this.blockColors.byId(BuiltInRegistries.BLOCK.getId(var1.getBlock()));
      if (var4 != null) {
         return var4.getColor(var1, (BlockAndTintGetter)null, (BlockPos)null, 0);
      } else {
         MapColor var5 = var1.getMapColor(var2, var3);
         return var5 != null ? var5.col : -1;
      }
   }

   public int getColor(BlockState var1, @Nullable BlockAndTintGetter var2, @Nullable BlockPos var3, int var4) {
      BlockColor var5 = (BlockColor)this.blockColors.byId(BuiltInRegistries.BLOCK.getId(var1.getBlock()));
      return var5 == null ? -1 : var5.getColor(var1, var2, var3, var4);
   }

   public void register(BlockColor var1, Block... var2) {
      Block[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Block var6 = var3[var5];
         this.blockColors.addMapping(var1, BuiltInRegistries.BLOCK.getId(var6));
      }

   }

   private void addColoringStates(Set<Property<?>> var1, Block... var2) {
      Block[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Block var6 = var3[var5];
         this.coloringStates.put(var6, var1);
      }

   }

   private void addColoringState(Property<?> var1, Block... var2) {
      this.addColoringStates(ImmutableSet.of(var1), var2);
   }

   public Set<Property<?>> getColoringProperties(Block var1) {
      return (Set)this.coloringStates.getOrDefault(var1, ImmutableSet.of());
   }
}
