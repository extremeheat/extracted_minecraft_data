package net.minecraft.commands;

@FunctionalInterface
public interface CommandResultCallback {
   CommandResultCallback EMPTY = new CommandResultCallback() {
      public void onResult(boolean var1, int var2) {
      }

      public String toString() {
         return "<empty>";
      }
   };

   void onResult(boolean var1, int var2);

   default void onSuccess(int var1) {
      this.onResult(true, var1);
   }

   default void onFailure() {
      this.onResult(false, 0);
   }

   static CommandResultCallback chain(CommandResultCallback var0, CommandResultCallback var1) {
      if (var0 == EMPTY) {
         return var1;
      } else {
         return var1 == EMPTY ? var0 : (var2, var3) -> {
            var0.onResult(var2, var3);
            var1.onResult(var2, var3);
         };
      }
   }
}
