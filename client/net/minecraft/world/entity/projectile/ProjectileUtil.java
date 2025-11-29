package net.minecraft.world.entity.projectile;

import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class ProjectileUtil {
   public static final float DEFAULT_ENTITY_HIT_RESULT_MARGIN = 0.3F;

   public ProjectileUtil() {
      super();
   }

   public static HitResult getHitResultOnMoveVector(Entity var0, Predicate<Entity> var1) {
      Vec3 var2 = var0.getDeltaMovement();
      Level var3 = var0.level();
      Vec3 var4 = var0.position();
      return getHitResult(var4, var0, var1, var2, var3, computeMargin(var0), ClipContext.Block.COLLIDER);
   }

   public static Either<BlockHitResult, Collection<EntityHitResult>> getHitEntitiesAlong(Entity var0, AttackRange var1, Predicate<Entity> var2, ClipContext.Block var3) {
      Vec3 var4 = var0.getHeadLookAngle();
      Vec3 var5 = var0.getEyePosition();
      Vec3 var6 = var5.add(var4.scale((double)var1.effectiveMinRange(var0)));
      double var7 = var0.getKnownMovement().dot(var4);
      Vec3 var9 = var5.add(var4.scale((double)var1.effectiveMaxRange(var0) + Math.max(0.0, var7)));
      return getHitEntitiesAlong(var0, var5, var6, var2, var9, var1.hitboxMargin(), var3);
   }

   public static HitResult getHitResultOnMoveVector(Entity var0, Predicate<Entity> var1, ClipContext.Block var2) {
      Vec3 var3 = var0.getDeltaMovement();
      Level var4 = var0.level();
      Vec3 var5 = var0.position();
      return getHitResult(var5, var0, var1, var3, var4, computeMargin(var0), var2);
   }

   public static HitResult getHitResultOnViewVector(Entity var0, Predicate<Entity> var1, double var2) {
      Vec3 var4 = var0.getViewVector(0.0F).scale(var2);
      Level var5 = var0.level();
      Vec3 var6 = var0.getEyePosition();
      return getHitResult(var6, var0, var1, var4, var5, 0.0F, ClipContext.Block.COLLIDER);
   }

   private static HitResult getHitResult(Vec3 var0, Entity var1, Predicate<Entity> var2, Vec3 var3, Level var4, float var5, ClipContext.Block var6) {
      Vec3 var7 = var0.add(var3);
      Object var8 = var4.clipIncludingBorder(new ClipContext(var0, var7, var6, ClipContext.Fluid.NONE, var1));
      if (((HitResult)var8).getType() != HitResult.Type.MISS) {
         var7 = ((HitResult)var8).getLocation();
      }

      EntityHitResult var9 = getEntityHitResult(var4, var1, var0, var7, var1.getBoundingBox().expandTowards(var3).inflate(1.0), var2, var5);
      if (var9 != null) {
         var8 = var9;
      }

      return (HitResult)var8;
   }

   private static Either<BlockHitResult, Collection<EntityHitResult>> getHitEntitiesAlong(Entity var0, Vec3 var1, Vec3 var2, Predicate<Entity> var3, Vec3 var4, float var5, ClipContext.Block var6) {
      Level var7 = var0.level();
      BlockHitResult var8 = var7.clipIncludingBorder(new ClipContext(var1, var4, var6, ClipContext.Fluid.NONE, var0));
      if (var8.getType() != HitResult.Type.MISS) {
         var4 = var8.getLocation();
         if (var1.distanceToSqr(var4) < var1.distanceToSqr(var2)) {
            return Either.left(var8);
         }
      }

      AABB var9 = AABB.ofSize(var2, (double)var5, (double)var5, (double)var5).expandTowards(var4.subtract(var2)).inflate(1.0);
      Collection var10 = getManyEntityHitResult(var7, var0, var2, var4, var9, var3, var5, var6, true);
      return !var10.isEmpty() ? Either.right(var10) : Either.left(var8);
   }

   public static @Nullable EntityHitResult getEntityHitResult(Entity var0, Vec3 var1, Vec3 var2, AABB var3, Predicate<Entity> var4, double var5) {
      Level var7 = var0.level();
      double var8 = var5;
      Entity var10 = null;
      Vec3 var11 = null;

      for(Entity var13 : var7.getEntities(var0, var3, var4)) {
         AABB var14 = var13.getBoundingBox().inflate((double)var13.getPickRadius());
         Optional var15 = var14.clip(var1, var2);
         if (var14.contains(var1)) {
            if (var8 >= 0.0) {
               var10 = var13;
               var11 = (Vec3)var15.orElse(var1);
               var8 = 0.0;
            }
         } else if (var15.isPresent()) {
            Vec3 var16 = (Vec3)var15.get();
            double var17 = var1.distanceToSqr(var16);
            if (var17 < var8 || var8 == 0.0) {
               if (var13.getRootVehicle() == var0.getRootVehicle()) {
                  if (var8 == 0.0) {
                     var10 = var13;
                     var11 = var16;
                  }
               } else {
                  var10 = var13;
                  var11 = var16;
                  var8 = var17;
               }
            }
         }
      }

      if (var10 == null) {
         return null;
      } else {
         return new EntityHitResult(var10, var11);
      }
   }

   public static @Nullable EntityHitResult getEntityHitResult(Level var0, Projectile var1, Vec3 var2, Vec3 var3, AABB var4, Predicate<Entity> var5) {
      return getEntityHitResult(var0, var1, var2, var3, var4, var5, computeMargin(var1));
   }

   public static float computeMargin(Entity var0) {
      return Math.max(0.0F, Math.min(0.3F, (float)(var0.tickCount - 2) / 20.0F));
   }

   public static @Nullable EntityHitResult getEntityHitResult(Level var0, Entity var1, Vec3 var2, Vec3 var3, AABB var4, Predicate<Entity> var5, float var6) {
      double var7 = 1.7976931348623157E308;
      Optional var9 = Optional.empty();
      Entity var10 = null;

      for(Entity var12 : var0.getEntities(var1, var4, var5)) {
         AABB var13 = var12.getBoundingBox().inflate((double)var6);
         Optional var14 = var13.clip(var2, var3);
         if (var14.isPresent()) {
            double var15 = var2.distanceToSqr((Vec3)var14.get());
            if (var15 < var7) {
               var10 = var12;
               var7 = var15;
               var9 = var14;
            }
         }
      }

      if (var10 == null) {
         return null;
      } else {
         return new EntityHitResult(var10, (Vec3)var9.get());
      }
   }

   public static Collection<EntityHitResult> getManyEntityHitResult(Level var0, Entity var1, Vec3 var2, Vec3 var3, AABB var4, Predicate<Entity> var5, boolean var6) {
      return getManyEntityHitResult(var0, var1, var2, var3, var4, var5, computeMargin(var1), ClipContext.Block.COLLIDER, var6);
   }

   public static Collection<EntityHitResult> getManyEntityHitResult(Level var0, Entity var1, Vec3 var2, Vec3 var3, AABB var4, Predicate<Entity> var5, float var6, ClipContext.Block var7, boolean var8) {
      ArrayList var9 = new ArrayList();

      for(Entity var11 : var0.getEntities(var1, var4, var5)) {
         AABB var12 = var11.getBoundingBox();
         if (var8 && var12.contains(var2)) {
            var9.add(new EntityHitResult(var11, var2));
         } else {
            Optional var13 = var12.clip(var2, var3);
            if (var13.isPresent()) {
               var9.add(new EntityHitResult(var11, (Vec3)var13.get()));
            } else if (!((double)var6 <= 0.0)) {
               Optional var14 = var12.inflate((double)var6).clip(var2, var3);
               if (!var14.isEmpty()) {
                  Vec3 var15 = (Vec3)var14.get();
                  Vec3 var16 = var12.getCenter();
                  BlockHitResult var17 = var0.clipIncludingBorder(new ClipContext(var15, var16, var7, ClipContext.Fluid.NONE, var1));
                  if (var17.getType() != HitResult.Type.MISS) {
                     var16 = var17.getLocation();
                  }

                  Optional var18 = var11.getBoundingBox().clip(var15, var16);
                  if (var18.isPresent()) {
                     var9.add(new EntityHitResult(var11, (Vec3)var18.get()));
                  }
               }
            }
         }
      }

      return var9;
   }

   public static void rotateTowardsMovement(Entity var0, float var1) {
      Vec3 var2 = var0.getDeltaMovement();
      if (var2.lengthSqr() != 0.0) {
         double var3 = var2.horizontalDistance();
         var0.setYRot((float)(Mth.atan2(var2.z, var2.x) * 57.2957763671875) + 90.0F);
         var0.setXRot((float)(Mth.atan2(var3, var2.y) * 57.2957763671875) - 90.0F);

         while(var0.getXRot() - var0.xRotO < -180.0F) {
            var0.xRotO -= 360.0F;
         }

         while(var0.getXRot() - var0.xRotO >= 180.0F) {
            var0.xRotO += 360.0F;
         }

         while(var0.getYRot() - var0.yRotO < -180.0F) {
            var0.yRotO -= 360.0F;
         }

         while(var0.getYRot() - var0.yRotO >= 180.0F) {
            var0.yRotO += 360.0F;
         }

         var0.setXRot(Mth.lerp(var1, var0.xRotO, var0.getXRot()));
         var0.setYRot(Mth.lerp(var1, var0.yRotO, var0.getYRot()));
      }
   }

   public static InteractionHand getWeaponHoldingHand(LivingEntity var0, Item var1) {
      return var0.getMainHandItem().is(var1) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
   }

   public static AbstractArrow getMobArrow(LivingEntity var0, ItemStack var1, float var2, @Nullable ItemStack var3) {
      ArrowItem var4 = (ArrowItem)(var1.getItem() instanceof ArrowItem ? var1.getItem() : Items.ARROW);
      AbstractArrow var5 = var4.createArrow(var0.level(), var1, var0, var3);
      var5.setBaseDamageFromMob(var2);
      return var5;
   }
}
