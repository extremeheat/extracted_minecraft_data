package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

public class GameTestDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float PADDING = 0.02F;
   private final Map<BlockPos, GameTestDebugRenderer.Marker> markers = Maps.newHashMap();

   public GameTestDebugRenderer() {
      super();
   }

   public void addMarker(BlockPos var1, int var2, String var3, int var4) {
      this.markers.put(var1, new GameTestDebugRenderer.Marker(var2, var3, Util.getMillis() + (long)var4));
   }

   public void clear() {
      this.markers.clear();
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      long var9 = Util.getMillis();
      this.markers.entrySet().removeIf((var2x) -> {
         return var9 > ((GameTestDebugRenderer.Marker)var2x.getValue()).removeAtTime;
      });
      this.markers.forEach(this::renderMarker);
   }

   private void renderMarker(BlockPos var1, GameTestDebugRenderer.Marker var2) {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      DebugRenderer.renderFilledBox(var1, 0.02F, var2.getR(), var2.getG(), var2.getB(), var2.getA());
      if (!var2.text.isEmpty()) {
         double var3 = (double)var1.getX() + 0.5D;
         double var5 = (double)var1.getY() + 1.2D;
         double var7 = (double)var1.getZ() + 0.5D;
         DebugRenderer.renderFloatingText(var2.text, var3, var5, var7, -1, 0.01F, true, 0.0F, true);
      }

      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   private static class Marker {
      public int color;
      public String text;
      public long removeAtTime;

      public Marker(int var1, String var2, long var3) {
         super();
         this.color = var1;
         this.text = var2;
         this.removeAtTime = var3;
      }

      public float getR() {
         return (float)(this.color >> 16 & 255) / 255.0F;
      }

      public float getG() {
         return (float)(this.color >> 8 & 255) / 255.0F;
      }

      public float getB() {
         return (float)(this.color & 255) / 255.0F;
      }

      public float getA() {
         return (float)(this.color >> 24 & 255) / 255.0F;
      }
   }
}
