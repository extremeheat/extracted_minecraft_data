package net.minecraft.client.tutorial;

import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameType;

public class OpenInventoryTutorialStep implements TutorialStepInstance {
   private static final Component TITLE = new TranslatableComponent("tutorial.open_inventory.title");
   private static final Component DESCRIPTION = new TranslatableComponent("tutorial.open_inventory.description", new Object[]{Tutorial.key("inventory")});
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public OpenInventoryTutorialStep(Tutorial var1) {
      super();
      this.tutorial = var1;
   }

   public void tick() {
      ++this.timeWaiting;
      if (this.tutorial.getGameMode() != GameType.SURVIVAL) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting >= 600 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.RECIPE_BOOK, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
         }

      }
   }

   public void clear() {
      if (this.toast != null) {
         this.toast.hide();
         this.toast = null;
      }

   }

   public void onOpenInventory() {
      this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
   }
}
