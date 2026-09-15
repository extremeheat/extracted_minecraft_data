package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.sounds.AmbientDesertBlockSoundsPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShortDryGrassBlock extends DryVegetationBlock implements BonemealableBlock {
   private static final VoxelShape SHAPE = Block.column(12.0, 0.0, 10.0);

   protected ShortDryGrassBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      AmbientDesertBlockSoundsPlayer.playAmbientDryGrassSounds(level, pos, random);
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return true;
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      level.setBlockAndUpdate(pos, Blocks.TALL_DRY_GRASS.defaultBlockState());
   }
}
