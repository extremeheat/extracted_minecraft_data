package net.minecraft.world;

public class InteractionResultHolder<T> {
   private final InteractionResult result;
   private final T object;

   public InteractionResultHolder(InteractionResult var1, T var2) {
      super();
      this.result = var1;
      this.object = (T)var2;
   }

   public InteractionResult getResult() {
      return this.result;
   }

   public T getObject() {
      return this.object;
   }

   public static <T> InteractionResultHolder<T> success(T var0) {
      return new InteractionResultHolder<>(InteractionResult.SUCCESS, (T)var0);
   }

   public static <T> InteractionResultHolder<T> consume(T var0) {
      return new InteractionResultHolder<>(InteractionResult.CONSUME, (T)var0);
   }

   public static <T> InteractionResultHolder<T> pass(T var0) {
      return new InteractionResultHolder<>(InteractionResult.PASS, (T)var0);
   }

   public static <T> InteractionResultHolder<T> fail(T var0) {
      return new InteractionResultHolder<>(InteractionResult.FAIL, (T)var0);
   }

   public static <T> InteractionResultHolder<T> sidedSuccess(T var0, boolean var1) {
      return var1 ? success((T)var0) : consume((T)var0);
   }
}
