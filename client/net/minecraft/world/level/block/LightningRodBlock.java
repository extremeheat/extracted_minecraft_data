package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class LightningRodBlock extends RodBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED;
   public static final BooleanProperty POWERED;
   private static final int ACTIVATION_TICKS = 8;
   public static final int RANGE = 128;
   private static final int SPARK_CYCLE = 200;

   public LightningRodBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP)).setValue(WATERLOGGED, false)).setValue(POWERED, false));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var3 = var2.getType() == Fluids.WATER;
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var1.getClickedFace())).setValue(WATERLOGGED, var3);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) && var1.getValue(FACING) == var4 ? 15 : 0;
   }

   public void onLightningStrike(BlockState var1, Level var2, BlockPos var3) {
      var2.setBlock(var3, (BlockState)var1.setValue(POWERED, true), 3);
      this.updateNeighbours(var1, var2, var3);
      var2.scheduleTick(var3, this, 8);
      var2.levelEvent(3002, var3, ((Direction)var1.getValue(FACING)).getAxis().ordinal());
   }

   private void updateNeighbours(BlockState var1, Level var2, BlockPos var3) {
      var2.updateNeighborsAt(var3.relative(((Direction)var1.getValue(FACING)).getOpposite()), this);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      var2.setBlock(var3, (BlockState)var1.setValue(POWERED, false), 3);
      this.updateNeighbours(var1, var2, var3);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var2.isThundering() && (long)var2.random.nextInt(200) <= var2.getGameTime() % 200L && var3.getY() == var2.getHeight(Heightmap.Types.WORLD_SURFACE, var3.getX(), var3.getZ()) - 1) {
         ParticleUtils.spawnParticlesAlongAxis(((Direction)var1.getValue(FACING)).getAxis(), var2, var3, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(1, 2));
      }
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         if ((Boolean)var1.getValue(POWERED)) {
            this.updateNeighbours(var1, var2, var3);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         if ((Boolean)var1.getValue(POWERED) && !var2.getBlockTicks().hasScheduledTick(var3, this)) {
            var2.setBlock(var3, (BlockState)var1.setValue(POWERED, false), 18);
         }

      }
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      if (var1.isThundering() && var4 instanceof ThrownTrident && ((ThrownTrident)var4).isChanneling()) {
         BlockPos var5 = var3.getBlockPos();
         if (var1.canSeeSky(var5)) {
            LightningBolt var6 = (LightningBolt)EntityType.LIGHTNING_BOLT.create(var1);
            var6.moveTo(Vec3.atBottomCenterOf(var5.above()));
            Entity var7 = var4.getOwner();
            var6.setCause(var7 instanceof ServerPlayer ? (ServerPlayer)var7 : null);
            var1.addFreshEntity(var6);
            var1.playSound((Player)null, (BlockPos)var5, SoundEvents.TRIDENT_THUNDER, SoundSource.WEATHER, 5.0F, 1.0F);
         }
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, WATERLOGGED);
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      POWERED = BlockStateProperties.POWERED;
   }
}
