package net.minecraft.commands.execution;

public record CommandQueueEntry<T>(Frame a, EntryAction<T> b) {
   private final Frame frame;
   private final EntryAction<T> action;

   public CommandQueueEntry(Frame var1, EntryAction<T> var2) {
      super();
      this.frame = var1;
      this.action = var2;
   }

   public void execute(ExecutionContext<T> var1) {
      this.action.execute(var1, this.frame);
   }
}
