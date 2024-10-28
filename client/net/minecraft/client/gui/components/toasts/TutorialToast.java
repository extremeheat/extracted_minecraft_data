package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TutorialToast implements Toast {
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/tutorial");
   public static final int PROGRESS_BAR_WIDTH = 154;
   public static final int PROGRESS_BAR_HEIGHT = 1;
   public static final int PROGRESS_BAR_X = 3;
   public static final int PROGRESS_BAR_Y = 28;
   private final Icons icon;
   private final Component title;
   @Nullable
   private final Component message;
   private Toast.Visibility visibility;
   private long lastProgressTime;
   private float lastProgress;
   private float progress;
   private final boolean progressable;

   public TutorialToast(Icons var1, Component var2, @Nullable Component var3, boolean var4) {
      super();
      this.visibility = Toast.Visibility.SHOW;
      this.icon = var1;
      this.title = var2;
      this.message = var3;
      this.progressable = var4;
   }

   public Toast.Visibility render(GuiGraphics var1, ToastComponent var2, long var3) {
      var1.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
      this.icon.render(var1, 6, 6);
      if (this.message == null) {
         var1.drawString(var2.getMinecraft().font, (Component)this.title, 30, 12, -11534256, false);
      } else {
         var1.drawString(var2.getMinecraft().font, (Component)this.title, 30, 7, -11534256, false);
         var1.drawString(var2.getMinecraft().font, (Component)this.message, 30, 18, -16777216, false);
      }

      if (this.progressable) {
         var1.fill(3, 28, 157, 29, -1);
         float var5 = Mth.clampedLerp(this.lastProgress, this.progress, (float)(var3 - this.lastProgressTime) / 100.0F);
         int var6;
         if (this.progress >= this.lastProgress) {
            var6 = -16755456;
         } else {
            var6 = -11206656;
         }

         var1.fill(3, 28, (int)(3.0F + 154.0F * var5), 29, var6);
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
      MOVEMENT_KEYS(ResourceLocation.withDefaultNamespace("toast/movement_keys")),
      MOUSE(ResourceLocation.withDefaultNamespace("toast/mouse")),
      TREE(ResourceLocation.withDefaultNamespace("toast/tree")),
      RECIPE_BOOK(ResourceLocation.withDefaultNamespace("toast/recipe_book")),
      WOODEN_PLANKS(ResourceLocation.withDefaultNamespace("toast/wooden_planks")),
      SOCIAL_INTERACTIONS(ResourceLocation.withDefaultNamespace("toast/social_interactions")),
      RIGHT_CLICK(ResourceLocation.withDefaultNamespace("toast/right_click"));

      private final ResourceLocation sprite;

      private Icons(final ResourceLocation var3) {
         this.sprite = var3;
      }

      public void render(GuiGraphics var1, int var2, int var3) {
         RenderSystem.enableBlend();
         var1.blitSprite(this.sprite, var2, var3, 20, 20);
      }

      // $FF: synthetic method
      private static Icons[] $values() {
         return new Icons[]{MOVEMENT_KEYS, MOUSE, TREE, RECIPE_BOOK, WOODEN_PLANKS, SOCIAL_INTERACTIONS, RIGHT_CLICK};
      }
   }
}
