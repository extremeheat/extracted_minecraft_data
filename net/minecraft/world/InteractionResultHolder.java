package net.minecraft.world;

public class InteractionResultHolder {
   private final InteractionResult result;
   private final Object object;

   public InteractionResultHolder(InteractionResult var1, Object var2) {
      this.result = var1;
      this.object = var2;
   }

   public InteractionResult getResult() {
      return this.result;
   }

   public Object getObject() {
      return this.object;
   }

   public static InteractionResultHolder success(Object var0) {
      return new InteractionResultHolder(InteractionResult.SUCCESS, var0);
   }

   public static InteractionResultHolder consume(Object var0) {
      return new InteractionResultHolder(InteractionResult.CONSUME, var0);
   }

   public static InteractionResultHolder pass(Object var0) {
      return new InteractionResultHolder(InteractionResult.PASS, var0);
   }

   public static InteractionResultHolder fail(Object var0) {
      return new InteractionResultHolder(InteractionResult.FAIL, var0);
   }
}
