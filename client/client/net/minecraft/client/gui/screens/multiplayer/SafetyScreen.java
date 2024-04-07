package net.minecraft.client.gui.screens.multiplayer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SafetyScreen extends WarningScreen {
   private static final Component TITLE = Component.translatable("multiplayerWarning.header").withStyle(ChatFormatting.BOLD);
   private static final Component CONTENT = Component.translatable("multiplayerWarning.message");
   private static final Component CHECK = Component.translatable("multiplayerWarning.check");
   private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);
   private final Screen previous;

   public SafetyScreen(Screen var1) {
      super(TITLE, CONTENT, CHECK, NARRATION);
      this.previous = var1;
   }

   @Override
   protected Layout addFooterButtons() {
      LinearLayout var1 = LinearLayout.horizontal().spacing(8);
      var1.addChild(Button.builder(CommonComponents.GUI_PROCEED, var1x -> {
         if (this.stopShowing.selected()) {
            this.minecraft.options.skipMultiplayerWarning = true;
            this.minecraft.options.save();
         }

         this.minecraft.setScreen(new JoinMultiplayerScreen(this.previous));
      }).build());
      var1.addChild(Button.builder(CommonComponents.GUI_BACK, var1x -> this.onClose()).build());
      return var1;
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.previous);
   }
}
