package net.minecraft.world.level;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ServerExplosion implements Explosion {
   private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
   private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
   private static final float LARGE_EXPLOSION_RADIUS = 2.0F;
   private final boolean fire;
   private final Explosion.BlockInteraction blockInteraction;
   private final ServerLevel level;
   private final Vec3 center;
   @Nullable
   private final Entity source;
   private final float radius;
   private final DamageSource damageSource;
   private final ExplosionDamageCalculator damageCalculator;
   private final Map<Player, Vec3> hitPlayers = new HashMap();

   public ServerExplosion(ServerLevel var1, @Nullable Entity var2, @Nullable DamageSource var3, @Nullable ExplosionDamageCalculator var4, Vec3 var5, float var6, boolean var7, Explosion.BlockInteraction var8) {
      super();
      this.level = var1;
      this.source = var2;
      this.radius = var6;
      this.center = var5;
      this.fire = var7;
      this.blockInteraction = var8;
      this.damageSource = var3 == null ? var1.damageSources().explosion(this) : var3;
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
                  if (var1.level().clip(new ClipContext(var27, var0, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var1)).getType() == HitResult.Type.MISS) {
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
      return this.center;
   }

   private List<BlockPos> calculateExplodedPositions() {
      HashSet var1 = new HashSet();
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
                  double var15 = this.center.x;
                  double var17 = this.center.y;
                  double var19 = this.center.z;

                  for(float var21 = 0.3F; var14 > 0.0F; var14 -= 0.22500001F) {
                     BlockPos var22 = BlockPos.containing(var15, var17, var19);
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

      return new ObjectArrayList(var1);
   }

   private void hurtEntities() {
      float var1 = this.radius * 2.0F;
      int var2 = Mth.floor(this.center.x - (double)var1 - 1.0);
      int var3 = Mth.floor(this.center.x + (double)var1 + 1.0);
      int var4 = Mth.floor(this.center.y - (double)var1 - 1.0);
      int var5 = Mth.floor(this.center.y + (double)var1 + 1.0);
      int var6 = Mth.floor(this.center.z - (double)var1 - 1.0);
      int var7 = Mth.floor(this.center.z + (double)var1 + 1.0);

      for(Entity var10 : this.level.getEntities(this.source, new AABB((double)var2, (double)var4, (double)var6, (double)var3, (double)var5, (double)var7))) {
         if (!var10.ignoreExplosion(this)) {
            double var11 = Math.sqrt(var10.distanceToSqr(this.center)) / (double)var1;
            if (var11 <= 1.0) {
               double var13 = var10.getX() - this.center.x;
               double var15 = (var10 instanceof PrimedTnt ? var10.getY() : var10.getEyeY()) - this.center.y;
               double var17 = var10.getZ() - this.center.z;
               double var19 = Math.sqrt(var13 * var13 + var15 * var15 + var17 * var17);
               if (var19 != 0.0) {
                  var13 /= var19;
                  var15 /= var19;
                  var17 /= var19;
                  boolean var21 = this.damageCalculator.shouldDamageEntity(this, var10);
                  float var22 = this.damageCalculator.getKnockbackMultiplier(var10);
                  float var23 = !var21 && var22 == 0.0F ? 0.0F : getSeenPercent(this.center, var10);
                  if (var21) {
                     var10.hurtServer(this.level, this.damageSource, this.damageCalculator.getEntityDamageAmount(this, var10, var23));
                  }

                  double var24 = (1.0 - var11) * (double)var23 * (double)var22;
                  double var26;
                  if (var10 instanceof LivingEntity) {
                     LivingEntity var28 = (LivingEntity)var10;
                     var26 = var24 * (1.0 - var28.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
                  } else {
                     var26 = var24;
                  }

                  var13 *= var26;
                  var15 *= var26;
                  var17 *= var26;
                  Vec3 var36 = new Vec3(var13, var15, var17);
                  var10.push(var36);
                  if (var10 instanceof Player) {
                     Player var29 = (Player)var10;
                     if (!var29.isSpectator() && (!var29.isCreative() || !var29.getAbilities().flying)) {
                        this.hitPlayers.put(var29, var36);
                     }
                  }

                  var10.onExplosionHit(this.source);
               }
            }
         }
      }

   }

   private void interactWithBlocks(List<BlockPos> var1) {
      ArrayList var2 = new ArrayList();
      Util.shuffle(var1, this.level.random);

      for(BlockPos var4 : var1) {
         this.level.getBlockState(var4).onExplosionHit(this.level, var4, this, (var1x, var2x) -> addOrAppendStack(var2, var1x, var2x));
      }

      for(StackCollector var6 : var2) {
         Block.popResource(this.level, var6.pos, var6.stack);
      }

   }

   private void createFire(List<BlockPos> var1) {
      for(BlockPos var3 : var1) {
         if (this.level.random.nextInt(3) == 0 && this.level.getBlockState(var3).isAir() && this.level.getBlockState(var3.below()).isSolidRender()) {
            this.level.setBlockAndUpdate(var3, BaseFireBlock.getState(this.level, var3));
         }
      }

   }

   public void explode() {
      this.level.gameEvent(this.source, GameEvent.EXPLODE, this.center);
      List var1 = this.calculateExplodedPositions();
      this.hurtEntities();
      if (this.interactsWithBlocks()) {
         ProfilerFiller var2 = Profiler.get();
         var2.push("explosion_blocks");
         this.interactWithBlocks(var1);
         var2.pop();
      }

      if (this.fire) {
         this.createFire(var1);
      }

   }

   private static void addOrAppendStack(List<StackCollector> var0, ItemStack var1, BlockPos var2) {
      for(StackCollector var4 : var0) {
         var4.tryMerge(var1);
         if (var1.isEmpty()) {
            return;
         }
      }

      var0.add(new StackCollector(var2, var1));
   }

   private boolean interactsWithBlocks() {
      return this.blockInteraction != Explosion.BlockInteraction.KEEP;
   }

   public Map<Player, Vec3> getHitPlayers() {
      return this.hitPlayers;
   }

   public ServerLevel level() {
      return this.level;
   }

   @Nullable
   public LivingEntity getIndirectSourceEntity() {
      return Explosion.getIndirectSourceEntity(this.source);
   }

   @Nullable
   public Entity getDirectSourceEntity() {
      return this.source;
   }

   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public Explosion.BlockInteraction getBlockInteraction() {
      return this.blockInteraction;
   }

   public boolean canTriggerBlocks() {
      if (this.blockInteraction != Explosion.BlockInteraction.TRIGGER_BLOCK) {
         return false;
      } else {
         return this.source != null && this.source.getType() == EntityType.BREEZE_WIND_CHARGE ? this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) : true;
      }
   }

   public boolean shouldAffectBlocklikeEntities() {
      boolean var1 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
      boolean var2 = this.source == null || !this.source.isInWater();
      boolean var3 = this.source == null || this.source.getType() != EntityType.BREEZE_WIND_CHARGE && this.source.getType() != EntityType.WIND_CHARGE;
      if (var1) {
         return var2 && var3;
      } else {
         return this.blockInteraction.shouldAffectBlocklikeEntities() && var2 && var3;
      }
   }

   public boolean isSmall() {
      return this.radius < 2.0F || !this.interactsWithBlocks();
   }

   static class StackCollector {
      final BlockPos pos;
      ItemStack stack;

      StackCollector(BlockPos var1, ItemStack var2) {
         super();
         this.pos = var1;
         this.stack = var2;
      }

      public void tryMerge(ItemStack var1) {
         if (ItemEntity.areMergable(this.stack, var1)) {
            this.stack = ItemEntity.merge(this.stack, var1, 16);
         }

      }
   }
}
