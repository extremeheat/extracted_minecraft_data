package net.minecraft.client.gui.components.toasts;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
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
   private long lastSmoothingTime;
   private float smoothedProgress;
   private float progress;
   private final boolean progressable;
   private final int timeToDisplayMs;

   public TutorialToast(Icons var1, Component var2, @Nullable Component var3, boolean var4, int var5) {
      super();
      this.visibility = Toast.Visibility.SHOW;
      this.icon = var1;
      this.title = var2;
      this.message = var3;
      this.progressable = var4;
      this.timeToDisplayMs = var5;
   }

   public TutorialToast(Icons var1, Component var2, @Nullable Component var3, boolean var4) {
      this(var1, var2, var3, var4, 0);
   }

   public Toast.Visibility getWantedVisibility() {
      return this.visibility;
   }

   public void update(ToastManager var1, long var2) {
      if (this.timeToDisplayMs > 0) {
         this.progress = Math.min((float)var2 / (float)this.timeToDisplayMs, 1.0F);
         this.smoothedProgress = this.progress;
         this.lastSmoothingTime = var2;
         if (var2 > (long)this.timeToDisplayMs) {
            this.hide();
         }
      } else if (this.progressable) {
         this.smoothedProgress = Mth.clampedLerp(this.smoothedProgress, this.progress, (float)(var2 - this.lastSmoothingTime) / 100.0F);
         this.lastSmoothingTime = var2;
      }

   }

   public void render(GuiGraphics var1, Font var2, long var3) {
      var1.blitSprite(RenderType::guiTextured, (ResourceLocation)BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
      this.icon.render(var1, 6, 6);
      if (this.message == null) {
         var1.drawString(var2, (Component)this.title, 30, 12, -11534256, false);
      } else {
         var1.drawString(var2, (Component)this.title, 30, 7, -11534256, false);
         var1.drawString(var2, (Component)this.message, 30, 18, -16777216, false);
      }

      if (this.progressable) {
         var1.fill(3, 28, 157, 29, -1);
         int var5;
         if (this.progress >= this.smoothedProgress) {
            var5 = -16755456;
         } else {
            var5 = -11206656;
         }

         var1.fill(3, 28, (int)(3.0F + 154.0F * this.smoothedProgress), 29, var5);
      }

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
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)this.sprite, var2, var3, 20, 20);
      }

      // $FF: synthetic method
      private static Icons[] $values() {
         return new Icons[]{MOVEMENT_KEYS, MOUSE, TREE, RECIPE_BOOK, WOODEN_PLANKS, SOCIAL_INTERACTIONS, RIGHT_CLICK};
      }
   }
}
