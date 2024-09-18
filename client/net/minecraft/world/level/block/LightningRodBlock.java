package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;

public class LightningRodBlock extends RodBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<LightningRodBlock> CODEC = simpleCodec(LightningRodBlock::new);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final int ACTIVATION_TICKS = 8;
   public static final int RANGE = 128;
   private static final int SPARK_CYCLE = 200;

   @Override
   public MapCodec<LightningRodBlock> codec() {
      return CODEC;
   }

   public LightningRodBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false))
      );
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var3 = var2.getType() == Fluids.WATER;
      return this.defaultBlockState().setValue(FACING, var1.getClickedFace()).setValue(WATERLOGGED, Boolean.valueOf(var3));
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getValue(POWERED) ? 15 : 0;
   }

   @Override
   protected int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getValue(POWERED) && var1.getValue(FACING) == var4 ? 15 : 0;
   }

   public void onLightningStrike(BlockState var1, Level var2, BlockPos var3) {
      var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(true)), 3);
      this.updateNeighbours(var1, var2, var3);
      var2.scheduleTick(var3, this, 8);
      var2.levelEvent(3002, var3, var1.getValue(FACING).getAxis().ordinal());
   }

   private void updateNeighbours(BlockState var1, Level var2, BlockPos var3) {
      Direction var4 = var1.getValue(FACING).getOpposite();
      var2.updateNeighborsAt(var3.relative(var4), this, ExperimentalRedstoneUtils.initialOrientation(var2, var4, null));
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(false)), 3);
      this.updateNeighbours(var1, var2, var3);
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var2.isThundering()
         && (long)var2.random.nextInt(200) <= var2.getGameTime() % 200L
         && var3.getY() == var2.getHeight(Heightmap.Types.WORLD_SURFACE, var3.getX(), var3.getZ()) - 1) {
         ParticleUtils.spawnParticlesAlongAxis(var1.getValue(FACING).getAxis(), var2, var3, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(1, 2));
      }
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         if (var1.getValue(POWERED)) {
            this.updateNeighbours(var1, var2, var3);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   @Override
   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         if (var1.getValue(POWERED) && !var2.getBlockTicks().hasScheduledTick(var3, this)) {
            var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(false)), 18);
         }
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, WATERLOGGED);
   }

   @Override
   protected boolean isSignalSource(BlockState var1) {
      return true;
   }
}
