package net.minecraft.client.gui.screens.multiplayer;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public abstract class WarningScreen extends Screen {
   private static final int MESSAGE_PADDING = 100;
   private final Component message;
   private final @Nullable Component check;
   private final Component narration;
   protected @Nullable Checkbox stopShowing;
   private @Nullable FittingMultiLineTextWidget messageWidget;
   private final FrameLayout layout;

   protected WarningScreen(Component var1, Component var2, Component var3) {
      this(var1, var2, (Component)null, var3);
   }

   protected WarningScreen(Component var1, Component var2, @Nullable Component var3, Component var4) {
      super(var1);
      this.message = var2;
      this.check = var3;
      this.narration = var4;
      this.layout = new FrameLayout(0, 0, this.width, this.height);
   }

   protected abstract Layout addFooterButtons();

   protected void init() {
      LinearLayout var1 = (LinearLayout)this.layout.addChild(LinearLayout.vertical().spacing(8));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.getTitle(), this.font));
      this.messageWidget = (FittingMultiLineTextWidget)var1.addChild(new FittingMultiLineTextWidget(0, 0, this.width - 100, this.height - 100, this.message, this.font), (Consumer)((var0) -> var0.padding(12)));
      LinearLayout var2 = (LinearLayout)var1.addChild(LinearLayout.vertical().spacing(8));
      var2.defaultCellSetting().alignHorizontallyCenter();
      if (this.check != null) {
         this.stopShowing = (Checkbox)var2.addChild(Checkbox.builder(this.check, this.font).build());
      }

      var2.addChild(this.addFooterButtons());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      if (this.messageWidget != null) {
         this.messageWidget.setWidth(this.width - 100);
         this.messageWidget.setHeight(this.height - 100);
         this.messageWidget.minimizeHeight();
      }

      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public Component getNarrationMessage() {
      return this.narration;
   }
}
