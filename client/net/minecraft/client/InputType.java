package net.minecraft.client;

public enum InputType {
   NONE,
   MOUSE,
   KEYBOARD_ARROW,
   KEYBOARD_TAB;

   private InputType() {
   }

   public boolean isMouse() {
      return this == MOUSE;
   }

   public boolean isKeyboard() {
      return this == KEYBOARD_ARROW || this == KEYBOARD_TAB;
   }

   // $FF: synthetic method
   private static InputType[] $values() {
      return new InputType[]{NONE, MOUSE, KEYBOARD_ARROW, KEYBOARD_TAB};
   }
}
