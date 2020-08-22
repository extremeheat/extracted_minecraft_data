package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownEnderpearl extends ThrowableItemProjectile {
   private LivingEntity originalOwner;

   public ThrownEnderpearl(EntityType var1, Level var2) {
      super(var1, var2);
   }

   public ThrownEnderpearl(Level var1, LivingEntity var2) {
      super(EntityType.ENDER_PEARL, var2, var1);
      this.originalOwner = var2;
   }

   public ThrownEnderpearl(Level var1, double var2, double var4, double var6) {
      super(EntityType.ENDER_PEARL, var2, var4, var6, var1);
   }

   protected Item getDefaultItem() {
      return Items.ENDER_PEARL;
   }

   protected void onHit(HitResult var1) {
      LivingEntity var2 = this.getOwner();
      if (var1.getType() == HitResult.Type.ENTITY) {
         Entity var3 = ((EntityHitResult)var1).getEntity();
         if (var3 == this.originalOwner) {
            return;
         }

         var3.hurt(DamageSource.thrown(this, var2), 0.0F);
      }

      if (var1.getType() == HitResult.Type.BLOCK) {
         BlockPos var6 = ((BlockHitResult)var1).getBlockPos();
         BlockEntity var4 = this.level.getBlockEntity(var6);
         if (var4 instanceof TheEndGatewayBlockEntity) {
            TheEndGatewayBlockEntity var5 = (TheEndGatewayBlockEntity)var4;
            if (var2 != null) {
               if (var2 instanceof ServerPlayer) {
                  CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayer)var2, this.level.getBlockState(var6));
               }

               var5.teleportEntity(var2);
               this.remove();
               return;
            }

            var5.teleportEntity(this);
            return;
         }
      }

      for(int var7 = 0; var7 < 32; ++var7) {
         this.level.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
      }

      if (!this.level.isClientSide) {
         if (var2 instanceof ServerPlayer) {
            ServerPlayer var8 = (ServerPlayer)var2;
            if (var8.connection.getConnection().isConnected() && var8.level == this.level && !var8.isSleeping()) {
               if (this.random.nextFloat() < 0.05F && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                  Endermite var9 = (Endermite)EntityType.ENDERMITE.create(this.level);
                  var9.setPlayerSpawned(true);
                  var9.moveTo(var2.getX(), var2.getY(), var2.getZ(), var2.yRot, var2.xRot);
                  this.level.addFreshEntity(var9);
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
      LivingEntity var1 = this.getOwner();
      if (var1 != null && var1 instanceof Player && !var1.isAlive()) {
         this.remove();
      } else {
         super.tick();
      }

   }

   @Nullable
   public Entity changeDimension(DimensionType var1) {
      if (this.owner.dimension != var1) {
         this.owner = null;
      }

      return super.changeDimension(var1);
   }
}
