package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;

public class OpenInventoryTutorialStep implements TutorialStepInstance {
   private static final int HINT_DELAY = 600;
   private static final Component TITLE = Component.translatable("tutorial.open_inventory.title");
   private static final Component DESCRIPTION = Component.translatable("tutorial.open_inventory.description", Tutorial.key("inventory"));
   private final Tutorial tutorial;
   @Nullable
   private TutorialToast toast;
   private int timeWaiting;

   public OpenInventoryTutorialStep(Tutorial var1) {
      super();
      this.tutorial = var1;
   }

   public void tick() {
      ++this.timeWaiting;
      if (!this.tutorial.isSurvival()) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting >= 600 && this.toast == null) {
            Minecraft var1 = this.tutorial.getMinecraft();
            this.toast = new TutorialToast(var1.font, TutorialToast.Icons.RECIPE_BOOK, TITLE, DESCRIPTION, false);
            var1.getToastManager().addToast(this.toast);
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
