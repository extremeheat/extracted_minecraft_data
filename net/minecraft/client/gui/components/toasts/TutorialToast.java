package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TutorialToast implements Toast {
   private final TutorialToast.Icons icon;
   private final String title;
   private final String message;
   private Toast.Visibility visibility;
   private long lastProgressTime;
   private float lastProgress;
   private float progress;
   private final boolean progressable;

   public TutorialToast(TutorialToast.Icons var1, Component var2, @Nullable Component var3, boolean var4) {
      this.visibility = Toast.Visibility.SHOW;
      this.icon = var1;
      this.title = var2.getColoredString();
      this.message = var3 == null ? null : var3.getColoredString();
      this.progressable = var4;
   }

   public Toast.Visibility render(ToastComponent var1, long var2) {
      var1.getMinecraft().getTextureManager().bind(TEXTURE);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      var1.blit(0, 0, 0, 96, 160, 32);
      this.icon.render(var1, 6, 6);
      if (this.message == null) {
         var1.getMinecraft().font.draw(this.title, 30.0F, 12.0F, -11534256);
      } else {
         var1.getMinecraft().font.draw(this.title, 30.0F, 7.0F, -11534256);
         var1.getMinecraft().font.draw(this.message, 30.0F, 18.0F, -16777216);
      }

      if (this.progressable) {
         GuiComponent.fill(3, 28, 157, 29, -1);
         float var4 = (float)Mth.clampedLerp((double)this.lastProgress, (double)this.progress, (double)((float)(var2 - this.lastProgressTime) / 100.0F));
         int var5;
         if (this.progress >= this.lastProgress) {
            var5 = -16755456;
         } else {
            var5 = -11206656;
         }

         GuiComponent.fill(3, 28, (int)(3.0F + 154.0F * var4), 29, var5);
         this.lastProgress = var4;
         this.lastProgressTime = var2;
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
      WOODEN_PLANKS(1, 1);

      private final int x;
      private final int y;

      private Icons(int var3, int var4) {
         this.x = var3;
         this.y = var4;
      }

      public void render(GuiComponent var1, int var2, int var3) {
         RenderSystem.enableBlend();
         var1.blit(var2, var3, 176 + this.x * 20, this.y * 20, 20, 20);
         RenderSystem.enableBlend();
      }
   }
}
