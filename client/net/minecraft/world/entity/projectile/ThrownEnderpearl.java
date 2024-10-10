package net.minecraft.world.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownEnderpearl extends ThrowableItemProjectile {
   private long ticketTimer = 0L;

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

   @Override
   protected void setOwnerThroughUUID(UUID var1) {
      this.deregisterFromCurrentOwner();
      super.setOwnerThroughUUID(var1);
      this.registerToCurrentOwner();
   }

   @Override
   public void setOwner(@Nullable Entity var1) {
      this.deregisterFromCurrentOwner();
      super.setOwner(var1);
      this.registerToCurrentOwner();
   }

   private void deregisterFromCurrentOwner() {
      if (this.getOwner() instanceof ServerPlayer var1) {
         var1.deregisterEnderPearl(this);
      }
   }

   private void registerToCurrentOwner() {
      if (this.getOwner() instanceof ServerPlayer var1) {
         var1.registerEnderPearl(this);
      }
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

      if (this.level() instanceof ServerLevel var7 && !this.isRemoved()) {
         Entity var8 = this.getOwner();
         if (var8 != null && isAllowedToTeleportOwner(var8, var7)) {
            if (var8.isPassenger()) {
               var8.unRide();
            }

            Vec3 var4 = this.oldPosition();
            if (var8 instanceof ServerPlayer var5) {
               if (var5.connection.isAcceptingMessages()) {
                  if (this.random.nextFloat() < 0.05F && var7.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                     Endermite var6 = EntityType.ENDERMITE.create(var7, EntitySpawnReason.TRIGGERED);
                     if (var6 != null) {
                        var6.moveTo(var8.getX(), var8.getY(), var8.getZ(), var8.getYRot(), var8.getXRot());
                        var7.addFreshEntity(var6);
                     }
                  }

                  if (this.isOnPortalCooldown()) {
                     var8.setPortalCooldown();
                  }

                  ServerPlayer var9 = var5.teleport(
                     new TeleportTransition(var7, var4, Vec3.ZERO, 0.0F, 0.0F, Relative.union(Relative.ROTATION, Relative.DELTA), TeleportTransition.DO_NOTHING)
                  );
                  if (var9 != null) {
                     var9.resetFallDistance();
                     var9.resetCurrentImpulseContext();
                     var9.hurtServer(var5.serverLevel(), this.damageSources().enderPearl(), 5.0F);
                  }

                  this.playSound(var7, var4);
               }
            } else {
               Entity var10 = var8.teleport(
                  new TeleportTransition(var7, var4, var8.getDeltaMovement(), var8.getYRot(), var8.getXRot(), TeleportTransition.DO_NOTHING)
               );
               if (var10 != null) {
                  var10.resetFallDistance();
               }

               this.playSound(var7, var4);
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
      int var1;
      int var2;
      Entity var3;
      label30: {
         var1 = SectionPos.blockToSectionCoord(this.position().x());
         var2 = SectionPos.blockToSectionCoord(this.position().z());
         var3 = this.getOwner();
         if (var3 instanceof ServerPlayer var4 && !var3.isAlive() && var4.serverLevel().getGameRules().getBoolean(GameRules.RULE_ENDER_PEARLS_VANISH_ON_DEATH)) {
            this.discard();
            break label30;
         }

         super.tick();
      }

      if (this.isAlive()) {
         BlockPos var6 = BlockPos.containing(this.position());
         if ((--this.ticketTimer <= 0L || var1 != SectionPos.blockToSectionCoord(var6.getX()) || var2 != SectionPos.blockToSectionCoord(var6.getZ()))
            && var3 instanceof ServerPlayer var5) {
            this.ticketTimer = var5.registerAndUpdateEnderPearlTicket(this);
         }
      }
   }

   private void playSound(Level var1, Vec3 var2) {
      var1.playSound(null, var2.x, var2.y, var2.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
   }

   @Nullable
   @Override
   public Entity teleport(TeleportTransition var1) {
      Entity var2 = super.teleport(var1);
      if (var2 != null) {
         var2.placePortalTicket(BlockPos.containing(var2.position()));
      }

      return var2;
   }

   @Override
   public boolean canTeleport(Level var1, Level var2) {
      return var1.dimension() == Level.END && var2.dimension() == Level.OVERWORLD && this.getOwner() instanceof ServerPlayer var3
         ? super.canTeleport(var1, var2) && var3.seenCredits
         : super.canTeleport(var1, var2);
   }

   @Override
   protected void onInsideBlock(BlockState var1) {
      super.onInsideBlock(var1);
      if (var1.is(Blocks.END_GATEWAY) && this.getOwner() instanceof ServerPlayer var2) {
         var2.onInsideBlock(var1);
      }
   }

   @Override
   public void onRemoval(Entity.RemovalReason var1) {
      if (var1 != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
         this.deregisterFromCurrentOwner();
      }

      super.onRemoval(var1);
   }
}
