package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BushBlock extends VegetationBlock implements BonemealableBlock {
   public static final MapCodec<BushBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(propertiesCodec(), Codec.intRange(0, 16).fieldOf("shape_height").forGetter((b) -> b.shapeHeight)).apply(i, BushBlock::new));
   public static final int DEFAULT_SHAPE_HEIGHT = 13;
   private final VoxelShape shape;
   private final int shapeHeight;

   public MapCodec<BushBlock> codec() {
      return CODEC;
   }

   protected BushBlock(final BlockBehaviour.Properties properties) {
      this(properties, 13);
   }

   protected BushBlock(final BlockBehaviour.Properties properties, final int shapeHeight) {
      super(properties);
      this.shapeHeight = shapeHeight;
      this.shape = Block.column(16.0, 0.0, (double)shapeHeight);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return this.shape;
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
      return BonemealableBlock.hasSpreadableNeighbourPos(level, pos, state);
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
      BonemealableBlock.findSpreadableNeighbourPos(level, pos, state).ifPresent((blockPos) -> level.setBlockAndUpdate(blockPos, this.defaultBlockState()));
   }
}
