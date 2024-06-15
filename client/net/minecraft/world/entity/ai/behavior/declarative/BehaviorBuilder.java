package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Unit;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorBuilder<E extends LivingEntity, M> implements App<BehaviorBuilder.Mu<E>, M> {
   private final BehaviorBuilder.TriggerWithResult<E, M> trigger;

   public static <E extends LivingEntity, M> BehaviorBuilder<E, M> unbox(App<BehaviorBuilder.Mu<E>, M> var0) {
      return (BehaviorBuilder<E, M>)var0;
   }

   public static <E extends LivingEntity> BehaviorBuilder.Instance<E> instance() {
      return new BehaviorBuilder.Instance<>();
   }

   public static <E extends LivingEntity> OneShot<E> create(Function<BehaviorBuilder.Instance<E>, ? extends App<BehaviorBuilder.Mu<E>, Trigger<E>>> var0) {
      final BehaviorBuilder.TriggerWithResult var1 = get((App<BehaviorBuilder.Mu<E>, M>)var0.apply(instance()));
      return new OneShot<E>() {
         @Override
         public boolean trigger(ServerLevel var1x, E var2, long var3) {
            Trigger var5 = (Trigger)var1.tryTrigger(var1x, var2, var3);
            return var5 == null ? false : var5.trigger(var1x, var2, var3);
         }

         @Override
         public String debugString() {
            return "OneShot[" + var1.debugString() + "]";
         }

         @Override
         public String toString() {
            return this.debugString();
         }
      };
   }

   public static <E extends LivingEntity> OneShot<E> sequence(Trigger<? super E> var0, Trigger<? super E> var1) {
      return create(var2 -> var2.group(var2.ifTriggered(var0)).apply(var2, var1xx -> var1::trigger));
   }

   public static <E extends LivingEntity> OneShot<E> triggerIf(Predicate<E> var0, OneShot<? super E> var1) {
      return sequence(triggerIf(var0), var1);
   }

   public static <E extends LivingEntity> OneShot<E> triggerIf(Predicate<E> var0) {
      return create(var1 -> var1.point((var1x, var2, var3) -> var0.test(var2)));
   }

   public static <E extends LivingEntity> OneShot<E> triggerIf(BiPredicate<ServerLevel, E> var0) {
      return create(var1 -> var1.point((var1x, var2, var3) -> var0.test(var1x, var2)));
   }

   static <E extends LivingEntity, M> BehaviorBuilder.TriggerWithResult<E, M> get(App<BehaviorBuilder.Mu<E>, M> var0) {
      return unbox(var0).trigger;
   }

   BehaviorBuilder(BehaviorBuilder.TriggerWithResult<E, M> var1) {
      super();
      this.trigger = var1;
   }

   static <E extends LivingEntity, M> BehaviorBuilder<E, M> create(BehaviorBuilder.TriggerWithResult<E, M> var0) {
      return new BehaviorBuilder<>(var0);
   }

   static final class Constant<E extends LivingEntity, A> extends BehaviorBuilder<E, A> {
      Constant(A var1) {
         this((A)var1, () -> "C[" + var1 + "]");
      }

      Constant(final A var1, final Supplier<String> var2) {
         super(new BehaviorBuilder.TriggerWithResult<E, A>() {
            @Override
            public A tryTrigger(ServerLevel var1x, E var2x, long var3) {
               return (A)var1;
            }

            @Override
            public String debugString() {
               return (String)var2.get();
            }

            @Override
            public String toString() {
               return this.debugString();
            }
         });
      }
   }

   public static final class Instance<E extends LivingEntity> implements Applicative<BehaviorBuilder.Mu<E>, BehaviorBuilder.Instance.Mu<E>> {
      public Instance() {
         super();
      }

      public <Value> Optional<Value> tryGet(MemoryAccessor<com.mojang.datafixers.kinds.OptionalBox.Mu, Value> var1) {
         return OptionalBox.unbox(var1.value());
      }

      public <Value> Value get(MemoryAccessor<com.mojang.datafixers.kinds.IdF.Mu, Value> var1) {
         return (Value)IdF.get(var1.value());
      }

      public <Value> BehaviorBuilder<E, MemoryAccessor<com.mojang.datafixers.kinds.OptionalBox.Mu, Value>> registered(MemoryModuleType<Value> var1) {
         return new BehaviorBuilder.PureMemory<>(new MemoryCondition.Registered(var1));
      }

      public <Value> BehaviorBuilder<E, MemoryAccessor<com.mojang.datafixers.kinds.IdF.Mu, Value>> present(MemoryModuleType<Value> var1) {
         return new BehaviorBuilder.PureMemory<>(new MemoryCondition.Present(var1));
      }

      public <Value> BehaviorBuilder<E, MemoryAccessor<com.mojang.datafixers.kinds.Const.Mu<Unit>, Value>> absent(MemoryModuleType<Value> var1) {
         return new BehaviorBuilder.PureMemory<>(new MemoryCondition.Absent(var1));
      }

      public BehaviorBuilder<E, Unit> ifTriggered(Trigger<? super E> var1) {
         return new BehaviorBuilder.TriggerWrapper<>(var1);
      }

      public <A> BehaviorBuilder<E, A> point(A var1) {
         return new BehaviorBuilder.Constant<>((A)var1);
      }

      public <A> BehaviorBuilder<E, A> point(Supplier<String> var1, A var2) {
         return new BehaviorBuilder.Constant<>((A)var2, var1);
      }

      public <A, R> Function<App<BehaviorBuilder.Mu<E>, A>, App<BehaviorBuilder.Mu<E>, R>> lift1(App<BehaviorBuilder.Mu<E>, Function<A, R>> var1) {
         return var2 -> {
            final BehaviorBuilder.TriggerWithResult var3 = BehaviorBuilder.get((App<BehaviorBuilder.Mu<E>, M>)var2);
            final BehaviorBuilder.TriggerWithResult var4 = BehaviorBuilder.get(var1);
            return BehaviorBuilder.create(new BehaviorBuilder.TriggerWithResult<E, R>() {
               @Override
               public R tryTrigger(ServerLevel var1, E var2, long var3x) {
                  Object var5 = var3.tryTrigger(var1, var2, var3x);
                  if (var5 == null) {
                     return null;
                  } else {
                     Function var6 = (Function)var4.tryTrigger(var1, var2, var3x);
                     return (R)(var6 == null ? null : var6.apply(var5));
                  }
               }

               @Override
               public String debugString() {
                  return var4.debugString() + " * " + var3.debugString();
               }

               @Override
               public String toString() {
                  return this.debugString();
               }
            });
         };
      }

      public <T, R> BehaviorBuilder<E, R> map(final Function<? super T, ? extends R> var1, App<BehaviorBuilder.Mu<E>, T> var2) {
         final BehaviorBuilder.TriggerWithResult var3 = BehaviorBuilder.get(var2);
         return BehaviorBuilder.create(new BehaviorBuilder.TriggerWithResult<E, R>() {
            @Override
            public R tryTrigger(ServerLevel var1x, E var2, long var3x) {
               Object var5 = var3.tryTrigger(var1x, var2, var3x);
               return (R)(var5 == null ? null : var1.apply(var5));
            }

            @Override
            public String debugString() {
               return var3.debugString() + ".map[" + var1 + "]";
            }

            @Override
            public String toString() {
               return this.debugString();
            }
         });
      }

      public <A, B, R> BehaviorBuilder<E, R> ap2(
         App<BehaviorBuilder.Mu<E>, BiFunction<A, B, R>> var1, App<BehaviorBuilder.Mu<E>, A> var2, App<BehaviorBuilder.Mu<E>, B> var3
      ) {
         final BehaviorBuilder.TriggerWithResult var4 = BehaviorBuilder.get(var2);
         final BehaviorBuilder.TriggerWithResult var5 = BehaviorBuilder.get(var3);
         final BehaviorBuilder.TriggerWithResult var6 = BehaviorBuilder.get(var1);
         return BehaviorBuilder.create(new BehaviorBuilder.TriggerWithResult<E, R>() {
            @Override
            public R tryTrigger(ServerLevel var1, E var2, long var3) {
               Object var5x = var4.tryTrigger(var1, var2, var3);
               if (var5x == null) {
                  return null;
               } else {
                  Object var6x = var5.tryTrigger(var1, var2, var3);
                  if (var6x == null) {
                     return null;
                  } else {
                     BiFunction var7 = (BiFunction)var6.tryTrigger(var1, var2, var3);
                     return (R)(var7 == null ? null : var7.apply(var5x, var6x));
                  }
               }
            }

            @Override
            public String debugString() {
               return var6.debugString() + " * " + var4.debugString() + " * " + var5.debugString();
            }

            @Override
            public String toString() {
               return this.debugString();
            }
         });
      }

      public <T1, T2, T3, R> BehaviorBuilder<E, R> ap3(
         App<BehaviorBuilder.Mu<E>, Function3<T1, T2, T3, R>> var1,
         App<BehaviorBuilder.Mu<E>, T1> var2,
         App<BehaviorBuilder.Mu<E>, T2> var3,
         App<BehaviorBuilder.Mu<E>, T3> var4
      ) {
         final BehaviorBuilder.TriggerWithResult var5 = BehaviorBuilder.get(var2);
         final BehaviorBuilder.TriggerWithResult var6 = BehaviorBuilder.get(var3);
         final BehaviorBuilder.TriggerWithResult var7 = BehaviorBuilder.get(var4);
         final BehaviorBuilder.TriggerWithResult var8 = BehaviorBuilder.get(var1);
         return BehaviorBuilder.create(new BehaviorBuilder.TriggerWithResult<E, R>() {
            @Override
            public R tryTrigger(ServerLevel var1, E var2, long var3) {
               Object var5x = var5.tryTrigger(var1, var2, var3);
               if (var5x == null) {
                  return null;
               } else {
                  Object var6x = var6.tryTrigger(var1, var2, var3);
                  if (var6x == null) {
                     return null;
                  } else {
                     Object var7x = var7.tryTrigger(var1, var2, var3);
                     if (var7x == null) {
                        return null;
                     } else {
                        Function3 var8x = (Function3)var8.tryTrigger(var1, var2, var3);
                        return (R)(var8x == null ? null : var8x.apply(var5x, var6x, var7x));
                     }
                  }
               }
            }

            @Override
            public String debugString() {
               return var8.debugString() + " * " + var5.debugString() + " * " + var6.debugString() + " * " + var7.debugString();
            }

            @Override
            public String toString() {
               return this.debugString();
            }
         });
      }

      public <T1, T2, T3, T4, R> BehaviorBuilder<E, R> ap4(
         App<BehaviorBuilder.Mu<E>, Function4<T1, T2, T3, T4, R>> var1,
         App<BehaviorBuilder.Mu<E>, T1> var2,
         App<BehaviorBuilder.Mu<E>, T2> var3,
         App<BehaviorBuilder.Mu<E>, T3> var4,
         App<BehaviorBuilder.Mu<E>, T4> var5
      ) {
         final BehaviorBuilder.TriggerWithResult var6 = BehaviorBuilder.get(var2);
         final BehaviorBuilder.TriggerWithResult var7 = BehaviorBuilder.get(var3);
         final BehaviorBuilder.TriggerWithResult var8 = BehaviorBuilder.get(var4);
         final BehaviorBuilder.TriggerWithResult var9 = BehaviorBuilder.get(var5);
         final BehaviorBuilder.TriggerWithResult var10 = BehaviorBuilder.get(var1);
         return BehaviorBuilder.create(new BehaviorBuilder.TriggerWithResult<E, R>() {
            @Override
            public R tryTrigger(ServerLevel var1, E var2, long var3) {
               Object var5 = var6.tryTrigger(var1, var2, var3);
               if (var5 == null) {
                  return null;
               } else {
                  Object var6x = var7.tryTrigger(var1, var2, var3);
                  if (var6x == null) {
                     return null;
                  } else {
                     Object var7x = var8.tryTrigger(var1, var2, var3);
                     if (var7x == null) {
                        return null;
                     } else {
                        Object var8x = var9.tryTrigger(var1, var2, var3);
                        if (var8x == null) {
                           return null;
                        } else {
                           Function4 var9x = (Function4)var10.tryTrigger(var1, var2, var3);
                           return (R)(var9x == null ? null : var9x.apply(var5, var6x, var7x, var8x));
                        }
                     }
                  }
               }
            }

            @Override
            public String debugString() {
               return var10.debugString() + " * " + var6.debugString() + " * " + var7.debugString() + " * " + var8.debugString() + " * " + var9.debugString();
            }

            @Override
            public String toString() {
               return this.debugString();
            }
         });
      }

      static final class Mu<E extends LivingEntity> implements com.mojang.datafixers.kinds.Applicative.Mu {
         private Mu() {
            super();
         }
      }
   }

   public static final class Mu<E extends LivingEntity> implements K1 {
      public Mu() {
         super();
      }
   }

   static final class PureMemory<E extends LivingEntity, F extends K1, Value> extends BehaviorBuilder<E, MemoryAccessor<F, Value>> {
      PureMemory(final MemoryCondition<F, Value> var1) {
         super(new BehaviorBuilder.TriggerWithResult<E, MemoryAccessor<F, Value>>() {
            public MemoryAccessor<F, Value> tryTrigger(ServerLevel var1x, E var2, long var3) {
               Brain var5 = var2.getBrain();
               Optional var6 = var5.getMemoryInternal(var1.memory());
               return var6 == null ? null : var1.createAccessor(var5, var6);
            }

            @Override
            public String debugString() {
               return "M[" + var1 + "]";
            }

            @Override
            public String toString() {
               return this.debugString();
            }
         });
      }
   }

   interface TriggerWithResult<E extends LivingEntity, R> {
      @Nullable
      R tryTrigger(ServerLevel var1, E var2, long var3);

      String debugString();
   }

   static final class TriggerWrapper<E extends LivingEntity> extends BehaviorBuilder<E, Unit> {
      TriggerWrapper(final Trigger<? super E> var1) {
         super(new BehaviorBuilder.TriggerWithResult<E, Unit>() {
            @Nullable
            public Unit tryTrigger(ServerLevel var1x, E var2, long var3) {
               return var1.trigger(var1x, var2, var3) ? Unit.INSTANCE : null;
            }

            @Override
            public String debugString() {
               return "T[" + var1 + "]";
            }
         });
      }
   }
}
