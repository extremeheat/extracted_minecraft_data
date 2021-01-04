package net.minecraft.world.entity.projectile;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class ProjectileUtil {
   public static HitResult forwardsRaycast(Entity var0, boolean var1, boolean var2, @Nullable Entity var3, ClipContext.Block var4) {
      return forwardsRaycast(var0, var1, var2, var3, var4, true, (var2x) -> {
         return !var2x.isSpectator() && var2x.isPickable() && (var2 || !var2x.is(var3)) && !var2x.noPhysics;
      }, var0.getBoundingBox().expandTowards(var0.getDeltaMovement()).inflate(1.0D));
   }

   public static HitResult getHitResult(Entity var0, AABB var1, Predicate<Entity> var2, ClipContext.Block var3, boolean var4) {
      return forwardsRaycast(var0, var4, false, (Entity)null, var3, false, var2, var1);
   }

   @Nullable
   public static EntityHitResult getHitResult(Level var0, Entity var1, Vec3 var2, Vec3 var3, AABB var4, Predicate<Entity> var5) {
      return getHitResult(var0, var1, var2, var3, var4, var5, 1.7976931348623157E308D);
   }

   private static HitResult forwardsRaycast(Entity var0, boolean var1, boolean var2, @Nullable Entity var3, ClipContext.Block var4, boolean var5, Predicate<Entity> var6, AABB var7) {
      double var8 = var0.x;
      double var10 = var0.y;
      double var12 = var0.z;
      Vec3 var14 = var0.getDeltaMovement();
      Level var15 = var0.level;
      Vec3 var16 = new Vec3(var8, var10, var12);
      if (var5 && !var15.noCollision(var0, var0.getBoundingBox(), (Set)(!var2 && var3 != null ? getIgnoredEntities(var3) : ImmutableSet.of()))) {
         return new BlockHitResult(var16, Direction.getNearest(var14.x, var14.y, var14.z), new BlockPos(var0), false);
      } else {
         Vec3 var17 = var16.add(var14);
         Object var18 = var15.clip(new ClipContext(var16, var17, var4, ClipContext.Fluid.NONE, var0));
         if (var1) {
            if (((HitResult)var18).getType() != HitResult.Type.MISS) {
               var17 = ((HitResult)var18).getLocation();
            }

            EntityHitResult var19 = getHitResult(var15, var0, var16, var17, var7, var6);
            if (var19 != null) {
               var18 = var19;
            }
         }

         return (HitResult)var18;
      }
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
   public static EntityHitResult getHitResult(Level var0, Entity var1, Vec3 var2, Vec3 var3, AABB var4, Predicate<Entity> var5, double var6) {
      double var8 = var6;
      Entity var10 = null;
      Iterator var11 = var0.getEntities(var1, var4, var5).iterator();

      while(var11.hasNext()) {
         Entity var12 = (Entity)var11.next();
         AABB var13 = var12.getBoundingBox().inflate(0.30000001192092896D);
         Optional var14 = var13.clip(var2, var3);
         if (var14.isPresent()) {
            double var15 = var2.distanceToSqr((Vec3)var14.get());
            if (var15 < var8) {
               var10 = var12;
               var8 = var15;
            }
         }
      }

      if (var10 == null) {
         return null;
      } else {
         return new EntityHitResult(var10);
      }
   }

   private static Set<Entity> getIgnoredEntities(Entity var0) {
      Entity var1 = var0.getVehicle();
      return var1 != null ? ImmutableSet.of(var0, var1) : ImmutableSet.of(var0);
   }

   public static final void rotateTowardsMovement(Entity var0, float var1) {
      Vec3 var2 = var0.getDeltaMovement();
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

   public static InteractionHand getWeaponHoldingHand(LivingEntity var0, Item var1) {
      return var0.getMainHandItem().getItem() == var1 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
   }

   public static AbstractArrow getMobArrow(LivingEntity var0, ItemStack var1, float var2) {
      ArrowItem var3 = (ArrowItem)((ArrowItem)(var1.getItem() instanceof ArrowItem ? var1.getItem() : Items.ARROW));
      AbstractArrow var4 = var3.createArrow(var0.level, var1, var0);
      var4.setEnchantmentEffectsFromEntity(var0, var2);
      if (var1.getItem() == Items.TIPPED_ARROW && var4 instanceof Arrow) {
         ((Arrow)var4).setEffectsFromItem(var1);
      }

      return var4;
   }
}
