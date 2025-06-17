package net.minecraft.client.gui.components.tabs;

import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public class LoadingTab implements Tab {
   private final Component title;
   private final Component loadingTitle;
   protected final LinearLayout layout = LinearLayout.vertical();

   public LoadingTab(Font var1, Component var2, Component var3) {
      super();
      this.title = var2;
      this.loadingTitle = var3;
      LoadingDotsWidget var4 = new LoadingDotsWidget(var1, var3);
      this.layout.defaultCellSetting().alignVerticallyMiddle().alignHorizontallyCenter();
      this.layout.addChild(var4, (Consumer)((var0) -> var0.paddingBottom(30)));
   }

   public Component getTabTitle() {
      return this.title;
   }

   public Component getTabExtraNarration() {
      return this.loadingTitle;
   }

   public void visitChildren(Consumer<AbstractWidget> var1) {
      this.layout.visitWidgets(var1);
   }

   public void doLayout(ScreenRectangle var1) {
      this.layout.arrangeElements();
      FrameLayout.alignInRectangle(this.layout, var1, 0.5F, 0.5F);
   }
}
