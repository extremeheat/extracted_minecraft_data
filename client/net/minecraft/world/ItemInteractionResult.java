package net.minecraft.world;

public enum ItemInteractionResult {
   SUCCESS,
   CONSUME,
   CONSUME_PARTIAL,
   PASS_TO_DEFAULT_BLOCK_INTERACTION,
   SKIP_DEFAULT_BLOCK_INTERACTION,
   FAIL;

   private ItemInteractionResult() {
   }

   public boolean consumesAction() {
      return this.result().consumesAction();
   }

   public static ItemInteractionResult sidedSuccess(boolean var0) {
      return var0 ? SUCCESS : CONSUME;
   }

   public InteractionResult result() {
      return switch (this) {
         case SUCCESS -> InteractionResult.SUCCESS;
         case CONSUME -> InteractionResult.CONSUME;
         case CONSUME_PARTIAL -> InteractionResult.CONSUME_PARTIAL;
         case PASS_TO_DEFAULT_BLOCK_INTERACTION, SKIP_DEFAULT_BLOCK_INTERACTION -> InteractionResult.PASS;
         case FAIL -> InteractionResult.FAIL;
      };
   }
}
