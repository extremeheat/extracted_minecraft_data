package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.K1;
import java.util.Optional;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public final class MemoryAccessor<F extends K1, Value> {
   private final Brain<?> brain;
   private final MemoryModuleType<Value> memoryType;
   private final App<F, Value> value;

   public MemoryAccessor(Brain<?> var1, MemoryModuleType<Value> var2, App<F, Value> var3) {
      super();
      this.brain = var1;
      this.memoryType = var2;
      this.value = var3;
   }

   public App<F, Value> value() {
      return this.value;
   }

   public void set(Value var1) {
      this.brain.setMemory(this.memoryType, Optional.of(var1));
   }

   public void setOrErase(Optional<Value> var1) {
      this.brain.setMemory(this.memoryType, var1);
   }

   public void setWithExpiry(Value var1, long var2) {
      this.brain.setMemoryWithExpiry(this.memoryType, var1, var2);
   }

   public void erase() {
      this.brain.eraseMemory(this.memoryType);
   }
}
