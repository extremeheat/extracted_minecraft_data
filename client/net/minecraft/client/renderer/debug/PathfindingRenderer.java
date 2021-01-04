package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

public class PathfindingRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Integer, Path> pathMap = Maps.newHashMap();
   private final Map<Integer, Float> pathMaxDist = Maps.newHashMap();
   private final Map<Integer, Long> creationMap = Maps.newHashMap();

   public PathfindingRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void addPath(int var1, Path var2, float var3) {
      this.pathMap.put(var1, var2);
      this.creationMap.put(var1, Util.getMillis());
      this.pathMaxDist.put(var1, var3);
   }

   public void render(long var1) {
      if (!this.pathMap.isEmpty()) {
         long var3 = Util.getMillis();
         Iterator var5 = this.pathMap.keySet().iterator();

         while(var5.hasNext()) {
            Integer var6 = (Integer)var5.next();
            Path var7 = (Path)this.pathMap.get(var6);
            float var8 = (Float)this.pathMaxDist.get(var6);
            renderPath(this.getCamera(), var7, var8, true, true);
         }

         Integer[] var9 = (Integer[])this.creationMap.keySet().toArray(new Integer[0]);
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Integer var12 = var9[var11];
            if (var3 - (Long)this.creationMap.get(var12) > 20000L) {
               this.pathMap.remove(var12);
               this.creationMap.remove(var12);
            }
         }

      }
   }

   public static void renderPath(Camera var0, Path var1, float var2, boolean var3, boolean var4) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      GlStateManager.disableTexture();
      GlStateManager.lineWidth(6.0F);
      doRenderPath(var0, var1, var2, var3, var4);
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   private static void doRenderPath(Camera var0, Path var1, float var2, boolean var3, boolean var4) {
      renderPathLine(var0, var1);
      double var5 = var0.getPosition().x;
      double var7 = var0.getPosition().y;
      double var9 = var0.getPosition().z;
      BlockPos var11 = var1.getTarget();
      int var12;
      Node var13;
      if (distanceToCamera(var0, var11) <= 40.0F) {
         DebugRenderer.renderFilledBox((new AABB((double)((float)var11.getX() + 0.25F), (double)((float)var11.getY() + 0.25F), (double)var11.getZ() + 0.25D, (double)((float)var11.getX() + 0.75F), (double)((float)var11.getY() + 0.75F), (double)((float)var11.getZ() + 0.75F))).move(-var5, -var7, -var9), 0.0F, 1.0F, 0.0F, 0.5F);

         for(var12 = 0; var12 < var1.getSize(); ++var12) {
            var13 = var1.get(var12);
            if (distanceToCamera(var0, var13.asBlockPos()) <= 40.0F) {
               float var14 = var12 == var1.getIndex() ? 1.0F : 0.0F;
               float var15 = var12 == var1.getIndex() ? 0.0F : 1.0F;
               DebugRenderer.renderFilledBox((new AABB((double)((float)var13.x + 0.5F - var2), (double)((float)var13.y + 0.01F * (float)var12), (double)((float)var13.z + 0.5F - var2), (double)((float)var13.x + 0.5F + var2), (double)((float)var13.y + 0.25F + 0.01F * (float)var12), (double)((float)var13.z + 0.5F + var2))).move(-var5, -var7, -var9), var14, 0.0F, var15, 0.5F);
            }
         }
      }

      if (var3) {
         Node[] var16 = var1.getClosedSet();
         int var17 = var16.length;

         int var18;
         Node var19;
         for(var18 = 0; var18 < var17; ++var18) {
            var19 = var16[var18];
            if (distanceToCamera(var0, var19.asBlockPos()) <= 40.0F) {
               DebugRenderer.renderFloatingText(String.format("%s", var19.type), (double)var19.x + 0.5D, (double)var19.y + 0.75D, (double)var19.z + 0.5D, -65536);
               DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", var19.costMalus), (double)var19.x + 0.5D, (double)var19.y + 0.25D, (double)var19.z + 0.5D, -65536);
            }
         }

         var16 = var1.getOpenSet();
         var17 = var16.length;

         for(var18 = 0; var18 < var17; ++var18) {
            var19 = var16[var18];
            if (distanceToCamera(var0, var19.asBlockPos()) <= 40.0F) {
               DebugRenderer.renderFloatingText(String.format("%s", var19.type), (double)var19.x + 0.5D, (double)var19.y + 0.75D, (double)var19.z + 0.5D, -16776961);
               DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", var19.costMalus), (double)var19.x + 0.5D, (double)var19.y + 0.25D, (double)var19.z + 0.5D, -16776961);
            }
         }
      }

      if (var4) {
         for(var12 = 0; var12 < var1.getSize(); ++var12) {
            var13 = var1.get(var12);
            if (distanceToCamera(var0, var13.asBlockPos()) <= 40.0F) {
               DebugRenderer.renderFloatingText(String.format("%s", var13.type), (double)var13.x + 0.5D, (double)var13.y + 0.75D, (double)var13.z + 0.5D, -1);
               DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", var13.costMalus), (double)var13.x + 0.5D, (double)var13.y + 0.25D, (double)var13.z + 0.5D, -1);
            }
         }
      }

   }

   public static void renderPathLine(Camera var0, Path var1) {
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      double var4 = var0.getPosition().x;
      double var6 = var0.getPosition().y;
      double var8 = var0.getPosition().z;
      var3.begin(3, DefaultVertexFormat.POSITION_COLOR);

      for(int var10 = 0; var10 < var1.getSize(); ++var10) {
         Node var11 = var1.get(var10);
         if (distanceToCamera(var0, var11.asBlockPos()) <= 40.0F) {
            float var12 = (float)var10 / (float)var1.getSize() * 0.33F;
            int var13 = var10 == 0 ? 0 : Mth.hsvToRgb(var12, 0.9F, 0.9F);
            int var14 = var13 >> 16 & 255;
            int var15 = var13 >> 8 & 255;
            int var16 = var13 & 255;
            var3.vertex((double)var11.x - var4 + 0.5D, (double)var11.y - var6 + 0.5D, (double)var11.z - var8 + 0.5D).color(var14, var15, var16, 255).endVertex();
         }
      }

      var2.end();
   }

   private static float distanceToCamera(Camera var0, BlockPos var1) {
      return (float)(Math.abs((double)var1.getX() - var0.getPosition().x) + Math.abs((double)var1.getY() - var0.getPosition().y) + Math.abs((double)var1.getZ() - var0.getPosition().z));
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }
}
