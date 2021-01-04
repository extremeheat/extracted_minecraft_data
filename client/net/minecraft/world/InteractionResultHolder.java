package net.minecraft.world;

public class InteractionResultHolder<T> {
   private final InteractionResult result;
   private final T object;

   public InteractionResultHolder(InteractionResult var1, T var2) {
      super();
      this.result = var1;
      this.object = var2;
   }

   public InteractionResult getResult() {
      return this.result;
   }

   public T getObject() {
      return this.object;
   }
}
