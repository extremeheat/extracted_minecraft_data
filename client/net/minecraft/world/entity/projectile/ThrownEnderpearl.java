package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
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

   @Override
   protected void onHit(HitResult var1) {
      super.onHit(var1);

      for (int var2 = 0; var2 < 32; var2++) {
         this.level()
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

      if (!this.level().isClientSide && !this.isRemoved()) {
         Entity var5 = this.getOwner();
         if (var5 instanceof ServerPlayer var3) {
            if (var3.connection.isAcceptingMessages() && var3.level() == this.level() && !var3.isSleeping() && !var3.isSpectator() && var3.isAlive()) {
               if (this.random.nextFloat() < 0.05F && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                  Endermite var4 = EntityType.ENDERMITE.create(this.level());
                  if (var4 != null) {
                     var4.moveTo(var5.getX(), var5.getY(), var5.getZ(), var5.getYRot(), var5.getXRot());
                     this.level().addFreshEntity(var4);
                  }
               }

               if (var5.isPassenger()) {
                  var3.dismountTo(this.getX(), this.getY(), this.getZ());
               } else {
                  var5.teleportTo(this.getX(), this.getY(), this.getZ());
               }

               var5.resetFallDistance();
               var5.hurt(this.damageSources().fall(), 5.0F);
               this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
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
      if (var1 instanceof ServerPlayer && !var1.isAlive() && this.level().getGameRules().getBoolean(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH)) {
         this.discard();
      } else {
         super.tick();
      }
   }

   @Nullable
   @Override
   public Entity changeDimension(Entity.DimensionTransitionSupplier var1) {
      return super.changeDimension(() -> {
         DimensionTransition var2 = var1.get();
         if (var2 != null && this.getOwner() != null && this.getOwner().level().dimension() != var2.newDimension().dimension()) {
            this.setOwner(null);
         }

         return var2;
      });
   }
}
