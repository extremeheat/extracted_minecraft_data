package net.minecraft.world.entity.ai.sensing;

import java.util.Random;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public abstract class Sensor<E extends LivingEntity> {
   private static final Random RANDOM = new Random();
   private static final TargetingConditions TARGET_CONDITIONS = (new TargetingConditions()).range(16.0D).allowSameTeam().allowNonAttackable();
   private static final TargetingConditions TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = (new TargetingConditions()).range(16.0D).allowSameTeam().allowNonAttackable().ignoreInvisibilityTesting();
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
         this.doTick(var1, var2);
      }

   }

   protected abstract void doTick(ServerLevel var1, E var2);

   public abstract Set<MemoryModuleType<?>> requires();

   protected static boolean isEntityTargetable(LivingEntity var0, LivingEntity var1) {
      return var0.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, var1) ? TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(var0, var1) : TARGET_CONDITIONS.test(var0, var1);
   }
}
