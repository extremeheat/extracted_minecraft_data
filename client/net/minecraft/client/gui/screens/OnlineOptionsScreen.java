package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public class OnlineOptionsScreen extends SimpleOptionsSubScreen {
   public OnlineOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("options.online.title"), new OptionInstance[]{var2.realmsNotifications(), var2.allowServerListing()});
   }

   @Override
   protected void createFooter() {
      if (this.minecraft.level != null) {
         CycleButton var1 = this.addRenderableWidget(
            OptionsScreen.createDifficultyButton(this.smallOptions.length, this.width, this.height, "options.difficulty.online", this.minecraft)
         );
         var1.active = false;
      }

      super.createFooter();
   }
}
