package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.AABB;

public class BellBlockEntity extends BlockEntity implements TickableBlockEntity {
   private long lastRingTimestamp;
   public int ticks;
   public boolean shaking;
   public Direction clickDirection;
   private List nearbyEntities;
   private boolean resonating;
   private int resonationTicks;

   public BellBlockEntity() {
      super(BlockEntityType.BELL);
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

   public void tick() {
      if (this.shaking) {
         ++this.ticks;
      }

      if (this.ticks >= 50) {
         this.shaking = false;
         this.ticks = 0;
      }

      if (this.ticks >= 5 && this.resonationTicks == 0 && this.areRaidersNearby()) {
         this.resonating = true;
         this.playResonateSound();
      }

      if (this.resonating) {
         if (this.resonationTicks < 40) {
            ++this.resonationTicks;
         } else {
            this.makeRaidersGlow(this.level);
            this.showBellParticles(this.level);
            this.resonating = false;
         }
      }

   }

   private void playResonateSound() {
      this.level.playSound((Player)null, (BlockPos)this.getBlockPos(), SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0F, 1.0F);
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
         AABB var2 = (new AABB(var1)).inflate(48.0D);
         this.nearbyEntities = this.level.getEntitiesOfClass(LivingEntity.class, var2);
      }

      if (!this.level.isClientSide) {
         Iterator var4 = this.nearbyEntities.iterator();

         while(var4.hasNext()) {
            LivingEntity var3 = (LivingEntity)var4.next();
            if (var3.isAlive() && !var3.removed && var1.closerThan(var3.position(), 32.0D)) {
               var3.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, (Object)this.level.getGameTime());
            }
         }
      }

   }

   private boolean areRaidersNearby() {
      BlockPos var1 = this.getBlockPos();
      Iterator var2 = this.nearbyEntities.iterator();

      LivingEntity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (LivingEntity)var2.next();
      } while(!var3.isAlive() || var3.removed || !var1.closerThan(var3.position(), 32.0D) || !var3.getType().is(EntityTypeTags.RAIDERS));

      return true;
   }

   private void makeRaidersGlow(Level var1) {
      if (!var1.isClientSide) {
         this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach(this::glow);
      }
   }

   private void showBellParticles(Level var1) {
      if (var1.isClientSide) {
         BlockPos var2 = this.getBlockPos();
         AtomicInteger var3 = new AtomicInteger(16700985);
         int var4 = (int)this.nearbyEntities.stream().filter((var1x) -> {
            return var2.closerThan(var1x.position(), 48.0D);
         }).count();
         this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach((var4x) -> {
            float var5 = 1.0F;
            float var6 = Mth.sqrt((var4x.getX() - (double)var2.getX()) * (var4x.getX() - (double)var2.getX()) + (var4x.getZ() - (double)var2.getZ()) * (var4x.getZ() - (double)var2.getZ()));
            double var7 = (double)((float)var2.getX() + 0.5F) + (double)(1.0F / var6) * (var4x.getX() - (double)var2.getX());
            double var9 = (double)((float)var2.getZ() + 0.5F) + (double)(1.0F / var6) * (var4x.getZ() - (double)var2.getZ());
            int var11 = Mth.clamp((var4 - 21) / -2, 3, 15);

            for(int var12 = 0; var12 < var11; ++var12) {
               var3.addAndGet(5);
               double var13 = (double)(var3.get() >> 16 & 255) / 255.0D;
               double var15 = (double)(var3.get() >> 8 & 255) / 255.0D;
               double var17 = (double)(var3.get() & 255) / 255.0D;
               var1.addParticle(ParticleTypes.ENTITY_EFFECT, var7, (double)((float)var2.getY() + 0.5F), var9, var13, var15, var17);
            }

         });
      }
   }

   private boolean isRaiderWithinRange(LivingEntity var1) {
      return var1.isAlive() && !var1.removed && this.getBlockPos().closerThan(var1.position(), 48.0D) && var1.getType().is(EntityTypeTags.RAIDERS);
   }

   private void glow(LivingEntity var1) {
      var1.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
   }
}
