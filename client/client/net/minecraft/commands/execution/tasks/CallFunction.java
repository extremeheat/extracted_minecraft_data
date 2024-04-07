package net.minecraft.commands.execution.tasks;

import java.util.List;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.TraceCallbacks;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.functions.InstantiatedFunction;

public class CallFunction<T extends ExecutionCommandSource<T>> implements UnboundEntryAction<T> {
   private final InstantiatedFunction<T> function;
   private final CommandResultCallback resultCallback;
   private final boolean returnParentFrame;

   public CallFunction(InstantiatedFunction<T> var1, CommandResultCallback var2, boolean var3) {
      super();
      this.function = var1;
      this.resultCallback = var2;
      this.returnParentFrame = var3;
   }

   public void execute(T var1, ExecutionContext<T> var2, Frame var3) {
      var2.incrementCost();
      List var4 = this.function.entries();
      TraceCallbacks var5 = var2.tracer();
      if (var5 != null) {
         var5.onCall(var3.depth(), this.function.id(), this.function.entries().size());
      }

      int var6 = var3.depth() + 1;
      Frame.FrameControl var7 = this.returnParentFrame ? var3.frameControl() : var2.frameControlForDepth(var6);
      Frame var8 = new Frame(var6, this.resultCallback, var7);
      ContinuationTask.schedule(var2, var8, var4, (var1x, var2x) -> new CommandQueueEntry<>(var1x, (EntryAction<T>)var2x.bind(var1)));
   }
}
