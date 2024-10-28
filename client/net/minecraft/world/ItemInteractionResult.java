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
      InteractionResult var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = InteractionResult.SUCCESS;
            break;
         case 1:
            var10000 = InteractionResult.CONSUME;
            break;
         case 2:
            var10000 = InteractionResult.CONSUME_PARTIAL;
            break;
         case 3:
         case 4:
            var10000 = InteractionResult.PASS;
            break;
         case 5:
            var10000 = InteractionResult.FAIL;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static ItemInteractionResult[] $values() {
      return new ItemInteractionResult[]{SUCCESS, CONSUME, CONSUME_PARTIAL, PASS_TO_DEFAULT_BLOCK_INTERACTION, SKIP_DEFAULT_BLOCK_INTERACTION, FAIL};
   }
}
