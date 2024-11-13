package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class MinecartTNT extends AbstractMinecart {
   private static final byte EVENT_PRIME = 10;
   private static final String TAG_EXPLOSION_POWER = "explosion_power";
   private static final String TAG_EXPLOSION_SPEED_FACTOR = "explosion_speed_factor";
   private static final String TAG_FUSE = "fuse";
   private static final float DEFAULT_EXPLOSION_POWER_BASE = 4.0F;
   private static final float DEFAULT_EXPLOSION_SPEED_FACTOR = 1.0F;
   private int fuse = -1;
   private float explosionPowerBase = 4.0F;
   private float explosionSpeedFactor = 1.0F;

   public MinecartTNT(EntityType<? extends MinecartTNT> var1, Level var2) {
      super(var1, var2);
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.TNT.defaultBlockState();
   }

   public void tick() {
      super.tick();
      if (this.fuse > 0) {
         --this.fuse;
         this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
      } else if (this.fuse == 0) {
         this.explode(this.getDeltaMovement().horizontalDistanceSqr());
      }

      if (this.horizontalCollision) {
         double var1 = this.getDeltaMovement().horizontalDistanceSqr();
         if (var1 >= 0.009999999776482582) {
            this.explode(var1);
         }
      }

   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      Entity var4 = var2.getDirectEntity();
      if (var4 instanceof Projectile var5) {
         if (var5.isOnFire()) {
            DamageSource var6 = this.damageSources().explosion(this, var2.getEntity());
            this.explode(var6, var5.getDeltaMovement().lengthSqr());
         }
      }

      return super.hurtServer(var1, var2, var3);
   }

   public void destroy(ServerLevel var1, DamageSource var2) {
      double var3 = this.getDeltaMovement().horizontalDistanceSqr();
      if (!damageSourceIgnitesTnt(var2) && !(var3 >= 0.009999999776482582)) {
         this.destroy(var1, this.getDropItem());
      } else {
         if (this.fuse < 0) {
            this.primeFuse();
            this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
         }

      }
   }

   protected Item getDropItem() {
      return Items.TNT_MINECART;
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.TNT_MINECART);
   }

   protected void explode(double var1) {
      this.explode((DamageSource)null, var1);
   }

   protected void explode(@Nullable DamageSource var1, double var2) {
      Level var5 = this.level();
      if (var5 instanceof ServerLevel var4) {
         double var7 = Math.min(Math.sqrt(var2), 5.0);
         var4.explode(this, var1, (ExplosionDamageCalculator)null, this.getX(), this.getY(), this.getZ(), (float)((double)this.explosionPowerBase + (double)this.explosionSpeedFactor * this.random.nextDouble() * 1.5 * var7), false, Level.ExplosionInteraction.TNT);
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
      if (!this.level().isClientSide) {
         this.level().broadcastEntityEvent(this, (byte)10);
         if (!this.isSilent()) {
            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
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
      if (var1.contains("fuse", 99)) {
         this.fuse = var1.getInt("fuse");
      }

      if (var1.contains("explosion_power", 99)) {
         this.explosionPowerBase = Mth.clamp(var1.getFloat("explosion_power"), 0.0F, 128.0F);
      }

      if (var1.contains("explosion_speed_factor", 99)) {
         this.explosionSpeedFactor = Mth.clamp(var1.getFloat("explosion_speed_factor"), 0.0F, 128.0F);
      }

   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("fuse", this.fuse);
      if (this.explosionPowerBase != 4.0F) {
         var1.putFloat("explosion_power", this.explosionPowerBase);
      }

      if (this.explosionSpeedFactor != 1.0F) {
         var1.putFloat("explosion_speed_factor", this.explosionSpeedFactor);
      }

   }

   boolean shouldSourceDestroy(DamageSource var1) {
      return damageSourceIgnitesTnt(var1);
   }

   private static boolean damageSourceIgnitesTnt(DamageSource var0) {
      return var0.is(DamageTypeTags.IS_FIRE) || var0.is(DamageTypeTags.IS_EXPLOSION);
   }
}
