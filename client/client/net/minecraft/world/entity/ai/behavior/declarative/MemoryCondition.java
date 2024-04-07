package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.kinds.Const.Mu;
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

   public static record Absent<Value>(MemoryModuleType<Value> memory) implements MemoryCondition<Mu<Unit>, Value> {
      public Absent(MemoryModuleType<Value> memory) {
         super();
         this.memory = memory;
      }

      @Override
      public MemoryStatus condition() {
         return MemoryStatus.VALUE_ABSENT;
      }

      @Override
      public MemoryAccessor<Mu<Unit>, Value> createAccessor(Brain<?> var1, Optional<Value> var2) {
         return var2.isPresent() ? null : new MemoryAccessor<>(var1, this.memory, Const.create(Unit.INSTANCE));
      }
   }

   public static record Present<Value>(MemoryModuleType<Value> memory) implements MemoryCondition<com.mojang.datafixers.kinds.IdF.Mu, Value> {
      public Present(MemoryModuleType<Value> memory) {
         super();
         this.memory = memory;
      }

      @Override
      public MemoryStatus condition() {
         return MemoryStatus.VALUE_PRESENT;
      }

      @Override
      public MemoryAccessor<com.mojang.datafixers.kinds.IdF.Mu, Value> createAccessor(Brain<?> var1, Optional<Value> var2) {
         return var2.isEmpty() ? null : new MemoryAccessor<>(var1, this.memory, IdF.create(var2.get()));
      }
   }

   public static record Registered<Value>(MemoryModuleType<Value> memory) implements MemoryCondition<com.mojang.datafixers.kinds.OptionalBox.Mu, Value> {
      public Registered(MemoryModuleType<Value> memory) {
         super();
         this.memory = memory;
      }

      @Override
      public MemoryStatus condition() {
         return MemoryStatus.REGISTERED;
      }

      @Override
      public MemoryAccessor<com.mojang.datafixers.kinds.OptionalBox.Mu, Value> createAccessor(Brain<?> var1, Optional<Value> var2) {
         return new MemoryAccessor<>(var1, this.memory, OptionalBox.create(var2));
      }
   }
}
