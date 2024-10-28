package net.minecraft.commands.execution;

@FunctionalInterface
public interface UnboundEntryAction<T> {
   void execute(T var1, ExecutionContext<T> var2, Frame var3);

   default EntryAction<T> bind(T var1) {
      return (var2, var3) -> {
         this.execute(var1, var2, var3);
      };
   }
}
