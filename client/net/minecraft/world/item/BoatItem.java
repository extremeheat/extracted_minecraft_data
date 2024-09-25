package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BoatItem extends Item {
   private final EntityType<? extends AbstractBoat> entityType;

   public BoatItem(EntityType<? extends AbstractBoat> var1, Item.Properties var2) {
      super(var2);
      this.entityType = var1;
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      BlockHitResult var5 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.ANY);
      if (var5.getType() == HitResult.Type.MISS) {
         return InteractionResult.PASS;
      } else {
         Vec3 var6 = var2.getViewVector(1.0F);
         double var7 = 5.0;
         List var9 = var1.getEntities(var2, var2.getBoundingBox().expandTowards(var6.scale(5.0)).inflate(1.0), EntitySelector.CAN_BE_PICKED);
         if (!var9.isEmpty()) {
            Vec3 var10 = var2.getEyePosition();

            for (Entity var12 : var9) {
               AABB var13 = var12.getBoundingBox().inflate((double)var12.getPickRadius());
               if (var13.contains(var10)) {
                  return InteractionResult.PASS;
               }
            }
         }

         if (var5.getType() == HitResult.Type.BLOCK) {
            AbstractBoat var14 = this.getBoat(var1, var5, var4, var2);
            if (var14 == null) {
               return InteractionResult.FAIL;
            } else {
               var14.setYRot(var2.getYRot());
               if (!var1.noCollision(var14, var14.getBoundingBox())) {
                  return InteractionResult.FAIL;
               } else {
                  if (!var1.isClientSide) {
                     var1.addFreshEntity(var14);
                     var1.gameEvent(var2, GameEvent.ENTITY_PLACE, var5.getLocation());
                     var4.consume(1, var2);
                  }

                  var2.awardStat(Stats.ITEM_USED.get(this));
                  return InteractionResult.SUCCESS;
               }
            }
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   @Nullable
   private AbstractBoat getBoat(Level var1, HitResult var2, ItemStack var3, Player var4) {
      AbstractBoat var5 = this.entityType.create(var1, EntitySpawnReason.SPAWN_ITEM_USE);
      if (var5 != null) {
         Vec3 var6 = var2.getLocation();
         var5.setInitialPos(var6.x, var6.y, var6.z);
         if (var1 instanceof ServerLevel var7) {
            EntityType.<AbstractBoat>createDefaultStackConfig(var7, var3, var4).accept(var5);
         }
      }

      return var5;
   }
}
