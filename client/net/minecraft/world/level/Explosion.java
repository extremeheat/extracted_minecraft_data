package net.minecraft.world.level;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Explosion {
   private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
   private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
   private final boolean fire;
   private final BlockInteraction blockInteraction;
   private final RandomSource random;
   private final Level level;
   private final double x;
   private final double y;
   private final double z;
   @Nullable
   private final Entity source;
   private final float radius;
   private final DamageSource damageSource;
   private final ExplosionDamageCalculator damageCalculator;
   private final ObjectArrayList<BlockPos> toBlow;
   private final Map<Player, Vec3> hitPlayers;

   public Explosion(Level var1, @Nullable Entity var2, double var3, double var5, double var7, float var9) {
      this(var1, var2, var3, var5, var7, var9, false, Explosion.BlockInteraction.DESTROY);
   }

   public Explosion(Level var1, @Nullable Entity var2, double var3, double var5, double var7, float var9, List<BlockPos> var10) {
      this(var1, var2, var3, var5, var7, var9, false, Explosion.BlockInteraction.DESTROY, var10);
   }

   public Explosion(Level var1, @Nullable Entity var2, double var3, double var5, double var7, float var9, boolean var10, BlockInteraction var11, List<BlockPos> var12) {
      this(var1, var2, var3, var5, var7, var9, var10, var11);
      this.toBlow.addAll(var12);
   }

   public Explosion(Level var1, @Nullable Entity var2, double var3, double var5, double var7, float var9, boolean var10, BlockInteraction var11) {
      this(var1, var2, (DamageSource)null, (ExplosionDamageCalculator)null, var3, var5, var7, var9, var10, var11);
   }

   public Explosion(Level var1, @Nullable Entity var2, @Nullable DamageSource var3, @Nullable ExplosionDamageCalculator var4, double var5, double var7, double var9, float var11, boolean var12, BlockInteraction var13) {
      super();
      this.random = RandomSource.create();
      this.toBlow = new ObjectArrayList();
      this.hitPlayers = Maps.newHashMap();
      this.level = var1;
      this.source = var2;
      this.radius = var11;
      this.x = var5;
      this.y = var7;
      this.z = var9;
      this.fire = var12;
      this.blockInteraction = var13;
      this.damageSource = var3 == null ? DamageSource.explosion(this) : var3;
      this.damageCalculator = var4 == null ? this.makeDamageCalculator(var2) : var4;
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
                  if (var1.level.clip(new ClipContext(var27, var0, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var1)).getType() == HitResult.Type.MISS) {
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

   public void explode() {
      this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
      HashSet var1 = Sets.newHashSet();
      boolean var2 = true;

      int var4;
      int var5;
      for(int var3 = 0; var3 < 16; ++var3) {
         for(var4 = 0; var4 < 16; ++var4) {
            for(var5 = 0; var5 < 16; ++var5) {
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
                     BlockPos var22 = new BlockPos(var15, var17, var19);
                     BlockState var23 = this.level.getBlockState(var22);
                     FluidState var24 = this.level.getFluidState(var22);
                     if (!this.level.isInWorldBounds(var22)) {
                        break;
                     }

                     Optional var25 = this.damageCalculator.getBlockExplosionResistance(this, this.level, var22, var23, var24);
                     if (var25.isPresent()) {
                        var14 -= ((Float)var25.get() + 0.3F) * 0.3F;
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
      float var31 = this.radius * 2.0F;
      var4 = Mth.floor(this.x - (double)var31 - 1.0);
      var5 = Mth.floor(this.x + (double)var31 + 1.0);
      int var32 = Mth.floor(this.y - (double)var31 - 1.0);
      int var7 = Mth.floor(this.y + (double)var31 + 1.0);
      int var33 = Mth.floor(this.z - (double)var31 - 1.0);
      int var9 = Mth.floor(this.z + (double)var31 + 1.0);
      List var34 = this.level.getEntities(this.source, new AABB((double)var4, (double)var32, (double)var33, (double)var5, (double)var7, (double)var9));
      Vec3 var11 = new Vec3(this.x, this.y, this.z);

      for(int var35 = 0; var35 < var34.size(); ++var35) {
         Entity var13 = (Entity)var34.get(var35);
         if (!var13.ignoreExplosion()) {
            double var36 = Math.sqrt(var13.distanceToSqr(var11)) / (double)var31;
            if (var36 <= 1.0) {
               double var16 = var13.getX() - this.x;
               double var18 = (var13 instanceof PrimedTnt ? var13.getY() : var13.getEyeY()) - this.y;
               double var20 = var13.getZ() - this.z;
               double var37 = Math.sqrt(var16 * var16 + var18 * var18 + var20 * var20);
               if (var37 != 0.0) {
                  var16 /= var37;
                  var18 /= var37;
                  var20 /= var37;
                  double var38 = (double)getSeenPercent(var11, var13);
                  double var26 = (1.0 - var36) * var38;
                  var13.hurt(this.getDamageSource(), (float)((int)((var26 * var26 + var26) / 2.0 * 7.0 * (double)var31 + 1.0)));
                  double var28 = var26;
                  if (var13 instanceof LivingEntity) {
                     var28 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)var13, var26);
                  }

                  var13.setDeltaMovement(var13.getDeltaMovement().add(var16 * var28, var18 * var28, var20 * var28));
                  if (var13 instanceof Player) {
                     Player var30 = (Player)var13;
                     if (!var30.isSpectator() && (!var30.isCreative() || !var30.getAbilities().flying)) {
                        this.hitPlayers.put(var30, new Vec3(var16 * var26, var18 * var26, var20 * var26));
                     }
                  }
               }
            }
         }
      }

   }

   public void finalizeExplosion(boolean var1) {
      if (this.level.isClientSide) {
         this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
      }

      boolean var2 = this.blockInteraction != Explosion.BlockInteraction.NONE;
      if (var1) {
         if (!(this.radius < 2.0F) && var2) {
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
         } else {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
         }
      }

      if (var2) {
         ObjectArrayList var3 = new ObjectArrayList();
         boolean var4 = this.getSourceMob() instanceof Player;
         Util.shuffle(this.toBlow, this.level.random);
         ObjectListIterator var5 = this.toBlow.iterator();

         while(var5.hasNext()) {
            BlockPos var6 = (BlockPos)var5.next();
            BlockState var7 = this.level.getBlockState(var6);
            Block var8 = var7.getBlock();
            if (!var7.isAir()) {
               BlockPos var9 = var6.immutable();
               this.level.getProfiler().push("explosion_blocks");
               if (var8.dropFromExplosion(this)) {
                  Level var11 = this.level;
                  if (var11 instanceof ServerLevel) {
                     ServerLevel var10 = (ServerLevel)var11;
                     BlockEntity var16 = var7.hasBlockEntity() ? this.level.getBlockEntity(var6) : null;
                     LootContext.Builder var12 = (new LootContext.Builder(var10)).withRandom(this.level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var6)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var16).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
                     if (this.blockInteraction == Explosion.BlockInteraction.DESTROY) {
                        var12.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
                     }

                     var7.spawnAfterBreak(var10, var6, ItemStack.EMPTY, var4);
                     var7.getDrops(var12).forEach((var2x) -> {
                        addBlockDrops(var3, var2x, var9);
                     });
                  }
               }

               this.level.setBlock(var6, Blocks.AIR.defaultBlockState(), 3);
               var8.wasExploded(this.level, var6, this);
               this.level.getProfiler().pop();
            }
         }

         var5 = var3.iterator();

         while(var5.hasNext()) {
            Pair var15 = (Pair)var5.next();
            Block.popResource(this.level, (BlockPos)var15.getSecond(), (ItemStack)var15.getFirst());
         }
      }

      if (this.fire) {
         ObjectListIterator var13 = this.toBlow.iterator();

         while(var13.hasNext()) {
            BlockPos var14 = (BlockPos)var13.next();
            if (this.random.nextInt(3) == 0 && this.level.getBlockState(var14).isAir() && this.level.getBlockState(var14.below()).isSolidRender(this.level, var14.below())) {
               this.level.setBlockAndUpdate(var14, BaseFireBlock.getState(this.level, var14));
            }
         }
      }

   }

   private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> var0, ItemStack var1, BlockPos var2) {
      int var3 = var0.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         Pair var5 = (Pair)var0.get(var4);
         ItemStack var6 = (ItemStack)var5.getFirst();
         if (ItemEntity.areMergable(var6, var1)) {
            ItemStack var7 = ItemEntity.merge(var6, var1, 16);
            var0.set(var4, Pair.of(var7, (BlockPos)var5.getSecond()));
            if (var1.isEmpty()) {
               return;
            }
         }
      }

      var0.add(Pair.of(var1, var2));
   }

   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public Map<Player, Vec3> getHitPlayers() {
      return this.hitPlayers;
   }

   @Nullable
   public LivingEntity getSourceMob() {
      if (this.source == null) {
         return null;
      } else if (this.source instanceof PrimedTnt) {
         return ((PrimedTnt)this.source).getOwner();
      } else if (this.source instanceof LivingEntity) {
         return (LivingEntity)this.source;
      } else {
         if (this.source instanceof Projectile) {
            Entity var1 = ((Projectile)this.source).getOwner();
            if (var1 instanceof LivingEntity) {
               return (LivingEntity)var1;
            }
         }

         return null;
      }
   }

   public void clearToBlow() {
      this.toBlow.clear();
   }

   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }

   public static enum BlockInteraction {
      NONE,
      BREAK,
      DESTROY;

      private BlockInteraction() {
      }

      // $FF: synthetic method
      private static BlockInteraction[] $values() {
         return new BlockInteraction[]{NONE, BREAK, DESTROY};
      }
   }
}
