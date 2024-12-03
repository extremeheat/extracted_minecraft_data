package net.minecraft.commands.execution.tasks;

import java.util.List;
import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

public class ContinuationTask<T, P> implements EntryAction<T> {
   private final TaskProvider<T, P> taskFactory;
   private final List<P> arguments;
   private final CommandQueueEntry<T> selfEntry;
   private int index;

   private ContinuationTask(TaskProvider<T, P> var1, List<P> var2, Frame var3) {
      super();
      this.taskFactory = var1;
      this.arguments = var2;
      this.selfEntry = new CommandQueueEntry<T>(var3, this);
   }

   public void execute(ExecutionContext<T> var1, Frame var2) {
      Object var3 = this.arguments.get(this.index);
      var1.queueNext(this.taskFactory.create(var2, var3));
      if (++this.index < this.arguments.size()) {
         var1.queueNext(this.selfEntry);
      }

   }

   public static <T, P> void schedule(ExecutionContext<T> var0, Frame var1, List<P> var2, TaskProvider<T, P> var3) {
      int var4 = var2.size();
      switch (var4) {
         case 0:
            break;
         case 1:
            var0.queueNext(var3.create(var1, var2.get(0)));
            break;
         case 2:
            var0.queueNext(var3.create(var1, var2.get(0)));
            var0.queueNext(var3.create(var1, var2.get(1)));
            break;
         default:
            var0.queueNext((new ContinuationTask(var3, var2, var1)).selfEntry);
      }

   }

   @FunctionalInterface
   public interface TaskProvider<T, P> {
      CommandQueueEntry<T> create(Frame var1, P var2);
   }
}
