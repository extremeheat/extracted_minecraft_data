package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class LightDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private static final int MAX_RENDER_DIST = 10;

   public LightDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      ClientLevel var11 = this.minecraft.level;
      BlockPos var12 = BlockPos.containing(var3, var5, var7);
      LongOpenHashSet var13 = new LongOpenHashSet();

      for(BlockPos var15 : BlockPos.betweenClosed(var12.offset(-10, -10, -10), var12.offset(10, 10, 10))) {
         int var16 = ((Level)var11).getBrightness(LightLayer.SKY, var15);
         float var17 = (float)(15 - var16) / 15.0F * 0.5F + 0.16F;
         int var18 = Mth.hsvToRgb(var17, 0.9F, 0.9F);
         long var19 = SectionPos.blockToSection(var15.asLong());
         if (var13.add(var19)) {
            DebugRenderer.renderFloatingText(var1, var2, ((Level)var11).getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of(var19)), (double)SectionPos.sectionToBlockCoord(SectionPos.x(var19), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.y(var19), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.z(var19), 8), -65536, 0.3F);
         }

         if (var16 != 15) {
            DebugRenderer.renderFloatingText(var1, var2, String.valueOf(var16), (double)var15.getX() + 0.5, (double)var15.getY() + 0.25, (double)var15.getZ() + 0.5, var18);
         }
      }

   }
}
