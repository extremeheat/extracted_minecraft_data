package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LlamaSpit extends Projectile {
   public LlamaSpit(EntityType<? extends LlamaSpit> var1, Level var2) {
      super(var1, var2);
   }

   public LlamaSpit(Level var1, Llama var2) {
      this(EntityType.LLAMA_SPIT, var1);
      this.setOwner(var2);
      this.setPos(var2.getX() - (double)(var2.getBbWidth() + 1.0F) * 0.5 * (double)Mth.sin(var2.yBodyRot * 0.017453292F), var2.getEyeY() - 0.10000000149011612, var2.getZ() + (double)(var2.getBbWidth() + 1.0F) * 0.5 * (double)Mth.cos(var2.yBodyRot * 0.017453292F));
   }

   public void tick() {
      super.tick();
      Vec3 var1 = this.getDeltaMovement();
      HitResult var2 = ProjectileUtil.getHitResult(this, this::canHitEntity);
      this.onHit(var2);
      double var3 = this.getX() + var1.x;
      double var5 = this.getY() + var1.y;
      double var7 = this.getZ() + var1.z;
      this.updateRotation();
      float var9 = 0.99F;
      float var10 = 0.06F;
      if (this.level.getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
         this.discard();
      } else if (this.isInWaterOrBubble()) {
         this.discard();
      } else {
         this.setDeltaMovement(var1.scale(0.9900000095367432));
         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.05999999865889549, 0.0));
         }

         this.setPos(var3, var5, var7);
      }
   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      Entity var2 = this.getOwner();
      if (var2 instanceof LivingEntity) {
         var1.getEntity().hurt(DamageSource.indirectMobAttack(this, (LivingEntity)var2).setProjectile(), 1.0F);
      }

   }

   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      if (!this.level.isClientSide) {
         this.discard();
      }

   }

   protected void defineSynchedData() {
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      double var2 = var1.getXa();
      double var4 = var1.getYa();
      double var6 = var1.getZa();

      for(int var8 = 0; var8 < 7; ++var8) {
         double var9 = 0.4 + 0.1 * (double)var8;
         this.level.addParticle(ParticleTypes.SPIT, this.getX(), this.getY(), this.getZ(), var2 * var9, var4, var6 * var9);
      }

      this.setDeltaMovement(var2, var4, var6);
   }
}
