package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

public class RaidDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST = 160;
   private static final float TEXT_SCALE = 0.04F;
   private final Minecraft minecraft;
   private Collection<BlockPos> raidCenters = Lists.newArrayList();

   public RaidDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void setRaidCenters(Collection<BlockPos> var1) {
      this.raidCenters = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      BlockPos var9 = this.getCamera().getBlockPosition();
      Iterator var10 = this.raidCenters.iterator();

      while(var10.hasNext()) {
         BlockPos var11 = (BlockPos)var10.next();
         if (var9.closerThan(var11, 160.0)) {
            highlightRaidCenter(var1, var2, var11);
         }
      }

   }

   private static void highlightRaidCenter(PoseStack var0, MultiBufferSource var1, BlockPos var2) {
      DebugRenderer.renderFilledUnitCube(var0, var1, var2, 1.0F, 0.0F, 0.0F, 0.15F);
      int var3 = -65536;
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
