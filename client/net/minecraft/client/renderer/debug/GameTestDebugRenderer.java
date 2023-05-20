package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
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

   @Override
   public void clear() {
      this.markers.clear();
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      long var9 = Util.getMillis();
      this.markers.entrySet().removeIf(var2x -> var9 > var2x.getValue().removeAtTime);
      this.markers.forEach((var3x, var4) -> this.renderMarker(var1, var2, var3x, var4));
   }

   private void renderMarker(PoseStack var1, MultiBufferSource var2, BlockPos var3, GameTestDebugRenderer.Marker var4) {
      DebugRenderer.renderFilledBox(var1, var2, var3, 0.02F, var4.getR(), var4.getG(), var4.getB(), var4.getA() * 0.75F);
      if (!var4.text.isEmpty()) {
         double var5 = (double)var3.getX() + 0.5;
         double var7 = (double)var3.getY() + 1.2;
         double var9 = (double)var3.getZ() + 0.5;
         DebugRenderer.renderFloatingText(var1, var2, var4.text, var5, var7, var9, -1, 0.01F, true, 0.0F, true);
      }
   }

   static class Marker {
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
         return (float)(this.color >> 16 & 0xFF) / 255.0F;
      }

      public float getG() {
         return (float)(this.color >> 8 & 0xFF) / 255.0F;
      }

      public float getB() {
         return (float)(this.color & 0xFF) / 255.0F;
      }

      public float getA() {
         return (float)(this.color >> 24 & 0xFF) / 255.0F;
      }
   }
}
