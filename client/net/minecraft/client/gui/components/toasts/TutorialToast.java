package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TutorialToast implements Toast {
   private final TutorialToast.Icons icon;
   private final Component title;
   private final Component message;
   private Toast.Visibility visibility;
   private long lastProgressTime;
   private float lastProgress;
   private float progress;
   private final boolean progressable;

   public TutorialToast(TutorialToast.Icons var1, Component var2, @Nullable Component var3, boolean var4) {
      super();
      this.visibility = Toast.Visibility.SHOW;
      this.icon = var1;
      this.title = var2;
      this.message = var3;
      this.progressable = var4;
   }

   public Toast.Visibility render(PoseStack var1, ToastComponent var2, long var3) {
      var2.getMinecraft().getTextureManager().bind(TEXTURE);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      var2.blit(var1, 0, 0, 0, 96, this.width(), this.height());
      this.icon.render(var1, var2, 6, 6);
      if (this.message == null) {
         var2.getMinecraft().font.draw(var1, this.title, 30.0F, 12.0F, -11534256);
      } else {
         var2.getMinecraft().font.draw(var1, this.title, 30.0F, 7.0F, -11534256);
         var2.getMinecraft().font.draw(var1, this.message, 30.0F, 18.0F, -16777216);
      }

      if (this.progressable) {
         GuiComponent.fill(var1, 3, 28, 157, 29, -1);
         float var5 = (float)Mth.clampedLerp((double)this.lastProgress, (double)this.progress, (double)((float)(var3 - this.lastProgressTime) / 100.0F));
         int var6;
         if (this.progress >= this.lastProgress) {
            var6 = -16755456;
         } else {
            var6 = -11206656;
         }

         GuiComponent.fill(var1, 3, 28, (int)(3.0F + 154.0F * var5), 29, var6);
         this.lastProgress = var5;
         this.lastProgressTime = var3;
      }

      return this.visibility;
   }

   public void hide() {
      this.visibility = Toast.Visibility.HIDE;
   }

   public void updateProgress(float var1) {
      this.progress = var1;
   }

   public static enum Icons {
      MOVEMENT_KEYS(0, 0),
      MOUSE(1, 0),
      TREE(2, 0),
      RECIPE_BOOK(0, 1),
      WOODEN_PLANKS(1, 1),
      SOCIAL_INTERACTIONS(2, 1);

      private final int x;
      private final int y;

      private Icons(int var3, int var4) {
         this.x = var3;
         this.y = var4;
      }

      public void render(PoseStack var1, GuiComponent var2, int var3, int var4) {
         RenderSystem.enableBlend();
         var2.blit(var1, var3, var4, 176 + this.x * 20, this.y * 20, 20, 20);
         RenderSystem.enableBlend();
      }
   }
}
