package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceGateBlock extends HorizontalDirectionalBlock {
   public static final BooleanProperty OPEN;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty IN_WALL;
   protected static final VoxelShape Z_SHAPE;
   protected static final VoxelShape X_SHAPE;
   protected static final VoxelShape Z_SHAPE_LOW;
   protected static final VoxelShape X_SHAPE_LOW;
   protected static final VoxelShape Z_COLLISION_SHAPE;
   protected static final VoxelShape X_COLLISION_SHAPE;
   protected static final VoxelShape Z_OCCLUSION_SHAPE;
   protected static final VoxelShape X_OCCLUSION_SHAPE;
   protected static final VoxelShape Z_OCCLUSION_SHAPE_LOW;
   protected static final VoxelShape X_OCCLUSION_SHAPE_LOW;

   public FenceGateBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(OPEN, false)).setValue(POWERED, false)).setValue(IN_WALL, false));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if ((Boolean)var1.getValue(IN_WALL)) {
         return ((Direction)var1.getValue(FACING)).getAxis() == Direction.Axis.field_500 ? X_SHAPE_LOW : Z_SHAPE_LOW;
      } else {
         return ((Direction)var1.getValue(FACING)).getAxis() == Direction.Axis.field_500 ? X_SHAPE : Z_SHAPE;
      }
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      Direction.Axis var7 = var2.getAxis();
      if (((Direction)var1.getValue(FACING)).getClockWise().getAxis() != var7) {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         boolean var8 = this.isWall(var3) || this.isWall(var4.getBlockState(var5.relative(var2.getOpposite())));
         return (BlockState)var1.setValue(IN_WALL, var8);
      }
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if ((Boolean)var1.getValue(OPEN)) {
         return Shapes.empty();
      } else {
         return ((Direction)var1.getValue(FACING)).getAxis() == Direction.Axis.field_502 ? Z_COLLISION_SHAPE : X_COLLISION_SHAPE;
      }
   }

   public VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      if ((Boolean)var1.getValue(IN_WALL)) {
         return ((Direction)var1.getValue(FACING)).getAxis() == Direction.Axis.field_500 ? X_OCCLUSION_SHAPE_LOW : Z_OCCLUSION_SHAPE_LOW;
      } else {
         return ((Direction)var1.getValue(FACING)).getAxis() == Direction.Axis.field_500 ? X_OCCLUSION_SHAPE : Z_OCCLUSION_SHAPE;
      }
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      switch(var4) {
      case LAND:
         return (Boolean)var1.getValue(OPEN);
      case WATER:
         return false;
      case AIR:
         return (Boolean)var1.getValue(OPEN);
      default:
         return false;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      boolean var4 = var2.hasNeighborSignal(var3);
      Direction var5 = var1.getHorizontalDirection();
      Direction.Axis var6 = var5.getAxis();
      boolean var7 = var6 == Direction.Axis.field_502 && (this.isWall(var2.getBlockState(var3.west())) || this.isWall(var2.getBlockState(var3.east()))) || var6 == Direction.Axis.field_500 && (this.isWall(var2.getBlockState(var3.north())) || this.isWall(var2.getBlockState(var3.south())));
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var5)).setValue(OPEN, var4)).setValue(POWERED, var4)).setValue(IN_WALL, var7);
   }

   private boolean isWall(BlockState var1) {
      return var1.is(BlockTags.WALLS);
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if ((Boolean)var1.getValue(OPEN)) {
         var1 = (BlockState)var1.setValue(OPEN, false);
         var2.setBlock(var3, var1, 10);
      } else {
         Direction var7 = var4.getDirection();
         if (var1.getValue(FACING) == var7.getOpposite()) {
            var1 = (BlockState)var1.setValue(FACING, var7);
         }

         var1 = (BlockState)var1.setValue(OPEN, true);
         var2.setBlock(var3, var1, 10);
      }

      boolean var8 = (Boolean)var1.getValue(OPEN);
      var2.levelEvent(var4, var8 ? 1008 : 1014, var3, 0);
      var2.gameEvent(var4, var8 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
      return InteractionResult.sidedSuccess(var2.isClientSide);
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         boolean var7 = var2.hasNeighborSignal(var3);
         if ((Boolean)var1.getValue(POWERED) != var7) {
            var2.setBlock(var3, (BlockState)((BlockState)var1.setValue(POWERED, var7)).setValue(OPEN, var7), 2);
            if ((Boolean)var1.getValue(OPEN) != var7) {
               var2.levelEvent((Player)null, var7 ? 1008 : 1014, var3, 0);
               var2.gameEvent(var7 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
            }
         }

      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, OPEN, POWERED, IN_WALL);
   }

   public static boolean connectsToDirection(BlockState var0, Direction var1) {
      return ((Direction)var0.getValue(FACING)).getAxis() == var1.getClockWise().getAxis();
   }

   static {
      OPEN = BlockStateProperties.OPEN;
      POWERED = BlockStateProperties.POWERED;
      IN_WALL = BlockStateProperties.IN_WALL;
      Z_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
      X_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
      Z_SHAPE_LOW = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
      X_SHAPE_LOW = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
      Z_COLLISION_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
      X_COLLISION_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
      Z_OCCLUSION_SHAPE = Shapes.method_31(Block.box(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.box(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
      X_OCCLUSION_SHAPE = Shapes.method_31(Block.box(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.box(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
      Z_OCCLUSION_SHAPE_LOW = Shapes.method_31(Block.box(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.box(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
      X_OCCLUSION_SHAPE_LOW = Shapes.method_31(Block.box(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.box(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));
   }
}
