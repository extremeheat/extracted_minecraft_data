package net.minecraft.util;

public enum Continuation {
   CONTINUE,
   ABORT;

   private Continuation() {
   }

   public boolean shouldAbort() {
      return this == ABORT;
   }

   public static Continuation abortIf(final boolean abortIf) {
      return abortIf ? ABORT : CONTINUE;
   }

   public static Continuation continueIf(final boolean continueIf) {
      return abortIf(!continueIf);
   }

   // $FF: synthetic method
   private static Continuation[] $values() {
      return new Continuation[]{CONTINUE, ABORT};
   }
}
