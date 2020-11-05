package net.minecraft.client;

public class GuiMessage<T> {
   private final int addedTime;
   private final T message;
   private final int id;

   public GuiMessage(int var1, T var2, int var3) {
      super();
      this.message = var2;
      this.addedTime = var1;
      this.id = var3;
   }

   public T getMessage() {
      return this.message;
   }

   public int getAddedTime() {
      return this.addedTime;
   }

   public int getId() {
      return this.id;
   }
}
