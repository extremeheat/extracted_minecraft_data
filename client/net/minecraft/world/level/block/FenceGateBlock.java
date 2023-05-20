package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceGateBlock extends HorizontalDirectionalBlock {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
   protected static final VoxelShape Z_SHAPE = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
   protected static final VoxelShape X_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
   protected static final VoxelShape Z_SHAPE_LOW = Block.box(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
   protected static final VoxelShape X_SHAPE_LOW = Block.box(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
   protected static final VoxelShape Z_COLLISION_SHAPE = Block.box(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
   protected static final VoxelShape X_COLLISION_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
   protected static final VoxelShape Z_SUPPORT_SHAPE = Block.box(0.0, 5.0, 6.0, 16.0, 24.0, 10.0);
   protected static final VoxelShape X_SUPPORT_SHAPE = Block.box(6.0, 5.0, 0.0, 10.0, 24.0, 16.0);
   protected static final VoxelShape Z_OCCLUSION_SHAPE = Shapes.or(Block.box(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.box(14.0, 5.0, 7.0, 16.0, 16.0, 9.0));
   protected static final VoxelShape X_OCCLUSION_SHAPE = Shapes.or(Block.box(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.box(7.0, 5.0, 14.0, 9.0, 16.0, 16.0));
   protected static final VoxelShape Z_OCCLUSION_SHAPE_LOW = Shapes.or(Block.box(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.box(14.0, 2.0, 7.0, 16.0, 13.0, 9.0));
   protected static final VoxelShape X_OCCLUSION_SHAPE_LOW = Shapes.or(Block.box(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.box(7.0, 2.0, 14.0, 9.0, 13.0, 16.0));
   private final WoodType type;

   public FenceGateBlock(BlockBehaviour.Properties var1, WoodType var2) {
      super(var1.sound(var2.soundType()));
      this.type = var2;
      this.registerDefaultState(
         this.stateDefinition.any().setValue(OPEN, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)).setValue(IN_WALL, Boolean.valueOf(false))
      );
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (var1.getValue(IN_WALL)) {
         return var1.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE_LOW : Z_SHAPE_LOW;
      } else {
         return var1.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
      }
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      Direction.Axis var7 = var2.getAxis();
      if (var1.getValue(FACING).getClockWise().getAxis() != var7) {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         boolean var8 = this.isWall(var3) || this.isWall(var4.getBlockState(var5.relative(var2.getOpposite())));
         return var1.setValue(IN_WALL, Boolean.valueOf(var8));
      }
   }

   @Override
   public VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      if (var1.getValue(OPEN)) {
         return Shapes.empty();
      } else {
         return var1.getValue(FACING).getAxis() == Direction.Axis.Z ? Z_SUPPORT_SHAPE : X_SUPPORT_SHAPE;
      }
   }

   @Override
   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (var1.getValue(OPEN)) {
         return Shapes.empty();
      } else {
         return var1.getValue(FACING).getAxis() == Direction.Axis.Z ? Z_COLLISION_SHAPE : X_COLLISION_SHAPE;
      }
   }

   @Override
   public VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      if (var1.getValue(IN_WALL)) {
         return var1.getValue(FACING).getAxis() == Direction.Axis.X ? X_OCCLUSION_SHAPE_LOW : Z_OCCLUSION_SHAPE_LOW;
      } else {
         return var1.getValue(FACING).getAxis() == Direction.Axis.X ? X_OCCLUSION_SHAPE : Z_OCCLUSION_SHAPE;
      }
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      switch(var4) {
         case LAND:
            return var1.getValue(OPEN);
         case WATER:
            return false;
         case AIR:
            return var1.getValue(OPEN);
         default:
            return false;
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      boolean var4 = var2.hasNeighborSignal(var3);
      Direction var5 = var1.getHorizontalDirection();
      Direction.Axis var6 = var5.getAxis();
      boolean var7 = var6 == Direction.Axis.Z && (this.isWall(var2.getBlockState(var3.west())) || this.isWall(var2.getBlockState(var3.east())))
         || var6 == Direction.Axis.X && (this.isWall(var2.getBlockState(var3.north())) || this.isWall(var2.getBlockState(var3.south())));
      return this.defaultBlockState()
         .setValue(FACING, var5)
         .setValue(OPEN, Boolean.valueOf(var4))
         .setValue(POWERED, Boolean.valueOf(var4))
         .setValue(IN_WALL, Boolean.valueOf(var7));
   }

   private boolean isWall(BlockState var1) {
      return var1.is(BlockTags.WALLS);
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var1.getValue(OPEN)) {
         var1 = var1.setValue(OPEN, Boolean.valueOf(false));
         var2.setBlock(var3, var1, 10);
      } else {
         Direction var7 = var4.getDirection();
         if (var1.getValue(FACING) == var7.getOpposite()) {
            var1 = var1.setValue(FACING, var7);
         }

         var1 = var1.setValue(OPEN, Boolean.valueOf(true));
         var2.setBlock(var3, var1, 10);
      }

      boolean var9 = var1.getValue(OPEN);
      var2.playSound(
         var4, var3, var9 ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.1F + 0.9F
      );
      var2.gameEvent(var4, var9 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
      return InteractionResult.sidedSuccess(var2.isClientSide);
   }

   @Override
   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         boolean var7 = var2.hasNeighborSignal(var3);
         if (var1.getValue(POWERED) != var7) {
            var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(var7)).setValue(OPEN, Boolean.valueOf(var7)), 2);
            if (var1.getValue(OPEN) != var7) {
               var2.playSound(
                  null,
                  var3,
                  var7 ? this.type.fenceGateOpen() : this.type.fenceGateClose(),
                  SoundSource.BLOCKS,
                  1.0F,
                  var2.getRandom().nextFloat() * 0.1F + 0.9F
               );
               var2.gameEvent(null, var7 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
            }
         }
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, OPEN, POWERED, IN_WALL);
   }

   public static boolean connectsToDirection(BlockState var0, Direction var1) {
      return var0.getValue(FACING).getAxis() == var1.getClockWise().getAxis();
   }
}
