package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfirmScreen extends Screen {
   private final Component message;
   protected LinearLayout layout;
   protected Component yesButtonComponent;
   protected Component noButtonComponent;
   @Nullable
   protected Button yesButton;
   @Nullable
   protected Button noButton;
   private int delayTicker;
   protected final BooleanConsumer callback;

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3) {
      this(var1, var2, var3, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
   }

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3, Component var4, Component var5) {
      super(var2);
      this.layout = LinearLayout.vertical().spacing(8);
      this.callback = var1;
      this.message = var3;
      this.yesButtonComponent = var4;
      this.noButtonComponent = var5;
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.message);
   }

   protected void init() {
      super.init();
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.layout.addChild(new StringWidget(this.title, this.font));
      this.layout.addChild((new MultiLineTextWidget(this.message, this.font)).setMaxWidth(this.width - 50).setMaxRows(15).setCentered(true));
      this.addAdditionalText();
      LinearLayout var1 = (LinearLayout)this.layout.addChild(LinearLayout.horizontal().spacing(4));
      var1.defaultCellSetting().paddingTop(16);
      this.addButtons(var1);
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   protected void addAdditionalText() {
   }

   protected void addButtons(LinearLayout var1) {
      this.yesButton = (Button)var1.addChild(Button.builder(this.yesButtonComponent, (var1x) -> this.callback.accept(true)).build());
      this.noButton = (Button)var1.addChild(Button.builder(this.noButtonComponent, (var1x) -> this.callback.accept(false)).build());
   }

   public void setDelay(int var1) {
      this.delayTicker = var1;
      this.yesButton.active = false;
      this.noButton.active = false;
   }

   public void tick() {
      super.tick();
      if (--this.delayTicker == 0) {
         this.yesButton.active = true;
         this.noButton.active = true;
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(KeyEvent var1) {
      if (this.delayTicker <= 0 && var1.key() == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(var1);
      }
   }
}
