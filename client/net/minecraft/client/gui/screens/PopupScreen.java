package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;

public class PopupScreen extends Screen {
   private static final int BUTTON_PADDING = 20;
   private static final int BUTTON_MARGIN = 5;
   private static final int BUTTON_HEIGHT = 20;
   private final Component narrationMessage;
   private final FormattedText message;
   private final ImmutableList<PopupScreen.ButtonOption> buttonOptions;
   private MultiLineLabel messageLines = MultiLineLabel.EMPTY;
   private int contentTop;
   private int buttonWidth;

   protected PopupScreen(Component var1, List<Component> var2, ImmutableList<PopupScreen.ButtonOption> var3) {
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
      PopupScreen.ButtonOption var2;
      for(UnmodifiableIterator var1 = this.buttonOptions.iterator();
         var1.hasNext();
         this.buttonWidth = Math.max(this.buttonWidth, 20 + this.font.width(var2.message) + 20)
      ) {
         var2 = (PopupScreen.ButtonOption)var1.next();
      }

      int var8 = 5 + this.buttonWidth + 5;
      int var9 = var8 * this.buttonOptions.size();
      this.messageLines = MultiLineLabel.create(this.font, this.message, var9);
      int var3 = this.messageLines.getLineCount() * 9;
      this.contentTop = (int)((double)this.height / 2.0 - (double)var3 / 2.0);
      int var4 = this.contentTop + var3 + 9 * 2;
      int var5 = (int)((double)this.width / 2.0 - (double)var9 / 2.0);

      for(UnmodifiableIterator var6 = this.buttonOptions.iterator(); var6.hasNext(); var5 += var8) {
         PopupScreen.ButtonOption var7 = (PopupScreen.ButtonOption)var6.next();
         this.addRenderableWidget(new Button(var5, var4, this.buttonWidth, 20, var7.message, var7.onPress));
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderDirtBackground(0);
      drawCenteredString(var1, this.font, this.title, this.width / 2, this.contentTop - 9 * 2, -1);
      this.messageLines.renderCentered(var1, this.width / 2, this.contentTop);
      super.render(var1, var2, var3, var4);
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
