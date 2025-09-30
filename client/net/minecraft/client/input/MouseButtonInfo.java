package net.minecraft.client.input;

public record MouseButtonInfo(int button, int modifiers) implements InputWithModifiers {
   public MouseButtonInfo(int var1, int var2) {
      super();
      this.button = var1;
      this.modifiers = var2;
   }

   public int input() {
      return this.button;
   }
}
