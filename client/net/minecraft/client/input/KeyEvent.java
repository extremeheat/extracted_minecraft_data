package net.minecraft.client.input;

public record KeyEvent(int key, int scancode, int modifiers) implements InputWithModifiers {
   public KeyEvent(int var1, int var2, int var3) {
      super();
      this.key = var1;
      this.scancode = var2;
      this.modifiers = var3;
   }

   public int input() {
      return this.key;
   }
}
