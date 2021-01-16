package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class EraseMemoryIf<E extends LivingEntity> extends Behavior<E> {
   private final Predicate<E> predicate;
   private final MemoryModuleType<?> memoryType;

   public EraseMemoryIf(Predicate<E> var1, MemoryModuleType<?> var2) {
      super(ImmutableMap.of(var2, MemoryStatus.VALUE_PRESENT));
      this.predicate = var1;
      this.memoryType = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return this.predicate.test(var2);
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.getBrain().eraseMemory(this.memoryType);
   }
}
