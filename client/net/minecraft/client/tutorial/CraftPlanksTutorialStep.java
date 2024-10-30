package net.minecraft.client.tutorial;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CraftPlanksTutorialStep implements TutorialStepInstance {
   private static final int HINT_DELAY = 1200;
   private static final Component CRAFT_TITLE = Component.translatable("tutorial.craft_planks.title");
   private static final Component CRAFT_DESCRIPTION = Component.translatable("tutorial.craft_planks.description");
   private final Tutorial tutorial;
   @Nullable
   private TutorialToast toast;
   private int timeWaiting;

   public CraftPlanksTutorialStep(Tutorial var1) {
      super();
      this.tutorial = var1;
   }

   public void tick() {
      ++this.timeWaiting;
      if (!this.tutorial.isSurvival()) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         Minecraft var1 = this.tutorial.getMinecraft();
         if (this.timeWaiting == 1) {
            LocalPlayer var2 = var1.player;
            if (var2 != null) {
               if (var2.getInventory().contains(ItemTags.PLANKS)) {
                  this.tutorial.setStep(TutorialSteps.NONE);
                  return;
               }

               if (hasCraftedPlanksPreviously(var2, ItemTags.PLANKS)) {
                  this.tutorial.setStep(TutorialSteps.NONE);
                  return;
               }
            }
         }

         if (this.timeWaiting >= 1200 && this.toast == null) {
            this.toast = new TutorialToast(var1.font, TutorialToast.Icons.WOODEN_PLANKS, CRAFT_TITLE, CRAFT_DESCRIPTION, false);
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

   public void onGetItem(ItemStack var1) {
      if (var1.is(ItemTags.PLANKS)) {
         this.tutorial.setStep(TutorialSteps.NONE);
      }

   }

   public static boolean hasCraftedPlanksPreviously(LocalPlayer var0, TagKey<Item> var1) {
      Iterator var2 = BuiltInRegistries.ITEM.getTagOrEmpty(var1).iterator();

      Holder var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Holder)var2.next();
      } while(var0.getStats().getValue(Stats.ITEM_CRAFTED.get((Item)var3.value())) <= 0);

      return true;
   }
}
