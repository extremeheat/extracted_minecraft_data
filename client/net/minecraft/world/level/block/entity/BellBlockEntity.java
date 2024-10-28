package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableInt;

public class BellBlockEntity extends BlockEntity {
   private static final int DURATION = 50;
   private static final int GLOW_DURATION = 60;
   private static final int MIN_TICKS_BETWEEN_SEARCHES = 60;
   private static final int MAX_RESONATION_TICKS = 40;
   private static final int TICKS_BEFORE_RESONATION = 5;
   private static final int SEARCH_RADIUS = 48;
   private static final int HEAR_BELL_RADIUS = 32;
   private static final int HIGHLIGHT_RAIDERS_RADIUS = 48;
   private long lastRingTimestamp;
   public int ticks;
   public boolean shaking;
   public Direction clickDirection;
   private List<LivingEntity> nearbyEntities;
   private boolean resonating;
   private int resonationTicks;

   public BellBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BELL, var1, var2);
   }

   public boolean triggerEvent(int var1, int var2) {
      if (var1 == 1) {
         this.updateEntities();
         this.resonationTicks = 0;
         this.clickDirection = Direction.from3DDataValue(var2);
         this.ticks = 0;
         this.shaking = true;
         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   private static void tick(Level var0, BlockPos var1, BlockState var2, BellBlockEntity var3, ResonationEndAction var4) {
      if (var3.shaking) {
         ++var3.ticks;
      }

      if (var3.ticks >= 50) {
         var3.shaking = false;
         var3.ticks = 0;
      }

      if (var3.ticks >= 5 && var3.resonationTicks == 0 && areRaidersNearby(var1, var3.nearbyEntities)) {
         var3.resonating = true;
         var0.playSound((Player)null, (BlockPos)var1, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      if (var3.resonating) {
         if (var3.resonationTicks < 40) {
            ++var3.resonationTicks;
         } else {
            var4.run(var0, var1, var3.nearbyEntities);
            var3.resonating = false;
         }
      }

   }

   public static void clientTick(Level var0, BlockPos var1, BlockState var2, BellBlockEntity var3) {
      tick(var0, var1, var2, var3, BellBlockEntity::showBellParticles);
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, BellBlockEntity var3) {
      tick(var0, var1, var2, var3, BellBlockEntity::makeRaidersGlow);
   }

   public void onHit(Direction var1) {
      BlockPos var2 = this.getBlockPos();
      this.clickDirection = var1;
      if (this.shaking) {
         this.ticks = 0;
      } else {
         this.shaking = true;
      }

      this.level.blockEvent(var2, this.getBlockState().getBlock(), 1, var1.get3DDataValue());
   }

   private void updateEntities() {
      BlockPos var1 = this.getBlockPos();
      if (this.level.getGameTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
         this.lastRingTimestamp = this.level.getGameTime();
         AABB var2 = (new AABB(var1)).inflate(48.0);
         this.nearbyEntities = this.level.getEntitiesOfClass(LivingEntity.class, var2);
      }

      if (!this.level.isClientSide) {
         Iterator var4 = this.nearbyEntities.iterator();

         while(var4.hasNext()) {
            LivingEntity var3 = (LivingEntity)var4.next();
            if (var3.isAlive() && !var3.isRemoved() && var1.closerToCenterThan(var3.position(), 32.0)) {
               var3.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, (Object)this.level.getGameTime());
            }
         }
      }

   }

   private static boolean areRaidersNearby(BlockPos var0, List<LivingEntity> var1) {
      Iterator var2 = var1.iterator();

      LivingEntity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (LivingEntity)var2.next();
      } while(!var3.isAlive() || var3.isRemoved() || !var0.closerToCenterThan(var3.position(), 32.0) || !var3.getType().is(EntityTypeTags.RAIDERS));

      return true;
   }

   private static void makeRaidersGlow(Level var0, BlockPos var1, List<LivingEntity> var2) {
      var2.stream().filter((var1x) -> {
         return isRaiderWithinRange(var1, var1x);
      }).forEach(BellBlockEntity::glow);
   }

   private static void showBellParticles(Level var0, BlockPos var1, List<LivingEntity> var2) {
      MutableInt var3 = new MutableInt(16700985);
      int var4 = (int)var2.stream().filter((var1x) -> {
         return var1.closerToCenterThan(var1x.position(), 48.0);
      }).count();
      var2.stream().filter((var1x) -> {
         return isRaiderWithinRange(var1, var1x);
      }).forEach((var4x) -> {
         float var5 = 1.0F;
         double var6 = Math.sqrt((var4x.getX() - (double)var1.getX()) * (var4x.getX() - (double)var1.getX()) + (var4x.getZ() - (double)var1.getZ()) * (var4x.getZ() - (double)var1.getZ()));
         double var8 = (double)((float)var1.getX() + 0.5F) + 1.0 / var6 * (var4x.getX() - (double)var1.getX());
         double var10 = (double)((float)var1.getZ() + 0.5F) + 1.0 / var6 * (var4x.getZ() - (double)var1.getZ());
         int var12 = Mth.clamp((var4 - 21) / -2, 3, 15);

         for(int var13 = 0; var13 < var12; ++var13) {
            int var14 = var3.addAndGet(5);
            var0.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, var14), var8, (double)((float)var1.getY() + 0.5F), var10, 0.0, 0.0, 0.0);
         }

      });
   }

   private static boolean isRaiderWithinRange(BlockPos var0, LivingEntity var1) {
      return var1.isAlive() && !var1.isRemoved() && var0.closerToCenterThan(var1.position(), 48.0) && var1.getType().is(EntityTypeTags.RAIDERS);
   }

   private static void glow(LivingEntity var0) {
      var0.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
   }

   @FunctionalInterface
   interface ResonationEndAction {
      void run(Level var1, BlockPos var2, List<LivingEntity> var3);
   }
}
