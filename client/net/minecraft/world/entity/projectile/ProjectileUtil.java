package net.minecraft.world.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class ProjectileUtil {
   private static final float DEFAULT_ENTITY_HIT_RESULT_MARGIN = 0.3F;

   public ProjectileUtil() {
      super();
   }

   public static HitResult getHitResultOnMoveVector(Entity var0, Predicate<Entity> var1) {
      Vec3 var2 = var0.getDeltaMovement();
      Level var3 = var0.level();
      Vec3 var4 = var0.position();
      return getHitResult(var4, var0, var1, var2, var3, 0.3F, ClipContext.Block.COLLIDER);
   }

   public static HitResult getHitResultOnMoveVector(Entity var0, Predicate<Entity> var1, ClipContext.Block var2) {
      Vec3 var3 = var0.getDeltaMovement();
      Level var4 = var0.level();
      Vec3 var5 = var0.position();
      return getHitResult(var5, var0, var1, var3, var4, 0.3F, var2);
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

   @Nullable
   public static EntityHitResult getEntityHitResult(Entity var0, Vec3 var1, Vec3 var2, AABB var3, Predicate<Entity> var4, double var5) {
      Level var7 = var0.level();
      double var8 = var5;
      Entity var10 = null;
      Vec3 var11 = null;

      for (Entity var13 : var7.getEntities(var0, var3, var4)) {
         AABB var14 = var13.getBoundingBox().inflate((double)var13.getPickRadius());
         Optional var15 = var14.clip(var1, var2);
         if (var14.contains(var1)) {
            if (var8 >= 0.0) {
               var10 = var13;
               var11 = var15.orElse(var1);
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

      return var10 == null ? null : new EntityHitResult(var10, var11);
   }

   @Nullable
   public static EntityHitResult getEntityHitResult(Level var0, Entity var1, Vec3 var2, Vec3 var3, AABB var4, Predicate<Entity> var5) {
      return getEntityHitResult(var0, var1, var2, var3, var4, var5, 0.3F);
   }

   @Nullable
   public static EntityHitResult getEntityHitResult(Level var0, Entity var1, Vec3 var2, Vec3 var3, AABB var4, Predicate<Entity> var5, float var6) {
      double var7 = 1.7976931348623157E308;
      Optional var9 = Optional.empty();
      Entity var10 = null;

      for (Entity var12 : var0.getEntities(var1, var4, var5)) {
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

      return var10 == null ? null : new EntityHitResult(var10, (Vec3)var9.get());
   }

   public static void rotateTowardsMovement(Entity var0, float var1) {
      Vec3 var2 = var0.getDeltaMovement();
      if (var2.lengthSqr() != 0.0) {
         double var3 = var2.horizontalDistance();
         var0.setYRot((float)(Mth.atan2(var2.z, var2.x) * 57.2957763671875) + 90.0F);
         var0.setXRot((float)(Mth.atan2(var3, var2.y) * 57.2957763671875) - 90.0F);

         while (var0.getXRot() - var0.xRotO < -180.0F) {
            var0.xRotO -= 360.0F;
         }

         while (var0.getXRot() - var0.xRotO >= 180.0F) {
            var0.xRotO += 360.0F;
         }

         while (var0.getYRot() - var0.yRotO < -180.0F) {
            var0.yRotO -= 360.0F;
         }

         while (var0.getYRot() - var0.yRotO >= 180.0F) {
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
