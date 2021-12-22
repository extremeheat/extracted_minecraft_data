package net.minecraft.client;

public class GuiMessage<T> {
   private final int addedTime;
   private final T message;
   // $FF: renamed from: id int
   private final int field_482;

   public GuiMessage(int var1, T var2, int var3) {
      super();
      this.message = var2;
      this.addedTime = var1;
      this.field_482 = var3;
   }

   public T getMessage() {
      return this.message;
   }

   public int getAddedTime() {
      return this.addedTime;
   }

   public int getId() {
      return this.field_482;
   }
}
