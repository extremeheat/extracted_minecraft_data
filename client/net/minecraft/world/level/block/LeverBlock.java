package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeverBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final BooleanProperty POWERED;
   protected static final int DEPTH = 6;
   protected static final int WIDTH = 6;
   protected static final int HEIGHT = 8;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape EAST_AABB;
   protected static final VoxelShape UP_AABB_Z;
   protected static final VoxelShape UP_AABB_X;
   protected static final VoxelShape DOWN_AABB_Z;
   protected static final VoxelShape DOWN_AABB_X;

   protected LeverBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch((AttachFace)var1.getValue(FACE)) {
      case FLOOR:
         switch(((Direction)var1.getValue(FACING)).getAxis()) {
         case field_500:
            return UP_AABB_X;
         case field_502:
         default:
            return UP_AABB_Z;
         }
      case WALL:
         switch((Direction)var1.getValue(FACING)) {
         case EAST:
            return EAST_AABB;
         case WEST:
            return WEST_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case NORTH:
         default:
            return NORTH_AABB;
         }
      case CEILING:
      default:
         switch(((Direction)var1.getValue(FACING)).getAxis()) {
         case field_500:
            return DOWN_AABB_X;
         case field_502:
         default:
            return DOWN_AABB_Z;
         }
      }
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      BlockState var7;
      if (var2.isClientSide) {
         var7 = (BlockState)var1.cycle(POWERED);
         if ((Boolean)var7.getValue(POWERED)) {
            makeParticle(var7, var2, var3, 1.0F);
         }

         return InteractionResult.SUCCESS;
      } else {
         var7 = this.pull(var1, var2, var3);
         float var8 = (Boolean)var7.getValue(POWERED) ? 0.6F : 0.5F;
         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, var8);
         var2.gameEvent(var4, (Boolean)var7.getValue(POWERED) ? GameEvent.BLOCK_SWITCH : GameEvent.BLOCK_UNSWITCH, var3);
         return InteractionResult.CONSUME;
      }
   }

   public BlockState pull(BlockState var1, Level var2, BlockPos var3) {
      var1 = (BlockState)var1.cycle(POWERED);
      var2.setBlock(var3, var1, 3);
      this.updateNeighbours(var1, var2, var3);
      return var1;
   }

   private static void makeParticle(BlockState var0, LevelAccessor var1, BlockPos var2, float var3) {
      Direction var4 = ((Direction)var0.getValue(FACING)).getOpposite();
      Direction var5 = getConnectedDirection(var0).getOpposite();
      double var6 = (double)var2.getX() + 0.5D + 0.1D * (double)var4.getStepX() + 0.2D * (double)var5.getStepX();
      double var8 = (double)var2.getY() + 0.5D + 0.1D * (double)var4.getStepY() + 0.2D * (double)var5.getStepY();
      double var10 = (double)var2.getZ() + 0.5D + 0.1D * (double)var4.getStepZ() + 0.2D * (double)var5.getStepZ();
      var1.addParticle(new DustParticleOptions(DustParticleOptions.REDSTONE_PARTICLE_COLOR, var3), var6, var8, var10, 0.0D, 0.0D, 0.0D);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.getValue(POWERED) && var4.nextFloat() < 0.25F) {
         makeParticle(var1, var2, var3, 0.5F);
      }

   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         if ((Boolean)var1.getValue(POWERED)) {
            this.updateNeighbours(var1, var2, var3);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) && getConnectedDirection(var1) == var4 ? 15 : 0;
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   private void updateNeighbours(BlockState var1, Level var2, BlockPos var3) {
      var2.updateNeighborsAt(var3, this);
      var2.updateNeighborsAt(var3.relative(getConnectedDirection(var1).getOpposite()), this);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACE, FACING, POWERED);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
      NORTH_AABB = Block.box(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
      SOUTH_AABB = Block.box(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
      WEST_AABB = Block.box(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
      EAST_AABB = Block.box(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
      UP_AABB_Z = Block.box(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
      UP_AABB_X = Block.box(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
      DOWN_AABB_Z = Block.box(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
      DOWN_AABB_X = Block.box(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);
   }
}
