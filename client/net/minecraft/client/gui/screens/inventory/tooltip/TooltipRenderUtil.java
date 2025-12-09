package net.minecraft.client.gui.screens.inventory.tooltip;

import java.util.function.UnaryOperator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class TooltipRenderUtil {
   private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("tooltip/background");
   private static final Identifier FRAME_SPRITE = Identifier.withDefaultNamespace("tooltip/frame");
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

   public static void renderTooltipBackground(GuiGraphics var0, int var1, int var2, int var3, int var4, @Nullable Identifier var5) {
      int var6 = var1 - 3 - 9;
      int var7 = var2 - 3 - 9;
      int var8 = var3 + 3 + 3 + 18;
      int var9 = var4 + 3 + 3 + 18;
      var0.blitSprite(RenderPipelines.GUI_TEXTURED, getBackgroundSprite(var5), var6, var7, var8, var9);
      var0.blitSprite(RenderPipelines.GUI_TEXTURED, getFrameSprite(var5), var6, var7, var8, var9);
   }

   private static Identifier getBackgroundSprite(@Nullable Identifier var0) {
      return var0 == null ? BACKGROUND_SPRITE : var0.withPath((UnaryOperator)((var0x) -> "tooltip/" + var0x + "_background"));
   }

   private static Identifier getFrameSprite(@Nullable Identifier var0) {
      return var0 == null ? FRAME_SPRITE : var0.withPath((UnaryOperator)((var0x) -> "tooltip/" + var0x + "_frame"));
   }
}
