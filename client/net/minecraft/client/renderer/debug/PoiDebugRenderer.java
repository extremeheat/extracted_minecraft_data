package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.util.debug.DebugPoiInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

public class PoiDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST_FOR_POI_INFO = 30;
   private static final float TEXT_SCALE = 0.02F;
   private static final int ORANGE = -23296;
   private final BrainDebugRenderer brainRenderer;

   public PoiDebugRenderer(BrainDebugRenderer var1) {
      super();
      this.brainRenderer = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      BlockPos var10 = BlockPos.containing(var3, var5, var7);
      var9.forEachBlock(DebugSubscriptions.POIS, (var5x, var6) -> {
         if (var10.closerThan(var5x, 30.0)) {
            highlightPoi(var1, var2, var5x);
            this.renderPoiInfo(var1, var2, var6, var9);
         }

      });
      this.brainRenderer.getGhostPois(var9).forEach((var5x, var6) -> {
         if (var9.getBlockValue(DebugSubscriptions.POIS, var5x) == null) {
            if (var10.closerThan(var5x, 30.0)) {
               this.renderGhostPoi(var1, var2, var5x, var6);
            }

         }
      });
   }

   private static void highlightPoi(PoseStack var0, MultiBufferSource var1, BlockPos var2) {
      float var3 = 0.05F;
      DebugRenderer.renderFilledBox(var0, var1, var2, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostPoi(PoseStack var1, MultiBufferSource var2, BlockPos var3, List<String> var4) {
      float var5 = 0.05F;
      DebugRenderer.renderFilledBox(var1, var2, var3, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      DebugRenderer.renderTextOverBlock(var1, var2, var4.toString(), var3, 0, -256, 0.02F);
      DebugRenderer.renderTextOverBlock(var1, var2, "Ghost POI", var3, 1, -65536, 0.02F);
   }

   private void renderPoiInfo(PoseStack var1, MultiBufferSource var2, DebugPoiInfo var3, DebugValueAccess var4) {
      int var5 = 0;
      if (SharedConstants.DEBUG_BRAIN) {
         List var6 = this.getTicketHolderNames(var3, false, var4);
         if (var6.size() < 4) {
            renderTextOverPoi(var1, var2, "Owners: " + String.valueOf(var6), var3, var5, -256);
         } else {
            renderTextOverPoi(var1, var2, var6.size() + " ticket holders", var3, var5, -256);
         }

         ++var5;
         List var7 = this.getTicketHolderNames(var3, true, var4);
         if (var7.size() < 4) {
            renderTextOverPoi(var1, var2, "Candidates: " + String.valueOf(var7), var3, var5, -23296);
         } else {
            renderTextOverPoi(var1, var2, var7.size() + " potential owners", var3, var5, -23296);
         }

         ++var5;
      }

      renderTextOverPoi(var1, var2, "Free tickets: " + var3.freeTicketCount(), var3, var5, -256);
      ++var5;
      renderTextOverPoi(var1, var2, var3.poiType().getRegisteredName(), var3, var5, -1);
   }

   private static void renderTextOverPoi(PoseStack var0, MultiBufferSource var1, String var2, DebugPoiInfo var3, int var4, int var5) {
      DebugRenderer.renderTextOverBlock(var0, var1, var2, var3.pos(), var4, var5, 0.02F);
   }

   private List<String> getTicketHolderNames(DebugPoiInfo var1, boolean var2, DebugValueAccess var3) {
      ArrayList var4 = new ArrayList();
      var3.forEachEntity(DebugSubscriptions.BRAINS, (var3x, var4x) -> {
         boolean var5 = var2 ? var4x.hasPotentialPoi(var1.pos()) : var4x.hasPoi(var1.pos());
         if (var5) {
            var4.add(DebugEntityNameGenerator.getEntityName(var3x.getUUID()));
         }

      });
      return var4;
   }
}
