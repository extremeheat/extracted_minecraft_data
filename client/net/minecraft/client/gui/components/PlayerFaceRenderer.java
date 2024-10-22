package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class PlayerFaceRenderer {
   public static final int SKIN_HEAD_U = 8;
   public static final int SKIN_HEAD_V = 8;
   public static final int SKIN_HEAD_WIDTH = 8;
   public static final int SKIN_HEAD_HEIGHT = 8;
   public static final int SKIN_HAT_U = 40;
   public static final int SKIN_HAT_V = 8;
   public static final int SKIN_HAT_WIDTH = 8;
   public static final int SKIN_HAT_HEIGHT = 8;
   public static final int SKIN_TEX_WIDTH = 64;
   public static final int SKIN_TEX_HEIGHT = 64;

   public PlayerFaceRenderer() {
      super();
   }

   public static void draw(GuiGraphics var0, PlayerSkin var1, int var2, int var3, int var4) {
      draw(var0, var1, var2, var3, var4, -1);
   }

   public static void draw(GuiGraphics var0, PlayerSkin var1, int var2, int var3, int var4, int var5) {
      draw(var0, var1.texture(), var2, var3, var4, true, false, var5);
   }

   public static void draw(GuiGraphics var0, ResourceLocation var1, int var2, int var3, int var4, boolean var5, boolean var6, int var7) {
      int var8 = 8 + (var6 ? 8 : 0);
      int var9 = 8 * (var6 ? -1 : 1);
      var0.blit(RenderType::guiTextured, var1, var2, var3, 8.0F, (float)var8, var4, var4, 8, var9, 64, 64, var7);
      if (var5) {
         drawHat(var0, var1, var2, var3, var4, var6, var7);
      }
   }

   private static void drawHat(GuiGraphics var0, ResourceLocation var1, int var2, int var3, int var4, boolean var5, int var6) {
      int var7 = 8 + (var5 ? 8 : 0);
      int var8 = 8 * (var5 ? -1 : 1);
      var0.blit(RenderType::guiTextured, var1, var2, var3, 40.0F, (float)var7, var4, var4, 8, var8, 64, 64, var6);
   }
}
