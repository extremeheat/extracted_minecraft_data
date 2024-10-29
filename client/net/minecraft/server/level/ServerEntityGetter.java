package net.minecraft.server.level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;

public interface ServerEntityGetter extends EntityGetter {
   ServerLevel getLevel();

   @Nullable
   default Player getNearestPlayer(TargetingConditions var1, LivingEntity var2) {
      return (Player)this.getNearestEntity(this.players(), var1, var2, var2.getX(), var2.getY(), var2.getZ());
   }

   @Nullable
   default Player getNearestPlayer(TargetingConditions var1, LivingEntity var2, double var3, double var5, double var7) {
      return (Player)this.getNearestEntity(this.players(), var1, var2, var3, var5, var7);
   }

   @Nullable
   default Player getNearestPlayer(TargetingConditions var1, double var2, double var4, double var6) {
      return (Player)this.getNearestEntity(this.players(), var1, (LivingEntity)null, var2, var4, var6);
   }

   @Nullable
   default <T extends LivingEntity> T getNearestEntity(Class<? extends T> var1, TargetingConditions var2, @Nullable LivingEntity var3, double var4, double var6, double var8, AABB var10) {
      return this.getNearestEntity(this.getEntitiesOfClass(var1, var10, (var0) -> {
         return true;
      }), var2, var3, var4, var6, var8);
   }

   @Nullable
   default <T extends LivingEntity> T getNearestEntity(List<? extends T> var1, TargetingConditions var2, @Nullable LivingEntity var3, double var4, double var6, double var8) {
      double var10 = -1.0;
      LivingEntity var12 = null;
      Iterator var13 = var1.iterator();

      while(true) {
         LivingEntity var14;
         double var15;
         do {
            do {
               if (!var13.hasNext()) {
                  return var12;
               }

               var14 = (LivingEntity)var13.next();
            } while(!var2.test(this.getLevel(), var3, var14));

            var15 = var14.distanceToSqr(var4, var6, var8);
         } while(var10 != -1.0 && !(var15 < var10));

         var10 = var15;
         var12 = var14;
      }
   }

   default List<Player> getNearbyPlayers(TargetingConditions var1, LivingEntity var2, AABB var3) {
      ArrayList var4 = new ArrayList();
      Iterator var5 = this.players().iterator();

      while(var5.hasNext()) {
         Player var6 = (Player)var5.next();
         if (var3.contains(var6.getX(), var6.getY(), var6.getZ()) && var1.test(this.getLevel(), var2, var6)) {
            var4.add(var6);
         }
      }

      return var4;
   }

   default <T extends LivingEntity> List<T> getNearbyEntities(Class<T> var1, TargetingConditions var2, LivingEntity var3, AABB var4) {
      List var5 = this.getEntitiesOfClass(var1, var4, (var0) -> {
         return true;
      });
      ArrayList var6 = new ArrayList();
      Iterator var7 = var5.iterator();

      while(var7.hasNext()) {
         LivingEntity var8 = (LivingEntity)var7.next();
         if (var2.test(this.getLevel(), var3, var8)) {
            var6.add(var8);
         }
      }

      return var6;
   }
}
