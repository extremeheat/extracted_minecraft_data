package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoorBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   protected static final float AABB_DOOR_THICKNESS = 3.0F;
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape WEST_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
   private final BlockSetType type;

   protected DoorBlock(BlockBehaviour.Properties var1, BlockSetType var2) {
      super(var1.sound(var2.soundType()));
      this.type = var2;
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(OPEN, Boolean.valueOf(false))
            .setValue(HINGE, DoorHingeSide.LEFT)
            .setValue(POWERED, Boolean.valueOf(false))
            .setValue(HALF, DoubleBlockHalf.LOWER)
      );
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction var5 = var1.getValue(FACING);
      boolean var6 = !var1.getValue(OPEN);
      boolean var7 = var1.getValue(HINGE) == DoorHingeSide.RIGHT;
      switch(var5) {
         case EAST:
         default:
            return var6 ? EAST_AABB : (var7 ? NORTH_AABB : SOUTH_AABB);
         case SOUTH:
            return var6 ? SOUTH_AABB : (var7 ? EAST_AABB : WEST_AABB);
         case WEST:
            return var6 ? WEST_AABB : (var7 ? SOUTH_AABB : NORTH_AABB);
         case NORTH:
            return var6 ? NORTH_AABB : (var7 ? WEST_AABB : EAST_AABB);
      }
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      DoubleBlockHalf var7 = var1.getValue(HALF);
      if (var2.getAxis() != Direction.Axis.Y || var7 == DoubleBlockHalf.LOWER != (var2 == Direction.UP)) {
         return var7 == DoubleBlockHalf.LOWER && var2 == Direction.DOWN && !var1.canSurvive(var4, var5)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         return var3.is(this) && var3.getValue(HALF) != var7
            ? var1.setValue(FACING, var3.getValue(FACING))
               .setValue(OPEN, var3.getValue(OPEN))
               .setValue(HINGE, var3.getValue(HINGE))
               .setValue(POWERED, var3.getValue(POWERED))
            : Blocks.AIR.defaultBlockState();
      }
   }

   @Override
   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide && var4.isCreative()) {
         DoublePlantBlock.preventCreativeDropFromBottomPart(var1, var2, var3, var4);
      }

      super.playerWillDestroy(var1, var2, var3, var4);
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

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockPos var2 = var1.getClickedPos();
      Level var3 = var1.getLevel();
      if (var2.getY() < var3.getMaxBuildHeight() - 1 && var3.getBlockState(var2.above()).canBeReplaced(var1)) {
         boolean var4 = var3.hasNeighborSignal(var2) || var3.hasNeighborSignal(var2.above());
         return this.defaultBlockState()
            .setValue(FACING, var1.getHorizontalDirection())
            .setValue(HINGE, this.getHinge(var1))
            .setValue(POWERED, Boolean.valueOf(var4))
            .setValue(OPEN, Boolean.valueOf(var4))
            .setValue(HALF, DoubleBlockHalf.LOWER);
      } else {
         return null;
      }
   }

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      var1.setBlock(var2.above(), var3.setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   private DoorHingeSide getHinge(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      Direction var4 = var1.getHorizontalDirection();
      BlockPos var5 = var3.above();
      Direction var6 = var4.getCounterClockWise();
      BlockPos var7 = var3.relative(var6);
      BlockState var8 = var2.getBlockState(var7);
      BlockPos var9 = var5.relative(var6);
      BlockState var10 = var2.getBlockState(var9);
      Direction var11 = var4.getClockWise();
      BlockPos var12 = var3.relative(var11);
      BlockState var13 = var2.getBlockState(var12);
      BlockPos var14 = var5.relative(var11);
      BlockState var15 = var2.getBlockState(var14);
      int var16 = (var8.isCollisionShapeFullBlock(var2, var7) ? -1 : 0)
         + (var10.isCollisionShapeFullBlock(var2, var9) ? -1 : 0)
         + (var13.isCollisionShapeFullBlock(var2, var12) ? 1 : 0)
         + (var15.isCollisionShapeFullBlock(var2, var14) ? 1 : 0);
      boolean var17 = var8.is(this) && var8.getValue(HALF) == DoubleBlockHalf.LOWER;
      boolean var18 = var13.is(this) && var13.getValue(HALF) == DoubleBlockHalf.LOWER;
      if ((!var17 || var18) && var16 <= 0) {
         if ((!var18 || var17) && var16 >= 0) {
            int var19 = var4.getStepX();
            int var20 = var4.getStepZ();
            Vec3 var21 = var1.getClickLocation();
            double var22 = var21.x - (double)var3.getX();
            double var24 = var21.z - (double)var3.getZ();
            return (var19 >= 0 || !(var24 < 0.5)) && (var19 <= 0 || !(var24 > 0.5)) && (var20 >= 0 || !(var22 > 0.5)) && (var20 <= 0 || !(var22 < 0.5))
               ? DoorHingeSide.LEFT
               : DoorHingeSide.RIGHT;
         } else {
            return DoorHingeSide.LEFT;
         }
      } else {
         return DoorHingeSide.RIGHT;
      }
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (this.material == Material.METAL) {
         return InteractionResult.PASS;
      } else {
         var1 = var1.cycle(OPEN);
         var2.setBlock(var3, var1, 10);
         this.playSound(var4, var2, var3, var1.getValue(OPEN));
         var2.gameEvent(var4, this.isOpen(var1) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      }
   }

   public boolean isOpen(BlockState var1) {
      return var1.getValue(OPEN);
   }

   public void setOpen(@Nullable Entity var1, Level var2, BlockState var3, BlockPos var4, boolean var5) {
      if (var3.is(this) && var3.getValue(OPEN) != var5) {
         var2.setBlock(var4, var3.setValue(OPEN, Boolean.valueOf(var5)), 10);
         this.playSound(var1, var2, var4, var5);
         var2.gameEvent(var1, var5 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var4);
      }
   }

   @Override
   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3)
         || var2.hasNeighborSignal(var3.relative(var1.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
      if (!this.defaultBlockState().is(var4) && var7 != var1.getValue(POWERED)) {
         if (var7 != var1.getValue(OPEN)) {
            this.playSound(null, var2, var3, var7);
            var2.gameEvent(null, var7 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
         }

         var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(var7)).setValue(OPEN, Boolean.valueOf(var7)), 2);
      }
   }

   @Override
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      return var1.getValue(HALF) == DoubleBlockHalf.LOWER ? var5.isFaceSturdy(var2, var4, Direction.UP) : var5.is(this);
   }

   private void playSound(@Nullable Entity var1, Level var2, BlockPos var3, boolean var4) {
      var2.playSound(var1, var3, var4 ? this.type.doorOpen() : this.type.doorClose(), SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.1F + 0.9F);
   }

   @Override
   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.DESTROY;
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var2 == Mirror.NONE ? var1 : var1.rotate(var2.getRotation(var1.getValue(FACING))).cycle(HINGE);
   }

   @Override
   public long getSeed(BlockState var1, BlockPos var2) {
      return Mth.getSeed(var2.getX(), var2.below(var1.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), var2.getZ());
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HALF, FACING, OPEN, HINGE, POWERED);
   }

   public static boolean isWoodenDoor(Level var0, BlockPos var1) {
      return isWoodenDoor(var0.getBlockState(var1));
   }

   public static boolean isWoodenDoor(BlockState var0) {
      return var0.getBlock() instanceof DoorBlock && (var0.getMaterial() == Material.WOOD || var0.getMaterial() == Material.NETHER_WOOD);
   }
}
