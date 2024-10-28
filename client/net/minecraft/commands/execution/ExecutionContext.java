package net.minecraft.commands.execution;

import com.google.common.collect.Queues;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.commands.execution.tasks.CallFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ExecutionContext<T> implements AutoCloseable {
   private static final int MAX_QUEUE_DEPTH = 10000000;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final int commandLimit;
   private final int forkLimit;
   private final ProfilerFiller profiler;
   @Nullable
   private TraceCallbacks tracer;
   private int commandQuota;
   private boolean queueOverflow;
   private final Deque<CommandQueueEntry<T>> commandQueue = Queues.newArrayDeque();
   private final List<CommandQueueEntry<T>> newTopCommands = new ObjectArrayList();
   private int currentFrameDepth;

   public ExecutionContext(int var1, int var2, ProfilerFiller var3) {
      super();
      this.commandLimit = var1;
      this.forkLimit = var2;
      this.profiler = var3;
      this.commandQuota = var1;
   }

   private static <T extends ExecutionCommandSource<T>> Frame createTopFrame(ExecutionContext<T> var0, CommandResultCallback var1) {
      if (var0.currentFrameDepth == 0) {
         Deque var10004 = var0.commandQueue;
         Objects.requireNonNull(var10004);
         return new Frame(0, var1, var10004::clear);
      } else {
         int var2 = var0.currentFrameDepth + 1;
         return new Frame(var2, var1, var0.frameControlForDepth(var2));
      }
   }

   public static <T extends ExecutionCommandSource<T>> void queueInitialFunctionCall(ExecutionContext<T> var0, InstantiatedFunction<T> var1, T var2, CommandResultCallback var3) {
      var0.queueNext(new CommandQueueEntry(createTopFrame(var0, var3), (new CallFunction(var1, var2.callback(), false)).bind(var2)));
   }

   public static <T extends ExecutionCommandSource<T>> void queueInitialCommandExecution(ExecutionContext<T> var0, String var1, ContextChain<T> var2, T var3, CommandResultCallback var4) {
      var0.queueNext(new CommandQueueEntry(createTopFrame(var0, var4), new BuildContexts.TopLevel(var1, var2, var3)));
   }

   private void handleQueueOverflow() {
      this.queueOverflow = true;
      this.newTopCommands.clear();
      this.commandQueue.clear();
   }

   public void queueNext(CommandQueueEntry<T> var1) {
      if (this.newTopCommands.size() + this.commandQueue.size() > 10000000) {
         this.handleQueueOverflow();
      }

      if (!this.queueOverflow) {
         this.newTopCommands.add(var1);
      }

   }

   public void discardAtDepthOrHigher(int var1) {
      while(!this.commandQueue.isEmpty() && ((CommandQueueEntry)this.commandQueue.peek()).frame().depth() >= var1) {
         this.commandQueue.removeFirst();
      }

   }

   public Frame.FrameControl frameControlForDepth(int var1) {
      return () -> {
         this.discardAtDepthOrHigher(var1);
      };
   }

   public void runCommandQueue() {
      this.pushNewCommands();

      while(true) {
         if (this.commandQuota <= 0) {
            LOGGER.info("Command execution stopped due to limit (executed {} commands)", this.commandLimit);
            break;
         }

         CommandQueueEntry var1 = (CommandQueueEntry)this.commandQueue.pollFirst();
         if (var1 == null) {
            return;
         }

         this.currentFrameDepth = var1.frame().depth();
         var1.execute(this);
         if (this.queueOverflow) {
            LOGGER.error("Command execution stopped due to command queue overflow (max {})", 10000000);
            break;
         }

         this.pushNewCommands();
      }

      this.currentFrameDepth = 0;
   }

   private void pushNewCommands() {
      for(int var1 = this.newTopCommands.size() - 1; var1 >= 0; --var1) {
         this.commandQueue.addFirst((CommandQueueEntry)this.newTopCommands.get(var1));
      }

      this.newTopCommands.clear();
   }

   public void tracer(@Nullable TraceCallbacks var1) {
      this.tracer = var1;
   }

   @Nullable
   public TraceCallbacks tracer() {
      return this.tracer;
   }

   public ProfilerFiller profiler() {
      return this.profiler;
   }

   public int forkLimit() {
      return this.forkLimit;
   }

   public void incrementCost() {
      --this.commandQuota;
   }

   public void close() {
      if (this.tracer != null) {
         this.tracer.close();
      }

   }
}
