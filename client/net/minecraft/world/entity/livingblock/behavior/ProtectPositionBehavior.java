package net.minecraft.world.entity.livingblock.behavior;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Desires;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.entity.livingblock.cognition.Prize;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class ProtectPositionBehavior implements LivingBlockBehavior {
   public static final LivingBlockBehaviorType BE_MAD_AT_INTRUDERS = LivingBlockBehaviorType.behaviorType((Function)((a) -> new BeMadAtIntruders(a.inPursuitOf(Desires.PROTECT), a.withIntentTo(LivingBlock.BE_MAD_AT))));
   public static final LivingBlockBehaviorType RETURN_HOME_WHEN_ABLE = LivingBlockBehaviorType.behaviorType((Function)((a) -> new ReturnHome(a.inPursuitOf(Desires.PROTECT), a.withIntentTo(LivingBlock.MOVE_TOWARDS))));
   protected final Prize<Vec3> protectedPosition;

   protected ProtectPositionBehavior(final Prize<Vec3> protectedPosition) {
      super();
      this.protectedPosition = protectedPosition;
   }

   public static class BeMadAtIntruders extends ProtectPositionBehavior {
      private static final double PROTECTION_RADIUS = 10.0;
      private static final int TARGET_SCAN_INTERVAL_TICKS = 10;
      private final Intent<Targetable> beMadAt;
      private int lastScanTick = 0;

      private BeMadAtIntruders(final Prize<Vec3> protectedPosition, final Intent<Targetable> beMadAt) {
         super(protectedPosition);
         this.beMadAt = beMadAt;
      }

      public boolean canStartUsing(final LivingBlock entity) {
         return true;
      }

      public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
         if (tickCount - this.lastScanTick >= 10 && !entity.isAttacking()) {
            Optional var10000 = this.scanForTargets(entity);
            Intent var10001 = this.beMadAt;
            Objects.requireNonNull(var10001);
            Consumer var5 = var10001::update;
            Intent var10002 = this.beMadAt;
            Objects.requireNonNull(var10002);
            var10000.ifPresentOrElse(var5, var10002::clear);
            this.lastScanTick = tickCount;
         }

         Targetable currentTarget = entity.getAttackTarget();
         if (currentTarget != null && !this.isStillAttackable(currentTarget)) {
            this.beMadAt.clear();
         }

         return true;
      }

      protected Optional<LivingEntity> scanForTargets(final LivingBlock entity) {
         Entity owner = entity.getCommander();
         Vec3 protectedPos = this.protectedPosition.get();
         AABB scanArea = AABB.around(protectedPos, 10.0);
         List<LivingEntity> nearbyEntities = entity.level().getEntitiesOfClass(LivingEntity.class, scanArea, (target) -> this.isValidTarget(target, owner));
         return nearbyEntities.stream().filter((livingEntity) -> !(livingEntity instanceof Player)).min(Comparator.comparingDouble((e) -> e.position().distanceToSqr(protectedPos)));
      }

      protected boolean isValidTarget(final Targetable target, final @Nullable Entity owner) {
         if (!target.isAlive()) {
            return false;
         } else {
            return owner == null || !target.getUUID().equals(owner.getUUID());
         }
      }

      protected boolean isStillAttackable(final Targetable target) {
         return target.isAlive() && this.isWithinProtectionRadius(target);
      }

      protected boolean isWithinProtectionRadius(final Targetable target) {
         return target.position().distanceTo(this.protectedPosition.get()) <= 10.0;
      }
   }

   public static class ReturnHome extends ProtectPositionBehavior {
      private static final double HOME_RADIUS = 1.0;
      private final Intent<Target> moveTowards;

      private ReturnHome(final Prize<Vec3> protectedPosition, final Intent<Target> moveTowards) {
         super(protectedPosition);
         this.moveTowards = moveTowards;
      }

      public boolean canStartUsing(final LivingBlock entity) {
         return entity.isIdle() && this.isFarFromHome(entity);
      }

      public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
         this.moveTowards.update(Target.near(this.protectedPosition.get(), 1.0));
         return false;
      }

      private boolean isFarFromHome(final LivingBlock entity) {
         double distanceToProtectedPosSqr = entity.distanceToSqr(this.protectedPosition.get());
         return distanceToProtectedPosSqr > Mth.square(1.0);
      }
   }
}
