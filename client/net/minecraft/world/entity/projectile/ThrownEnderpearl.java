package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

   @Override
   protected Item getDefaultItem() {
      return Items.ENDER_PEARL;
   }

   @Override
   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      var1.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void onHit(HitResult var1) {
      super.onHit(var1);

      for(int var2 = 0; var2 < 32; ++var2) {
         this.level
            .addParticle(
               ParticleTypes.PORTAL,
               this.getX(),
               this.getY() + this.random.nextDouble() * 2.0,
               this.getZ(),
               this.random.nextGaussian(),
               0.0,
               this.random.nextGaussian()
            );
      }

      if (!this.level.isClientSide && !this.isRemoved()) {
         Entity var5 = this.getOwner();
         if (var5 instanceof ServerPlayer var3) {
            if (var3.connection.isAcceptingMessages() && var3.level == this.level && !var3.isSleeping()) {
               if (this.random.nextFloat() < 0.05F && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                  Endermite var4 = EntityType.ENDERMITE.create(this.level);
                  if (var4 != null) {
                     var4.moveTo(var5.getX(), var5.getY(), var5.getZ(), var5.getYRot(), var5.getXRot());
                     this.level.addFreshEntity(var4);
                  }
               }

               if (var5.isPassenger()) {
                  var3.dismountTo(this.getX(), this.getY(), this.getZ());
               } else {
                  var5.teleportTo(this.getX(), this.getY(), this.getZ());
               }

               var5.resetFallDistance();
               var5.hurt(this.damageSources().fall(), 5.0F);
            }
         } else if (var5 != null) {
            var5.teleportTo(this.getX(), this.getY(), this.getZ());
            var5.resetFallDistance();
         }

         this.discard();
      }
   }

   @Override
   public void tick() {
      Entity var1 = this.getOwner();
      if (var1 instanceof Player && !var1.isAlive()) {
         this.discard();
      } else {
         super.tick();
      }
   }

   @Nullable
   @Override
   public Entity changeDimension(ServerLevel var1) {
      Entity var2 = this.getOwner();
      if (var2 != null && var2.level.dimension() != var1.dimension()) {
         this.setOwner(null);
      }

      return super.changeDimension(var1);
   }
}
