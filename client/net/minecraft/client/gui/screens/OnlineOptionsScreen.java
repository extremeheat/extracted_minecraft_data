package net.minecraft.client.gui.screens;

import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.TranslatableComponent;

public class OnlineOptionsScreen extends SimpleOptionsSubScreen {
   private static final Option[] ONLINE_OPTIONS;

   public OnlineOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, new TranslatableComponent("options.online.title"), ONLINE_OPTIONS);
   }

   protected void createFooter() {
      if (this.minecraft.level != null) {
         CycleButton var1 = (CycleButton)this.addRenderableWidget(OptionsScreen.createDifficultyButton(ONLINE_OPTIONS.length, this.width, this.height, "options.difficulty.online", this.minecraft));
         var1.active = false;
      }

      super.createFooter();
   }

   static {
      ONLINE_OPTIONS = new Option[]{Option.REALMS_NOTIFICATIONS, Option.ALLOW_SERVER_LISTING};
   }
}
