package net.minecraft.world.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownEnderpearl extends ThrowableItemProjectile {
   public ThrownEnderpearl(EntityType<? extends ThrownEnderpearl> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownEnderpearl(Level var1, LivingEntity var2, ItemStack var3) {
      super(EntityType.ENDER_PEARL, var2, var1, var3);
   }

   @Override
   protected Item getDefaultItem() {
      return Items.ENDER_PEARL;
   }

   @Nullable
   @Override
   protected Entity findOwner(UUID var1) {
      if (this.level() instanceof ServerLevel var2) {
         Entity var6 = super.findOwner(var1);
         if (var6 != null) {
            return var6;
         } else {
            for (ServerLevel var5 : var2.getServer().getAllLevels()) {
               if (var5 != var2) {
                  var6 = var5.getEntity(var1);
                  if (var6 != null) {
                     return var6;
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
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

      if (this.level() instanceof ServerLevel var8 && !this.isRemoved()) {
         Entity var9 = this.getOwner();
         if (var9 != null && isAllowedToTeleportOwner(var9, var8)) {
            if (var9.isPassenger()) {
               var9.unRide();
            }

            Vec3 var4;
            if (this.getDeltaMovement().lengthSqr() > 0.0) {
               AABB var5 = var9.getBoundingBox();
               Vec3 var6 = new Vec3(var5.getXsize(), var5.getYsize(), var5.getZsize()).scale(0.5000099999997474);
               Vec3 var7 = new Vec3(Math.signum(this.getDeltaMovement().x), Math.signum(this.getDeltaMovement().y), Math.signum(this.getDeltaMovement().z));
               var4 = var7.multiply(var6).add(0.0, var5.getYsize() * 0.5, 0.0);
            } else {
               var4 = Vec3.ZERO;
            }

            Vec3 var10 = this.position().subtract(var4);
            if (var9 instanceof ServerPlayer var11) {
               if (var11.connection.isAcceptingMessages()) {
                  if (this.random.nextFloat() < 0.05F && var8.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                     Endermite var12 = EntityType.ENDERMITE.create(var8, EntitySpawnReason.TRIGGERED);
                     if (var12 != null) {
                        var12.moveTo(var9.getX(), var9.getY(), var9.getZ(), var9.getYRot(), var9.getXRot());
                        var8.addFreshEntity(var12);
                     }
                  }

                  Player var13 = var11.changeDimension(
                     new DimensionTransition(var8, var10, Vec3.ZERO, 0.0F, 0.0F, Relative.ALL, DimensionTransition.DO_NOTHING)
                  );
                  if (var13 != null) {
                     var13.resetFallDistance();
                     var13.resetCurrentImpulseContext();
                     var13.hurt(this.damageSources().enderPearl(), 5.0F);
                  }

                  this.playSound(var8, var10);
               }
            } else {
               Entity var14 = var9.changeDimension(
                  new DimensionTransition(var8, var10, var9.getDeltaMovement(), var9.getYRot(), var9.getXRot(), DimensionTransition.DO_NOTHING)
               );
               if (var14 != null) {
                  var14.resetFallDistance();
               }

               this.playSound(var8, var10);
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
      return var1.dimension() == Level.END && var2.dimension() == Level.OVERWORLD && this.getOwner() instanceof ServerPlayer var3
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
