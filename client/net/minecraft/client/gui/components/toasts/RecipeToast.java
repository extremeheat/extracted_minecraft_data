package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.GameRenderer;
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

   public Toast.Visibility render(PoseStack var1, ToastComponent var2, long var3) {
      if (this.changed) {
         this.lastChanged = var3;
         this.changed = false;
      }

      if (this.recipes.isEmpty()) {
         return Toast.Visibility.HIDE;
      } else {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, TEXTURE);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         var2.blit(var1, 0, 0, 0, 32, this.width(), this.height());
         var2.getMinecraft().font.draw(var1, TITLE_TEXT, 30.0F, 7.0F, -11534256);
         var2.getMinecraft().font.draw(var1, DESCRIPTION_TEXT, 30.0F, 18.0F, -16777216);
         Recipe var5 = (Recipe)this.recipes.get((int)(var3 / Math.max(1L, 5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
         ItemStack var6 = var5.getToastSymbol();
         PoseStack var7 = RenderSystem.getModelViewStack();
         var7.pushPose();
         var7.scale(0.6F, 0.6F, 1.0F);
         RenderSystem.applyModelViewMatrix();
         var2.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(var6, 3, 3);
         var7.popPose();
         RenderSystem.applyModelViewMatrix();
         var2.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(var5.getResultItem(), 8, 8);
         return var3 - this.lastChanged >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      }
   }

   private void addItem(Recipe<?> var1) {
      this.recipes.add(var1);
      this.changed = true;
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
