package net.minecraft.client.gui.screens.inventory.tooltip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class TooltipRenderUtil {
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("tooltip/background");
   private static final ResourceLocation FRAME_SPRITE = ResourceLocation.withDefaultNamespace("tooltip/frame");
   public static final int MOUSE_OFFSET = 12;
   private static final int PADDING = 3;
   public static final int PADDING_LEFT = 3;
   public static final int PADDING_RIGHT = 3;
   public static final int PADDING_TOP = 3;
   public static final int PADDING_BOTTOM = 3;
   private static final int MARGIN = 9;

   public TooltipRenderUtil() {
      super();
   }

   public static void renderTooltipBackground(GuiGraphics var0, int var1, int var2, int var3, int var4, int var5, @Nullable ResourceLocation var6) {
      int var7 = var1 - 3 - 9;
      int var8 = var2 - 3 - 9;
      int var9 = var3 + 3 + 3 + 18;
      int var10 = var4 + 3 + 3 + 18;
      var0.pose().pushPose();
      var0.pose().translate(0.0F, 0.0F, (float)var5);
      var0.blitSprite(RenderType::guiTextured, getBackgroundSprite(var6), var7, var8, var9, var10);
      var0.blitSprite(RenderType::guiTextured, getFrameSprite(var6), var7, var8, var9, var10);
      var0.pose().popPose();
   }

   private static ResourceLocation getBackgroundSprite(@Nullable ResourceLocation var0) {
      return var0 == null ? BACKGROUND_SPRITE : var0.withPath((var0x) -> {
         return "tooltip/" + var0x + "_background";
      });
   }

   private static ResourceLocation getFrameSprite(@Nullable ResourceLocation var0) {
      return var0 == null ? FRAME_SPRITE : var0.withPath((var0x) -> {
         return "tooltip/" + var0x + "_frame";
      });
   }
}
