package net.minecraft.commands.execution.tasks;

import java.util.function.Consumer;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.Frame;

public class IsolatedCall<T extends ExecutionCommandSource<T>> implements EntryAction<T> {
   private final Consumer<ExecutionControl<T>> taskProducer;
   private final CommandResultCallback output;

   public IsolatedCall(Consumer<ExecutionControl<T>> var1, CommandResultCallback var2) {
      super();
      this.taskProducer = var1;
      this.output = var2;
   }

   public void execute(ExecutionContext<T> var1, Frame var2) {
      int var3 = var2.depth() + 1;
      Frame var4 = new Frame(var3, this.output, var1.frameControlForDepth(var3));
      this.taskProducer.accept(ExecutionControl.create(var1, var4));
   }
}
