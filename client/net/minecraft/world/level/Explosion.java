package net.minecraft.world.level;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Explosion {
   private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
   private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
   private final boolean fire;
   private final Explosion.BlockInteraction blockInteraction;
   private final RandomSource random = RandomSource.create();
   private final Level level;
   private final double x;
   private final double y;
   private final double z;
   @Nullable
   private final Entity source;
   private final float radius;
   private final DamageSource damageSource;
   private final ExplosionDamageCalculator damageCalculator;
   private final ParticleOptions smallExplosionParticles;
   private final ParticleOptions largeExplosionParticles;
   private final SoundEvent explosionSound;
   private final ObjectArrayList<BlockPos> toBlow = new ObjectArrayList();
   private final Map<Player, Vec3> hitPlayers = Maps.newHashMap();

   public static DamageSource getDefaultDamageSource(Level var0, @Nullable Entity var1) {
      return var0.damageSources().explosion(var1, getIndirectSourceEntityInternal(var1));
   }

   public Explosion(
      Level var1,
      @Nullable Entity var2,
      double var3,
      double var5,
      double var7,
      float var9,
      List<BlockPos> var10,
      Explosion.BlockInteraction var11,
      ParticleOptions var12,
      ParticleOptions var13,
      SoundEvent var14
   ) {
      this(var1, var2, getDefaultDamageSource(var1, var2), null, var3, var5, var7, var9, false, var11, var12, var13, var14);
      this.toBlow.addAll(var10);
   }

   public Explosion(
      Level var1,
      @Nullable Entity var2,
      double var3,
      double var5,
      double var7,
      float var9,
      boolean var10,
      Explosion.BlockInteraction var11,
      List<BlockPos> var12
   ) {
      this(var1, var2, var3, var5, var7, var9, var10, var11);
      this.toBlow.addAll(var12);
   }

   public Explosion(Level var1, @Nullable Entity var2, double var3, double var5, double var7, float var9, boolean var10, Explosion.BlockInteraction var11) {
      this(
         var1,
         var2,
         getDefaultDamageSource(var1, var2),
         null,
         var3,
         var5,
         var7,
         var9,
         var10,
         var11,
         ParticleTypes.EXPLOSION,
         ParticleTypes.EXPLOSION_EMITTER,
         SoundEvents.GENERIC_EXPLODE
      );
   }

   public Explosion(
      Level var1,
      @Nullable Entity var2,
      @Nullable DamageSource var3,
      @Nullable ExplosionDamageCalculator var4,
      double var5,
      double var7,
      double var9,
      float var11,
      boolean var12,
      Explosion.BlockInteraction var13,
      ParticleOptions var14,
      ParticleOptions var15,
      SoundEvent var16
   ) {
      super();
      this.level = var1;
      this.source = var2;
      this.radius = var11;
      this.x = var5;
      this.y = var7;
      this.z = var9;
      this.fire = var12;
      this.blockInteraction = var13;
      this.damageSource = var3 == null ? var1.damageSources().explosion(this) : var3;
      this.damageCalculator = var4 == null ? this.makeDamageCalculator(var2) : var4;
      this.smallExplosionParticles = var14;
      this.largeExplosionParticles = var15;
      this.explosionSound = var16;
   }

   private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity var1) {
      return (ExplosionDamageCalculator)(var1 == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator(var1));
   }

   public static float getSeenPercent(Vec3 var0, Entity var1) {
      AABB var2 = var1.getBoundingBox();
      double var3 = 1.0 / ((var2.maxX - var2.minX) * 2.0 + 1.0);
      double var5 = 1.0 / ((var2.maxY - var2.minY) * 2.0 + 1.0);
      double var7 = 1.0 / ((var2.maxZ - var2.minZ) * 2.0 + 1.0);
      double var9 = (1.0 - Math.floor(1.0 / var3) * var3) / 2.0;
      double var11 = (1.0 - Math.floor(1.0 / var7) * var7) / 2.0;
      if (!(var3 < 0.0) && !(var5 < 0.0) && !(var7 < 0.0)) {
         int var13 = 0;
         int var14 = 0;

         for(double var15 = 0.0; var15 <= 1.0; var15 += var3) {
            for(double var17 = 0.0; var17 <= 1.0; var17 += var5) {
               for(double var19 = 0.0; var19 <= 1.0; var19 += var7) {
                  double var21 = Mth.lerp(var15, var2.minX, var2.maxX);
                  double var23 = Mth.lerp(var17, var2.minY, var2.maxY);
                  double var25 = Mth.lerp(var19, var2.minZ, var2.maxZ);
                  Vec3 var27 = new Vec3(var21 + var9, var23, var25 + var11);
                  if (var1.level().clip(new ClipContext(var27, var0, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var1)).getType()
                     == HitResult.Type.MISS) {
                     ++var13;
                  }

                  ++var14;
               }
            }
         }

         return (float)var13 / (float)var14;
      } else {
         return 0.0F;
      }
   }

   public float radius() {
      return this.radius;
   }

   public Vec3 center() {
      return new Vec3(this.x, this.y, this.z);
   }

   public void explode() {
      this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
      HashSet var1 = Sets.newHashSet();
      boolean var2 = true;

      for(int var3 = 0; var3 < 16; ++var3) {
         for(int var4 = 0; var4 < 16; ++var4) {
            for(int var5 = 0; var5 < 16; ++var5) {
               if (var3 == 0 || var3 == 15 || var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15) {
                  double var6 = (double)((float)var3 / 15.0F * 2.0F - 1.0F);
                  double var8 = (double)((float)var4 / 15.0F * 2.0F - 1.0F);
                  double var10 = (double)((float)var5 / 15.0F * 2.0F - 1.0F);
                  double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
                  var6 /= var12;
                  var8 /= var12;
                  var10 /= var12;
                  float var14 = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                  double var15 = this.x;
                  double var17 = this.y;
                  double var19 = this.z;

                  for(float var21 = 0.3F; var14 > 0.0F; var14 -= 0.22500001F) {
                     BlockPos var22 = BlockPos.containing(var15, var17, var19);
                     BlockState var23 = this.level.getBlockState(var22);
                     FluidState var24 = this.level.getFluidState(var22);
                     if (!this.level.isInWorldBounds(var22)) {
                        break;
                     }

                     Optional var25 = this.damageCalculator.getBlockExplosionResistance(this, this.level, var22, var23, var24);
                     if (var25.isPresent()) {
                        var14 -= (var25.get() + 0.3F) * 0.3F;
                     }

                     if (var14 > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, var22, var23, var14)) {
                        var1.add(var22);
                     }

                     var15 += var6 * 0.30000001192092896;
                     var17 += var8 * 0.30000001192092896;
                     var19 += var10 * 0.30000001192092896;
                  }
               }
            }
         }
      }

      this.toBlow.addAll(var1);
      float var30 = this.radius * 2.0F;
      int var31 = Mth.floor(this.x - (double)var30 - 1.0);
      int var32 = Mth.floor(this.x + (double)var30 + 1.0);
      int var34 = Mth.floor(this.y - (double)var30 - 1.0);
      int var7 = Mth.floor(this.y + (double)var30 + 1.0);
      int var36 = Mth.floor(this.z - (double)var30 - 1.0);
      int var9 = Mth.floor(this.z + (double)var30 + 1.0);
      List var38 = this.level.getEntities(this.source, new AABB((double)var31, (double)var34, (double)var36, (double)var32, (double)var7, (double)var9));
      Vec3 var11 = new Vec3(this.x, this.y, this.z);

      for(Entity var13 : var38) {
         if (!var13.ignoreExplosion(this)) {
            double var40 = Math.sqrt(var13.distanceToSqr(var11)) / (double)var30;
            if (var40 <= 1.0) {
               double var16 = var13.getX() - this.x;
               double var18 = (var13 instanceof PrimedTnt ? var13.getY() : var13.getEyeY()) - this.y;
               double var20 = var13.getZ() - this.z;
               double var47 = Math.sqrt(var16 * var16 + var18 * var18 + var20 * var20);
               if (var47 != 0.0) {
                  var16 /= var47;
                  var18 /= var47;
                  var20 /= var47;
                  if (this.damageCalculator.shouldDamageEntity(this, var13)) {
                     var13.hurt(this.damageSource, this.damageCalculator.getEntityDamageAmount(this, var13));
                  }

                  double var48 = (1.0 - var40) * (double)getSeenPercent(var11, var13);
                  double var26;
                  if (var13 instanceof LivingEntity var28) {
                     var26 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)var28, var48);
                  } else {
                     var26 = var48;
                  }

                  var16 *= var26;
                  var18 *= var26;
                  var20 *= var26;
                  Vec3 var49 = new Vec3(var16, var18, var20);
                  var13.setDeltaMovement(var13.getDeltaMovement().add(var49));
                  if (var13 instanceof Player var29
                     && !((Player)var29).isSpectator()
                     && (!((Player)var29).isCreative() || !((Player)var29).getAbilities().flying)) {
                     this.hitPlayers.put((Player)var29, var49);
                  }
               }
            }
         }
      }
   }

   public void finalizeExplosion(boolean var1) {
      if (this.level.isClientSide) {
         this.level
            .playLocalSound(
               this.x,
               this.y,
               this.z,
               this.explosionSound,
               SoundSource.BLOCKS,
               4.0F,
               (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F,
               false
            );
      }

      boolean var2 = this.interactsWithBlocks();
      if (var1) {
         ParticleOptions var3;
         if (!(this.radius < 2.0F) && var2) {
            var3 = this.largeExplosionParticles;
         } else {
            var3 = this.smallExplosionParticles;
         }

         this.level.addParticle(var3, this.x, this.y, this.z, 1.0, 0.0, 0.0);
      }

      if (var2) {
         this.level.getProfiler().push("explosion_blocks");
         ArrayList var6 = new ArrayList();
         Util.shuffle(this.toBlow, this.level.random);
         ObjectListIterator var4 = this.toBlow.iterator();

         while(var4.hasNext()) {
            BlockPos var5 = (BlockPos)var4.next();
            this.level.getBlockState(var5).onExplosionHit(this.level, var5, this, (var1x, var2x) -> addOrAppendStack(var6, var1x, var2x));
         }

         for(Pair var10 : var6) {
            Block.popResource(this.level, (BlockPos)var10.getSecond(), (ItemStack)var10.getFirst());
         }

         this.level.getProfiler().pop();
      }

      if (this.fire) {
         ObjectListIterator var7 = this.toBlow.iterator();

         while(var7.hasNext()) {
            BlockPos var9 = (BlockPos)var7.next();
            if (this.random.nextInt(3) == 0
               && this.level.getBlockState(var9).isAir()
               && this.level.getBlockState(var9.below()).isSolidRender(this.level, var9.below())) {
               this.level.setBlockAndUpdate(var9, BaseFireBlock.getState(this.level, var9));
            }
         }
      }
   }

   private static void addOrAppendStack(List<Pair<ItemStack, BlockPos>> var0, ItemStack var1, BlockPos var2) {
      for(int var3 = 0; var3 < var0.size(); ++var3) {
         Pair var4 = (Pair)var0.get(var3);
         ItemStack var5 = (ItemStack)var4.getFirst();
         if (ItemEntity.areMergable(var5, var1)) {
            var0.set(var3, Pair.of(ItemEntity.merge(var5, var1, 16), (BlockPos)var4.getSecond()));
            if (var1.isEmpty()) {
               return;
            }
         }
      }

      var0.add(Pair.of(var1, var2));
   }

   public boolean interactsWithBlocks() {
      return this.blockInteraction != Explosion.BlockInteraction.KEEP;
   }

   public Map<Player, Vec3> getHitPlayers() {
      return this.hitPlayers;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   private static LivingEntity getIndirectSourceEntityInternal(@Nullable Entity var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof PrimedTnt var4) {
         return var4.getOwner();
      } else if (var0 instanceof LivingEntity) {
         return (LivingEntity)var0;
      } else {
         if (var0 instanceof Projectile var1) {
            Entity var2 = var1.getOwner();
            if (var2 instanceof LivingEntity) {
               return (LivingEntity)var2;
            }
         }

         return null;
      }
   }

   @Nullable
   public LivingEntity getIndirectSourceEntity() {
      return getIndirectSourceEntityInternal(this.source);
   }

   @Nullable
   public Entity getDirectSourceEntity() {
      return this.source;
   }

   public void clearToBlow() {
      this.toBlow.clear();
   }

   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }

   public Explosion.BlockInteraction getBlockInteraction() {
      return this.blockInteraction;
   }

   public ParticleOptions getSmallExplosionParticles() {
      return this.smallExplosionParticles;
   }

   public ParticleOptions getLargeExplosionParticles() {
      return this.largeExplosionParticles;
   }

   public SoundEvent getExplosionSound() {
      return this.explosionSound;
   }

   public static enum BlockInteraction {
      KEEP,
      DESTROY,
      DESTROY_WITH_DECAY,
      TRIGGER_BLOCK;

      private BlockInteraction() {
      }
   }
}
