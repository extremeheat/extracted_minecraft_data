package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SculkSensorBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final int ACTIVE_TICKS = 40;
   public static final int COOLDOWN_TICKS = 1;
   public static final EnumProperty<SculkSensorPhase> PHASE = BlockStateProperties.SCULK_SENSOR_PHASE;
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   private final int listenerRange;

   public SculkSensorBlock(BlockBehaviour.Properties var1, int var2) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(PHASE, SculkSensorPhase.INACTIVE)
            .setValue(POWER, Integer.valueOf(0))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
      this.listenerRange = var2;
   }

   public int getListenerRange() {
      return this.listenerRange;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockPos var2 = var1.getClickedPos();
      FluidState var3 = var1.getLevel().getFluidState(var2);
      return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(var3.getType() == Fluids.WATER));
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (getPhase(var1) != SculkSensorPhase.ACTIVE) {
         if (getPhase(var1) == SculkSensorPhase.COOLDOWN) {
            var2.setBlock(var3, var1.setValue(PHASE, SculkSensorPhase.INACTIVE), 3);
         }
      } else {
         deactivate(var2, var3, var1);
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
      if (!var1.isClientSide() && canActivate(var3) && var4.getType() != EntityType.WARDEN) {
         BlockEntity var5 = var1.getBlockEntity(var2);
         if (var5 instanceof SculkSensorBlockEntity var6 && var1 instanceof ServerLevel var7) {
            var6.getListener().forceGameEvent((ServerLevel)var7, GameEvent.STEP, GameEvent.Context.of(var4), var4.position());
         }
      }

      super.stepOn(var1, var2, var3, var4);
   }

   @Override
   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var2.isClientSide() && !var1.is(var4.getBlock())) {
         if (var1.getValue(POWER) > 0 && !var2.getBlockTicks().hasScheduledTick(var3, this)) {
            var2.setBlock(var3, var1.setValue(POWER, Integer.valueOf(0)), 18);
         }

         var2.scheduleTick(new BlockPos(var3), var1.getBlock(), 1);
      }
   }

   @Override
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         if (getPhase(var1) == SculkSensorPhase.ACTIVE) {
            updateNeighbours(var2, var3);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   private static void updateNeighbours(Level var0, BlockPos var1) {
      var0.updateNeighborsAt(var1, Blocks.SCULK_SENSOR);
      var0.updateNeighborsAt(var1.relative(Direction.UP.getOpposite()), Blocks.SCULK_SENSOR);
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new SculkSensorBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> GameEventListener getListener(ServerLevel var1, T var2) {
      return var2 instanceof SculkSensorBlockEntity ? ((SculkSensorBlockEntity)var2).getListener() : null;
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return !var1.isClientSide ? createTickerHelper(var3, BlockEntityType.SCULK_SENSOR, (var0, var1x, var2x, var3x) -> var3x.getListener().tick(var0)) : null;
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   @Override
   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getValue(POWER);
   }

   public static SculkSensorPhase getPhase(BlockState var0) {
      return var0.getValue(PHASE);
   }

   public static boolean canActivate(BlockState var0) {
      return getPhase(var0) == SculkSensorPhase.INACTIVE;
   }

   public static void deactivate(Level var0, BlockPos var1, BlockState var2) {
      var0.setBlock(var1, var2.setValue(PHASE, SculkSensorPhase.COOLDOWN).setValue(POWER, Integer.valueOf(0)), 3);
      var0.scheduleTick(var1, var2.getBlock(), 1);
      if (!var2.getValue(WATERLOGGED)) {
         var0.playSound(null, var1, SoundEvents.SCULK_CLICKING_STOP, SoundSource.BLOCKS, 1.0F, var0.random.nextFloat() * 0.2F + 0.8F);
      }

      updateNeighbours(var0, var1);
   }

   public static void activate(@Nullable Entity var0, Level var1, BlockPos var2, BlockState var3, int var4) {
      var1.setBlock(var2, var3.setValue(PHASE, SculkSensorPhase.ACTIVE).setValue(POWER, Integer.valueOf(var4)), 3);
      var1.scheduleTick(var2, var3.getBlock(), 40);
      updateNeighbours(var1, var2);
      var1.gameEvent(var0, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, var2);
      if (!var3.getValue(WATERLOGGED)) {
         var1.playSound(
            null,
            (double)var2.getX() + 0.5,
            (double)var2.getY() + 0.5,
            (double)var2.getZ() + 0.5,
            SoundEvents.SCULK_CLICKING,
            SoundSource.BLOCKS,
            1.0F,
            var1.random.nextFloat() * 0.2F + 0.8F
         );
      }
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (getPhase(var1) == SculkSensorPhase.ACTIVE) {
         Direction var5 = Direction.getRandom(var4);
         if (var5 != Direction.UP && var5 != Direction.DOWN) {
            double var6 = (double)var3.getX() + 0.5 + (var5.getStepX() == 0 ? 0.5 - var4.nextDouble() : (double)var5.getStepX() * 0.6);
            double var8 = (double)var3.getY() + 0.25;
            double var10 = (double)var3.getZ() + 0.5 + (var5.getStepZ() == 0 ? 0.5 - var4.nextDouble() : (double)var5.getStepZ() * 0.6);
            double var12 = (double)var4.nextFloat() * 0.04;
            var2.addParticle(DustColorTransitionOptions.SCULK_TO_REDSTONE, var6, var8, var10, 0.0, var12, 0.0);
         }
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(PHASE, POWER, WATERLOGGED);
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      if (var4 instanceof SculkSensorBlockEntity var5) {
         return getPhase(var1) == SculkSensorPhase.ACTIVE ? var5.getLastVibrationFrequency() : 0;
      } else {
         return 0;
      }
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   @Override
   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   @Override
   public void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
      super.spawnAfterBreak(var1, var2, var3, var4, var5);
      if (var5) {
         this.tryDropExperience(var2, var3, var4, ConstantInt.of(5));
      }
   }
}
