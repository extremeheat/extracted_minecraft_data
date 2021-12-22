package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TutorialToast implements Toast {
   public static final int PROGRESS_BAR_WIDTH = 154;
   public static final int PROGRESS_BAR_HEIGHT = 1;
   public static final int PROGRESS_BAR_X = 3;
   public static final int PROGRESS_BAR_Y = 28;
   private final TutorialToast.Icons icon;
   private final Component title;
   @Nullable
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
      RenderSystem.setShaderTexture(0, TEXTURE);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
         float var5 = Mth.clampedLerp(this.lastProgress, this.progress, (float)(var3 - this.lastProgressTime) / 100.0F);
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
      SOCIAL_INTERACTIONS(2, 1),
      RIGHT_CLICK(3, 1);

      // $FF: renamed from: x int
      private final int field_154;
      // $FF: renamed from: y int
      private final int field_155;

      private Icons(int var3, int var4) {
         this.field_154 = var3;
         this.field_155 = var4;
      }

      public void render(PoseStack var1, GuiComponent var2, int var3, int var4) {
         RenderSystem.enableBlend();
         var2.blit(var1, var3, var4, 176 + this.field_154 * 20, this.field_155 * 20, 20, 20);
         RenderSystem.enableBlend();
      }

      // $FF: synthetic method
      private static TutorialToast.Icons[] $values() {
         return new TutorialToast.Icons[]{MOVEMENT_KEYS, MOUSE, TREE, RECIPE_BOOK, WOODEN_PLANKS, SOCIAL_INTERACTIONS, RIGHT_CLICK};
      }
   }
}
