package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChestBlock extends AbstractChestBlock<ChestBlockEntity> implements SimpleWaterloggedBlock {
   public static final MapCodec<ChestBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("open_sound").forGetter(ChestBlock::getOpenChestSound), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("close_sound").forGetter(ChestBlock::getCloseChestSound), propertiesCodec()).apply(var0, (var0x, var1, var2) -> new ChestBlock(() -> BlockEntityType.CHEST, var0x, var1, var2)));
   public static final EnumProperty<Direction> FACING;
   public static final EnumProperty<ChestType> TYPE;
   public static final BooleanProperty WATERLOGGED;
   public static final int EVENT_SET_OPEN_COUNT = 1;
   private static final VoxelShape SHAPE;
   private static final Map<Direction, VoxelShape> HALF_SHAPES;
   private final SoundEvent openSound;
   private final SoundEvent closeSound;
   private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<Container>> CHEST_COMBINER;
   private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER;

   public MapCodec<? extends ChestBlock> codec() {
      return CODEC;
   }

   protected ChestBlock(Supplier<BlockEntityType<? extends ChestBlockEntity>> var1, SoundEvent var2, SoundEvent var3, BlockBehaviour.Properties var4) {
      super(var4, var1);
      this.openSound = var2;
      this.closeSound = var3;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, ChestType.SINGLE)).setValue(WATERLOGGED, false));
   }

   public static DoubleBlockCombiner.BlockType getBlockType(BlockState var0) {
      ChestType var1 = (ChestType)var0.getValue(TYPE);
      if (var1 == ChestType.SINGLE) {
         return DoubleBlockCombiner.BlockType.SINGLE;
      } else {
         return var1 == ChestType.RIGHT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
      }
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      if (this.chestCanConnectTo(var7) && var5.getAxis().isHorizontal()) {
         ChestType var9 = (ChestType)var7.getValue(TYPE);
         if (var1.getValue(TYPE) == ChestType.SINGLE && var9 != ChestType.SINGLE && var1.getValue(FACING) == var7.getValue(FACING) && getConnectedDirection(var7) == var5.getOpposite()) {
            return (BlockState)var1.setValue(TYPE, var9.getOpposite());
         }
      } else if (getConnectedDirection(var1) == var5) {
         return (BlockState)var1.setValue(TYPE, ChestType.SINGLE);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public boolean chestCanConnectTo(BlockState var1) {
      return var1.is(this);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      VoxelShape var10000;
      switch ((ChestType)var1.getValue(TYPE)) {
         case SINGLE:
            var10000 = SHAPE;
            break;
         case LEFT:
         case RIGHT:
            var10000 = (VoxelShape)HALF_SHAPES.get(getConnectedDirection(var1));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static Direction getConnectedDirection(BlockState var0) {
      Direction var1 = (Direction)var0.getValue(FACING);
      return var0.getValue(TYPE) == ChestType.LEFT ? var1.getClockWise() : var1.getCounterClockWise();
   }

   public static BlockPos getConnectedBlockPos(BlockPos var0, BlockState var1) {
      Direction var2 = getConnectedDirection(var1);
      return var0.relative(var2);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      ChestType var2 = ChestType.SINGLE;
      Direction var3 = var1.getHorizontalDirection().getOpposite();
      FluidState var4 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var5 = var1.isSecondaryUseActive();
      Direction var6 = var1.getClickedFace();
      if (var6.getAxis().isHorizontal() && var5) {
         Direction var7 = this.candidatePartnerFacing(var1.getLevel(), var1.getClickedPos(), var6.getOpposite());
         if (var7 != null && var7.getAxis() != var6.getAxis()) {
            var3 = var7;
            var2 = var7.getCounterClockWise() == var6.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (var2 == ChestType.SINGLE && !var5) {
         var2 = this.getChestType(var1.getLevel(), var1.getClickedPos(), var3);
      }

      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var3)).setValue(TYPE, var2)).setValue(WATERLOGGED, var4.getType() == Fluids.WATER);
   }

   protected ChestType getChestType(Level var1, BlockPos var2, Direction var3) {
      if (var3 == this.candidatePartnerFacing(var1, var2, var3.getClockWise())) {
         return ChestType.LEFT;
      } else {
         return var3 == this.candidatePartnerFacing(var1, var2, var3.getCounterClockWise()) ? ChestType.RIGHT : ChestType.SINGLE;
      }
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Nullable
   private Direction candidatePartnerFacing(Level var1, BlockPos var2, Direction var3) {
      BlockState var4 = var1.getBlockState(var2.relative(var3));
      return this.chestCanConnectTo(var4) && var4.getValue(TYPE) == ChestType.SINGLE ? (Direction)var4.getValue(FACING) : null;
   }

   protected void affectNeighborsAfterRemoval(BlockState var1, ServerLevel var2, BlockPos var3, boolean var4) {
      Containers.updateNeighboursAfterDestroy(var1, var2, var3);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2 instanceof ServerLevel var6) {
         MenuProvider var7 = this.getMenuProvider(var1, var2, var3);
         if (var7 != null) {
            var4.openMenu(var7);
            var4.awardStat(this.getOpenChestStat());
            PiglinAi.angerNearbyPiglins(var6, var4, true);
         }
      }

      return InteractionResult.SUCCESS;
   }

   protected Stat<ResourceLocation> getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
   }

   public BlockEntityType<? extends ChestBlockEntity> blockEntityType() {
      return (BlockEntityType)this.blockEntityType.get();
   }

   @Nullable
   public static Container getContainer(ChestBlock var0, BlockState var1, Level var2, BlockPos var3, boolean var4) {
      return (Container)((Optional)var0.combine(var1, var2, var3, var4).apply(CHEST_COMBINER)).orElse((Object)null);
   }

   public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState var1, Level var2, BlockPos var3, boolean var4) {
      BiPredicate var5;
      if (var4) {
         var5 = (var0, var1x) -> false;
      } else {
         var5 = ChestBlock::isChestBlockedAt;
      }

      return DoubleBlockCombiner.<ChestBlockEntity>combineWithNeigbour((BlockEntityType)this.blockEntityType.get(), ChestBlock::getBlockType, ChestBlock::getConnectedDirection, FACING, var1, var2, var3, var5);
   }

   @Nullable
   protected MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return (MenuProvider)((Optional)this.combine(var1, var2, var3, false).apply(MENU_PROVIDER_COMBINER)).orElse((Object)null);
   }

   public static DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction> opennessCombiner(final LidBlockEntity var0) {
      return new DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction>() {
         public Float2FloatFunction acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
            return (var2x) -> Math.max(var1.getOpenNess(var2x), var2.getOpenNess(var2x));
         }

         public Float2FloatFunction acceptSingle(ChestBlockEntity var1) {
            Objects.requireNonNull(var1);
            return var1::getOpenNess;
         }

         public Float2FloatFunction acceptNone() {
            LidBlockEntity var10000 = var0;
            Objects.requireNonNull(var10000);
            return var10000::getOpenNess;
         }

         // $FF: synthetic method
         public Object acceptNone() {
            return this.acceptNone();
         }
      };
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new ChestBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide() ? createTickerHelper(var3, this.blockEntityType(), ChestBlockEntity::lidAnimateTick) : null;
   }

   public static boolean isChestBlockedAt(LevelAccessor var0, BlockPos var1) {
      return isBlockedChestByBlock(var0, var1) || isCatSittingOnChest(var0, var1);
   }

   private static boolean isBlockedChestByBlock(BlockGetter var0, BlockPos var1) {
      BlockPos var2 = var1.above();
      return var0.getBlockState(var2).isRedstoneConductor(var0, var2);
   }

   private static boolean isCatSittingOnChest(LevelAccessor var0, BlockPos var1) {
      List var2 = var0.getEntitiesOfClass(Cat.class, new AABB((double)var1.getX(), (double)(var1.getY() + 1), (double)var1.getZ(), (double)(var1.getX() + 1), (double)(var1.getY() + 2), (double)(var1.getZ() + 1)));
      if (!var2.isEmpty()) {
         for(Cat var4 : var2) {
            if (var4.isInSittingPose()) {
               return true;
            }
         }
      }

      return false;
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3, Direction var4) {
      return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(this, var1, var2, var3, false));
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, TYPE, WATERLOGGED);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof ChestBlockEntity) {
         ((ChestBlockEntity)var5).recheckOpen();
      }

   }

   public SoundEvent getOpenChestSound() {
      return this.openSound;
   }

   public SoundEvent getCloseChestSound() {
      return this.closeSound;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      TYPE = BlockStateProperties.CHEST_TYPE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.column(14.0, 0.0, 14.0);
      HALF_SHAPES = Shapes.rotateHorizontal(Block.boxZ(14.0, 0.0, 14.0, 0.0, 15.0));
      CHEST_COMBINER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<Container>>() {
         public Optional<Container> acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
            return Optional.of(new CompoundContainer(var1, var2));
         }

         public Optional<Container> acceptSingle(ChestBlockEntity var1) {
            return Optional.of(var1);
         }

         public Optional<Container> acceptNone() {
            return Optional.empty();
         }

         // $FF: synthetic method
         public Object acceptNone() {
            return this.acceptNone();
         }
      };
      MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>>() {
         public Optional<MenuProvider> acceptDouble(final ChestBlockEntity var1, final ChestBlockEntity var2) {
            final CompoundContainer var3 = new CompoundContainer(var1, var2);
            return Optional.of(new MenuProvider() {
               @Nullable
               public AbstractContainerMenu createMenu(int var1x, Inventory var2x, Player var3x) {
                  if (var1.canOpen(var3x) && var2.canOpen(var3x)) {
                     var1.unpackLootTable(var2x.player);
                     var2.unpackLootTable(var2x.player);
                     return ChestMenu.sixRows(var1x, var2x, var3);
                  } else {
                     return null;
                  }
               }

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

         // $FF: synthetic method
         public Object acceptNone() {
            return this.acceptNone();
         }
      };
   }
}
