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
      return switch (var0) {
         case null, default -> null;
         case PrimedTnt var3 -> var3.getOwner();
         case LivingEntity var4 -> var4;
         case Projectile var5 when var5.getOwner() instanceof LivingEntity var6 -> var6;
      };
   }

   ServerLevel level();

   Explosion.BlockInteraction getBlockInteraction();

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

      private BlockInteraction(final boolean nullxx) {
         this.shouldAffectBlocklikeEntities = nullxx;
      }

      public boolean shouldAffectBlocklikeEntities() {
         return this.shouldAffectBlocklikeEntities;
      }
   }
}
