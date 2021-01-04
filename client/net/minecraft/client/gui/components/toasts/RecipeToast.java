package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeToast implements Toast {
   private final List<Recipe<?>> recipes = Lists.newArrayList();
   private long lastChanged;
   private boolean changed;

   public RecipeToast(Recipe<?> var1) {
      super();
      this.recipes.add(var1);
   }

   public Toast.Visibility render(ToastComponent var1, long var2) {
      if (this.changed) {
         this.lastChanged = var2;
         this.changed = false;
      }

      if (this.recipes.isEmpty()) {
         return Toast.Visibility.HIDE;
      } else {
         var1.getMinecraft().getTextureManager().bind(TEXTURE);
         GlStateManager.color3f(1.0F, 1.0F, 1.0F);
         var1.blit(0, 0, 0, 32, 160, 32);
         var1.getMinecraft().font.draw(I18n.get("recipe.toast.title"), 30.0F, 7.0F, -11534256);
         var1.getMinecraft().font.draw(I18n.get("recipe.toast.description"), 30.0F, 18.0F, -16777216);
         Lighting.turnOnGui();
         Recipe var4 = (Recipe)this.recipes.get((int)(var2 / (5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
         ItemStack var5 = var4.getToastSymbol();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6F, 0.6F, 1.0F);
         var1.getMinecraft().getItemRenderer().renderAndDecorateItem((LivingEntity)null, var5, 3, 3);
         GlStateManager.popMatrix();
         var1.getMinecraft().getItemRenderer().renderAndDecorateItem((LivingEntity)null, var4.getResultItem(), 8, 8);
         return var2 - this.lastChanged >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      }
   }

   public void addItem(Recipe<?> var1) {
      if (this.recipes.add(var1)) {
         this.changed = true;
      }

   }

   public static void addOrUpdate(ToastComponent var0, Recipe<?> var1) {
      RecipeToast var2 = (RecipeToast)var0.getToast(RecipeToast.class, NO_TOKEN);
      if (var2 == null) {
         var0.addToast(new RecipeToast(var1));
      } else {
         var2.addItem(var1);
      }

   }
}
