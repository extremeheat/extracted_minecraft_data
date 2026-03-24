package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ConfirmScreen extends Screen {
   private final Component message;
   protected final LinearLayout layout;
   protected Component yesButtonComponent;
   protected Component noButtonComponent;
   protected @Nullable Button yesButton;
   protected @Nullable Button noButton;
   private int delayTicker;
   protected final BooleanConsumer callback;

   public ConfirmScreen(final BooleanConsumer callback, final Component title, final Component message) {
      this(callback, title, message, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
   }

   public ConfirmScreen(final BooleanConsumer callback, final Component title, final Component message, final Component yesButtonComponent, final Component noButtonComponent) {
      super(title);
      this.layout = LinearLayout.vertical().spacing(8);
      this.callback = callback;
      this.message = message;
      this.yesButtonComponent = yesButtonComponent;
      this.noButtonComponent = noButtonComponent;
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
      LinearLayout buttonLayout = (LinearLayout)this.layout.addChild(LinearLayout.horizontal().spacing(4));
      buttonLayout.defaultCellSetting().paddingTop(16);
      this.addButtons(buttonLayout);
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   protected void addAdditionalText() {
   }

   protected void addButtons(final LinearLayout buttonLayout) {
      this.yesButton = (Button)buttonLayout.addChild(Button.builder(this.yesButtonComponent, (button) -> this.callback.accept(true)).build());
      this.noButton = (Button)buttonLayout.addChild(Button.builder(this.noButtonComponent, (button) -> this.callback.accept(false)).build());
   }

   public void setDelay(final int delay) {
      this.delayTicker = delay;
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

   public boolean keyPressed(final KeyEvent event) {
      if (this.delayTicker <= 0 && event.isEscape()) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(event);
      }
   }
}
