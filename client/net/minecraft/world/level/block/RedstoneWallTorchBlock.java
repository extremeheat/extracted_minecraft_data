package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneWallTorchBlock extends RedstoneTorchBlock {
   public static final MapCodec<RedstoneWallTorchBlock> CODEC = simpleCodec(RedstoneWallTorchBlock::new);
   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   @Override
   public MapCodec<RedstoneWallTorchBlock> codec() {
      return CODEC;
   }

   protected RedstoneWallTorchBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.valueOf(true)));
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return WallTorchBlock.getShape(var1);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return WallTorchBlock.canSurvive(var2, var3, var1.getValue(FACING));
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      return var5.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : var1;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = Blocks.WALL_TORCH.getStateForPlacement(var1);
      return var2 == null ? null : this.defaultBlockState().setValue(FACING, var2.getValue(FACING));
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(LIT)) {
         Direction var5 = var1.getValue(FACING).getOpposite();
         double var6 = 0.27;
         double var8 = (double)var3.getX() + 0.5 + (var4.nextDouble() - 0.5) * 0.2 + 0.27 * (double)var5.getStepX();
         double var10 = (double)var3.getY() + 0.7 + (var4.nextDouble() - 0.5) * 0.2 + 0.22;
         double var12 = (double)var3.getZ() + 0.5 + (var4.nextDouble() - 0.5) * 0.2 + 0.27 * (double)var5.getStepZ();
         var2.addParticle(DustParticleOptions.REDSTONE, var8, var10, var12, 0.0, 0.0, 0.0);
      }
   }

   @Override
   protected boolean hasNeighborSignal(Level var1, BlockPos var2, BlockState var3) {
      Direction var4 = var3.getValue(FACING).getOpposite();
      return var1.hasSignal(var2.relative(var4), var4);
   }

   @Override
   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getValue(LIT) && var1.getValue(FACING) != var4 ? 15 : 0;
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, LIT);
   }

   @Nullable
   @Override
   protected Orientation randomOrientation(Level var1, BlockState var2) {
      return ExperimentalRedstoneUtils.initialOrientation(var1, var2.getValue(FACING).getOpposite(), Direction.UP);
   }
}
