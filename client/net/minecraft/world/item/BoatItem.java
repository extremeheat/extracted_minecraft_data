package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BoatItem extends Item {
   private static final Predicate<Entity> ENTITY_PREDICATE;
   private final Boat.Type type;

   public BoatItem(Boat.Type var1, Item.Properties var2) {
      super(var2);
      this.type = var1;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      BlockHitResult var5 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.ANY);
      if (var5.getType() == HitResult.Type.MISS) {
         return InteractionResultHolder.pass(var4);
      } else {
         Vec3 var6 = var2.getViewVector(1.0F);
         double var7 = 5.0D;
         List var9 = var1.getEntities((Entity)var2, var2.getBoundingBox().expandTowards(var6.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
         if (!var9.isEmpty()) {
            Vec3 var10 = var2.getEyePosition(1.0F);
            Iterator var11 = var9.iterator();

            while(var11.hasNext()) {
               Entity var12 = (Entity)var11.next();
               AABB var13 = var12.getBoundingBox().inflate((double)var12.getPickRadius());
               if (var13.contains(var10)) {
                  return InteractionResultHolder.pass(var4);
               }
            }
         }

         if (var5.getType() == HitResult.Type.BLOCK) {
            Boat var14 = new Boat(var1, var5.getLocation().x, var5.getLocation().y, var5.getLocation().z);
            var14.setType(this.type);
            var14.yRot = var2.yRot;
            if (!var1.noCollision(var14, var14.getBoundingBox().inflate(-0.1D))) {
               return InteractionResultHolder.fail(var4);
            } else {
               if (!var1.isClientSide) {
                  var1.addFreshEntity(var14);
                  if (!var2.abilities.instabuild) {
                     var4.shrink(1);
                  }
               }

               var2.awardStat(Stats.ITEM_USED.get(this));
               return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
            }
         } else {
            return InteractionResultHolder.pass(var4);
         }
      }
   }

   static {
      ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
   }
}
