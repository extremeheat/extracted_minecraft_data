package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeToast implements Toast {
   private static final long DISPLAY_TIME = 5000L;
   private static final Component TITLE_TEXT = Component.translatable("recipe.toast.title");
   private static final Component DESCRIPTION_TEXT = Component.translatable("recipe.toast.description");
   private final List<Recipe<?>> recipes = Lists.newArrayList();
   private long lastChanged;
   private boolean changed;

   public RecipeToast(Recipe<?> var1) {
      super();
      this.recipes.add(var1);
   }

   @Override
   public Toast.Visibility render(GuiGraphics var1, ToastComponent var2, long var3) {
      if (this.changed) {
         this.lastChanged = var3;
         this.changed = false;
      }

      if (this.recipes.isEmpty()) {
         return Toast.Visibility.HIDE;
      } else {
         var1.blit(TEXTURE, 0, 0, 0, 32, this.width(), this.height());
         var1.drawString(var2.getMinecraft().font, TITLE_TEXT, 30, 7, -11534256, false);
         var1.drawString(var2.getMinecraft().font, DESCRIPTION_TEXT, 30, 18, -16777216, false);
         Recipe var5 = this.recipes
            .get(
               (int)(
                  (double)var3
                     / Math.max(1.0, 5000.0 * var2.getNotificationDisplayTimeMultiplier() / (double)this.recipes.size())
                     % (double)this.recipes.size()
               )
            );
         ItemStack var6 = var5.getToastSymbol();
         var1.pose().pushPose();
         var1.pose().scale(0.6F, 0.6F, 1.0F);
         var1.renderFakeItem(var6, 3, 3);
         var1.pose().popPose();
         var1.renderFakeItem(var5.getResultItem(var2.getMinecraft().level.registryAccess()), 8, 8);
         return (double)(var3 - this.lastChanged) >= 5000.0 * var2.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      }
   }

   private void addItem(Recipe<?> var1) {
      this.recipes.add(var1);
      this.changed = true;
   }

   public static void addOrUpdate(ToastComponent var0, Recipe<?> var1) {
      RecipeToast var2 = var0.getToast(RecipeToast.class, NO_TOKEN);
      if (var2 == null) {
         var0.addToast(new RecipeToast(var1));
      } else {
         var2.addItem(var1);
      }
   }
}
