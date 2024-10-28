package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LecternBlock extends BaseEntityBlock {
   public static final MapCodec<LecternBlock> CODEC = simpleCodec(LecternBlock::new);
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
   private static final int PAGE_CHANGE_IMPULSE_TICKS = 2;

   public MapCodec<LecternBlock> codec() {
      return CODEC;
   }

   protected LecternBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(HAS_BOOK, false));
   }

   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   protected VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return SHAPE_COMMON;
   }

   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      ItemStack var3 = var1.getItemInHand();
      Player var4 = var1.getPlayer();
      boolean var5 = false;
      if (!var2.isClientSide && var4 != null && var4.canUseGameMasterBlocks()) {
         CustomData var6 = (CustomData)var3.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
         if (var6.contains("Book")) {
            var5 = true;
         }
      }

      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite())).setValue(HAS_BOOK, var5);
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_COLLISION;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch ((Direction)var1.getValue(FACING)) {
         case NORTH -> {
            return SHAPE_NORTH;
         }
         case SOUTH -> {
            return SHAPE_SOUTH;
         }
         case EAST -> {
            return SHAPE_EAST;
         }
         case WEST -> {
            return SHAPE_WEST;
         }
         default -> {
            return SHAPE_COMMON;
         }
      }
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, HAS_BOOK);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new LecternBlockEntity(var1, var2);
   }

   public static boolean tryPlaceBook(@Nullable Entity var0, Level var1, BlockPos var2, BlockState var3, ItemStack var4) {
      if (!(Boolean)var3.getValue(HAS_BOOK)) {
         if (!var1.isClientSide) {
            placeBook(var0, var1, var2, var3, var4);
         }

         return true;
      } else {
         return false;
      }
   }

   private static void placeBook(@Nullable Entity var0, Level var1, BlockPos var2, BlockState var3, ItemStack var4) {
      BlockEntity var5 = var1.getBlockEntity(var2);
      if (var5 instanceof LecternBlockEntity var6) {
         var6.setBook(var4.split(1));
         resetBookState(var0, var1, var2, var3, true);
         var1.playSound((Player)null, (BlockPos)var2, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   public static void resetBookState(@Nullable Entity var0, Level var1, BlockPos var2, BlockState var3, boolean var4) {
      BlockState var5 = (BlockState)((BlockState)var3.setValue(POWERED, false)).setValue(HAS_BOOK, var4);
      var1.setBlock(var2, var5, 3);
      var1.gameEvent(GameEvent.BLOCK_CHANGE, var2, GameEvent.Context.of(var0, var5));
      updateBelow(var1, var2, var3);
   }

   public static void signalPageChange(Level var0, BlockPos var1, BlockState var2) {
      changePowered(var0, var1, var2, true);
      var0.scheduleTick(var1, var2.getBlock(), 2);
      var0.levelEvent(1043, var1, 0);
   }

   private static void changePowered(Level var0, BlockPos var1, BlockState var2, boolean var3) {
      var0.setBlock(var1, (BlockState)var2.setValue(POWERED, var3), 3);
      updateBelow(var0, var1, var2);
   }

   private static void updateBelow(Level var0, BlockPos var1, BlockState var2) {
      var0.updateNeighborsAt(var1.below(), var2.getBlock());
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      changePowered(var2, var3, var1, false);
   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
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
      if (var4 instanceof LecternBlockEntity var5) {
         Direction var6 = (Direction)var1.getValue(FACING);
         ItemStack var7 = var5.getBook().copy();
         float var8 = 0.25F * (float)var6.getStepX();
         float var9 = 0.25F * (float)var6.getStepZ();
         ItemEntity var10 = new ItemEntity(var2, (double)var3.getX() + 0.5 + (double)var8, (double)(var3.getY() + 1), (double)var3.getZ() + 0.5 + (double)var9, var7);
         var10.setDefaultPickUpDelay();
         var2.addFreshEntity(var10);
         var5.clearContent();
      }

   }

   protected boolean isSignalSource(BlockState var1) {
      return true;
   }

   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   protected int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var4 == Direction.UP && (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      if ((Boolean)var1.getValue(HAS_BOOK)) {
         BlockEntity var4 = var2.getBlockEntity(var3);
         if (var4 instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)var4).getRedstoneSignal();
         }
      }

      return 0;
   }

   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if ((Boolean)var2.getValue(HAS_BOOK)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else if (var1.is(ItemTags.LECTERN_BOOKS)) {
         return tryPlaceBook(var5, var3, var4, var2, var1) ? ItemInteractionResult.sidedSuccess(var3.isClientSide) : ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      } else {
         return var1.isEmpty() && var6 == InteractionHand.MAIN_HAND ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if ((Boolean)var1.getValue(HAS_BOOK)) {
         if (!var2.isClientSide) {
            this.openScreen(var2, var3, var4);
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.CONSUME;
      }
   }

   @Nullable
   protected MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return !(Boolean)var1.getValue(HAS_BOOK) ? null : super.getMenuProvider(var1, var2, var3);
   }

   private void openScreen(Level var1, BlockPos var2, Player var3) {
      BlockEntity var4 = var1.getBlockEntity(var2);
      if (var4 instanceof LecternBlockEntity) {
         var3.openMenu((LecternBlockEntity)var4);
         var3.awardStat(Stats.INTERACT_WITH_LECTERN);
      }

   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      POWERED = BlockStateProperties.POWERED;
      HAS_BOOK = BlockStateProperties.HAS_BOOK;
      SHAPE_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
      SHAPE_POST = Block.box(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
      SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_POST);
      SHAPE_TOP_PLATE = Block.box(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
      SHAPE_COLLISION = Shapes.or(SHAPE_COMMON, SHAPE_TOP_PLATE);
      SHAPE_WEST = Shapes.or(Block.box(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0), Block.box(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0), Block.box(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0), SHAPE_COMMON);
      SHAPE_NORTH = Shapes.or(Block.box(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333), Block.box(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667), Block.box(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0), SHAPE_COMMON);
      SHAPE_EAST = Shapes.or(Block.box(10.666667, 10.0, 0.0, 15.0, 14.0, 16.0), Block.box(6.333333, 12.0, 0.0, 10.666667, 16.0, 16.0), Block.box(2.0, 14.0, 0.0, 6.333333, 18.0, 16.0), SHAPE_COMMON);
      SHAPE_SOUTH = Shapes.or(Block.box(0.0, 10.0, 10.666667, 16.0, 14.0, 15.0), Block.box(0.0, 12.0, 6.333333, 16.0, 16.0, 10.666667), Block.box(0.0, 14.0, 2.0, 16.0, 18.0, 6.333333), SHAPE_COMMON);
   }
}
