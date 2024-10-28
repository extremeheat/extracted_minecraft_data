package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public interface Explosion {
   static DamageSource getDefaultDamageSource(Level var0, @Nullable Entity var1) {
      return var0.damageSources().explosion(var1, getIndirectSourceEntity(var1));
   }

   @Nullable
   static LivingEntity getIndirectSourceEntity(@Nullable Entity var0) {
      Entity var1 = var0;
      byte var2 = 0;

      while(true) {
         LivingEntity var10000;
         //$FF: var2->value
         //0->net/minecraft/world/entity/item/PrimedTnt
         //1->net/minecraft/world/entity/LivingEntity
         //2->net/minecraft/world/entity/projectile/Projectile
         switch (var1.typeSwitch<invokedynamic>(var1, var2)) {
            case -1:
            default:
               var10000 = null;
               return var10000;
            case 0:
               PrimedTnt var3 = (PrimedTnt)var1;
               var10000 = var3.getOwner();
               return var10000;
            case 1:
               LivingEntity var4 = (LivingEntity)var1;
               var10000 = var4;
               return var10000;
            case 2:
               Projectile var5 = (Projectile)var1;
               Entity var7 = var5.getOwner();
               if (var7 instanceof LivingEntity var6) {
                  var10000 = var6;
                  return var10000;
               }

               var2 = 3;
         }
      }
   }

   ServerLevel level();

   BlockInteraction getBlockInteraction();

   @Nullable
   LivingEntity getIndirectSourceEntity();

   @Nullable
   Entity getDirectSourceEntity();

   float radius();

   Vec3 center();

   boolean canTriggerBlocks();

   boolean shouldAffectBlocklikeEntities();

   public static enum BlockInteraction {
      KEEP(false),
      DESTROY(true),
      DESTROY_WITH_DECAY(true),
      TRIGGER_BLOCK(false);

      private final boolean shouldAffectBlocklikeEntities;

      private BlockInteraction(final boolean var3) {
         this.shouldAffectBlocklikeEntities = var3;
      }

      public boolean shouldAffectBlocklikeEntities() {
         return this.shouldAffectBlocklikeEntities;
      }

      // $FF: synthetic method
      private static BlockInteraction[] $values() {
         return new BlockInteraction[]{KEEP, DESTROY, DESTROY_WITH_DECAY, TRIGGER_BLOCK};
      }
   }
}
