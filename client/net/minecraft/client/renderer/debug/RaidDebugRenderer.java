package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

public class RaidDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST = 160;
   private static final float TEXT_SCALE = 0.04F;
   private final Minecraft minecraft;

   public RaidDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      BlockPos var10 = this.getCamera().getBlockPosition();
      var9.forEachChunk(DebugSubscriptions.RAIDS, (var3x, var4) -> {
         for(BlockPos var6 : var4) {
            if (var10.closerThan(var6, 160.0)) {
               highlightRaidCenter(var1, var2, var6);
            }
         }

      });
   }

   private static void highlightRaidCenter(PoseStack var0, MultiBufferSource var1, BlockPos var2) {
      DebugRenderer.renderFilledUnitCube(var0, var1, var2, 1.0F, 0.0F, 0.0F, 0.15F);
      renderTextOverBlock(var0, var1, "Raid center", var2, -65536);
   }

   private static void renderTextOverBlock(PoseStack var0, MultiBufferSource var1, String var2, BlockPos var3, int var4) {
      double var5 = (double)var3.getX() + 0.5;
      double var7 = (double)var3.getY() + 1.3;
      double var9 = (double)var3.getZ() + 0.5;
      DebugRenderer.renderFloatingText(var0, var1, var2, var5, var7, var9, var4, 0.04F, true, 0.0F, true);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }
}
