package net.minecraft.commands.execution;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.commands.ExecutionCommandSource;

public interface CustomCommandExecutor<T> {
   void run(T var1, ContextChain<T> var2, ChainModifiers var3, ExecutionControl<T> var4);

   public abstract static class WithErrorHandling<T extends ExecutionCommandSource<T>> implements CustomCommandExecutor<T> {
      public WithErrorHandling() {
         super();
      }

      public final void run(T var1, ContextChain<T> var2, ChainModifiers var3, ExecutionControl<T> var4) {
         try {
            this.runGuarded(var1, var2, var3, var4);
         } catch (CommandSyntaxException var6) {
            this.onError(var6, var1, var3, var4.tracer());
            var1.callback().onFailure();
         }

      }

      protected void onError(CommandSyntaxException var1, T var2, ChainModifiers var3, @Nullable TraceCallbacks var4) {
         var2.handleError(var1, var3.isForked(), var4);
      }

      protected abstract void runGuarded(T var1, ContextChain<T> var2, ChainModifiers var3, ExecutionControl<T> var4) throws CommandSyntaxException;
   }

   public interface CommandAdapter<T> extends Command<T>, CustomCommandExecutor<T> {
      default int run(CommandContext<T> var1) throws CommandSyntaxException {
         throw new UnsupportedOperationException("This function should not run");
      }
   }
}
