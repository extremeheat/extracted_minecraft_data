package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class RaidDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private Collection<BlockPos> raidCenters = Lists.newArrayList();

   public RaidDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void setRaidCenters(Collection<BlockPos> var1) {
      this.raidCenters = var1;
   }

   public void render(long var1) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      this.doRender();
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   private void doRender() {
      BlockPos var1 = this.getCamera().getBlockPosition();
      Iterator var2 = this.raidCenters.iterator();

      while(var2.hasNext()) {
         BlockPos var3 = (BlockPos)var2.next();
         if (var1.closerThan(var3, 160.0D)) {
            highlightRaidCenter(var3);
         }
      }

   }

   private static void highlightRaidCenter(BlockPos var0) {
      DebugRenderer.renderFilledBox(var0.offset(-0.5D, -0.5D, -0.5D), var0.offset(1.5D, 1.5D, 1.5D), 1.0F, 0.0F, 0.0F, 0.15F);
      int var1 = -65536;
      renderTextOverBlock("Raid center", var0, -65536);
   }

   private static void renderTextOverBlock(String var0, BlockPos var1, int var2) {
      double var3 = (double)var1.getX() + 0.5D;
      double var5 = (double)var1.getY() + 1.3D;
      double var7 = (double)var1.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(var0, var3, var5, var7, var2, 0.04F, true, 0.0F, true);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }
}
