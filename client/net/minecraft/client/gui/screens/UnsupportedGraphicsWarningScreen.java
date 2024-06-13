package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
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
   private final ImmutableList<UnsupportedGraphicsWarningScreen.ButtonOption> buttonOptions;
   private MultiLineLabel messageLines = MultiLineLabel.EMPTY;
   private int contentTop;
   private int buttonWidth;

   protected UnsupportedGraphicsWarningScreen(Component var1, List<Component> var2, ImmutableList<UnsupportedGraphicsWarningScreen.ButtonOption> var3) {
      super(var1);
      this.message = FormattedText.composite(var2);
      this.narrationMessage = CommonComponents.joinForNarration(var1, ComponentUtils.formatList(var2, CommonComponents.EMPTY));
      this.buttonOptions = var3;
   }

   @Override
   public Component getNarrationMessage() {
      return this.narrationMessage;
   }

   @Override
   public void init() {
      UnmodifiableIterator var1 = this.buttonOptions.iterator();

      while (var1.hasNext()) {
         UnsupportedGraphicsWarningScreen.ButtonOption var2 = (UnsupportedGraphicsWarningScreen.ButtonOption)var1.next();
         this.buttonWidth = Math.max(this.buttonWidth, 20 + this.font.width(var2.message) + 20);
      }

      int var8 = 5 + this.buttonWidth + 5;
      int var9 = var8 * this.buttonOptions.size();
      this.messageLines = MultiLineLabel.create(this.font, this.message, var9);
      int var3 = this.messageLines.getLineCount() * 9;
      this.contentTop = (int)((double)this.height / 2.0 - (double)var3 / 2.0);
      int var4 = this.contentTop + var3 + 9 * 2;
      int var5 = (int)((double)this.width / 2.0 - (double)var9 / 2.0);

      for (UnmodifiableIterator var6 = this.buttonOptions.iterator(); var6.hasNext(); var5 += var8) {
         UnsupportedGraphicsWarningScreen.ButtonOption var7 = (UnsupportedGraphicsWarningScreen.ButtonOption)var6.next();
         this.addRenderableWidget(Button.builder(var7.message, var7.onPress).bounds(var5, var4, this.buttonWidth, 20).build());
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, this.contentTop - 9 * 2, -1);
      this.messageLines.renderCentered(var1, this.width / 2, this.contentTop);
   }

   @Override
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
