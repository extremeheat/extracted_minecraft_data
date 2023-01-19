package net.minecraft.client;

public enum InputType {
   NONE,
   MOUSE,
   KEYBOARD_OTHER,
   KEYBOARD_TAB;

   private InputType() {
   }

   public boolean isMouse() {
      return this == MOUSE;
   }

   public boolean isKeyboard() {
      return this == KEYBOARD_OTHER || this == KEYBOARD_TAB;
   }
}
