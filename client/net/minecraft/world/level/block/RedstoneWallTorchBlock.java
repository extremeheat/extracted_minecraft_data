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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneWallTorchBlock extends RedstoneTorchBlock {
   public static final MapCodec<RedstoneWallTorchBlock> CODEC = simpleCodec(RedstoneWallTorchBlock::new);
   public static final DirectionProperty FACING;
   public static final BooleanProperty LIT;

   public MapCodec<RedstoneWallTorchBlock> codec() {
      return CODEC;
   }

   protected RedstoneWallTorchBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, true));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return WallTorchBlock.getShape(var1);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return WallTorchBlock.canSurvive(var2, var3, (Direction)var1.getValue(FACING));
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : var1;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = Blocks.WALL_TORCH.getStateForPlacement(var1);
      return var2 == null ? null : (BlockState)this.defaultBlockState().setValue(FACING, (Direction)var2.getValue(FACING));
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(LIT)) {
         Direction var5 = ((Direction)var1.getValue(FACING)).getOpposite();
         double var6 = 0.27;
         double var8 = (double)var3.getX() + 0.5 + (var4.nextDouble() - 0.5) * 0.2 + 0.27 * (double)var5.getStepX();
         double var10 = (double)var3.getY() + 0.7 + (var4.nextDouble() - 0.5) * 0.2 + 0.22;
         double var12 = (double)var3.getZ() + 0.5 + (var4.nextDouble() - 0.5) * 0.2 + 0.27 * (double)var5.getStepZ();
         var2.addParticle(DustParticleOptions.REDSTONE, var8, var10, var12, 0.0, 0.0, 0.0);
      }
   }

   protected boolean hasNeighborSignal(Level var1, BlockPos var2, BlockState var3) {
      Direction var4 = ((Direction)var3.getValue(FACING)).getOpposite();
      return var1.hasSignal(var2.relative(var4), var4);
   }

   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(LIT) && var1.getValue(FACING) != var4 ? 15 : 0;
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, LIT);
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      LIT = RedstoneTorchBlock.LIT;
   }
}
