package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Iterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;

public class LightDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public LightDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(long var1) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      MultiPlayerLevel var4 = this.minecraft.level;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      BlockPos var5 = new BlockPos(var3.getPosition());
      LongOpenHashSet var6 = new LongOpenHashSet();
      Iterator var7 = BlockPos.betweenClosed(var5.offset(-10, -10, -10), var5.offset(10, 10, 10)).iterator();

      while(var7.hasNext()) {
         BlockPos var8 = (BlockPos)var7.next();
         int var9 = var4.getBrightness(LightLayer.SKY, var8);
         float var10 = (float)(15 - var9) / 15.0F * 0.5F + 0.16F;
         int var11 = Mth.hsvToRgb(var10, 0.9F, 0.9F);
         long var12 = SectionPos.blockToSection(var8.asLong());
         if (var6.add(var12)) {
            DebugRenderer.renderFloatingText(var4.getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of(var12)), (double)(SectionPos.x(var12) * 16 + 8), (double)(SectionPos.y(var12) * 16 + 8), (double)(SectionPos.z(var12) * 16 + 8), 16711680, 0.3F);
         }

         if (var9 != 15) {
            DebugRenderer.renderFloatingText(String.valueOf(var9), (double)var8.getX() + 0.5D, (double)var8.getY() + 0.25D, (double)var8.getZ() + 0.5D, var11);
         }
      }

      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }
}
