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

   public GridLayoutTab(final Component title) {
      super();
      this.title = title;
   }

   public Component getTabTitle() {
      return this.title;
   }

   public Component getTabExtraNarration() {
      return Component.empty();
   }

   public void visitChildren(final Consumer<AbstractWidget> childrenConsumer) {
      this.layout.visitWidgets(childrenConsumer);
   }

   public void doLayout(final ScreenRectangle screenRectangle) {
      this.layout.arrangeElements();
      FrameLayout.alignInRectangle(this.layout, screenRectangle, 0.5F, 0.16666667F);
   }
}
