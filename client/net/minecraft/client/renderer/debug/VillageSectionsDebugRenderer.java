package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.SectionPos;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

public class VillageSectionsDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   public VillageSectionsDebugRenderer() {
      super();
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      var9.forEachBlock(DebugSubscriptions.VILLAGE_SECTIONS, (var2x, var3x) -> {
         SectionPos var4 = SectionPos.of(var2x);
         DebugRenderer.renderFilledUnitCube(var1, var2, var4.center(), 0.2F, 1.0F, 0.2F, 0.15F);
      });
   }
}
