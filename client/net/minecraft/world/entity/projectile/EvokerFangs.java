package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EvokerFangs extends Entity {
   public static final int ATTACK_DURATION = 20;
   public static final int LIFE_OFFSET = 2;
   public static final int ATTACK_TRIGGER_TICKS = 14;
   private int warmupDelayTicks;
   private boolean sentSpikeEvent;
   private int lifeTicks;
   private boolean clientSideAttackStarted;
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;

   public EvokerFangs(EntityType<? extends EvokerFangs> var1, Level var2) {
      super(var1, var2);
      this.lifeTicks = 22;
   }

   public EvokerFangs(Level var1, double var2, double var4, double var6, float var8, int var9, LivingEntity var10) {
      this(EntityType.EVOKER_FANGS, var1);
      this.warmupDelayTicks = var9;
      this.setOwner(var10);
      this.setYRot(var8 * 57.295776F);
      this.setPos(var2, var4, var6);
   }

   protected void defineSynchedData() {
   }

   public void setOwner(@Nullable LivingEntity var1) {
      this.owner = var1;
      this.ownerUUID = var1 == null ? null : var1.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
         Entity var1 = ((ServerLevel)this.level).getEntity(this.ownerUUID);
         if (var1 instanceof LivingEntity) {
            this.owner = (LivingEntity)var1;
         }
      }

      return this.owner;
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      this.warmupDelayTicks = var1.getInt("Warmup");
      if (var1.hasUUID("Owner")) {
         this.ownerUUID = var1.getUUID("Owner");
      }

   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putInt("Warmup", this.warmupDelayTicks);
      if (this.ownerUUID != null) {
         var1.putUUID("Owner", this.ownerUUID);
      }

   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         if (this.clientSideAttackStarted) {
            --this.lifeTicks;
            if (this.lifeTicks == 14) {
               for(int var1 = 0; var1 < 12; ++var1) {
                  double var2 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                  double var4 = this.getY() + 0.05D + this.random.nextDouble();
                  double var6 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                  double var8 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  double var10 = 0.3D + this.random.nextDouble() * 0.3D;
                  double var12 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  this.level.addParticle(ParticleTypes.CRIT, var2, var4 + 1.0D, var6, var8, var10, var12);
               }
            }
         }
      } else if (--this.warmupDelayTicks < 0) {
         if (this.warmupDelayTicks == -8) {
            List var14 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D));
            Iterator var15 = var14.iterator();

            while(var15.hasNext()) {
               LivingEntity var3 = (LivingEntity)var15.next();
               this.dealDamageTo(var3);
            }
         }

         if (!this.sentSpikeEvent) {
            this.level.broadcastEntityEvent(this, (byte)4);
            this.sentSpikeEvent = true;
         }

         if (--this.lifeTicks < 0) {
            this.discard();
         }
      }

   }

   private void dealDamageTo(LivingEntity var1) {
      LivingEntity var2 = this.getOwner();
      if (var1.isAlive() && !var1.isInvulnerable() && var1 != var2) {
         if (var2 == null) {
            var1.hurt(DamageSource.MAGIC, 6.0F);
         } else {
            if (var2.isAlliedTo(var1)) {
               return;
            }

            var1.hurt(DamageSource.indirectMagic(this, var2), 6.0F);
         }

      }
   }

   public void handleEntityEvent(byte var1) {
      super.handleEntityEvent(var1);
      if (var1 == 4) {
         this.clientSideAttackStarted = true;
         if (!this.isSilent()) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
         }
      }

   }

   public float getAnimationProgress(float var1) {
      if (!this.clientSideAttackStarted) {
         return 0.0F;
      } else {
         int var2 = this.lifeTicks - 2;
         return var2 <= 0 ? 1.0F : 1.0F - ((float)var2 - var1) / 20.0F;
      }
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
