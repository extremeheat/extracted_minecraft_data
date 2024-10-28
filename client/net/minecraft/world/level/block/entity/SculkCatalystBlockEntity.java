package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener.Provider<CatalystListener> {
   private final CatalystListener catalystListener;

   public SculkCatalystBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SCULK_CATALYST, var1, var2);
      this.catalystListener = new CatalystListener(var2, new BlockPositionSource(var1));
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, SculkCatalystBlockEntity var3) {
      var3.catalystListener.getSculkSpreader().updateCursors(var0, var1, var0.getRandom(), true);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.catalystListener.sculkSpreader.load(var1);
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      this.catalystListener.sculkSpreader.save(var1);
      super.saveAdditional(var1, var2);
   }

   public CatalystListener getListener() {
      return this.catalystListener;
   }

   // $FF: synthetic method
   public GameEventListener getListener() {
      return this.getListener();
   }

   public static class CatalystListener implements GameEventListener {
      public static final int PULSE_TICKS = 8;
      final SculkSpreader sculkSpreader;
      private final BlockState blockState;
      private final PositionSource positionSource;

      public CatalystListener(BlockState var1, PositionSource var2) {
         super();
         this.blockState = var1;
         this.positionSource = var2;
         this.sculkSpreader = SculkSpreader.createLevelSpreader();
      }

      public PositionSource getListenerSource() {
         return this.positionSource;
      }

      public int getListenerRadius() {
         return 8;
      }

      public GameEventListener.DeliveryMode getDeliveryMode() {
         return GameEventListener.DeliveryMode.BY_DISTANCE;
      }

      public boolean handleGameEvent(ServerLevel var1, Holder<GameEvent> var2, GameEvent.Context var3, Vec3 var4) {
         if (var2.is((Holder)GameEvent.ENTITY_DIE)) {
            Entity var6 = var3.sourceEntity();
            if (var6 instanceof LivingEntity) {
               LivingEntity var5 = (LivingEntity)var6;
               if (!var5.wasExperienceConsumed()) {
                  int var7 = var5.getExperienceReward();
                  if (var5.shouldDropExperience() && var7 > 0) {
                     this.sculkSpreader.addCursors(BlockPos.containing(var4.relative(Direction.UP, 0.5)), var7);
                     this.tryAwardItSpreadsAdvancement(var1, var5);
                  }

                  var5.skipDropExperience();
                  this.positionSource.getPosition(var1).ifPresent((var2x) -> {
                     this.bloom(var1, BlockPos.containing(var2x), this.blockState, var1.getRandom());
                  });
               }

               return true;
            }
         }

         return false;
      }

      @VisibleForTesting
      public SculkSpreader getSculkSpreader() {
         return this.sculkSpreader;
      }

      private void bloom(ServerLevel var1, BlockPos var2, BlockState var3, RandomSource var4) {
         var1.setBlock(var2, (BlockState)var3.setValue(SculkCatalystBlock.PULSE, true), 3);
         var1.scheduleTick(var2, var3.getBlock(), 8);
         var1.sendParticles(ParticleTypes.SCULK_SOUL, (double)var2.getX() + 0.5, (double)var2.getY() + 1.15, (double)var2.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.0);
         var1.playSound((Player)null, var2, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + var4.nextFloat() * 0.4F);
      }

      private void tryAwardItSpreadsAdvancement(Level var1, LivingEntity var2) {
         LivingEntity var3 = var2.getLastHurtByMob();
         if (var3 instanceof ServerPlayer var4) {
            DamageSource var5 = var2.getLastDamageSource() == null ? var1.damageSources().playerAttack(var4) : var2.getLastDamageSource();
            CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger(var4, var2, var5);
         }

      }
   }
}
