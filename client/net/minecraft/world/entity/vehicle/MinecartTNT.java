package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class MinecartTNT extends AbstractMinecart {
   private static final byte EVENT_PRIME = 10;
   private int fuse = -1;

   public MinecartTNT(EntityType<? extends MinecartTNT> var1, Level var2) {
      super(var1, var2);
   }

   public MinecartTNT(Level var1, double var2, double var4, double var6) {
      super(EntityType.TNT_MINECART, var1, var2, var4, var6);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.TNT;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.TNT.defaultBlockState();
   }

   public void tick() {
      super.tick();
      if (this.fuse > 0) {
         --this.fuse;
         this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
      } else if (this.fuse == 0) {
         this.explode(this.getDeltaMovement().horizontalDistanceSqr());
      }

      if (this.horizontalCollision) {
         double var1 = this.getDeltaMovement().horizontalDistanceSqr();
         if (var1 >= 0.009999999776482582D) {
            this.explode(var1);
         }
      }

   }

   public boolean hurt(DamageSource var1, float var2) {
      Entity var3 = var1.getDirectEntity();
      if (var3 instanceof AbstractArrow) {
         AbstractArrow var4 = (AbstractArrow)var3;
         if (var4.isOnFire()) {
            this.explode(var4.getDeltaMovement().lengthSqr());
         }
      }

      return super.hurt(var1, var2);
   }

   public void destroy(DamageSource var1) {
      double var2 = this.getDeltaMovement().horizontalDistanceSqr();
      if (!var1.isFire() && !var1.isExplosion() && !(var2 >= 0.009999999776482582D)) {
         super.destroy(var1);
         if (!var1.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Blocks.TNT);
         }

      } else {
         if (this.fuse < 0) {
            this.primeFuse();
            this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
         }

      }
   }

   protected void explode(double var1) {
      if (!this.level.isClientSide) {
         double var3 = Math.sqrt(var1);
         if (var3 > 5.0D) {
            var3 = 5.0D;
         }

         this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float)(4.0D + this.random.nextDouble() * 1.5D * var3), Explosion.BlockInteraction.BREAK);
         this.discard();
      }

   }

   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      if (var1 >= 3.0F) {
         float var4 = var1 / 10.0F;
         this.explode((double)(var4 * var4));
      }

      return super.causeFallDamage(var1, var2, var3);
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
      if (var4 && this.fuse < 0) {
         this.primeFuse();
      }

   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 10) {
         this.primeFuse();
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public void primeFuse() {
      this.fuse = 80;
      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)10);
         if (!this.isSilent()) {
            this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   public int getFuse() {
      return this.fuse;
   }

   public boolean isPrimed() {
      return this.fuse > -1;
   }

   public float getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5, float var6) {
      return !this.isPrimed() || !var4.is(BlockTags.RAILS) && !var2.getBlockState(var3.above()).is(BlockTags.RAILS) ? super.getBlockExplosionResistance(var1, var2, var3, var4, var5, var6) : 0.0F;
   }

   public boolean shouldBlockExplode(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, float var5) {
      return !this.isPrimed() || !var4.is(BlockTags.RAILS) && !var2.getBlockState(var3.above()).is(BlockTags.RAILS) ? super.shouldBlockExplode(var1, var2, var3, var4, var5) : false;
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("TNTFuse", 99)) {
         this.fuse = var1.getInt("TNTFuse");
      }

   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("TNTFuse", this.fuse);
   }
}
