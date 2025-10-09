package net.minecraft.client.input;

public record MouseButtonEvent(double x, double y, MouseButtonInfo buttonInfo) implements InputWithModifiers {
   public MouseButtonEvent(double var1, double var3, MouseButtonInfo var5) {
      super();
      this.x = var1;
      this.y = var3;
      this.buttonInfo = var5;
   }

   public int input() {
      return this.button();
   }

   public int button() {
      return this.buttonInfo().button();
   }

   public @InputWithModifiers.Modifiers int modifiers() {
      return this.buttonInfo().modifiers();
   }
}
