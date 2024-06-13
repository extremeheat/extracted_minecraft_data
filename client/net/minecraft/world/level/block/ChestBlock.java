package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChestBlock extends AbstractChestBlock<ChestBlockEntity> implements SimpleWaterloggedBlock {
   public static final MapCodec<ChestBlock> CODEC = simpleCodec(var0 -> new ChestBlock(var0, () -> BlockEntityType.CHEST));
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final int EVENT_SET_OPEN_COUNT = 1;
   protected static final int AABB_OFFSET = 1;
   protected static final int AABB_HEIGHT = 14;
   protected static final VoxelShape NORTH_AABB = Block.box(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
   protected static final VoxelShape SOUTH_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
   protected static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
   protected static final VoxelShape EAST_AABB = Block.box(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
   protected static final VoxelShape AABB = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
   private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<Container>> CHEST_COMBINER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<Container>>() {
      public Optional<Container> acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
         return Optional.of(new CompoundContainer(var1, var2));
      }

      public Optional<Container> acceptSingle(ChestBlockEntity var1) {
         return Optional.of(var1);
      }

      public Optional<Container> acceptNone() {
         return Optional.empty();
      }
   };
   private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>>() {
      public Optional<MenuProvider> acceptDouble(final ChestBlockEntity var1, final ChestBlockEntity var2) {
         final CompoundContainer var3 = new CompoundContainer(var1, var2);
         return Optional.of(new MenuProvider() {
            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int var1x, Inventory var2x, Player var3x) {
               if (var1.canOpen(var3x) && var2.canOpen(var3x)) {
                  var1.unpackLootTable(var2x.player);
                  var2.unpackLootTable(var2x.player);
                  return ChestMenu.sixRows(var1x, var2x, var3);
               } else {
                  return null;
               }
            }

            @Override
            public Component getDisplayName() {
               if (var1.hasCustomName()) {
                  return var1.getDisplayName();
               } else {
                  return (Component)(var2.hasCustomName() ? var2.getDisplayName() : Component.translatable("container.chestDouble"));
               }
            }
         });
      }

      public Optional<MenuProvider> acceptSingle(ChestBlockEntity var1) {
         return Optional.of(var1);
      }

      public Optional<MenuProvider> acceptNone() {
         return Optional.empty();
      }
   };

   @Override
   public MapCodec<? extends ChestBlock> codec() {
      return CODEC;
   }

   protected ChestBlock(BlockBehaviour.Properties var1, Supplier<BlockEntityType<? extends ChestBlockEntity>> var2) {
      super(var1, var2);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, ChestType.SINGLE).setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   public static DoubleBlockCombiner.BlockType getBlockType(BlockState var0) {
      ChestType var1 = var0.getValue(TYPE);
      if (var1 == ChestType.SINGLE) {
         return DoubleBlockCombiner.BlockType.SINGLE;
      } else {
         return var1 == ChestType.RIGHT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
      }
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      if (var3.is(this) && var2.getAxis().isHorizontal()) {
         ChestType var7 = var3.getValue(TYPE);
         if (var1.getValue(TYPE) == ChestType.SINGLE
            && var7 != ChestType.SINGLE
            && var1.getValue(FACING) == var3.getValue(FACING)
            && getConnectedDirection(var3) == var2.getOpposite()) {
            return var1.setValue(TYPE, var7.getOpposite());
         }
      } else if (getConnectedDirection(var1) == var2) {
         return var1.setValue(TYPE, ChestType.SINGLE);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (var1.getValue(TYPE) == ChestType.SINGLE) {
         return AABB;
      } else {
         switch (getConnectedDirection(var1)) {
            case NORTH:
            default:
               return NORTH_AABB;
            case SOUTH:
               return SOUTH_AABB;
            case WEST:
               return WEST_AABB;
            case EAST:
               return EAST_AABB;
         }
      }
   }

   public static Direction getConnectedDirection(BlockState var0) {
      Direction var1 = var0.getValue(FACING);
      return var0.getValue(TYPE) == ChestType.LEFT ? var1.getClockWise() : var1.getCounterClockWise();
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      ChestType var2 = ChestType.SINGLE;
      Direction var3 = var1.getHorizontalDirection().getOpposite();
      FluidState var4 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var5 = var1.isSecondaryUseActive();
      Direction var6 = var1.getClickedFace();
      if (var6.getAxis().isHorizontal() && var5) {
         Direction var7 = this.candidatePartnerFacing(var1, var6.getOpposite());
         if (var7 != null && var7.getAxis() != var6.getAxis()) {
            var3 = var7;
            var2 = var7.getCounterClockWise() == var6.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (var2 == ChestType.SINGLE && !var5) {
         if (var3 == this.candidatePartnerFacing(var1, var3.getClockWise())) {
            var2 = ChestType.LEFT;
         } else if (var3 == this.candidatePartnerFacing(var1, var3.getCounterClockWise())) {
            var2 = ChestType.RIGHT;
         }
      }

      return this.defaultBlockState().setValue(FACING, var3).setValue(TYPE, var2).setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Nullable
   private Direction candidatePartnerFacing(BlockPlaceContext var1, Direction var2) {
      BlockState var3 = var1.getLevel().getBlockState(var1.getClickedPos().relative(var2));
      return var3.is(this) && var3.getValue(TYPE) == ChestType.SINGLE ? var3.getValue(FACING) : null;
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Containers.dropContentsOnDestroy(var1, var4, var2, var3);
      super.onRemove(var1, var2, var3, var4, var5);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         MenuProvider var6 = this.getMenuProvider(var1, var2, var3);
         if (var6 != null) {
            var4.openMenu(var6);
            var4.awardStat(this.getOpenChestStat());
            PiglinAi.angerNearbyPiglins(var4, true);
         }

         return InteractionResult.CONSUME;
      }
   }

   protected Stat<ResourceLocation> getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
   }

   public BlockEntityType<? extends ChestBlockEntity> blockEntityType() {
      return this.blockEntityType.get();
   }

   @Nullable
   public static Container getContainer(ChestBlock var0, BlockState var1, Level var2, BlockPos var3, boolean var4) {
      return var0.combine(var1, var2, var3, var4).apply(CHEST_COMBINER).orElse(null);
   }

   @Override
   public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState var1, Level var2, BlockPos var3, boolean var4) {
      BiPredicate var5;
      if (var4) {
         var5 = (var0, var1x) -> false;
      } else {
         var5 = ChestBlock::isChestBlockedAt;
      }

      return DoubleBlockCombiner.combineWithNeigbour(
         this.blockEntityType.get(), ChestBlock::getBlockType, ChestBlock::getConnectedDirection, FACING, var1, var2, var3, var5
      );
   }

   @Nullable
   @Override
   protected MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return this.combine(var1, var2, var3, false).apply(MENU_PROVIDER_COMBINER).orElse(null);
   }

   public static DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction> opennessCombiner(final LidBlockEntity var0) {
      return new DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction>() {
         public Float2FloatFunction acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
            return var2x -> Math.max(var1.getOpenNess(var2x), var2.getOpenNess(var2x));
         }

         public Float2FloatFunction acceptSingle(ChestBlockEntity var1) {
            return var1::getOpenNess;
         }

         public Float2FloatFunction acceptNone() {
            return var0::getOpenNess;
         }
      };
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new ChestBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? createTickerHelper(var3, this.blockEntityType(), ChestBlockEntity::lidAnimateTick) : null;
   }

   public static boolean isChestBlockedAt(LevelAccessor var0, BlockPos var1) {
      return isBlockedChestByBlock(var0, var1) || isCatSittingOnChest(var0, var1);
   }

   private static boolean isBlockedChestByBlock(BlockGetter var0, BlockPos var1) {
      BlockPos var2 = var1.above();
      return var0.getBlockState(var2).isRedstoneConductor(var0, var2);
   }

   private static boolean isCatSittingOnChest(LevelAccessor var0, BlockPos var1) {
      List var2 = var0.getEntitiesOfClass(
         Cat.class,
         new AABB(
            (double)var1.getX(),
            (double)(var1.getY() + 1),
            (double)var1.getZ(),
            (double)(var1.getX() + 1),
            (double)(var1.getY() + 2),
            (double)(var1.getZ() + 1)
         )
      );
      if (!var2.isEmpty()) {
         for (Cat var4 : var2) {
            if (var4.isInSittingPose()) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(this, var1, var2, var3, false));
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
      var1.add(FACING, TYPE, WATERLOGGED);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof ChestBlockEntity) {
         ((ChestBlockEntity)var5).recheckOpen();
      }
   }
}
