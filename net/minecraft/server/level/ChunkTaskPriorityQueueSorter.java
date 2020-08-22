package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskPriorityQueueSorter implements AutoCloseable, ChunkHolder.LevelChangeListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map queues;
   private final Set sleeping;
   private final ProcessorMailbox mailbox;

   public ChunkTaskPriorityQueueSorter(List var1, Executor var2, int var3) {
      this.queues = (Map)var1.stream().collect(Collectors.toMap(Function.identity(), (var1x) -> {
         return new ChunkTaskPriorityQueue(var1x.name() + "_queue", var3);
      }));
      this.sleeping = Sets.newHashSet(var1);
      this.mailbox = new ProcessorMailbox(new StrictQueue.FixedPriorityQueue(4), var2, "sorter");
   }

   public static ChunkTaskPriorityQueueSorter.Message message(Runnable var0, long var1, IntSupplier var3) {
      return new ChunkTaskPriorityQueueSorter.Message((var1x) -> {
         return () -> {
            var0.run();
            var1x.tell(Unit.INSTANCE);
         };
      }, var1, var3);
   }

   public static ChunkTaskPriorityQueueSorter.Message message(ChunkHolder var0, Runnable var1) {
      long var10001 = var0.getPos().toLong();
      var0.getClass();
      return message(var1, var10001, var0::getQueueLevel);
   }

   public static ChunkTaskPriorityQueueSorter.Release release(Runnable var0, long var1, boolean var3) {
      return new ChunkTaskPriorityQueueSorter.Release(var0, var1, var3);
   }

   public ProcessorHandle getProcessor(ProcessorHandle var1, boolean var2) {
      return (ProcessorHandle)this.mailbox.ask((var3) -> {
         return new StrictQueue.IntRunnable(0, () -> {
            this.getQueue(var1);
            var3.tell(ProcessorHandle.of("chunk priority sorter around " + var1.name(), (var3x) -> {
               this.submit(var1, var3x.task, var3x.pos, var3x.level, var2);
            }));
         });
      }).join();
   }

   public ProcessorHandle getReleaseProcessor(ProcessorHandle var1) {
      return (ProcessorHandle)this.mailbox.ask((var2) -> {
         return new StrictQueue.IntRunnable(0, () -> {
            var2.tell(ProcessorHandle.of("chunk priority sorter around " + var1.name(), (var2x) -> {
               this.release(var1, var2x.pos, var2x.task, var2x.clearQueue);
            }));
         });
      }).join();
   }

   public void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4) {
      this.mailbox.tell(new StrictQueue.IntRunnable(0, () -> {
         int var5 = var2.getAsInt();
         this.queues.values().forEach((var3x) -> {
            var3x.resortChunkTasks(var5, var1, var3);
         });
         var4.accept(var3);
      }));
   }

   private void release(ProcessorHandle var1, long var2, Runnable var4, boolean var5) {
      this.mailbox.tell(new StrictQueue.IntRunnable(1, () -> {
         ChunkTaskPriorityQueue var6 = this.getQueue(var1);
         var6.release(var2, var5);
         if (this.sleeping.remove(var1)) {
            this.pollTask(var6, var1);
         }

         var4.run();
      }));
   }

   private void submit(ProcessorHandle var1, Function var2, long var3, IntSupplier var5, boolean var6) {
      this.mailbox.tell(new StrictQueue.IntRunnable(2, () -> {
         ChunkTaskPriorityQueue var7 = this.getQueue(var1);
         int var8 = var5.getAsInt();
         var7.submit(Optional.of(var2), var3, var8);
         if (var6) {
            var7.submit(Optional.empty(), var3, var8);
         }

         if (this.sleeping.remove(var1)) {
            this.pollTask(var7, var1);
         }

      }));
   }

   private void pollTask(ChunkTaskPriorityQueue var1, ProcessorHandle var2) {
      this.mailbox.tell(new StrictQueue.IntRunnable(3, () -> {
         Stream var3 = var1.pop();
         if (var3 == null) {
            this.sleeping.add(var2);
         } else {
            Util.sequence((List)var3.map((var1x) -> {
               return (CompletableFuture)var1x.map(var2::ask, (var0) -> {
                  var0.run();
                  return CompletableFuture.completedFuture(Unit.INSTANCE);
               });
            }).collect(Collectors.toList())).thenAccept((var3x) -> {
               this.pollTask(var1, var2);
            });
         }

      }));
   }

   private ChunkTaskPriorityQueue getQueue(ProcessorHandle var1) {
      ChunkTaskPriorityQueue var2 = (ChunkTaskPriorityQueue)this.queues.get(var1);
      if (var2 == null) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("No queue for: " + var1));
      } else {
         return var2;
      }
   }

   @VisibleForTesting
   public String getDebugStatus() {
      return (String)this.queues.entrySet().stream().map((var0) -> {
         return ((ProcessorHandle)var0.getKey()).name() + "=[" + (String)((ChunkTaskPriorityQueue)var0.getValue()).getAcquired().stream().map((var0x) -> {
            return var0x + ":" + new ChunkPos(var0x);
         }).collect(Collectors.joining(",")) + "]";
      }).collect(Collectors.joining(",")) + ", s=" + this.sleeping.size();
   }

   public void close() {
      this.queues.keySet().forEach(ProcessorHandle::close);
   }

   public static final class Release {
      private final Runnable task;
      private final long pos;
      private final boolean clearQueue;

      private Release(Runnable var1, long var2, boolean var4) {
         this.task = var1;
         this.pos = var2;
         this.clearQueue = var4;
      }

      // $FF: synthetic method
      Release(Runnable var1, long var2, boolean var4, Object var5) {
         this(var1, var2, var4);
      }
   }

   public static final class Message {
      private final Function task;
      private final long pos;
      private final IntSupplier level;

      private Message(Function var1, long var2, IntSupplier var4) {
         this.task = var1;
         this.pos = var2;
         this.level = var4;
      }

      // $FF: synthetic method
      Message(Function var1, long var2, IntSupplier var4, Object var5) {
         this(var1, var2, var4);
      }
   }
}
