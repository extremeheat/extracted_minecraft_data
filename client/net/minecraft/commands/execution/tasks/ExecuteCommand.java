package net.minecraft.commands.execution.tasks;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.commands.execution.UnboundEntryAction;

public class ExecuteCommand<T extends ExecutionCommandSource<T>> implements UnboundEntryAction<T> {
   private final String commandInput;
   private final ChainModifiers modifiers;
   private final CommandContext<T> executionContext;

   public ExecuteCommand(String var1, ChainModifiers var2, CommandContext<T> var3) {
      super();
      this.commandInput = var1;
      this.modifiers = var2;
      this.executionContext = var3;
   }

   public void execute(T var1, ExecutionContext<T> var2, Frame var3) {
      var2.profiler().push(() -> {
         return "execute " + this.commandInput;
      });

      try {
         var2.incrementCost();
         int var4 = ContextChain.runExecutable(this.executionContext, var1, ExecutionCommandSource.resultConsumer(), this.modifiers.isForked());
         TraceCallbacks var5 = var2.tracer();
         if (var5 != null) {
            var5.onReturn(var3.depth(), this.commandInput, var4);
         }
      } catch (CommandSyntaxException var9) {
         var1.handleError(var9, this.modifiers.isForked(), var2.tracer());
      } finally {
         var2.profiler().pop();
      }

   }

   // $FF: synthetic method
   public void execute(final Object var1, final ExecutionContext var2, final Frame var3) {
      this.execute((ExecutionCommandSource)var1, var2, var3);
   }
}
