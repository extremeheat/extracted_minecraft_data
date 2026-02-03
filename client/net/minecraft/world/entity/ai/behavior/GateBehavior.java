package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GateBehavior<E extends LivingEntity> implements BehaviorControl<E> {
   private final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
   private final Set<MemoryModuleType<?>> exitErasedMemories;
   private final OrderPolicy orderPolicy;
   private final RunningPolicy runningPolicy;
   private final ShufflingList<BehaviorControl<? super E>> behaviors = new ShufflingList<BehaviorControl<? super E>>();
   private Behavior.Status status;

   public GateBehavior(final Map<MemoryModuleType<?>, MemoryStatus> entryCondition, final Set<MemoryModuleType<?>> exitErasedMemories, final OrderPolicy orderPolicy, final RunningPolicy runningPolicy, final List<Pair<? extends BehaviorControl<? super E>, Integer>> behaviors) {
      super();
      this.status = Behavior.Status.STOPPED;
      this.entryCondition = entryCondition;
      this.exitErasedMemories = exitErasedMemories;
      this.orderPolicy = orderPolicy;
      this.runningPolicy = runningPolicy;
      behaviors.forEach((entry) -> this.behaviors.add((BehaviorControl)entry.getFirst(), (Integer)entry.getSecond()));
   }

   public Behavior.Status getStatus() {
      return this.status;
   }

   public Set<MemoryModuleType<?>> getRequiredMemories() {
      Set<MemoryModuleType<?>> memories = new HashSet(this.entryCondition.keySet());

      for(BehaviorControl<? super E> behavior : this.behaviors) {
         memories.addAll(behavior.getRequiredMemories());
      }

      return memories;
   }

   private boolean hasRequiredMemories(final E body) {
      for(Map.Entry<MemoryModuleType<?>, MemoryStatus> entry : this.entryCondition.entrySet()) {
         MemoryModuleType<?> memoryType = (MemoryModuleType)entry.getKey();
         MemoryStatus requiredStatus = (MemoryStatus)entry.getValue();
         if (!body.getBrain().checkMemory(memoryType, requiredStatus)) {
            return false;
         }
      }

      return true;
   }

   public final boolean tryStart(final ServerLevel level, final E body, final long timestamp) {
      if (this.hasRequiredMemories(body)) {
         this.status = Behavior.Status.RUNNING;
         this.orderPolicy.apply(this.behaviors);
         this.runningPolicy.apply(this.behaviors.stream(), level, body, timestamp);
         return true;
      } else {
         return false;
      }
   }

   public final void tickOrStop(final ServerLevel level, final E body, final long timestamp) {
      this.behaviors.stream().filter((goal) -> goal.getStatus() == Behavior.Status.RUNNING).forEach((goal) -> goal.tickOrStop(level, body, timestamp));
      if (this.behaviors.stream().noneMatch((g) -> g.getStatus() == Behavior.Status.RUNNING)) {
         this.doStop(level, body, timestamp);
      }

   }

   public final void doStop(final ServerLevel level, final E body, final long timestamp) {
      this.status = Behavior.Status.STOPPED;
      this.behaviors.stream().filter((goal) -> goal.getStatus() == Behavior.Status.RUNNING).forEach((goal) -> goal.doStop(level, body, timestamp));
      Set var10000 = this.exitErasedMemories;
      Brain var10001 = ((LivingEntity)body).getBrain();
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::eraseMemory);
   }

   public String debugString() {
      Set<String> runningBehaviours = (Set)this.behaviors.stream().filter((goal) -> goal.getStatus() == Behavior.Status.RUNNING).map((b) -> b.getClass().getSimpleName()).collect(Collectors.toSet());
      String var10000 = this.getClass().getSimpleName();
      return var10000 + ": " + String.valueOf(runningBehaviours);
   }

   public static enum OrderPolicy {
      ORDERED((t) -> {
      }),
      SHUFFLED(ShufflingList::shuffle);

      private final Consumer<ShufflingList<?>> consumer;

      private OrderPolicy(final Consumer<ShufflingList<?>> consumer) {
         this.consumer = consumer;
      }

      public void apply(final ShufflingList<?> list) {
         this.consumer.accept(list);
      }

      // $FF: synthetic method
      private static OrderPolicy[] $values() {
         return new OrderPolicy[]{ORDERED, SHUFFLED};
      }
   }

   public static enum RunningPolicy {
      RUN_ONE {
         public <E extends LivingEntity> void apply(final Stream<BehaviorControl<? super E>> behaviors, final ServerLevel level, final E body, final long timestamp) {
            behaviors.filter((goal) -> goal.getStatus() == Behavior.Status.STOPPED).filter((goal) -> goal.tryStart(level, body, timestamp)).findFirst();
         }
      },
      TRY_ALL {
         public <E extends LivingEntity> void apply(final Stream<BehaviorControl<? super E>> behaviors, final ServerLevel level, final E body, final long timestamp) {
            behaviors.filter((goal) -> goal.getStatus() == Behavior.Status.STOPPED).forEach((goal) -> goal.tryStart(level, body, timestamp));
         }
      };

      private RunningPolicy() {
      }

      public abstract <E extends LivingEntity> void apply(final Stream<BehaviorControl<? super E>> behaviors, final ServerLevel level, final E body, final long timestamp);

      // $FF: synthetic method
      private static RunningPolicy[] $values() {
         return new RunningPolicy[]{RUN_ONE, TRY_ALL};
      }
   }
}
