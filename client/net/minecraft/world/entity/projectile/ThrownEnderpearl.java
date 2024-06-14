package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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

      if (this.level() instanceof ServerLevel var6 && !this.isRemoved()) {
         Entity var7 = this.getOwner();
         if (var7 != null && isAllowedToTeleportOwner(var7, var6)) {
            if (var7.isPassenger()) {
               var7.unRide();
            }

            if (var7 instanceof ServerPlayer var4) {
               if (var4.connection.isAcceptingMessages()) {
                  if (this.random.nextFloat() < 0.05F && var6.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                     Endermite var5 = EntityType.ENDERMITE.create(var6);
                     if (var5 != null) {
                        var5.moveTo(var7.getX(), var7.getY(), var7.getZ(), var7.getYRot(), var7.getXRot());
                        var6.addFreshEntity(var5);
                     }
                  }

                  var7.changeDimension(
                     new DimensionTransition(var6, this.position(), var7.getDeltaMovement(), var7.getYRot(), var7.getXRot(), DimensionTransition.DO_NOTHING)
                  );
                  var7.resetFallDistance();
                  var4.resetCurrentImpulseContext();
                  var7.hurt(this.damageSources().fall(), 5.0F);
                  this.playSound(var6, this.position());
               }
            } else {
               var7.changeDimension(
                  new DimensionTransition(var6, this.position(), var7.getDeltaMovement(), var7.getYRot(), var7.getXRot(), DimensionTransition.DO_NOTHING)
               );
               var7.resetFallDistance();
               this.playSound(var6, this.position());
            }

            this.discard();
            return;
         }

         this.discard();
         return;
      }
   }

   private static boolean isAllowedToTeleportOwner(Entity var0, Level var1) {
      if (var0.level().dimension() == var1.dimension()) {
         return !(var0 instanceof LivingEntity var2) ? var0.isAlive() : var2.isAlive() && !var2.isSleeping();
      } else {
         return var0.canUsePortal(true);
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

   private void playSound(Level var1, Vec3 var2) {
      var1.playSound(null, var2.x, var2.y, var2.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
   }

   @Override
   public boolean canChangeDimensions(Level var1, Level var2) {
      return var1.dimension() == Level.END && this.getOwner() instanceof ServerPlayer var3
         ? super.canChangeDimensions(var1, var2) && var3.seenCredits
         : super.canChangeDimensions(var1, var2);
   }

   @Override
   protected void onInsideBlock(BlockState var1) {
      super.onInsideBlock(var1);
      if (var1.is(Blocks.END_GATEWAY) && this.getOwner() instanceof ServerPlayer var2) {
         var2.onInsideBlock(var1);
      }
   }
}
