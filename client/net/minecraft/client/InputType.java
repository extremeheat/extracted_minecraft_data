package net.minecraft.client;

public enum InputType {
   NONE,
   MOUSE,
   KEYBOARD_ARROW,
   KEYBOARD_TAB,
   KEYBOARD_SELECTION;

   private InputType() {
   }

   public boolean isMouse() {
      return this == MOUSE;
   }

   public boolean isKeyboard() {
      return this == KEYBOARD_ARROW || this == KEYBOARD_TAB || this == KEYBOARD_SELECTION;
   }

   // $FF: synthetic method
   private static InputType[] $values() {
      return new InputType[]{NONE, MOUSE, KEYBOARD_ARROW, KEYBOARD_TAB, KEYBOARD_SELECTION};
   }
}
