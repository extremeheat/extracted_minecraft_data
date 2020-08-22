package net.minecraft.client.gui.components;

public class Button extends AbstractButton {
   protected final Button.OnPress onPress;

   public Button(int var1, int var2, int var3, int var4, String var5, Button.OnPress var6) {
      super(var1, var2, var3, var4, var5);
      this.onPress = var6;
   }

   public void onPress() {
      this.onPress.onPress(this);
   }

   public interface OnPress {
      void onPress(Button var1);
   }
}
