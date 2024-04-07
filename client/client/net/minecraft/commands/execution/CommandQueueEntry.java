package net.minecraft.commands.execution;

public record CommandQueueEntry<T>(Frame frame, EntryAction<T> action) {
   public CommandQueueEntry(Frame frame, EntryAction<T> action) {
      super();
      this.frame = frame;
      this.action = action;
   }

   public void execute(ExecutionContext<T> var1) {
      this.action.execute(var1, this.frame);
   }
}
