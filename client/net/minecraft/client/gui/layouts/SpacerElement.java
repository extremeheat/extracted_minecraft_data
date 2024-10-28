package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;

public class SpacerElement implements LayoutElement {
   private int x;
   private int y;
   private final int width;
   private final int height;

   public SpacerElement(int var1, int var2) {
      this(0, 0, var1, var2);
   }

   public SpacerElement(int var1, int var2, int var3, int var4) {
      super();
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public static SpacerElement width(int var0) {
      return new SpacerElement(var0, 0);
   }

   public static SpacerElement height(int var0) {
      return new SpacerElement(0, var0);
   }

   public void setX(int var1) {
      this.x = var1;
   }

   public void setY(int var1) {
      this.y = var1;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void visitWidgets(Consumer<AbstractWidget> var1) {
   }
}
