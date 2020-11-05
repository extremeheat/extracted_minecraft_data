package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownEnderpearl extends ThrowableItemProjectile {
   public ThrownEnderpearl(EntityType<? extends ThrownEnderpearl> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownEnderpearl(Level var1, LivingEntity var2) {
      super(EntityType.ENDER_PEARL, var2, var1);
   }

   public ThrownEnderpearl(Level var1, double var2, double var4, double var6) {
      super(EntityType.ENDER_PEARL, var2, var4, var6, var1);
   }

   protected Item getDefaultItem() {
      return Items.ENDER_PEARL;
   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      var1.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      Entity var2 = this.getOwner();

      for(int var3 = 0; var3 < 32; ++var3) {
         this.level.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
      }

      if (!this.level.isClientSide && !this.removed) {
         if (var2 instanceof ServerPlayer) {
            ServerPlayer var5 = (ServerPlayer)var2;
            if (var5.connection.getConnection().isConnected() && var5.level == this.level && !var5.isSleeping()) {
               if (this.random.nextFloat() < 0.05F && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                  Endermite var4 = (Endermite)EntityType.ENDERMITE.create(this.level);
                  var4.setPlayerSpawned(true);
                  var4.moveTo(var2.getX(), var2.getY(), var2.getZ(), var2.yRot, var2.xRot);
                  this.level.addFreshEntity(var4);
               }

               if (var2.isPassenger()) {
                  var2.stopRiding();
               }

               var2.teleportTo(this.getX(), this.getY(), this.getZ());
               var2.fallDistance = 0.0F;
               var2.hurt(DamageSource.FALL, 5.0F);
            }
         } else if (var2 != null) {
            var2.teleportTo(this.getX(), this.getY(), this.getZ());
            var2.fallDistance = 0.0F;
         }

         this.remove();
      }

   }

   public void tick() {
      Entity var1 = this.getOwner();
      if (var1 instanceof Player && !var1.isAlive()) {
         this.remove();
      } else {
         super.tick();
      }

   }

   @Nullable
   public Entity changeDimension(ServerLevel var1) {
      Entity var2 = this.getOwner();
      if (var2 != null && var2.level.dimension() != var1.dimension()) {
         this.setOwner((Entity)null);
      }

      return super.changeDimension(var1);
   }
}
