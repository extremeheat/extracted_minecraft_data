package net.minecraft.client.gui.screens.multiplayer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class Realms32bitWarningScreen extends WarningScreen {
   private static final Component TITLE = Component.translatable("title.32bit.deprecation.realms.header").withStyle(ChatFormatting.BOLD);
   private static final Component CONTENT = Component.translatable("title.32bit.deprecation.realms");
   private static final Component CHECK = Component.translatable("title.32bit.deprecation.realms.check");
   private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);
   private final Screen previous;

   public Realms32bitWarningScreen(Screen var1) {
      super(TITLE, CONTENT, CHECK, NARRATION);
      this.previous = var1;
   }

   @Override
   protected void initButtons(int var1) {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, var1x -> {
         if (this.stopShowing.selected()) {
            this.minecraft.options.skipRealms32bitWarning = true;
            this.minecraft.options.save();
         }

         this.minecraft.setScreen(this.previous);
      }).bounds(this.width / 2 - 75, 100 + var1, 150, 20).build());
   }
}
