package net.minecraft.client.gui.screens.multiplayer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SafetyScreen extends WarningScreen {
   private static final Component TITLE;
   private static final Component CONTENT;
   private static final Component CHECK;
   private static final Component NARRATION;
   private final Screen previous;

   public SafetyScreen(final Screen previous) {
      super(TITLE, CONTENT, CHECK, NARRATION);
      this.previous = previous;
   }

   protected Layout addFooterButtons() {
      LinearLayout footer = LinearLayout.horizontal().spacing(8);
      footer.addChild(Button.builder(CommonComponents.GUI_PROCEED, (button) -> {
         if (this.stopShowing.selected()) {
            this.minecraft.options.skipMultiplayerWarning = true;
            this.minecraft.options.save();
         }

         this.minecraft.setScreen(new JoinMultiplayerScreen(this.previous));
      }).build());
      footer.addChild(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose()).build());
      return footer;
   }

   public void onClose() {
      this.minecraft.setScreen(this.previous);
   }

   static {
      TITLE = Component.translatable("multiplayerWarning.header").withStyle(ChatFormatting.BOLD);
      CONTENT = Component.translatable("multiplayerWarning.message");
      CHECK = Component.translatable("multiplayerWarning.check").withColor(-2039584);
      NARRATION = TITLE.copy().append("\n").append(CONTENT);
   }
}
