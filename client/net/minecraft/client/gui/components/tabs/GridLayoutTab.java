package net.minecraft.client.gui.components.tabs;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public class GridLayoutTab implements Tab {
   private final Component title;
   protected final GridLayout layout = new GridLayout();

   public GridLayoutTab(Component var1) {
      super();
      this.title = var1;
   }

   public Component getTabTitle() {
      return this.title;
   }

   public void visitChildren(Consumer<AbstractWidget> var1) {
      this.layout.visitWidgets(var1);
   }

   public void doLayout(ScreenRectangle var1) {
      this.layout.arrangeElements();
      FrameLayout.alignInRectangle(this.layout, var1, 0.5F, 0.16666667F);
   }
}
