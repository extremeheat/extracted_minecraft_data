package net.minecraft.client.gui.screens.options;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
   private final FormattedText message;
   private final ImmutableList<ButtonOption> buttonOptions;
   private MultiLineLabel messageLines;
   private int contentTop;
   private int buttonWidth;

   protected UnsupportedGraphicsWarningScreen(Component var1, List<Component> var2, ImmutableList<ButtonOption> var3) {
      super(var1);
      this.messageLines = MultiLineLabel.EMPTY;
      this.message = FormattedText.composite(var2);
      this.narrationMessage = CommonComponents.joinForNarration(var1, ComponentUtils.formatList(var2, (Component)CommonComponents.EMPTY));
      this.buttonOptions = var3;
   }

   public Component getNarrationMessage() {
      return this.narrationMessage;
   }

   public void init() {
      ButtonOption var2;
      for(UnmodifiableIterator var1 = this.buttonOptions.iterator(); var1.hasNext(); this.buttonWidth = Math.max(this.buttonWidth, 20 + this.font.width((FormattedText)var2.message) + 20)) {
         var2 = (ButtonOption)var1.next();
      }

      int var8 = 5 + this.buttonWidth + 5;
      int var9 = var8 * this.buttonOptions.size();
      this.messageLines = MultiLineLabel.create(this.font, this.message, var9);
      int var10000 = this.messageLines.getLineCount();
      Objects.requireNonNull(this.font);
      int var3 = var10000 * 9;
      this.contentTop = (int)((double)this.height / 2.0 - (double)var3 / 2.0);
      var10000 = this.contentTop + var3;
      Objects.requireNonNull(this.font);
      int var4 = var10000 + 9 * 2;
      int var5 = (int)((double)this.width / 2.0 - (double)var9 / 2.0);

      for(UnmodifiableIterator var6 = this.buttonOptions.iterator(); var6.hasNext(); var5 += var8) {
         ButtonOption var7 = (ButtonOption)var6.next();
         this.addRenderableWidget(Button.builder(var7.message, var7.onPress).bounds(var5, var4, this.buttonWidth, 20).build());
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      Font var10001 = this.font;
      Component var10002 = this.title;
      int var10003 = this.width / 2;
      int var10004 = this.contentTop;
      Objects.requireNonNull(this.font);
      var1.drawCenteredString(var10001, (Component)var10002, var10003, var10004 - 9 * 2, -1);
      this.messageLines.renderCentered(var1, this.width / 2, this.contentTop);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public static final class ButtonOption {
      final Component message;
      final Button.OnPress onPress;

      public ButtonOption(Component var1, Button.OnPress var2) {
         super();
         this.message = var1;
         this.onPress = var2;
      }
   }
}
