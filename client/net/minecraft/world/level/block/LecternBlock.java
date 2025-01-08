package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LecternBlock extends BaseEntityBlock {
   public static final MapCodec<LecternBlock> CODEC = simpleCodec(LecternBlock::new);
   public static final EnumProperty<Direction> FACING;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty HAS_BOOK;
   private static final VoxelShape SHAPE_COLLISION;
   private static final Map<Direction, VoxelShape> SHAPES;
   private static final int PAGE_CHANGE_IMPULSE_TICKS = 2;

   public MapCodec<LecternBlock> codec() {
      return CODEC;
   }

   protected LecternBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(HAS_BOOK, false));
   }

   protected VoxelShape getOcclusionShape(BlockState var1) {
      return SHAPE_COLLISION;
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
      return (VoxelShape)SHAPES.get(var1.getValue(FACING));
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

   public static boolean tryPlaceBook(@Nullable LivingEntity var0, Level var1, BlockPos var2, BlockState var3, ItemStack var4) {
      if (!(Boolean)var3.getValue(HAS_BOOK)) {
         if (!var1.isClientSide) {
            placeBook(var0, var1, var2, var3, var4);
         }

         return true;
      } else {
         return false;
      }
   }

   private static void placeBook(@Nullable LivingEntity var0, Level var1, BlockPos var2, BlockState var3, ItemStack var4) {
      BlockEntity var5 = var1.getBlockEntity(var2);
      if (var5 instanceof LecternBlockEntity var6) {
         var6.setBook(var4.consumeAndReturn(1, var0));
         resetBookState(var0, var1, var2, var3, true);
         var1.playSound((Entity)null, (BlockPos)var2, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
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
      Orientation var3 = ExperimentalRedstoneUtils.initialOrientation(var0, ((Direction)var2.getValue(FACING)).getOpposite(), Direction.UP);
      var0.updateNeighborsAt(var1.below(), var2.getBlock(), var3);
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      changePowered(var2, var3, var1, false);
   }

   protected void affectNeighborsAfterRemoval(BlockState var1, ServerLevel var2, BlockPos var3, boolean var4) {
      if ((Boolean)var1.getValue(POWERED)) {
         updateBelow(var2, var3, var1);
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

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if ((Boolean)var2.getValue(HAS_BOOK)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else if (var1.is(ItemTags.LECTERN_BOOKS)) {
         return (InteractionResult)(tryPlaceBook(var5, var3, var4, var2, var1) ? InteractionResult.SUCCESS : InteractionResult.PASS);
      } else {
         return (InteractionResult)(var1.isEmpty() && var6 == InteractionHand.MAIN_HAND ? InteractionResult.PASS : InteractionResult.TRY_WITH_EMPTY_HAND);
      }
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if ((Boolean)var1.getValue(HAS_BOOK)) {
         if (!var2.isClientSide) {
            this.openScreen(var2, var3, var4);
         }

         return InteractionResult.SUCCESS;
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
      SHAPE_COLLISION = Shapes.or(Block.column(16.0, 0.0, 2.0), Block.column(8.0, 2.0, 14.0));
      SHAPES = Shapes.rotateHorizontal(Shapes.or(Block.boxZ(16.0, 10.0, 14.0, 1.0, 5.333333), Block.boxZ(16.0, 12.0, 16.0, 5.333333, 9.666667), Block.boxZ(16.0, 14.0, 18.0, 9.666667, 14.0), SHAPE_COLLISION));
   }
}
