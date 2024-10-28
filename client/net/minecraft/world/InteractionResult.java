package net.minecraft.world;

public enum InteractionResult {
   SUCCESS,
   SUCCESS_NO_ITEM_USED,
   CONSUME,
   CONSUME_PARTIAL,
   PASS,
   FAIL;

   private InteractionResult() {
   }

   public boolean consumesAction() {
      return this == SUCCESS || this == CONSUME || this == CONSUME_PARTIAL || this == SUCCESS_NO_ITEM_USED;
   }

   public boolean shouldSwing() {
      return this == SUCCESS || this == SUCCESS_NO_ITEM_USED;
   }

   public boolean indicateItemUse() {
      return this == SUCCESS || this == CONSUME;
   }

   public static InteractionResult sidedSuccess(boolean var0) {
      return var0 ? SUCCESS : CONSUME;
   }

   // $FF: synthetic method
   private static InteractionResult[] $values() {
      return new InteractionResult[]{SUCCESS, SUCCESS_NO_ITEM_USED, CONSUME, CONSUME_PARTIAL, PASS, FAIL};
   }
}
