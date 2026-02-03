package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.BlockAndTintGetter;
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
import org.jspecify.annotations.Nullable;

public class BlockColors {
   private static final int DEFAULT = -1;
   public static final int LILY_PAD_IN_WORLD = -14647248;
   public static final int LILY_PAD_DEFAULT = -9321636;
   private final IdMapper<BlockColor> blockColors = new IdMapper<BlockColor>(32);
   private final Map<Block, Set<Property<?>>> coloringStates = Maps.newHashMap();

   public BlockColors() {
      super();
   }

   public static BlockColors createDefault() {
      BlockColors colors = new BlockColors();
      colors.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageGrassColor(level, state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos) : GrassColor.getDefaultColor(), Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      colors.addColoringState(DoublePlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      colors.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.getDefaultColor(), Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.SHORT_GRASS, Blocks.POTTED_FERN, Blocks.BUSH);
      colors.register((state, level, pos, tintIndex) -> {
         if (tintIndex != 0) {
            return level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.getDefaultColor();
         } else {
            return -1;
         }
      }, Blocks.PINK_PETALS, Blocks.WILDFLOWERS);
      colors.register((state, level, pos, tintIndex) -> -10380959, Blocks.SPRUCE_LEAVES);
      colors.register((state, level, pos, tintIndex) -> -8345771, Blocks.BIRCH_LEAVES);
      colors.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : -12012264, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE, Blocks.MANGROVE_LEAVES);
      colors.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageDryFoliageColor(level, pos) : -10732494, Blocks.LEAF_LITTER);
      colors.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : -1, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.WATER_CAULDRON);
      colors.register((state, level, pos, tintIndex) -> RedStoneWireBlock.getColorForPower((Integer)state.getValue(RedStoneWireBlock.POWER)), Blocks.REDSTONE_WIRE);
      colors.addColoringState(RedStoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
      colors.register((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : -1, Blocks.SUGAR_CANE);
      colors.register((state, level, pos, tintIndex) -> -2046180, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      colors.register((state, level, pos, tintIndex) -> {
         int age = (Integer)state.getValue(StemBlock.AGE);
         return ARGB.color(age * 32, 255 - age * 8, age * 4);
      }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      colors.addColoringState(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      colors.register((state, level, pos, tintIndex) -> level != null && pos != null ? -14647248 : -9321636, Blocks.LILY_PAD);
      return colors;
   }

   public int getColor(final BlockState state, final Level level, final BlockPos blockPos) {
      BlockColor blockColor = this.blockColors.byId(BuiltInRegistries.BLOCK.getId(state.getBlock()));
      if (blockColor != null) {
         return blockColor.getColor(state, (BlockAndTintGetter)null, (BlockPos)null, 0);
      } else {
         MapColor color = state.getMapColor(level, blockPos);
         return color != null ? color.col : -1;
      }
   }

   public int getColor(final BlockState state, final @Nullable BlockAndTintGetter level, final @Nullable BlockPos pos, final int tintIndex) {
      BlockColor blockColor = this.blockColors.byId(BuiltInRegistries.BLOCK.getId(state.getBlock()));
      return blockColor == null ? -1 : blockColor.getColor(state, level, pos, tintIndex);
   }

   public void register(final BlockColor color, final Block... blocks) {
      for(Block block : blocks) {
         this.blockColors.addMapping(color, BuiltInRegistries.BLOCK.getId(block));
      }

   }

   private void addColoringStates(final Set<Property<?>> properties, final Block... blocks) {
      for(Block block : blocks) {
         this.coloringStates.put(block, properties);
      }

   }

   private void addColoringState(final Property<?> property, final Block... blocks) {
      this.addColoringStates(ImmutableSet.of(property), blocks);
   }

   public Set<Property<?>> getColoringProperties(final Block block) {
      return (Set)this.coloringStates.getOrDefault(block, ImmutableSet.of());
   }
}
