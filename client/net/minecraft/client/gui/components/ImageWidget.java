package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public abstract class ImageWidget extends AbstractWidget {
   ImageWidget(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, CommonComponents.EMPTY);
   }

   public static ImageWidget texture(int var0, int var1, ResourceLocation var2, int var3, int var4) {
      return new Texture(0, 0, var0, var1, var2, var3, var4);
   }

   public static ImageWidget sprite(int var0, int var1, ResourceLocation var2) {
      return new Sprite(0, 0, var0, var1, var2);
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
   }

   public void playDownSound(SoundManager var1) {
   }

   public boolean isActive() {
      return false;
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      return null;
   }

   private static class Texture extends ImageWidget {
      private final ResourceLocation texture;
      private final int textureWidth;
      private final int textureHeight;

      public Texture(int var1, int var2, int var3, int var4, ResourceLocation var5, int var6, int var7) {
         super(var1, var2, var3, var4);
         this.texture = var5;
         this.textureWidth = var6;
         this.textureHeight = var7;
      }

      protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         var1.blit(this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), this.textureWidth, this.textureHeight);
      }
   }

   static class Sprite extends ImageWidget {
      private final ResourceLocation sprite;

      public Sprite(int var1, int var2, int var3, int var4, ResourceLocation var5) {
         super(var1, var2, var3, var4);
         this.sprite = var5;
      }

      public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         var1.blitSprite(this.sprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      }
   }
}
