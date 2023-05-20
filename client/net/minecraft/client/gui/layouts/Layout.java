package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;

public interface Layout extends LayoutElement {
   void visitChildren(Consumer<LayoutElement> var1);

   @Override
   default void visitWidgets(Consumer<AbstractWidget> var1) {
      this.visitChildren(var1x -> var1x.visitWidgets(var1));
   }

   default void arrangeElements() {
      this.visitChildren(var0 -> {
         if (var0 instanceof Layout var1) {
            var1.arrangeElements();
         }
      });
   }
}
