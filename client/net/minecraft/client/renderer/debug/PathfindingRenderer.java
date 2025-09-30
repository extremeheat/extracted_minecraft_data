package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Locale;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugPathInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

public class PathfindingRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float MAX_RENDER_DIST = 80.0F;
   private static final int MAX_TARGETING_DIST = 8;
   private static final boolean SHOW_ONLY_SELECTED = false;
   private static final boolean SHOW_OPEN_CLOSED = true;
   private static final boolean SHOW_OPEN_CLOSED_COST_MALUS = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_TEXT = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_BOX = true;
   private static final boolean SHOW_GROUND_LABELS = true;
   private static final float TEXT_SCALE = 0.02F;

   public PathfindingRenderer() {
      super();
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      var9.forEachEntity(DebugSubscriptions.ENTITY_PATHS, (var8, var9x) -> renderPath(var1, var2, var3, var5, var7, var9x.path(), var9x.maxNodeDistance()));
   }

   private static void renderPath(PoseStack var0, MultiBufferSource var1, double var2, double var4, double var6, Path var8, float var9) {
      renderPath(var0, var1, var8, var9, true, true, var2, var4, var6);
   }

   public static void renderPath(PoseStack var0, MultiBufferSource var1, Path var2, float var3, boolean var4, boolean var5, double var6, double var8, double var10) {
      renderPathLine(var0, var1.getBuffer(RenderType.debugLineStrip(6.0)), var2, var6, var8, var10);
      BlockPos var12 = var2.getTarget();
      if (distanceToCamera(var12, var6, var8, var10) <= 80.0F) {
         DebugRenderer.renderFilledBox(var0, var1, (new AABB((double)((float)var12.getX() + 0.25F), (double)((float)var12.getY() + 0.25F), (double)var12.getZ() + 0.25, (double)((float)var12.getX() + 0.75F), (double)((float)var12.getY() + 0.75F), (double)((float)var12.getZ() + 0.75F))).move(-var6, -var8, -var10), 0.0F, 1.0F, 0.0F, 0.5F);

         for(int var13 = 0; var13 < var2.getNodeCount(); ++var13) {
            Node var14 = var2.getNode(var13);
            if (distanceToCamera(var14.asBlockPos(), var6, var8, var10) <= 80.0F) {
               float var15 = var13 == var2.getNextNodeIndex() ? 1.0F : 0.0F;
               float var16 = var13 == var2.getNextNodeIndex() ? 0.0F : 1.0F;
               DebugRenderer.renderFilledBox(var0, var1, (new AABB((double)((float)var14.x + 0.5F - var3), (double)((float)var14.y + 0.01F * (float)var13), (double)((float)var14.z + 0.5F - var3), (double)((float)var14.x + 0.5F + var3), (double)((float)var14.y + 0.25F + 0.01F * (float)var13), (double)((float)var14.z + 0.5F + var3))).move(-var6, -var8, -var10), var15, 0.0F, var16, 0.5F);
            }
         }
      }

      Path.DebugData var18 = var2.debugData();
      if (var4 && var18 != null) {
         for(Node var17 : var18.closedSet()) {
            if (distanceToCamera(var17.asBlockPos(), var6, var8, var10) <= 80.0F) {
               DebugRenderer.renderFilledBox(var0, var1, (new AABB((double)((float)var17.x + 0.5F - var3 / 2.0F), (double)((float)var17.y + 0.01F), (double)((float)var17.z + 0.5F - var3 / 2.0F), (double)((float)var17.x + 0.5F + var3 / 2.0F), (double)var17.y + 0.1, (double)((float)var17.z + 0.5F + var3 / 2.0F))).move(-var6, -var8, -var10), 1.0F, 0.8F, 0.8F, 0.5F);
            }
         }

         for(Node var27 : var18.openSet()) {
            if (distanceToCamera(var27.asBlockPos(), var6, var8, var10) <= 80.0F) {
               DebugRenderer.renderFilledBox(var0, var1, (new AABB((double)((float)var27.x + 0.5F - var3 / 2.0F), (double)((float)var27.y + 0.01F), (double)((float)var27.z + 0.5F - var3 / 2.0F), (double)((float)var27.x + 0.5F + var3 / 2.0F), (double)var27.y + 0.1, (double)((float)var27.z + 0.5F + var3 / 2.0F))).move(-var6, -var8, -var10), 0.8F, 1.0F, 1.0F, 0.5F);
            }
         }
      }

      if (var5) {
         for(int var21 = 0; var21 < var2.getNodeCount(); ++var21) {
            Node var24 = var2.getNode(var21);
            if (distanceToCamera(var24.asBlockPos(), var6, var8, var10) <= 80.0F) {
               DebugRenderer.renderFloatingText(var0, var1, String.valueOf(var24.type), (double)var24.x + 0.5, (double)var24.y + 0.75, (double)var24.z + 0.5, -1, 0.02F, true, 0.0F, true);
               DebugRenderer.renderFloatingText(var0, var1, String.format(Locale.ROOT, "%.2f", var24.costMalus), (double)var24.x + 0.5, (double)var24.y + 0.25, (double)var24.z + 0.5, -1, 0.02F, true, 0.0F, true);
            }
         }
      }

   }

   public static void renderPathLine(PoseStack var0, VertexConsumer var1, Path var2, double var3, double var5, double var7) {
      for(int var9 = 0; var9 < var2.getNodeCount(); ++var9) {
         Node var10 = var2.getNode(var9);
         if (!(distanceToCamera(var10.asBlockPos(), var3, var5, var7) > 80.0F)) {
            float var11 = (float)var9 / (float)var2.getNodeCount() * 0.33F;
            int var12 = var9 == 0 ? -16777216 : ARGB.opaque(Mth.hsvToRgb(var11, 0.9F, 0.9F));
            var1.addVertex(var0.last(), (float)((double)var10.x - var3 + 0.5), (float)((double)var10.y - var5 + 0.5), (float)((double)var10.z - var7 + 0.5)).setColor(var12);
         }
      }

   }

   private static float distanceToCamera(BlockPos var0, double var1, double var3, double var5) {
      return (float)(Math.abs((double)var0.getX() - var1) + Math.abs((double)var0.getY() - var3) + Math.abs((double)var0.getZ() - var5));
   }

   // $FF: synthetic method
   private static void lambda$render$0(DebugValueAccess var0, PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, Entity var9) {
      DebugPathInfo var10 = (DebugPathInfo)var0.getEntityValue(DebugSubscriptions.ENTITY_PATHS, var9);
      if (var10 != null) {
         renderPath(var1, var2, var3, var5, var7, var10.path(), var10.maxNodeDistance());
      }

   }
}
