package net.minecraft.client.gui;

public interface IGuiEventListener {
   default boolean mouseClicked(double var1, double var3, int var5) {
      return false;
   }

   default boolean mouseReleased(double var1, double var3, int var5) {
      return false;
   }

   default boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return false;
   }

   default boolean mouseScrolled(double var1) {
      return false;
   }

   default boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   default boolean keyReleased(int var1, int var2, int var3) {
      return false;
   }

   default boolean charTyped(char var1, int var2) {
      return false;
   }

   default void func_205700_b(boolean var1) {
   }

   default boolean func_207704_ae_() {
      return false;
   }
}
