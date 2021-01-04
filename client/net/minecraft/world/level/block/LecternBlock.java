package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LecternBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty HAS_BOOK;
   public static final VoxelShape SHAPE_BASE;
   public static final VoxelShape SHAPE_POST;
   public static final VoxelShape SHAPE_COMMON;
   public static final VoxelShape SHAPE_TOP_PLATE;
   public static final VoxelShape SHAPE_COLLISION;
   public static final VoxelShape SHAPE_WEST;
   public static final VoxelShape SHAPE_NORTH;
   public static final VoxelShape SHAPE_EAST;
   public static final VoxelShape SHAPE_SOUTH;

   protected LecternBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(HAS_BOOK, false));
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return SHAPE_COMMON;
   }

   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_COLLISION;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch((Direction)var1.getValue(FACING)) {
      case NORTH:
         return SHAPE_NORTH;
      case SOUTH:
         return SHAPE_SOUTH;
      case EAST:
         return SHAPE_EAST;
      case WEST:
         return SHAPE_WEST;
      default:
         return SHAPE_COMMON;
      }
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, HAS_BOOK);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new LecternBlockEntity();
   }

   public static boolean tryPlaceBook(Level var0, BlockPos var1, BlockState var2, ItemStack var3) {
      if (!(Boolean)var2.getValue(HAS_BOOK)) {
         if (!var0.isClientSide) {
            placeBook(var0, var1, var2, var3);
         }

         return true;
      } else {
         return false;
      }
   }

   private static void placeBook(Level var0, BlockPos var1, BlockState var2, ItemStack var3) {
      BlockEntity var4 = var0.getBlockEntity(var1);
      if (var4 instanceof LecternBlockEntity) {
         LecternBlockEntity var5 = (LecternBlockEntity)var4;
         var5.setBook(var3.split(1));
         resetBookState(var0, var1, var2, true);
         var0.playSound((Player)null, (BlockPos)var1, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   public static void resetBookState(Level var0, BlockPos var1, BlockState var2, boolean var3) {
      var0.setBlock(var1, (BlockState)((BlockState)var2.setValue(POWERED, false)).setValue(HAS_BOOK, var3), 3);
      updateBelow(var0, var1, var2);
   }

   public static void signalPageChange(Level var0, BlockPos var1, BlockState var2) {
      changePowered(var0, var1, var2, true);
      var0.getBlockTicks().scheduleTick(var1, var2.getBlock(), 2);
      var0.levelEvent(1043, var1, 0);
   }

   private static void changePowered(Level var0, BlockPos var1, BlockState var2, boolean var3) {
      var0.setBlock(var1, (BlockState)var2.setValue(POWERED, var3), 3);
      updateBelow(var0, var1, var2);
   }

   private static void updateBelow(Level var0, BlockPos var1, BlockState var2) {
      var0.updateNeighborsAt(var1.below(), var2.getBlock());
   }

   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (!var2.isClientSide) {
         changePowered(var2, var3, var1, false);
      }
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var1.getBlock() != var4.getBlock()) {
         if ((Boolean)var1.getValue(HAS_BOOK)) {
            this.popBook(var1, var2, var3);
         }

         if ((Boolean)var1.getValue(POWERED)) {
            var2.updateNeighborsAt(var3.below(), this);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   private void popBook(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      if (var4 instanceof LecternBlockEntity) {
         LecternBlockEntity var5 = (LecternBlockEntity)var4;
         Direction var6 = (Direction)var1.getValue(FACING);
         ItemStack var7 = var5.getBook().copy();
         float var8 = 0.25F * (float)var6.getStepX();
         float var9 = 0.25F * (float)var6.getStepZ();
         ItemEntity var10 = new ItemEntity(var2, (double)var3.getX() + 0.5D + (double)var8, (double)(var3.getY() + 1), (double)var3.getZ() + 0.5D + (double)var9, var7);
         var10.setDefaultPickUpDelay();
         var2.addFreshEntity(var10);
         var5.clearContent();
      }

   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var4 == Direction.UP && (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      if ((Boolean)var1.getValue(HAS_BOOK)) {
         BlockEntity var4 = var2.getBlockEntity(var3);
         if (var4 instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)var4).getRedstoneSignal();
         }
      }

      return 0;
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if ((Boolean)var1.getValue(HAS_BOOK)) {
         if (!var2.isClientSide) {
            this.openScreen(var2, var3, var4);
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return !(Boolean)var1.getValue(HAS_BOOK) ? null : super.getMenuProvider(var1, var2, var3);
   }

   private void openScreen(Level var1, BlockPos var2, Player var3) {
      BlockEntity var4 = var1.getBlockEntity(var2);
      if (var4 instanceof LecternBlockEntity) {
         var3.openMenu((LecternBlockEntity)var4);
         var3.awardStat(Stats.INTERACT_WITH_LECTERN);
      }

   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      POWERED = BlockStateProperties.POWERED;
      HAS_BOOK = BlockStateProperties.HAS_BOOK;
      SHAPE_BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
      SHAPE_POST = Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
      SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_POST);
      SHAPE_TOP_PLATE = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
      SHAPE_COLLISION = Shapes.or(SHAPE_COMMON, SHAPE_TOP_PLATE);
      SHAPE_WEST = Shapes.or(Block.box(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.box(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.box(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), SHAPE_COMMON);
      SHAPE_NORTH = Shapes.or(Block.box(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.box(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.box(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), SHAPE_COMMON);
      SHAPE_EAST = Shapes.or(Block.box(15.0D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D), Block.box(10.666667D, 12.0D, 0.0D, 6.333333D, 16.0D, 16.0D), Block.box(6.333333D, 14.0D, 0.0D, 2.0D, 18.0D, 16.0D), SHAPE_COMMON);
      SHAPE_SOUTH = Shapes.or(Block.box(0.0D, 10.0D, 15.0D, 16.0D, 14.0D, 10.666667D), Block.box(0.0D, 12.0D, 10.666667D, 16.0D, 16.0D, 6.333333D), Block.box(0.0D, 14.0D, 6.333333D, 16.0D, 18.0D, 2.0D), SHAPE_COMMON);
   }
}
