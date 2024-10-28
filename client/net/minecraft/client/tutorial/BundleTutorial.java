package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BundleTutorial {
   private final Tutorial tutorial;
   private final Options options;
   @Nullable
   private TutorialToast toast;

   public BundleTutorial(Tutorial var1, Options var2) {
      super();
      this.tutorial = var1;
      this.options = var2;
   }

   private void showToast() {
      if (this.toast != null) {
         this.tutorial.removeTimedToast(this.toast);
      }

      MutableComponent var1 = Component.translatable("tutorial.bundleInsert.title");
      MutableComponent var2 = Component.translatable("tutorial.bundleInsert.description");
      this.toast = new TutorialToast(TutorialToast.Icons.RIGHT_CLICK, var1, var2, true);
      this.tutorial.addTimedToast(this.toast, 160);
   }

   private void clearToast() {
      if (this.toast != null) {
         this.tutorial.removeTimedToast(this.toast);
         this.toast = null;
      }

      if (!this.options.hideBundleTutorial) {
         this.options.hideBundleTutorial = true;
         this.options.save();
      }

   }

   public void onInventoryAction(ItemStack var1, ItemStack var2, ClickAction var3) {
      if (!this.options.hideBundleTutorial) {
         if (!var1.isEmpty() && var2.is(Items.BUNDLE)) {
            if (var3 == ClickAction.PRIMARY) {
               this.showToast();
            } else if (var3 == ClickAction.SECONDARY) {
               this.clearToast();
            }
         } else if (var1.is(Items.BUNDLE) && !var2.isEmpty() && var3 == ClickAction.SECONDARY) {
            this.clearToast();
         }

      }
   }
}
