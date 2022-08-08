package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class CopyMemoryWithExpiry<E extends Mob, T> extends Behavior<E> {
   private final Predicate<E> predicate;
   private final MemoryModuleType<? extends T> sourceMemory;
   private final MemoryModuleType<T> targetMemory;
   private final UniformInt durationOfCopy;

   public CopyMemoryWithExpiry(Predicate<E> var1, MemoryModuleType<? extends T> var2, MemoryModuleType<T> var3, UniformInt var4) {
      super(ImmutableMap.of(var2, MemoryStatus.VALUE_PRESENT, var3, MemoryStatus.VALUE_ABSENT));
      this.predicate = var1;
      this.sourceMemory = var2;
      this.targetMemory = var3;
      this.durationOfCopy = var4;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return this.predicate.test(var2);
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.setMemoryWithExpiry(this.targetMemory, var5.getMemory(this.sourceMemory).get(), (long)this.durationOfCopy.sample(var1.random));
   }
}
