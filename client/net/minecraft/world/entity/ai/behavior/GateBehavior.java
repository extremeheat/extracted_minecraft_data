package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
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
   private final ShufflingList<BehaviorControl<? super E>> behaviors = new ShufflingList();
   private Behavior.Status status;

   public GateBehavior(Map<MemoryModuleType<?>, MemoryStatus> var1, Set<MemoryModuleType<?>> var2, OrderPolicy var3, RunningPolicy var4, List<Pair<? extends BehaviorControl<? super E>, Integer>> var5) {
      super();
      this.status = Behavior.Status.STOPPED;
      this.entryCondition = var1;
      this.exitErasedMemories = var2;
      this.orderPolicy = var3;
      this.runningPolicy = var4;
      var5.forEach((var1x) -> {
         this.behaviors.add((BehaviorControl)var1x.getFirst(), (Integer)var1x.getSecond());
      });
   }

   public Behavior.Status getStatus() {
      return this.status;
   }

   private boolean hasRequiredMemories(E var1) {
      Iterator var2 = this.entryCondition.entrySet().iterator();

      MemoryModuleType var4;
      MemoryStatus var5;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         Map.Entry var3 = (Map.Entry)var2.next();
         var4 = (MemoryModuleType)var3.getKey();
         var5 = (MemoryStatus)var3.getValue();
      } while(var1.getBrain().checkMemory(var4, var5));

      return false;
   }

   public final boolean tryStart(ServerLevel var1, E var2, long var3) {
      if (this.hasRequiredMemories(var2)) {
         this.status = Behavior.Status.RUNNING;
         this.orderPolicy.apply(this.behaviors);
         this.runningPolicy.apply(this.behaviors.stream(), var1, var2, var3);
         return true;
      } else {
         return false;
      }
   }

   public final void tickOrStop(ServerLevel var1, E var2, long var3) {
      this.behaviors.stream().filter((var0) -> {
         return var0.getStatus() == Behavior.Status.RUNNING;
      }).forEach((var4) -> {
         var4.tickOrStop(var1, var2, var3);
      });
      if (this.behaviors.stream().noneMatch((var0) -> {
         return var0.getStatus() == Behavior.Status.RUNNING;
      })) {
         this.doStop(var1, var2, var3);
      }

   }

   public final void doStop(ServerLevel var1, E var2, long var3) {
      this.status = Behavior.Status.STOPPED;
      this.behaviors.stream().filter((var0) -> {
         return var0.getStatus() == Behavior.Status.RUNNING;
      }).forEach((var4) -> {
         var4.doStop(var1, var2, var3);
      });
      Set var10000 = this.exitErasedMemories;
      Brain var10001 = var2.getBrain();
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::eraseMemory);
   }

   public String debugString() {
      return this.getClass().getSimpleName();
   }

   public String toString() {
      Set var1 = (Set)this.behaviors.stream().filter((var0) -> {
         return var0.getStatus() == Behavior.Status.RUNNING;
      }).collect(Collectors.toSet());
      String var10000 = this.getClass().getSimpleName();
      return "(" + var10000 + "): " + String.valueOf(var1);
   }

   public static enum OrderPolicy {
      ORDERED((var0) -> {
      }),
      SHUFFLED(ShufflingList::shuffle);

      private final Consumer<ShufflingList<?>> consumer;

      private OrderPolicy(Consumer var3) {
         this.consumer = var3;
      }

      public void apply(ShufflingList<?> var1) {
         this.consumer.accept(var1);
      }

      // $FF: synthetic method
      private static OrderPolicy[] $values() {
         return new OrderPolicy[]{ORDERED, SHUFFLED};
      }
   }

   public static enum RunningPolicy {
      RUN_ONE {
         public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> var1, ServerLevel var2, E var3, long var4) {
            var1.filter((var0) -> {
               return var0.getStatus() == Behavior.Status.STOPPED;
            }).filter((var4x) -> {
               return var4x.tryStart(var2, var3, var4);
            }).findFirst();
         }
      },
      TRY_ALL {
         public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> var1, ServerLevel var2, E var3, long var4) {
            var1.filter((var0) -> {
               return var0.getStatus() == Behavior.Status.STOPPED;
            }).forEach((var4x) -> {
               var4x.tryStart(var2, var3, var4);
            });
         }
      };

      RunningPolicy() {
      }

      public abstract <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> var1, ServerLevel var2, E var3, long var4);

      // $FF: synthetic method
      private static RunningPolicy[] $values() {
         return new RunningPolicy[]{RUN_ONE, TRY_ALL};
      }
   }
}
