package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Scheduler<K, T extends ITaskType<K, T>, R> {
   private static final Logger field_202856_b = LogManager.getLogger();
   protected final ExecutorService field_202855_a;
   private final ExecutorService field_202857_c;
   private final AtomicInteger field_202858_d = new AtomicInteger(1);
   private final List<CompletableFuture<R>> field_202859_e = Lists.newArrayList();
   private CompletableFuture<R> field_202860_f = CompletableFuture.completedFuture((Object)null);
   private CompletableFuture<R> field_202861_g = CompletableFuture.completedFuture((Object)null);
   private final Supplier<Map<T, CompletableFuture<R>>> field_202862_h;
   private final Supplier<Map<T, CompletableFuture<Void>>> field_202863_i;
   private final T field_202864_j;

   public Scheduler(String var1, int var2, T var3, Supplier<Map<T, CompletableFuture<R>>> var4, Supplier<Map<T, CompletableFuture<Void>>> var5) {
      super();
      this.field_202864_j = var3;
      this.field_202862_h = var4;
      this.field_202863_i = var5;
      if (var2 == 0) {
         this.field_202855_a = MoreExecutors.newDirectExecutorService();
      } else {
         this.field_202855_a = Executors.newSingleThreadExecutor(new NamedThreadFactory(var1 + "-Scheduler"));
      }

      if (var2 <= 1) {
         this.field_202857_c = MoreExecutors.newDirectExecutorService();
      } else {
         this.field_202857_c = new ForkJoinPool(var2 - 1, (var2x) -> {
            return new ForkJoinWorkerThread(var2x) {
               {
                  this.setName(var1 + "-Worker-" + Scheduler.this.field_202858_d.getAndIncrement());
               }
            };
         }, (var0, var1x) -> {
            field_202856_b.error(String.format("Caught exception in thread %s", var0), var1x);
         }, true);
      }

   }

   public CompletableFuture<R> func_202851_b(K var1) {
      CompletableFuture var2 = this.field_202860_f;
      Supplier var3 = () -> {
         return this.func_201494_a_(var1).func_202914_a(var2, this.field_202864_j);
      };
      CompletableFuture var4 = CompletableFuture.supplyAsync(var3, this.field_202855_a);
      CompletableFuture var5 = var4.thenComposeAsync((var0) -> {
         return var0;
      }, this.field_202857_c);
      this.field_202859_e.add(var5);
      return var5;
   }

   public CompletableFuture<R> func_202845_a() {
      CompletableFuture var1 = (CompletableFuture)this.field_202859_e.remove(this.field_202859_e.size() - 1);
      CompletableFuture var2 = CompletableFuture.allOf((CompletableFuture[])this.field_202859_e.toArray(new CompletableFuture[0])).thenCompose((var1x) -> {
         return var1;
      });
      this.field_202861_g = var2;
      this.field_202859_e.clear();
      this.field_202860_f = var2;
      return var2;
   }

   protected Scheduler<K, T, R>.FutureWrapper func_201494_a_(K var1) {
      return this.func_212252_a_(var1, true);
   }

   @Nullable
   protected abstract Scheduler<K, T, R>.FutureWrapper func_212252_a_(K var1, boolean var2);

   public void func_202854_b() throws InterruptedException {
      this.field_202855_a.shutdown();
      this.field_202855_a.awaitTermination(1L, TimeUnit.DAYS);
      this.field_202857_c.shutdown();
      this.field_202857_c.awaitTermination(1L, TimeUnit.DAYS);
   }

   protected abstract R func_201493_a_(K var1, T var2, Map<K, R> var3);

   @Nullable
   public R func_212537_b(K var1, boolean var2) {
      Scheduler.FutureWrapper var3 = this.func_212252_a_(var1, var2);
      return var3 != null ? var3.func_202917_a() : null;
   }

   public CompletableFuture<R> func_202846_c() {
      CompletableFuture var1 = this.field_202861_g;
      return var1.thenApply((var0) -> {
         return var0;
      });
   }

   protected abstract void func_205607_b_(K var1, Scheduler<K, T, R>.FutureWrapper var2);

   protected abstract Scheduler<K, T, R>.FutureWrapper func_205606_a_(K var1, Scheduler<K, T, R>.FutureWrapper var2);

   public final class FutureWrapper {
      private final Map<T, CompletableFuture<R>> field_202920_b;
      private final K field_202921_c;
      private final R field_202922_d;

      public FutureWrapper(K var2, R var3, T var4) {
         super();
         this.field_202920_b = (Map)Scheduler.this.field_202862_h.get();
         this.field_202921_c = var2;

         for(this.field_202922_d = var3; var4 != null; var4 = var4.func_201497_a_()) {
            this.field_202920_b.put(var4, CompletableFuture.completedFuture(var3));
         }

      }

      public R func_202917_a() {
         return this.field_202922_d;
      }

      private CompletableFuture<R> func_202914_a(CompletableFuture<R> var1, T var2) {
         ConcurrentHashMap var3 = new ConcurrentHashMap();
         return (CompletableFuture)this.field_202920_b.computeIfAbsent(var2, (var4) -> {
            if (var2.func_201497_a_() == null) {
               return CompletableFuture.completedFuture(this.field_202922_d);
            } else {
               var2.func_201492_a_(this.field_202921_c, (var3x, var4x) -> {
                  CompletableFuture var10000 = (CompletableFuture)var3.put(var3x, Scheduler.this.func_205606_a_(var3x, Scheduler.this.func_201494_a_(var3x)).func_202914_a(var1, var4x));
               });
               CompletableFuture[] var5 = (CompletableFuture[])Streams.concat(new Stream[]{Stream.of(var1), var3.values().stream()}).toArray((var0) -> {
                  return new CompletableFuture[var0];
               });
               CompletableFuture var6 = CompletableFuture.allOf(var5).thenApplyAsync((var3x) -> {
                  return Scheduler.this.func_201493_a_(this.field_202921_c, var2, Maps.transformValues(var3, (var0) -> {
                     try {
                        return var0.get();
                     } catch (ExecutionException | InterruptedException var2) {
                        throw new RuntimeException(var2);
                     }
                  }));
               }, Scheduler.this.field_202857_c).thenApplyAsync((var2x) -> {
                  Iterator var3x = var3.keySet().iterator();

                  while(var3x.hasNext()) {
                     Object var4 = var3x.next();
                     Scheduler.this.func_205607_b_(var4, Scheduler.this.func_201494_a_(var4));
                  }

                  return var2x;
               }, Scheduler.this.field_202855_a);
               this.field_202920_b.put(var2, var6);
               return var6;
            }
         });
      }
   }
}
