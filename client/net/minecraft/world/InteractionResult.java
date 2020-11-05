package net.minecraft.world;

public enum InteractionResult {
   SUCCESS,
   CONSUME,
   PASS,
   FAIL;

   private InteractionResult() {
   }

   public boolean consumesAction() {
      return this == SUCCESS || this == CONSUME;
   }

   public boolean shouldSwing() {
      return this == SUCCESS;
   }

   public static InteractionResult sidedSuccess(boolean var0) {
      return var0 ? SUCCESS : CONSUME;
   }
}
