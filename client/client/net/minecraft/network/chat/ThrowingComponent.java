package net.minecraft.network.chat;

public class ThrowingComponent extends Exception {
   private final Component component;

   public ThrowingComponent(Component var1) {
      super(var1.getString());
      this.component = var1;
   }

   public ThrowingComponent(Component var1, Throwable var2) {
      super(var1.getString(), var2);
      this.component = var1;
   }

   public Component getComponent() {
      return this.component;
   }
}
