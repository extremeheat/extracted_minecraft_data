package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

public class PathfindingRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Map<Integer, Path> pathMap = Maps.newHashMap();
   private final Map<Integer, Float> pathMaxDist = Maps.newHashMap();
   private final Map<Integer, Long> creationMap = Maps.newHashMap();
   private static final long TIMEOUT = 5000L;
   private static final float MAX_RENDER_DIST = 80.0F;
   private static final boolean SHOW_OPEN_CLOSED = true;
   private static final boolean SHOW_OPEN_CLOSED_COST_MALUS = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_TEXT = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_BOX = true;
   private static final boolean SHOW_GROUND_LABELS = true;
   private static final float TEXT_SCALE = 0.02F;

   public PathfindingRenderer() {
      super();
   }

   public void addPath(int var1, Path var2, float var3) {
      this.pathMap.put(var1, var2);
      this.creationMap.put(var1, Util.getMillis());
      this.pathMaxDist.put(var1, var3);
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      if (!this.pathMap.isEmpty()) {
         long var9 = Util.getMillis();
         Iterator var11 = this.pathMap.keySet().iterator();

         while(var11.hasNext()) {
            Integer var12 = (Integer)var11.next();
            Path var13 = (Path)this.pathMap.get(var12);
            float var14 = (Float)this.pathMaxDist.get(var12);
            renderPath(var13, var14, true, true, var3, var5, var7);
         }

         Integer[] var15 = (Integer[])this.creationMap.keySet().toArray(new Integer[0]);
         int var16 = var15.length;

         for(int var17 = 0; var17 < var16; ++var17) {
            Integer var18 = var15[var17];
            if (var9 - (Long)this.creationMap.get(var18) > 5000L) {
               this.pathMap.remove(var18);
               this.creationMap.remove(var18);
            }
         }

      }
   }

   public static void renderPath(Path var0, float var1, boolean var2, boolean var3, double var4, double var6, double var8) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      RenderSystem.lineWidth(6.0F);
      doRenderPath(var0, var1, var2, var3, var4, var6, var8);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   private static void doRenderPath(Path var0, float var1, boolean var2, boolean var3, double var4, double var6, double var8) {
      renderPathLine(var0, var4, var6, var8);
      BlockPos var10 = var0.getTarget();
      int var11;
      Node var12;
      if (distanceToCamera(var10, var4, var6, var8) <= 80.0F) {
         DebugRenderer.renderFilledBox((new AABB((double)((float)var10.getX() + 0.25F), (double)((float)var10.getY() + 0.25F), (double)var10.getZ() + 0.25D, (double)((float)var10.getX() + 0.75F), (double)((float)var10.getY() + 0.75F), (double)((float)var10.getZ() + 0.75F))).move(-var4, -var6, -var8), 0.0F, 1.0F, 0.0F, 0.5F);

         for(var11 = 0; var11 < var0.getNodeCount(); ++var11) {
            var12 = var0.getNode(var11);
            if (distanceToCamera(var12.asBlockPos(), var4, var6, var8) <= 80.0F) {
               float var13 = var11 == var0.getNextNodeIndex() ? 1.0F : 0.0F;
               float var14 = var11 == var0.getNextNodeIndex() ? 0.0F : 1.0F;
               DebugRenderer.renderFilledBox((new AABB((double)((float)var12.field_333 + 0.5F - var1), (double)((float)var12.field_334 + 0.01F * (float)var11), (double)((float)var12.field_335 + 0.5F - var1), (double)((float)var12.field_333 + 0.5F + var1), (double)((float)var12.field_334 + 0.25F + 0.01F * (float)var11), (double)((float)var12.field_335 + 0.5F + var1))).move(-var4, -var6, -var8), var13, 0.0F, var14, 0.5F);
            }
         }
      }

      if (var2) {
         Node[] var15 = var0.getClosedSet();
         int var16 = var15.length;

         int var17;
         Node var18;
         for(var17 = 0; var17 < var16; ++var17) {
            var18 = var15[var17];
            if (distanceToCamera(var18.asBlockPos(), var4, var6, var8) <= 80.0F) {
               DebugRenderer.renderFilledBox((new AABB((double)((float)var18.field_333 + 0.5F - var1 / 2.0F), (double)((float)var18.field_334 + 0.01F), (double)((float)var18.field_335 + 0.5F - var1 / 2.0F), (double)((float)var18.field_333 + 0.5F + var1 / 2.0F), (double)var18.field_334 + 0.1D, (double)((float)var18.field_335 + 0.5F + var1 / 2.0F))).move(-var4, -var6, -var8), 1.0F, 0.8F, 0.8F, 0.5F);
            }
         }

         var15 = var0.getOpenSet();
         var16 = var15.length;

         for(var17 = 0; var17 < var16; ++var17) {
            var18 = var15[var17];
            if (distanceToCamera(var18.asBlockPos(), var4, var6, var8) <= 80.0F) {
               DebugRenderer.renderFilledBox((new AABB((double)((float)var18.field_333 + 0.5F - var1 / 2.0F), (double)((float)var18.field_334 + 0.01F), (double)((float)var18.field_335 + 0.5F - var1 / 2.0F), (double)((float)var18.field_333 + 0.5F + var1 / 2.0F), (double)var18.field_334 + 0.1D, (double)((float)var18.field_335 + 0.5F + var1 / 2.0F))).move(-var4, -var6, -var8), 0.8F, 1.0F, 1.0F, 0.5F);
            }
         }
      }

      if (var3) {
         for(var11 = 0; var11 < var0.getNodeCount(); ++var11) {
            var12 = var0.getNode(var11);
            if (distanceToCamera(var12.asBlockPos(), var4, var6, var8) <= 80.0F) {
               DebugRenderer.renderFloatingText(String.format("%s", var12.type), (double)var12.field_333 + 0.5D, (double)var12.field_334 + 0.75D, (double)var12.field_335 + 0.5D, -1, 0.02F, true, 0.0F, true);
               DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", var12.costMalus), (double)var12.field_333 + 0.5D, (double)var12.field_334 + 0.25D, (double)var12.field_335 + 0.5D, -1, 0.02F, true, 0.0F, true);
            }
         }
      }

   }

   public static void renderPathLine(Path var0, double var1, double var3, double var5) {
      Tesselator var7 = Tesselator.getInstance();
      BufferBuilder var8 = var7.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      var8.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

      for(int var9 = 0; var9 < var0.getNodeCount(); ++var9) {
         Node var10 = var0.getNode(var9);
         if (!(distanceToCamera(var10.asBlockPos(), var1, var3, var5) > 80.0F)) {
            float var11 = (float)var9 / (float)var0.getNodeCount() * 0.33F;
            int var12 = var9 == 0 ? 0 : Mth.hsvToRgb(var11, 0.9F, 0.9F);
            int var13 = var12 >> 16 & 255;
            int var14 = var12 >> 8 & 255;
            int var15 = var12 & 255;
            var8.vertex((double)var10.field_333 - var1 + 0.5D, (double)var10.field_334 - var3 + 0.5D, (double)var10.field_335 - var5 + 0.5D).color(var13, var14, var15, 255).endVertex();
         }
      }

      var7.end();
   }

   private static float distanceToCamera(BlockPos var0, double var1, double var3, double var5) {
      return (float)(Math.abs((double)var0.getX() - var1) + Math.abs((double)var0.getY() - var3) + Math.abs((double)var0.getZ() - var5));
   }
}
