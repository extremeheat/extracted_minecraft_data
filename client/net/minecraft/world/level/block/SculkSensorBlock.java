package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.Util;
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
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SculkSensorBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final int ACTIVE_TICKS = 40;
   public static final int COOLDOWN_TICKS = 1;
   public static final Object2IntMap<GameEvent> VIBRATION_FREQUENCY_FOR_EVENT = Object2IntMaps.unmodifiable((Object2IntMap)Util.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.put(GameEvent.STEP, 1);
      var0.put(GameEvent.FLAP, 2);
      var0.put(GameEvent.SWIM, 3);
      var0.put(GameEvent.ELYTRA_GLIDE, 4);
      var0.put(GameEvent.HIT_GROUND, 5);
      var0.put(GameEvent.TELEPORT, 5);
      var0.put(GameEvent.SPLASH, 6);
      var0.put(GameEvent.ENTITY_SHAKE, 6);
      var0.put(GameEvent.BLOCK_CHANGE, 6);
      var0.put(GameEvent.NOTE_BLOCK_PLAY, 6);
      var0.put(GameEvent.PROJECTILE_SHOOT, 7);
      var0.put(GameEvent.DRINK, 7);
      var0.put(GameEvent.PRIME_FUSE, 7);
      var0.put(GameEvent.PROJECTILE_LAND, 8);
      var0.put(GameEvent.EAT, 8);
      var0.put(GameEvent.ENTITY_INTERACT, 8);
      var0.put(GameEvent.ENTITY_DAMAGE, 8);
      var0.put(GameEvent.EQUIP, 9);
      var0.put(GameEvent.SHEAR, 9);
      var0.put(GameEvent.ENTITY_ROAR, 9);
      var0.put(GameEvent.BLOCK_CLOSE, 10);
      var0.put(GameEvent.BLOCK_DEACTIVATE, 10);
      var0.put(GameEvent.BLOCK_DETACH, 10);
      var0.put(GameEvent.DISPENSE_FAIL, 10);
      var0.put(GameEvent.BLOCK_OPEN, 11);
      var0.put(GameEvent.BLOCK_ACTIVATE, 11);
      var0.put(GameEvent.BLOCK_ATTACH, 11);
      var0.put(GameEvent.ENTITY_PLACE, 12);
      var0.put(GameEvent.BLOCK_PLACE, 12);
      var0.put(GameEvent.FLUID_PLACE, 12);
      var0.put(GameEvent.ENTITY_DIE, 13);
      var0.put(GameEvent.BLOCK_DESTROY, 13);
      var0.put(GameEvent.FLUID_PICKUP, 13);
      var0.put(GameEvent.ITEM_INTERACT_FINISH, 14);
      var0.put(GameEvent.CONTAINER_CLOSE, 14);
      var0.put(GameEvent.PISTON_CONTRACT, 14);
      var0.put(GameEvent.PISTON_EXTEND, 15);
      var0.put(GameEvent.CONTAINER_OPEN, 15);
      var0.put(GameEvent.ITEM_INTERACT_START, 15);
      var0.put(GameEvent.EXPLODE, 15);
      var0.put(GameEvent.LIGHTNING_STRIKE, 15);
      var0.put(GameEvent.INSTRUMENT_PLAY, 15);
   }));
   public static final EnumProperty<SculkSensorPhase> PHASE;
   public static final IntegerProperty POWER;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape SHAPE;
   private final int listenerRange;

   public SculkSensorBlock(BlockBehaviour.Properties var1, int var2) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PHASE, SculkSensorPhase.INACTIVE)).setValue(POWER, 0)).setValue(WATERLOGGED, false));
      this.listenerRange = var2;
   }

   public int getListenerRange() {
      return this.listenerRange;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockPos var2 = var1.getClickedPos();
      FluidState var3 = var1.getLevel().getFluidState(var2);
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, var3.getType() == Fluids.WATER);
   }

   public FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (getPhase(var1) != SculkSensorPhase.ACTIVE) {
         if (getPhase(var1) == SculkSensorPhase.COOLDOWN) {
            var2.setBlock(var3, (BlockState)var1.setValue(PHASE, SculkSensorPhase.INACTIVE), 3);
         }

      } else {
         deactivate(var2, var3, var1);
      }
   }

   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
      if (!var1.isClientSide() && canActivate(var3) && var4.getType() != EntityType.WARDEN) {
         BlockEntity var5 = var1.getBlockEntity(var2);
         if (var5 instanceof SculkSensorBlockEntity) {
            SculkSensorBlockEntity var6 = (SculkSensorBlockEntity)var5;
            var6.setLastVibrationFrequency(VIBRATION_FREQUENCY_FOR_EVENT.get(GameEvent.STEP));
         }

         activate(var4, var1, var2, var3, 15);
      }

      super.stepOn(var1, var2, var3, var4);
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var2.isClientSide() && !var1.is(var4.getBlock())) {
         if ((Integer)var1.getValue(POWER) > 0 && !var2.getBlockTicks().hasScheduledTick(var3, this)) {
            var2.setBlock(var3, (BlockState)var1.setValue(POWER, 0), 18);
         }

         var2.scheduleTick(new BlockPos(var3), var1.getBlock(), 1);
      }
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         if (getPhase(var1) == SculkSensorPhase.ACTIVE) {
            updateNeighbours(var2, var3);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   private static void updateNeighbours(Level var0, BlockPos var1) {
      var0.updateNeighborsAt(var1, Blocks.SCULK_SENSOR);
      var0.updateNeighborsAt(var1.relative(Direction.UP.getOpposite()), Blocks.SCULK_SENSOR);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new SculkSensorBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> GameEventListener getListener(ServerLevel var1, T var2) {
      return var2 instanceof SculkSensorBlockEntity ? ((SculkSensorBlockEntity)var2).getListener() : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return !var1.isClientSide ? createTickerHelper(var3, BlockEntityType.SCULK_SENSOR, (var0, var1x, var2x, var3x) -> {
         var3x.getListener().tick(var0);
      }) : null;
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Integer)var1.getValue(POWER);
   }

   public static SculkSensorPhase getPhase(BlockState var0) {
      return (SculkSensorPhase)var0.getValue(PHASE);
   }

   public static boolean canActivate(BlockState var0) {
      return getPhase(var0) == SculkSensorPhase.INACTIVE;
   }

   public static void deactivate(Level var0, BlockPos var1, BlockState var2) {
      var0.setBlock(var1, (BlockState)((BlockState)var2.setValue(PHASE, SculkSensorPhase.COOLDOWN)).setValue(POWER, 0), 3);
      var0.scheduleTick(var1, var2.getBlock(), 1);
      if (!(Boolean)var2.getValue(WATERLOGGED)) {
         var0.playSound((Player)null, (BlockPos)var1, SoundEvents.SCULK_CLICKING_STOP, SoundSource.BLOCKS, 1.0F, var0.random.nextFloat() * 0.2F + 0.8F);
      }

      updateNeighbours(var0, var1);
   }

   public static void activate(@Nullable Entity var0, Level var1, BlockPos var2, BlockState var3, int var4) {
      var1.setBlock(var2, (BlockState)((BlockState)var3.setValue(PHASE, SculkSensorPhase.ACTIVE)).setValue(POWER, var4), 3);
      var1.scheduleTick(var2, var3.getBlock(), 40);
      updateNeighbours(var1, var2);
      var1.gameEvent(var0, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, var2);
      if (!(Boolean)var3.getValue(WATERLOGGED)) {
         var1.playSound((Player)null, (double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0F, var1.random.nextFloat() * 0.2F + 0.8F);
      }

   }

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

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(PHASE, POWER, WATERLOGGED);
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      if (var4 instanceof SculkSensorBlockEntity var5) {
         return getPhase(var1) == SculkSensorPhase.ACTIVE ? var5.getLastVibrationFrequency() : 0;
      } else {
         return 0;
      }
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   public void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
      super.spawnAfterBreak(var1, var2, var3, var4, var5);
      if (var5) {
         this.tryDropExperience(var2, var3, var4, ConstantInt.of(5));
      }

   }

   static {
      PHASE = BlockStateProperties.SCULK_SENSOR_PHASE;
      POWER = BlockStateProperties.POWER;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   }
}
