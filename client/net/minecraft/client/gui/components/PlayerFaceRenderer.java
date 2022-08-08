package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

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

   public static void draw(PoseStack var0, int var1, int var2, int var3) {
      draw(var0, var1, var2, var3, true, false);
   }

   public static void draw(PoseStack var0, int var1, int var2, int var3, boolean var4, boolean var5) {
      int var6 = 8 + (var5 ? 8 : 0);
      int var7 = 8 * (var5 ? -1 : 1);
      GuiComponent.blit(var0, var1, var2, var3, var3, 8.0F, (float)var6, 8, var7, 64, 64);
      if (var4) {
         drawHat(var0, var1, var2, var3, var5);
      }

   }

   private static void drawHat(PoseStack var0, int var1, int var2, int var3, boolean var4) {
      int var5 = 8 + (var4 ? 8 : 0);
      int var6 = 8 * (var4 ? -1 : 1);
      RenderSystem.enableBlend();
      GuiComponent.blit(var0, var1, var2, var3, var3, 40.0F, (float)var5, 8, var6, 64, 64);
      RenderSystem.disableBlend();
   }
}
