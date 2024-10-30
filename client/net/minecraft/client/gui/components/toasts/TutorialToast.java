package net.minecraft.client.gui.components.toasts;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class TutorialToast implements Toast {
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/tutorial");
   public static final int PROGRESS_BAR_WIDTH = 154;
   public static final int PROGRESS_BAR_HEIGHT = 1;
   public static final int PROGRESS_BAR_X = 3;
   public static final int PROGRESS_BAR_MARGIN_BOTTOM = 4;
   private static final int PADDING_TOP = 7;
   private static final int PADDING_BOTTOM = 3;
   private static final int LINE_SPACING = 11;
   private static final int TEXT_LEFT = 30;
   private static final int TEXT_WIDTH = 126;
   private final Icons icon;
   private final List<FormattedCharSequence> lines;
   private Toast.Visibility visibility;
   private long lastSmoothingTime;
   private float smoothedProgress;
   private float progress;
   private final boolean progressable;
   private final int timeToDisplayMs;

   public TutorialToast(Font var1, Icons var2, Component var3, @Nullable Component var4, boolean var5, int var6) {
      super();
      this.visibility = Toast.Visibility.SHOW;
      this.icon = var2;
      this.lines = new ArrayList(2);
      this.lines.addAll(var1.split(var3.copy().withColor(-11534256), 126));
      if (var4 != null) {
         this.lines.addAll(var1.split(var4, 126));
      }

      this.progressable = var5;
      this.timeToDisplayMs = var6;
   }

   public TutorialToast(Font var1, Icons var2, Component var3, @Nullable Component var4, boolean var5) {
      this(var1, var2, var3, var4, var5, 0);
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

   public int height() {
      return 7 + this.contentHeight() + 3;
   }

   private int contentHeight() {
      return Math.max(this.lines.size(), 2) * 11;
   }

   public void render(GuiGraphics var1, Font var2, long var3) {
      int var5 = this.height();
      var1.blitSprite(RenderType::guiTextured, (ResourceLocation)BACKGROUND_SPRITE, 0, 0, this.width(), var5);
      this.icon.render(var1, 6, 6);
      int var6 = this.lines.size() * 11;
      int var7 = 7 + (this.contentHeight() - var6) / 2;

      int var8;
      for(var8 = 0; var8 < this.lines.size(); ++var8) {
         var1.drawString(var2, (FormattedCharSequence)((FormattedCharSequence)this.lines.get(var8)), 30, var7 + var8 * 11, -16777216, false);
      }

      if (this.progressable) {
         var8 = var5 - 4;
         var1.fill(3, var8, 157, var8 + 1, -1);
         int var9;
         if (this.progress >= this.smoothedProgress) {
            var9 = -16755456;
         } else {
            var9 = -11206656;
         }

         var1.fill(3, var8, (int)(3.0F + 154.0F * this.smoothedProgress), var8 + 1, var9);
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
