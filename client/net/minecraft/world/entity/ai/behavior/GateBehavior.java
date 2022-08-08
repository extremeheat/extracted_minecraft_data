package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
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

public class GateBehavior<E extends LivingEntity> extends Behavior<E> {
   private final Set<MemoryModuleType<?>> exitErasedMemories;
   private final OrderPolicy orderPolicy;
   private final RunningPolicy runningPolicy;
   private final ShufflingList<Behavior<? super E>> behaviors = new ShufflingList();

   public GateBehavior(Map<MemoryModuleType<?>, MemoryStatus> var1, Set<MemoryModuleType<?>> var2, OrderPolicy var3, RunningPolicy var4, List<Pair<Behavior<? super E>, Integer>> var5) {
      super(var1);
      this.exitErasedMemories = var2;
      this.orderPolicy = var3;
      this.runningPolicy = var4;
      var5.forEach((var1x) -> {
         this.behaviors.add((Behavior)var1x.getFirst(), (Integer)var1x.getSecond());
      });
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return this.behaviors.stream().filter((var0) -> {
         return var0.getStatus() == Behavior.Status.RUNNING;
      }).anyMatch((var4) -> {
         return var4.canStillUse(var1, var2, var3);
      });
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      this.orderPolicy.apply(this.behaviors);
      this.runningPolicy.apply(this.behaviors.stream(), var1, var2, var3);
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      this.behaviors.stream().filter((var0) -> {
         return var0.getStatus() == Behavior.Status.RUNNING;
      }).forEach((var4) -> {
         var4.tickOrStop(var1, var2, var3);
      });
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
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

   public String toString() {
      Set var1 = (Set)this.behaviors.stream().filter((var0) -> {
         return var0.getStatus() == Behavior.Status.RUNNING;
      }).collect(Collectors.toSet());
      String var10000 = this.getClass().getSimpleName();
      return "(" + var10000 + "): " + var1;
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
         public <E extends LivingEntity> void apply(Stream<Behavior<? super E>> var1, ServerLevel var2, E var3, long var4) {
            var1.filter((var0) -> {
               return var0.getStatus() == Behavior.Status.STOPPED;
            }).filter((var4x) -> {
               return var4x.tryStart(var2, var3, var4);
            }).findFirst();
         }
      },
      TRY_ALL {
         public <E extends LivingEntity> void apply(Stream<Behavior<? super E>> var1, ServerLevel var2, E var3, long var4) {
            var1.filter((var0) -> {
               return var0.getStatus() == Behavior.Status.STOPPED;
            }).forEach((var4x) -> {
               var4x.tryStart(var2, var3, var4);
            });
         }
      };

      RunningPolicy() {
      }

      public abstract <E extends LivingEntity> void apply(Stream<Behavior<? super E>> var1, ServerLevel var2, E var3, long var4);

      // $FF: synthetic method
      private static RunningPolicy[] $values() {
         return new RunningPolicy[]{RUN_ONE, TRY_ALL};
      }
   }
}
