package net.minecraft.client.input;

public record MouseButtonEvent(double x, double y, MouseButtonInfo buttonInfo) implements InputWithModifiers {
   public MouseButtonEvent {
      super();
   }

   public int input() {
      return this.button();
   }

   public @MouseButtonInfo.MouseButton int button() {
      return this.buttonInfo().button();
   }

   public @InputWithModifiers.Modifiers int modifiers() {
      return this.buttonInfo().modifiers();
   }
}
