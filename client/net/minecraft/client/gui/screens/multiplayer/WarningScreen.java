package net.minecraft.client.gui.screens.multiplayer;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class WarningScreen extends Screen {
   private static final int MESSAGE_PADDING = 100;
   private final Component message;
   @Nullable
   private final Component check;
   private final Component narration;
   @Nullable
   protected Checkbox stopShowing;
   @Nullable
   private FocusableTextWidget messageWidget;
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
      this.messageWidget = (FocusableTextWidget)var1.addChild(new FocusableTextWidget(this.width - 100, this.message, this.font, 12), (Consumer)((var0) -> {
         var0.padding(12);
      }));
      this.messageWidget.setCentered(false);
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
         this.messageWidget.setMaxWidth(this.width - 100);
      }

      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public Component getNarrationMessage() {
      return this.narration;
   }
}
