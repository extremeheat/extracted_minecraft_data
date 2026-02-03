package net.minecraft.client.gui.screens.options;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;

public class UnsupportedGraphicsWarningScreen extends Screen {
   private static final int BUTTON_PADDING = 20;
   private static final int BUTTON_MARGIN = 5;
   private static final int BUTTON_HEIGHT = 20;
   private final Component narrationMessage;
   private final List<Component> message;
   private final ImmutableList<ButtonOption> buttonOptions;
   private MultiLineLabel messageLines;
   private int contentTop;
   private int buttonWidth;

   protected UnsupportedGraphicsWarningScreen(final Component title, final List<Component> message, final ImmutableList<ButtonOption> buttonOptions) {
      super(title);
      this.messageLines = MultiLineLabel.EMPTY;
      this.message = message;
      this.narrationMessage = CommonComponents.joinForNarration(title, ComponentUtils.formatList(message, CommonComponents.EMPTY));
      this.buttonOptions = buttonOptions;
   }

   public Component getNarrationMessage() {
      return this.narrationMessage;
   }

   public void init() {
      ButtonOption buttonOption;
      for(UnmodifiableIterator var1 = this.buttonOptions.iterator(); var1.hasNext(); this.buttonWidth = Math.max(this.buttonWidth, 20 + this.font.width((FormattedText)buttonOption.message) + 20)) {
         buttonOption = (ButtonOption)var1.next();
      }

      int buttonAdvance = 5 + this.buttonWidth + 5;
      int contentWidth = buttonAdvance * this.buttonOptions.size();
      this.messageLines = MultiLineLabel.create(this.font, contentWidth, (Component[])this.message.toArray(new Component[0]));
      int var10000 = this.messageLines.getLineCount();
      Objects.requireNonNull(this.font);
      int messageHeight = var10000 * 9;
      this.contentTop = (int)((double)this.height / 2.0 - (double)messageHeight / 2.0);
      var10000 = this.contentTop + messageHeight;
      Objects.requireNonNull(this.font);
      int buttonTop = var10000 + 9 * 2;
      int x = (int)((double)this.width / 2.0 - (double)contentWidth / 2.0);

      for(UnmodifiableIterator var6 = this.buttonOptions.iterator(); var6.hasNext(); x += buttonAdvance) {
         ButtonOption buttonOption = (ButtonOption)var6.next();
         this.addRenderableWidget(Button.builder(buttonOption.message, buttonOption.onPress).bounds(x, buttonTop, this.buttonWidth, 20).build());
      }

   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      super.render(graphics, mouseX, mouseY, a);
      ActiveTextCollector textRenderer = graphics.textRenderer();
      Font var10001 = this.font;
      Component var10002 = this.title;
      int var10003 = this.width / 2;
      int var10004 = this.contentTop;
      Objects.requireNonNull(this.font);
      graphics.drawCenteredString(var10001, (Component)var10002, var10003, var10004 - 9 * 2, -1);
      MultiLineLabel var10000 = this.messageLines;
      TextAlignment var6 = TextAlignment.CENTER;
      int var7 = this.width / 2;
      var10003 = this.contentTop;
      Objects.requireNonNull(this.font);
      var10000.visitLines(var6, var7, var10003, 9, textRenderer);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public static final class ButtonOption {
      private final Component message;
      private final Button.OnPress onPress;

      public ButtonOption(final Component message, final Button.OnPress onPress) {
         super();
         this.message = message;
         this.onPress = onPress;
      }
   }
}
