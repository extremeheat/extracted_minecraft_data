package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Unit;
import java.util.Objects;
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

public class BehaviorBuilder<E extends LivingEntity, M> implements App<Mu<E>, M> {
   private final TriggerWithResult<E, M> trigger;

   public static <E extends LivingEntity, M> BehaviorBuilder<E, M> unbox(App<Mu<E>, M> var0) {
      return (BehaviorBuilder)var0;
   }

   public static <E extends LivingEntity> Instance<E> instance() {
      return new Instance<E>();
   }

   public static <E extends LivingEntity> OneShot<E> create(Function<Instance<E>, ? extends App<Mu<E>, Trigger<E>>> var0) {
      final TriggerWithResult var1 = get((App)var0.apply(instance()));
      return new OneShot<E>() {
         public boolean trigger(ServerLevel var1x, E var2, long var3) {
            Trigger var5 = (Trigger)var1.tryTrigger(var1x, var2, var3);
            return var5 == null ? false : var5.trigger(var1x, var2, var3);
         }

         public String debugString() {
            return "OneShot[" + var1.debugString() + "]";
         }

         public String toString() {
            return this.debugString();
         }
      };
   }

   public static <E extends LivingEntity> OneShot<E> sequence(Trigger<? super E> var0, Trigger<? super E> var1) {
      return create((Function)((var2) -> var2.group(var2.ifTriggered(var0)).apply(var2, (var1x) -> {
            Objects.requireNonNull(var1);
            return var1::trigger;
         })));
   }

   public static <E extends LivingEntity> OneShot<E> triggerIf(Predicate<E> var0, OneShot<? super E> var1) {
      return sequence(triggerIf(var0), var1);
   }

   public static <E extends LivingEntity> OneShot<E> triggerIf(Predicate<E> var0) {
      return create((Function)((var1) -> var1.point((Trigger)(var1x, var2, var3) -> var0.test(var2))));
   }

   public static <E extends LivingEntity> OneShot<E> triggerIf(BiPredicate<ServerLevel, E> var0) {
      return create((Function)((var1) -> var1.point((Trigger)(var1x, var2, var3) -> var0.test(var1x, var2))));
   }

   static <E extends LivingEntity, M> TriggerWithResult<E, M> get(App<Mu<E>, M> var0) {
      return unbox(var0).trigger;
   }

   BehaviorBuilder(TriggerWithResult<E, M> var1) {
      super();
      this.trigger = var1;
   }

   static <E extends LivingEntity, M> BehaviorBuilder<E, M> create(TriggerWithResult<E, M> var0) {
      return new BehaviorBuilder<E, M>(var0);
   }

   public static final class Mu<E extends LivingEntity> implements K1 {
      public Mu() {
         super();
      }
   }

   static final class PureMemory<E extends LivingEntity, F extends K1, Value> extends BehaviorBuilder<E, MemoryAccessor<F, Value>> {
      PureMemory(final MemoryCondition<F, Value> var1) {
         super(new TriggerWithResult<E, MemoryAccessor<F, Value>>() {
            public MemoryAccessor<F, Value> tryTrigger(ServerLevel var1x, E var2, long var3) {
               Brain var5 = var2.getBrain();
               Optional var6 = var5.getMemoryInternal(var1.memory());
               return var6 == null ? null : var1.createAccessor(var5, var6);
            }

            public String debugString() {
               return "M[" + String.valueOf(var1) + "]";
            }

            public String toString() {
               return this.debugString();
            }

            // $FF: synthetic method
            public Object tryTrigger(final ServerLevel var1x, final LivingEntity var2, final long var3) {
               return this.tryTrigger(var1x, var2, var3);
            }
         });
      }
   }

   static final class Constant<E extends LivingEntity, A> extends BehaviorBuilder<E, A> {
      Constant(A var1) {
         this(var1, () -> "C[" + String.valueOf(var1) + "]");
      }

      Constant(final A var1, final Supplier<String> var2) {
         super(new TriggerWithResult<E, A>() {
            public A tryTrigger(ServerLevel var1x, E var2x, long var3) {
               return (A)var1;
            }

            public String debugString() {
               return (String)var2.get();
            }

            public String toString() {
               return this.debugString();
            }
         });
      }
   }

   static final class TriggerWrapper<E extends LivingEntity> extends BehaviorBuilder<E, Unit> {
      TriggerWrapper(final Trigger<? super E> var1) {
         super(new TriggerWithResult<E, Unit>() {
            @Nullable
            public Unit tryTrigger(ServerLevel var1x, E var2, long var3) {
               return var1.trigger(var1x, var2, var3) ? Unit.INSTANCE : null;
            }

            public String debugString() {
               return "T[" + String.valueOf(var1) + "]";
            }

            // $FF: synthetic method
            @Nullable
            public Object tryTrigger(final ServerLevel var1x, final LivingEntity var2, final long var3) {
               return this.tryTrigger(var1x, var2, var3);
            }
         });
      }
   }

   public static final class Instance<E extends LivingEntity> implements Applicative<Mu<E>, Mu<E>> {
      public Instance() {
         super();
      }

      public <Value> Optional<Value> tryGet(MemoryAccessor<OptionalBox.Mu, Value> var1) {
         return OptionalBox.unbox(var1.value());
      }

      public <Value> Value get(MemoryAccessor<IdF.Mu, Value> var1) {
         return (Value)IdF.get(var1.value());
      }

      public <Value> BehaviorBuilder<E, MemoryAccessor<OptionalBox.Mu, Value>> registered(MemoryModuleType<Value> var1) {
         return new PureMemory(new MemoryCondition.Registered(var1));
      }

      public <Value> BehaviorBuilder<E, MemoryAccessor<IdF.Mu, Value>> present(MemoryModuleType<Value> var1) {
         return new PureMemory(new MemoryCondition.Present(var1));
      }

      public <Value> BehaviorBuilder<E, MemoryAccessor<Const.Mu<Unit>, Value>> absent(MemoryModuleType<Value> var1) {
         return new PureMemory(new MemoryCondition.Absent(var1));
      }

      public BehaviorBuilder<E, Unit> ifTriggered(Trigger<? super E> var1) {
         return new TriggerWrapper(var1);
      }

      public <A> BehaviorBuilder<E, A> point(A var1) {
         return new Constant<E, A>(var1);
      }

      public <A> BehaviorBuilder<E, A> point(Supplier<String> var1, A var2) {
         return new Constant<E, A>(var2, var1);
      }

      public <A, R> Function<App<Mu<E>, A>, App<Mu<E>, R>> lift1(App<Mu<E>, Function<A, R>> var1) {
         return (var2) -> {
            final TriggerWithResult var3 = BehaviorBuilder.get(var2);
            final TriggerWithResult var4 = BehaviorBuilder.get(var1);
            return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
               public R tryTrigger(ServerLevel var1, E var2, long var3x) {
                  Object var5 = var3.tryTrigger(var1, var2, var3x);
                  if (var5 == null) {
                     return null;
                  } else {
                     Function var6 = (Function)var4.tryTrigger(var1, var2, var3x);
                     return (R)(var6 == null ? null : var6.apply(var5));
                  }
               }

               public String debugString() {
                  String var10000 = var4.debugString();
                  return var10000 + " * " + var3.debugString();
               }

               public String toString() {
                  return this.debugString();
               }
            });
         };
      }

      public <T, R> BehaviorBuilder<E, R> map(final Function<? super T, ? extends R> var1, App<Mu<E>, T> var2) {
         final TriggerWithResult var3 = BehaviorBuilder.get(var2);
         return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
            public R tryTrigger(ServerLevel var1x, E var2, long var3x) {
               Object var5 = var3.tryTrigger(var1x, var2, var3x);
               return (R)(var5 == null ? null : var1.apply(var5));
            }

            public String debugString() {
               String var10000 = var3.debugString();
               return var10000 + ".map[" + String.valueOf(var1) + "]";
            }

            public String toString() {
               return this.debugString();
            }
         });
      }

      public <A, B, R> BehaviorBuilder<E, R> ap2(App<Mu<E>, BiFunction<A, B, R>> var1, App<Mu<E>, A> var2, App<Mu<E>, B> var3) {
         final TriggerWithResult var4 = BehaviorBuilder.get(var2);
         final TriggerWithResult var5 = BehaviorBuilder.get(var3);
         final TriggerWithResult var6 = BehaviorBuilder.get(var1);
         return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
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

            public String debugString() {
               String var10000 = var6.debugString();
               return var10000 + " * " + var4.debugString() + " * " + var5.debugString();
            }

            public String toString() {
               return this.debugString();
            }
         });
      }

      public <T1, T2, T3, R> BehaviorBuilder<E, R> ap3(App<Mu<E>, Function3<T1, T2, T3, R>> var1, App<Mu<E>, T1> var2, App<Mu<E>, T2> var3, App<Mu<E>, T3> var4) {
         final TriggerWithResult var5 = BehaviorBuilder.get(var2);
         final TriggerWithResult var6 = BehaviorBuilder.get(var3);
         final TriggerWithResult var7 = BehaviorBuilder.get(var4);
         final TriggerWithResult var8 = BehaviorBuilder.get(var1);
         return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
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

            public String debugString() {
               String var10000 = var8.debugString();
               return var10000 + " * " + var5.debugString() + " * " + var6.debugString() + " * " + var7.debugString();
            }

            public String toString() {
               return this.debugString();
            }
         });
      }

      public <T1, T2, T3, T4, R> BehaviorBuilder<E, R> ap4(App<Mu<E>, Function4<T1, T2, T3, T4, R>> var1, App<Mu<E>, T1> var2, App<Mu<E>, T2> var3, App<Mu<E>, T3> var4, App<Mu<E>, T4> var5) {
         final TriggerWithResult var6 = BehaviorBuilder.get(var2);
         final TriggerWithResult var7 = BehaviorBuilder.get(var3);
         final TriggerWithResult var8 = BehaviorBuilder.get(var4);
         final TriggerWithResult var9 = BehaviorBuilder.get(var5);
         final TriggerWithResult var10 = BehaviorBuilder.get(var1);
         return BehaviorBuilder.create(new TriggerWithResult<E, R>() {
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

            public String debugString() {
               String var10000 = var10.debugString();
               return var10000 + " * " + var6.debugString() + " * " + var7.debugString() + " * " + var8.debugString() + " * " + var9.debugString();
            }

            public String toString() {
               return this.debugString();
            }
         });
      }

      // $FF: synthetic method
      public App ap4(final App var1, final App var2, final App var3, final App var4, final App var5) {
         return this.ap4(var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      public App ap3(final App var1, final App var2, final App var3, final App var4) {
         return this.ap3(var1, var2, var3, var4);
      }

      // $FF: synthetic method
      public App ap2(final App var1, final App var2, final App var3) {
         return this.ap2(var1, var2, var3);
      }

      // $FF: synthetic method
      public App point(final Object var1) {
         return this.point(var1);
      }

      // $FF: synthetic method
      public App map(final Function var1, final App var2) {
         return this.map(var1, var2);
      }

      static final class Mu<E extends LivingEntity> implements Applicative.Mu {
         private Mu() {
            super();
         }
      }
   }

   interface TriggerWithResult<E extends LivingEntity, R> {
      @Nullable
      R tryTrigger(ServerLevel var1, E var2, long var3);

      String debugString();
   }
}
