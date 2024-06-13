package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GateBehavior<E extends LivingEntity> implements BehaviorControl<E> {
   private final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
   private final Set<MemoryModuleType<?>> exitErasedMemories;
   private final GateBehavior.OrderPolicy orderPolicy;
   private final GateBehavior.RunningPolicy runningPolicy;
   private final ShufflingList<BehaviorControl<? super E>> behaviors = new ShufflingList<>();
   private Behavior.Status status = Behavior.Status.STOPPED;

   public GateBehavior(
      Map<MemoryModuleType<?>, MemoryStatus> var1,
      Set<MemoryModuleType<?>> var2,
      GateBehavior.OrderPolicy var3,
      GateBehavior.RunningPolicy var4,
      List<Pair<? extends BehaviorControl<? super E>, Integer>> var5
   ) {
      super();
      this.entryCondition = var1;
      this.exitErasedMemories = var2;
      this.orderPolicy = var3;
      this.runningPolicy = var4;
      var5.forEach(var1x -> this.behaviors.add((BehaviorControl<? super E>)var1x.getFirst(), (Integer)var1x.getSecond()));
   }

   @Override
   public Behavior.Status getStatus() {
      return this.status;
   }

   private boolean hasRequiredMemories(E var1) {
      for (Entry var3 : this.entryCondition.entrySet()) {
         MemoryModuleType var4 = (MemoryModuleType)var3.getKey();
         MemoryStatus var5 = (MemoryStatus)var3.getValue();
         if (!var1.getBrain().checkMemory(var4, var5)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final boolean tryStart(ServerLevel var1, E var2, long var3) {
      if (this.hasRequiredMemories((E)var2)) {
         this.status = Behavior.Status.RUNNING;
         this.orderPolicy.apply(this.behaviors);
         this.runningPolicy.apply(this.behaviors.stream(), var1, (E)var2, var3);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void tickOrStop(ServerLevel var1, E var2, long var3) {
      this.behaviors.stream().filter(var0 -> var0.getStatus() == Behavior.Status.RUNNING).forEach(var4 -> var4.tickOrStop(var1, (E)var2, var3));
      if (this.behaviors.stream().noneMatch(var0 -> var0.getStatus() == Behavior.Status.RUNNING)) {
         this.doStop(var1, (E)var2, var3);
      }
   }

   @Override
   public final void doStop(ServerLevel var1, E var2, long var3) {
      this.status = Behavior.Status.STOPPED;
      this.behaviors.stream().filter(var0 -> var0.getStatus() == Behavior.Status.RUNNING).forEach(var4 -> var4.doStop(var1, (E)var2, var3));
      this.exitErasedMemories.forEach(var2.getBrain()::eraseMemory);
   }

   @Override
   public String debugString() {
      return this.getClass().getSimpleName();
   }

   @Override
   public String toString() {
      Set var1 = this.behaviors.stream().filter(var0 -> var0.getStatus() == Behavior.Status.RUNNING).collect(Collectors.toSet());
      return "(" + this.getClass().getSimpleName() + "): " + var1;
   }

   public static enum OrderPolicy {
      ORDERED(var0 -> {
      }),
      SHUFFLED(ShufflingList::shuffle);

      private final Consumer<ShufflingList<?>> consumer;

      private OrderPolicy(final Consumer<ShufflingList<?>> nullxx) {
         this.consumer = nullxx;
      }

      public void apply(ShufflingList<?> var1) {
         this.consumer.accept(var1);
      }
   }

   public static enum RunningPolicy {
      RUN_ONE {
         @Override
         public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> var1, ServerLevel var2, E var3, long var4) {
            var1.filter(var0 -> var0.getStatus() == Behavior.Status.STOPPED).filter(var4x -> var4x.tryStart(var2, var3, var4)).findFirst();
         }
      },
      TRY_ALL {
         @Override
         public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> var1, ServerLevel var2, E var3, long var4) {
            var1.filter(var0 -> var0.getStatus() == Behavior.Status.STOPPED).forEach(var4x -> var4x.tryStart(var2, var3, var4));
         }
      };

      RunningPolicy() {
      }

      public abstract <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> var1, ServerLevel var2, E var3, long var4);
   }
}