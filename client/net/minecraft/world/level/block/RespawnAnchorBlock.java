package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RespawnAnchorBlock extends Block {
   public static final IntegerProperty CHARGE;
   private static final ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS;
   private static final ImmutableList<Vec3i> RESPAWN_OFFSETS;

   public RespawnAnchorBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(CHARGE, 0));
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      if (var5 == InteractionHand.MAIN_HAND && !isRespawnFuel(var7) && isRespawnFuel(var4.getItemInHand(InteractionHand.OFF_HAND))) {
         return InteractionResult.PASS;
      } else if (isRespawnFuel(var7) && canBeCharged(var1)) {
         charge(var2, var3, var1);
         if (!var4.getAbilities().instabuild) {
            var7.shrink(1);
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else if ((Integer)var1.getValue(CHARGE) == 0) {
         return InteractionResult.PASS;
      } else if (!canSetSpawn(var2)) {
         if (!var2.isClientSide) {
            this.explode(var1, var2, var3);
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         if (!var2.isClientSide) {
            ServerPlayer var8 = (ServerPlayer)var4;
            if (var8.getRespawnDimension() != var2.dimension() || !var8.getRespawnPosition().equals(var3)) {
               var8.setRespawnPosition(var2.dimension(), var3, 0.0F, false, true);
               var2.playSound((Player)null, (double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
               return InteractionResult.SUCCESS;
            }
         }

         return InteractionResult.CONSUME;
      }
   }

   private static boolean isRespawnFuel(ItemStack var0) {
      return var0.is(Items.GLOWSTONE);
   }

   private static boolean canBeCharged(BlockState var0) {
      return (Integer)var0.getValue(CHARGE) < 4;
   }

   private static boolean isWaterThatWouldFlow(BlockPos var0, Level var1) {
      FluidState var2 = var1.getFluidState(var0);
      if (!var2.is(FluidTags.WATER)) {
         return false;
      } else if (var2.isSource()) {
         return true;
      } else {
         float var3 = (float)var2.getAmount();
         if (var3 < 2.0F) {
            return false;
         } else {
            FluidState var4 = var1.getFluidState(var0.below());
            return !var4.is(FluidTags.WATER);
         }
      }
   }

   private void explode(BlockState var1, Level var2, final BlockPos var3) {
      var2.removeBlock(var3, false);
      Stream var10000 = Direction.Plane.HORIZONTAL.stream();
      var3.getClass();
      boolean var4 = var10000.map(var3::relative).anyMatch((var1x) -> {
         return isWaterThatWouldFlow(var1x, var2);
      });
      final boolean var5 = var4 || var2.getFluidState(var3.above()).is(FluidTags.WATER);
      ExplosionDamageCalculator var6 = new ExplosionDamageCalculator() {
         public Optional<Float> getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3x, BlockState var4, FluidState var5x) {
            return var3x.equals(var3) && var5 ? Optional.of(Blocks.WATER.getExplosionResistance()) : super.getBlockExplosionResistance(var1, var2, var3x, var4, var5x);
         }
      };
      var2.explode((Entity)null, DamageSource.badRespawnPointExplosion(), var6, (double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
   }

   public static boolean canSetSpawn(Level var0) {
      return var0.dimensionType().respawnAnchorWorks();
   }

   public static void charge(Level var0, BlockPos var1, BlockState var2) {
      var0.setBlock(var1, (BlockState)var2.setValue(CHARGE, (Integer)var2.getValue(CHARGE) + 1), 3);
      var0.playSound((Player)null, (double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if ((Integer)var1.getValue(CHARGE) != 0) {
         if (var4.nextInt(100) == 0) {
            var2.playSound((Player)null, (double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         double var5 = (double)var3.getX() + 0.5D + (0.5D - var4.nextDouble());
         double var7 = (double)var3.getY() + 1.0D;
         double var9 = (double)var3.getZ() + 0.5D + (0.5D - var4.nextDouble());
         double var11 = (double)var4.nextFloat() * 0.04D;
         var2.addParticle(ParticleTypes.REVERSE_PORTAL, var5, var7, var9, 0.0D, var11, 0.0D);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(CHARGE);
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public static int getScaledChargeLevel(BlockState var0, int var1) {
      return Mth.floor((float)((Integer)var0.getValue(CHARGE) - 0) / 4.0F * (float)var1);
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return getScaledChargeLevel(var1, 15);
   }

   public static Optional<Vec3> findStandUpPosition(EntityType<?> var0, CollisionGetter var1, BlockPos var2) {
      Optional var3 = findStandUpPosition(var0, var1, var2, true);
      return var3.isPresent() ? var3 : findStandUpPosition(var0, var1, var2, false);
   }

   private static Optional<Vec3> findStandUpPosition(EntityType<?> var0, CollisionGetter var1, BlockPos var2, boolean var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      UnmodifiableIterator var5 = RESPAWN_OFFSETS.iterator();

      Vec3 var7;
      do {
         if (!var5.hasNext()) {
            return Optional.empty();
         }

         Vec3i var6 = (Vec3i)var5.next();
         var4.set(var2).move(var6);
         var7 = DismountHelper.findSafeDismountLocation(var0, var1, var4, var3);
      } while(var7 == null);

      return Optional.of(var7);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      CHARGE = BlockStateProperties.RESPAWN_ANCHOR_CHARGES;
      RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, -1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, 1));
      RESPAWN_OFFSETS = (new Builder()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::above).iterator()).add(new Vec3i(0, 1, 0)).build();
   }
}
