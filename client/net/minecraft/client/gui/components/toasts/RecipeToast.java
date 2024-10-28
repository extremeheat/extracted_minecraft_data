package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeToast implements Toast {
   private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("toast/recipe");
   private static final long DISPLAY_TIME = 5000L;
   private static final Component TITLE_TEXT = Component.translatable("recipe.toast.title");
   private static final Component DESCRIPTION_TEXT = Component.translatable("recipe.toast.description");
   private final List<RecipeHolder<?>> recipes = Lists.newArrayList();
   private long lastChanged;
   private boolean changed;

   public RecipeToast(RecipeHolder<?> var1) {
      super();
      this.recipes.add(var1);
   }

   public Toast.Visibility render(GuiGraphics var1, ToastComponent var2, long var3) {
      if (this.changed) {
         this.lastChanged = var3;
         this.changed = false;
      }

      if (this.recipes.isEmpty()) {
         return Toast.Visibility.HIDE;
      } else {
         var1.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
         var1.drawString(var2.getMinecraft().font, (Component)TITLE_TEXT, 30, 7, -11534256, false);
         var1.drawString(var2.getMinecraft().font, (Component)DESCRIPTION_TEXT, 30, 18, -16777216, false);
         RecipeHolder var5 = (RecipeHolder)this.recipes.get((int)((double)var3 / Math.max(1.0, 5000.0 * var2.getNotificationDisplayTimeMultiplier() / (double)this.recipes.size()) % (double)this.recipes.size()));
         ItemStack var6 = var5.value().getToastSymbol();
         var1.pose().pushPose();
         var1.pose().scale(0.6F, 0.6F, 1.0F);
         var1.renderFakeItem(var6, 3, 3);
         var1.pose().popPose();
         var1.renderFakeItem(var5.value().getResultItem(var2.getMinecraft().level.registryAccess()), 8, 8);
         return (double)(var3 - this.lastChanged) >= 5000.0 * var2.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      }
   }

   private void addItem(RecipeHolder<?> var1) {
      this.recipes.add(var1);
      this.changed = true;
   }

   public static void addOrUpdate(ToastComponent var0, RecipeHolder<?> var1) {
      RecipeToast var2 = (RecipeToast)var0.getToast(RecipeToast.class, NO_TOKEN);
      if (var2 == null) {
         var0.addToast(new RecipeToast(var1));
      } else {
         var2.addItem(var1);
      }

   }
}
