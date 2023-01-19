package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;

public interface LayoutElement {
   void setX(int var1);

   void setY(int var1);

   int getX();

   int getY();

   int getWidth();

   int getHeight();

   default void setPosition(int var1, int var2) {
      this.setX(var1);
      this.setY(var2);
   }

   void visitWidgets(Consumer<AbstractWidget> var1);
}
