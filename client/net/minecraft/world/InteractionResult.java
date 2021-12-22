package net.minecraft.world;

public enum InteractionResult {
   SUCCESS,
   CONSUME,
   CONSUME_PARTIAL,
   PASS,
   FAIL;

   private InteractionResult() {
   }

   public boolean consumesAction() {
      return this == SUCCESS || this == CONSUME || this == CONSUME_PARTIAL;
   }

   public boolean shouldSwing() {
      return this == SUCCESS;
   }

   public boolean shouldAwardStats() {
      return this == SUCCESS || this == CONSUME;
   }

   public static InteractionResult sidedSuccess(boolean var0) {
      return var0 ? SUCCESS : CONSUME;
   }

   // $FF: synthetic method
   private static InteractionResult[] $values() {
      return new InteractionResult[]{SUCCESS, CONSUME, CONSUME_PARTIAL, PASS, FAIL};
   }
}
