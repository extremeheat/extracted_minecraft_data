package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class StopAttackingIfTargetInvalid<E extends Mob> extends Behavior<E> {
   private static final int TIMEOUT_TO_GET_WITHIN_ATTACK_RANGE = 200;
   private final Predicate<LivingEntity> stopAttackingWhen;
   private final BiConsumer<E, LivingEntity> onTargetErased;
   private final boolean canGrowTiredOfTryingToReachTarget;

   public StopAttackingIfTargetInvalid(Predicate<LivingEntity> var1, BiConsumer<E, LivingEntity> var2, boolean var3) {
      super(
         ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED)
      );
      this.stopAttackingWhen = var1;
      this.onTargetErased = var2;
      this.canGrowTiredOfTryingToReachTarget = var3;
   }

   public StopAttackingIfTargetInvalid(Predicate<LivingEntity> var1, BiConsumer<E, LivingEntity> var2) {
      this(var1, var2, true);
   }

   public StopAttackingIfTargetInvalid(Predicate<LivingEntity> var1) {
      this(var1, (var0, var1x) -> {
      });
   }

   public StopAttackingIfTargetInvalid(BiConsumer<E, LivingEntity> var1) {
      this(var0 -> false, var1);
   }

   public StopAttackingIfTargetInvalid() {
      this(var0 -> false, (var0, var1) -> {
      });
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      LivingEntity var5 = this.getAttackTarget((E)var2);
      if (!var2.canAttack(var5)) {
         this.clearAttackTarget((E)var2);
      } else if (this.canGrowTiredOfTryingToReachTarget && isTiredOfTryingToReachTarget(var2)) {
         this.clearAttackTarget((E)var2);
      } else if (this.isCurrentTargetDeadOrRemoved((E)var2)) {
         this.clearAttackTarget((E)var2);
      } else if (this.isCurrentTargetInDifferentLevel((E)var2)) {
         this.clearAttackTarget((E)var2);
      } else if (this.stopAttackingWhen.test(this.getAttackTarget((E)var2))) {
         this.clearAttackTarget((E)var2);
      }
   }

   private boolean isCurrentTargetInDifferentLevel(E var1) {
      return this.getAttackTarget((E)var1).level != var1.level;
   }

   private LivingEntity getAttackTarget(E var1) {
      return var1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }

   private static <E extends LivingEntity> boolean isTiredOfTryingToReachTarget(E var0) {
      Optional var1 = var0.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      return var1.isPresent() && var0.level.getGameTime() - var1.get() > 200L;
   }

   private boolean isCurrentTargetDeadOrRemoved(E var1) {
      Optional var2 = var1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
      return var2.isPresent() && !((LivingEntity)var2.get()).isAlive();
   }

   protected void clearAttackTarget(E var1) {
      this.onTargetErased.accept((E)var1, this.getAttackTarget((E)var1));
      var1.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
   }
}
