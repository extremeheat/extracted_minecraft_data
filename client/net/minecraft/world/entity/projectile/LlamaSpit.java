package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LlamaSpit extends Entity implements Projectile {
   public Llama owner;
   private CompoundTag ownerTag;

   public LlamaSpit(EntityType<? extends LlamaSpit> var1, Level var2) {
      super(var1, var2);
   }

   public LlamaSpit(Level var1, Llama var2) {
      this(EntityType.LLAMA_SPIT, var1);
      this.owner = var2;
      this.setPos(var2.x - (double)(var2.getBbWidth() + 1.0F) * 0.5D * (double)Mth.sin(var2.yBodyRot * 0.017453292F), var2.y + (double)var2.getEyeHeight() - 0.10000000149011612D, var2.z + (double)(var2.getBbWidth() + 1.0F) * 0.5D * (double)Mth.cos(var2.yBodyRot * 0.017453292F));
   }

   public LlamaSpit(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(EntityType.LLAMA_SPIT, var1);
      this.setPos(var2, var4, var6);

      for(int var14 = 0; var14 < 7; ++var14) {
         double var15 = 0.4D + 0.1D * (double)var14;
         var1.addParticle(ParticleTypes.SPIT, var2, var4, var6, var8 * var15, var10, var12 * var15);
      }

      this.setDeltaMovement(var8, var10, var12);
   }

   public void tick() {
      super.tick();
      if (this.ownerTag != null) {
         this.restoreOwnerFromSave();
      }

      Vec3 var1 = this.getDeltaMovement();
      HitResult var2 = ProjectileUtil.getHitResult(this, this.getBoundingBox().expandTowards(var1).inflate(1.0D), (var1x) -> {
         return !var1x.isSpectator() && var1x != this.owner;
      }, ClipContext.Block.OUTLINE, true);
      if (var2 != null) {
         this.onHit(var2);
      }

      this.x += var1.x;
      this.y += var1.y;
      this.z += var1.z;
      float var3 = Mth.sqrt(getHorizontalDistanceSqr(var1));
      this.yRot = (float)(Mth.atan2(var1.x, var1.z) * 57.2957763671875D);

      for(this.xRot = (float)(Mth.atan2(var1.y, (double)var3) * 57.2957763671875D); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
      }

      while(this.xRot - this.xRotO >= 180.0F) {
         this.xRotO += 360.0F;
      }

      while(this.yRot - this.yRotO < -180.0F) {
         this.yRotO -= 360.0F;
      }

      while(this.yRot - this.yRotO >= 180.0F) {
         this.yRotO += 360.0F;
      }

      this.xRot = Mth.lerp(0.2F, this.xRotO, this.xRot);
      this.yRot = Mth.lerp(0.2F, this.yRotO, this.yRot);
      float var4 = 0.99F;
      float var5 = 0.06F;
      if (!this.level.containsMaterial(this.getBoundingBox(), Material.AIR)) {
         this.remove();
      } else if (this.isInWaterOrBubble()) {
         this.remove();
      } else {
         this.setDeltaMovement(var1.scale(0.9900000095367432D));
         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.05999999865889549D, 0.0D));
         }

         this.setPos(this.x, this.y, this.z);
      }
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float var7 = Mth.sqrt(var1 * var1 + var5 * var5);
         this.xRot = (float)(Mth.atan2(var3, (double)var7) * 57.2957763671875D);
         this.yRot = (float)(Mth.atan2(var1, var5) * 57.2957763671875D);
         this.xRotO = this.xRot;
         this.yRotO = this.yRot;
         this.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
      }

   }

   public void shoot(double var1, double var3, double var5, float var7, float var8) {
      Vec3 var9 = (new Vec3(var1, var3, var5)).normalize().add(this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8).scale((double)var7);
      this.setDeltaMovement(var9);
      float var10 = Mth.sqrt(getHorizontalDistanceSqr(var9));
      this.yRot = (float)(Mth.atan2(var9.x, var5) * 57.2957763671875D);
      this.xRot = (float)(Mth.atan2(var9.y, (double)var10) * 57.2957763671875D);
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   public void onHit(HitResult var1) {
      HitResult.Type var2 = var1.getType();
      if (var2 == HitResult.Type.ENTITY && this.owner != null) {
         ((EntityHitResult)var1).getEntity().hurt(DamageSource.indirectMobAttack(this, this.owner).setProjectile(), 1.0F);
      } else if (var2 == HitResult.Type.BLOCK && !this.level.isClientSide) {
         this.remove();
      }

   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.contains("Owner", 10)) {
         this.ownerTag = var1.getCompound("Owner");
      }

   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      if (this.owner != null) {
         CompoundTag var2 = new CompoundTag();
         UUID var3 = this.owner.getUUID();
         var2.putUUID("OwnerUUID", var3);
         var1.put("Owner", var2);
      }

   }

   private void restoreOwnerFromSave() {
      if (this.ownerTag != null && this.ownerTag.hasUUID("OwnerUUID")) {
         UUID var1 = this.ownerTag.getUUID("OwnerUUID");
         List var2 = this.level.getEntitiesOfClass(Llama.class, this.getBoundingBox().inflate(15.0D));
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Llama var4 = (Llama)var3.next();
            if (var4.getUUID().equals(var1)) {
               this.owner = var4;
               break;
            }
         }
      }

      this.ownerTag = null;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
