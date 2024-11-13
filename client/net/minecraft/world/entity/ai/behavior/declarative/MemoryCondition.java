package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Unit;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public interface MemoryCondition<F extends K1, Value> {
   MemoryModuleType<Value> memory();

   MemoryStatus condition();

   @Nullable
   MemoryAccessor<F, Value> createAccessor(Brain<?> var1, Optional<Value> var2);

   public static record Registered<Value>(MemoryModuleType<Value> memory) implements MemoryCondition<OptionalBox.Mu, Value> {
      public Registered(MemoryModuleType<Value> var1) {
         super();
         this.memory = var1;
      }

      public MemoryStatus condition() {
         return MemoryStatus.REGISTERED;
      }

      public MemoryAccessor<OptionalBox.Mu, Value> createAccessor(Brain<?> var1, Optional<Value> var2) {
         return new MemoryAccessor<OptionalBox.Mu, Value>(var1, this.memory, OptionalBox.create(var2));
      }
   }

   public static record Present<Value>(MemoryModuleType<Value> memory) implements MemoryCondition<IdF.Mu, Value> {
      public Present(MemoryModuleType<Value> var1) {
         super();
         this.memory = var1;
      }

      public MemoryStatus condition() {
         return MemoryStatus.VALUE_PRESENT;
      }

      public MemoryAccessor<IdF.Mu, Value> createAccessor(Brain<?> var1, Optional<Value> var2) {
         return var2.isEmpty() ? null : new MemoryAccessor(var1, this.memory, IdF.create(var2.get()));
      }
   }

   public static record Absent<Value>(MemoryModuleType<Value> memory) implements MemoryCondition<Const.Mu<Unit>, Value> {
      public Absent(MemoryModuleType<Value> var1) {
         super();
         this.memory = var1;
      }

      public MemoryStatus condition() {
         return MemoryStatus.VALUE_ABSENT;
      }

      public MemoryAccessor<Const.Mu<Unit>, Value> createAccessor(Brain<?> var1, Optional<Value> var2) {
         return var2.isPresent() ? null : new MemoryAccessor(var1, this.memory, Const.create(Unit.INSTANCE));
      }
   }
}
