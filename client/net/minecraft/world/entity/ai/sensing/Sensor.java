package net.minecraft.world.entity.ai.sensing;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public abstract class Sensor<E extends LivingEntity> {
   private static final RandomSource RANDOM = RandomSource.createThreadSafe();
   private static final int DEFAULT_SCAN_RATE = 20;
   private static final int DEFAULT_TARGETING_RANGE = 16;
   private static final TargetingConditions TARGET_CONDITIONS = TargetingConditions.forNonCombat().range(16.0);
   private static final TargetingConditions TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = TargetingConditions.forNonCombat()
      .range(16.0)
      .ignoreInvisibilityTesting();
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS = TargetingConditions.forCombat().range(16.0);
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = TargetingConditions.forCombat()
      .range(16.0)
      .ignoreInvisibilityTesting();
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT = TargetingConditions.forCombat().range(16.0).ignoreLineOfSight();
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT = TargetingConditions.forCombat()
      .range(16.0)
      .ignoreLineOfSight()
      .ignoreInvisibilityTesting();
   private final int scanRate;
   private long timeToTick;

   public Sensor(int var1) {
      super();
      this.scanRate = var1;
      this.timeToTick = (long)RANDOM.nextInt(var1);
   }

   public Sensor() {
      this(20);
   }

   public final void tick(ServerLevel var1, E var2) {
      if (--this.timeToTick <= 0L) {
         this.timeToTick = (long)this.scanRate;
         this.updateTargetingConditionRanges((E)var2);
         this.doTick(var1, (E)var2);
      }
   }

   private void updateTargetingConditionRanges(E var1) {
      double var2 = var1.getAttributeValue(Attributes.FOLLOW_RANGE);
      TARGET_CONDITIONS.range(var2);
      TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.range(var2);
      ATTACK_TARGET_CONDITIONS.range(var2);
      ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.range(var2);
      ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT.range(var2);
      ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT.range(var2);
   }

   protected abstract void doTick(ServerLevel var1, E var2);

   public abstract Set<MemoryModuleType<?>> requires();

   public static boolean isEntityTargetable(ServerLevel var0, LivingEntity var1, LivingEntity var2) {
      return var1.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, var2)
         ? TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(var0, var1, var2)
         : TARGET_CONDITIONS.test(var0, var1, var2);
   }

   public static boolean isEntityAttackable(ServerLevel var0, LivingEntity var1, LivingEntity var2) {
      return var1.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, var2)
         ? ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(var0, var1, var2)
         : ATTACK_TARGET_CONDITIONS.test(var0, var1, var2);
   }

   public static BiPredicate<ServerLevel, LivingEntity> wasEntityAttackableLastNTicks(LivingEntity var0, int var1) {
      return rememberPositives(var1, (var1x, var2) -> isEntityAttackable(var1x, var0, var2));
   }

   public static boolean isEntityAttackableIgnoringLineOfSight(ServerLevel var0, LivingEntity var1, LivingEntity var2) {
      return var1.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, var2)
         ? ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT.test(var0, var1, var2)
         : ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT.test(var0, var1, var2);
   }

   static <T, U> BiPredicate<T, U> rememberPositives(int var0, BiPredicate<T, U> var1) {
      AtomicInteger var2 = new AtomicInteger(0);
      return (var3, var4) -> {
         if (var1.test(var3, var4)) {
            var2.set(var0);
            return true;
         } else {
            return var2.decrementAndGet() >= 0;
         }
      };
   }
}
