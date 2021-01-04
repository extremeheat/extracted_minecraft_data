package net.minecraft.client.gui.screens;

import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

public class ChatOptionsScreen extends Screen {
   private static final Option[] CHAT_OPTIONS;
   private final Screen lastScreen;
   private final Options options;
   private AbstractWidget narratorButton;

   public ChatOptionsScreen(Screen var1, Options var2) {
      super(new TranslatableComponent("options.chat.title", new Object[0]));
      this.lastScreen = var1;
      this.options = var2;
   }

   protected void init() {
      int var1 = 0;
      Option[] var2 = CHAT_OPTIONS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Option var5 = var2[var4];
         int var6 = this.width / 2 - 155 + var1 % 2 * 160;
         int var7 = this.height / 6 + 24 * (var1 >> 1);
         AbstractWidget var8 = this.addButton(var5.createButton(this.minecraft.options, var6, var7, 150));
         if (var5 == Option.NARRATOR) {
            this.narratorButton = var8;
            var8.active = NarratorChatListener.INSTANCE.isActive();
         }

         ++var1;
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (var1 + 1) / 2, 200, 20, I18n.get("gui.done"), (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
      super.render(var1, var2, var3);
   }

   public void updateNarratorButton() {
      this.narratorButton.setMessage(Option.NARRATOR.getMessage(this.options));
   }

   static {
      CHAT_OPTIONS = new Option[]{Option.CHAT_VISIBILITY, Option.CHAT_COLOR, Option.CHAT_LINKS, Option.CHAT_LINKS_PROMPT, Option.CHAT_OPACITY, Option.TEXT_BACKGROUND_OPACITY, Option.CHAT_SCALE, Option.CHAT_WIDTH, Option.CHAT_HEIGHT_FOCUSED, Option.CHAT_HEIGHT_UNFOCUSED, Option.REDUCED_DEBUG_INFO, Option.AUTO_SUGGESTIONS, Option.NARRATOR};
   }
}
