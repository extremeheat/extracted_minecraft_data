package net.minecraft.commands.execution.tasks;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.context.ContextChain.Stage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.CustomCommandExecutor;
import net.minecraft.commands.execution.CustomModifierExecutor;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.network.chat.Component;

public class BuildContexts<T extends ExecutionCommandSource<T>> {
   @VisibleForTesting
   public static final DynamicCommandExceptionType ERROR_FORK_LIMIT_REACHED = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("command.forkLimit", var0)
   );
   private final String commandInput;
   private final ContextChain<T> command;

   public BuildContexts(String var1, ContextChain<T> var2) {
      super();
      this.commandInput = var1;
      this.command = var2;
   }

   protected void execute(T var1, List<T> var2, ExecutionContext<T> var3, Frame var4, ChainModifiers var5) {
      ContextChain var6 = this.command;
      ChainModifiers var7 = var5;
      Object var8 = var2;
      if (var6.getStage() != Stage.EXECUTE) {
         var3.profiler().push(() -> "prepare " + this.commandInput);

         try {
            for (int var9 = var3.forkLimit(); var6.getStage() != Stage.EXECUTE; var6 = var6.nextStage()) {
               CommandContext var10 = var6.getTopContext();
               if (var10.isForked()) {
                  var7 = var7.setForked();
               }

               RedirectModifier var11 = var10.getRedirectModifier();
               if (var11 instanceof CustomModifierExecutor var28) {
                  var28.apply(var1, (List<ExecutionCommandSource>)var8, var6, var7, ExecutionControl.create(var3, var4));
                  return;
               }

               if (var11 != null) {
                  var3.incrementCost();
                  boolean var12 = var7.isForked();
                  ObjectArrayList var13 = new ObjectArrayList();

                  for (ExecutionCommandSource var15 : var8) {
                     try {
                        Collection var16 = ContextChain.runModifier(var10, var15, (var0, var1x, var2x) -> {
                        }, var12);
                        if (var13.size() + var16.size() >= var9) {
                           var1.handleError(ERROR_FORK_LIMIT_REACHED.create(var9), var12, var3.tracer());
                           return;
                        }

                        var13.addAll(var16);
                     } catch (CommandSyntaxException var20) {
                        var15.handleError(var20, var12, var3.tracer());
                        if (!var12) {
                           return;
                        }
                     }
                  }

                  var8 = var13;
               }
            }
         } finally {
            var3.profiler().pop();
         }
      }

      if (var8.isEmpty()) {
         if (var7.isReturn()) {
            var3.queueNext(new CommandQueueEntry<>(var4, FallthroughTask.instance()));
         }
      } else {
         CommandContext var22 = var6.getTopContext();
         if (var22.getCommand() instanceof CustomCommandExecutor var24) {
            ExecutionControl var29 = ExecutionControl.create(var3, var4);

            for (ExecutionCommandSource var31 : var8) {
               var24.run(var31, var6, var7, var29);
            }
         } else {
            if (var7.isReturn()) {
               ExecutionCommandSource var25 = (ExecutionCommandSource)var8.get(0);
               var25 = var25.withCallback(CommandResultCallback.chain(var25.callback(), var4.returnValueConsumer()));
               var8 = List.of(var25);
            }

            ExecuteCommand var27 = new ExecuteCommand(this.commandInput, var7, var22);
            ContinuationTask.schedule(var3, var4, (List<ExecutionCommandSource>)var8, (var1x, var2x) -> new CommandQueueEntry<>(var1x, var27.bind((T)var2x)));
         }
      }
   }

   protected void traceCommandStart(ExecutionContext<T> var1, Frame var2) {
      TraceCallbacks var3 = var1.tracer();
      if (var3 != null) {
         var3.onCommand(var2.depth(), this.commandInput);
      }
   }

   @Override
   public String toString() {
      return this.commandInput;
   }

   public static class Continuation<T extends ExecutionCommandSource<T>> extends BuildContexts<T> implements EntryAction<T> {
      private final ChainModifiers modifiers;
      private final T originalSource;
      private final List<T> sources;

      public Continuation(String var1, ContextChain<T> var2, ChainModifiers var3, T var4, List<T> var5) {
         super(var1, var2);
         this.originalSource = (T)var4;
         this.sources = var5;
         this.modifiers = var3;
      }

      @Override
      public void execute(ExecutionContext<T> var1, Frame var2) {
         this.execute(this.originalSource, this.sources, var1, var2, this.modifiers);
      }
   }

   public static class TopLevel<T extends ExecutionCommandSource<T>> extends BuildContexts<T> implements EntryAction<T> {
      private final T source;

      public TopLevel(String var1, ContextChain<T> var2, T var3) {
         super(var1, var2);
         this.source = (T)var3;
      }

      @Override
      public void execute(ExecutionContext<T> var1, Frame var2) {
         this.traceCommandStart(var1, var2);
         this.execute(this.source, List.of(this.source), var1, var2, ChainModifiers.DEFAULT);
      }
   }

   public static class Unbound<T extends ExecutionCommandSource<T>> extends BuildContexts<T> implements UnboundEntryAction<T> {
      public Unbound(String var1, ContextChain<T> var2) {
         super(var1, var2);
      }

      public void execute(T var1, ExecutionContext<T> var2, Frame var3) {
         this.traceCommandStart(var2, var3);
         this.execute((T)var1, List.of((T)var1), var2, var3, ChainModifiers.DEFAULT);
      }
   }
}
