package net.minecraft.world.entity.projectile;

import java.util.Iterator;
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
   public static HitResult getHitResult(Entity var0, Predicate<Entity> var1) {
      Vec3 var2 = var0.getDeltaMovement();
      Level var3 = var0.level;
      Vec3 var4 = var0.position();
      Vec3 var5 = var4.add(var2);
      Object var6 = var3.clip(new ClipContext(var4, var5, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var0));
      if (((HitResult)var6).getType() != HitResult.Type.MISS) {
         var5 = ((HitResult)var6).getLocation();
      }

      EntityHitResult var7 = getEntityHitResult(var3, var0, var4, var5, var0.getBoundingBox().expandTowards(var0.getDeltaMovement()).inflate(1.0D), var1);
      if (var7 != null) {
         var6 = var7;
      }

      return (HitResult)var6;
   }

   @Nullable
   public static EntityHitResult getEntityHitResult(Entity var0, Vec3 var1, Vec3 var2, AABB var3, Predicate<Entity> var4, double var5) {
      Level var7 = var0.level;
      double var8 = var5;
      Entity var10 = null;
      Vec3 var11 = null;
      Iterator var12 = var7.getEntities(var0, var3, var4).iterator();

      while(true) {
         while(var12.hasNext()) {
            Entity var13 = (Entity)var12.next();
            AABB var14 = var13.getBoundingBox().inflate((double)var13.getPickRadius());
            Optional var15 = var14.clip(var1, var2);
            if (var14.contains(var1)) {
               if (var8 >= 0.0D) {
                  var10 = var13;
                  var11 = (Vec3)var15.orElse(var1);
                  var8 = 0.0D;
               }
            } else if (var15.isPresent()) {
               Vec3 var16 = (Vec3)var15.get();
               double var17 = var1.distanceToSqr(var16);
               if (var17 < var8 || var8 == 0.0D) {
                  if (var13.getRootVehicle() == var0.getRootVehicle()) {
                     if (var8 == 0.0D) {
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
         }

         return new EntityHitResult(var10, var11);
      }
   }

   @Nullable
   public static EntityHitResult getEntityHitResult(Level var0, Entity var1, Vec3 var2, Vec3 var3, AABB var4, Predicate<Entity> var5) {
      double var6 = 1.7976931348623157E308D;
      Entity var8 = null;
      Iterator var9 = var0.getEntities(var1, var4, var5).iterator();

      while(var9.hasNext()) {
         Entity var10 = (Entity)var9.next();
         AABB var11 = var10.getBoundingBox().inflate(0.30000001192092896D);
         Optional var12 = var11.clip(var2, var3);
         if (var12.isPresent()) {
            double var13 = var2.distanceToSqr((Vec3)var12.get());
            if (var13 < var6) {
               var8 = var10;
               var6 = var13;
            }
         }
      }

      if (var8 == null) {
         return null;
      } else {
         return new EntityHitResult(var8);
      }
   }

   public static final void rotateTowardsMovement(Entity var0, float var1) {
      Vec3 var2 = var0.getDeltaMovement();
      if (var2.lengthSqr() != 0.0D) {
         float var3 = Mth.sqrt(Entity.getHorizontalDistanceSqr(var2));
         var0.yRot = (float)(Mth.atan2(var2.z, var2.x) * 57.2957763671875D) + 90.0F;

         for(var0.xRot = (float)(Mth.atan2((double)var3, var2.y) * 57.2957763671875D) - 90.0F; var0.xRot - var0.xRotO < -180.0F; var0.xRotO -= 360.0F) {
         }

         while(var0.xRot - var0.xRotO >= 180.0F) {
            var0.xRotO += 360.0F;
         }

         while(var0.yRot - var0.yRotO < -180.0F) {
            var0.yRotO -= 360.0F;
         }

         while(var0.yRot - var0.yRotO >= 180.0F) {
            var0.yRotO += 360.0F;
         }

         var0.xRot = Mth.lerp(var1, var0.xRotO, var0.xRot);
         var0.yRot = Mth.lerp(var1, var0.yRotO, var0.yRot);
      }
   }

   public static InteractionHand getWeaponHoldingHand(LivingEntity var0, Item var1) {
      return var0.getMainHandItem().is(var1) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
   }

   public static AbstractArrow getMobArrow(LivingEntity var0, ItemStack var1, float var2) {
      ArrowItem var3 = (ArrowItem)((ArrowItem)(var1.getItem() instanceof ArrowItem ? var1.getItem() : Items.ARROW));
      AbstractArrow var4 = var3.createArrow(var0.level, var1, var0);
      var4.setEnchantmentEffectsFromEntity(var0, var2);
      if (var1.is(Items.TIPPED_ARROW) && var4 instanceof Arrow) {
         ((Arrow)var4).setEffectsFromItem(var1);
      }

      return var4;
   }
}
