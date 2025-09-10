package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      ClientLevel var10 = this.minecraft.level;
      BlockPos var11 = BlockPos.containing(var3, var5, var7);
      LongOpenHashSet var12 = new LongOpenHashSet();

      for(BlockPos var14 : BlockPos.betweenClosed(var11.offset(-10, -10, -10), var11.offset(10, 10, 10))) {
         int var15 = ((Level)var10).getBrightness(LightLayer.SKY, var14);
         float var16 = (float)(15 - var15) / 15.0F * 0.5F + 0.16F;
         int var17 = Mth.hsvToRgb(var16, 0.9F, 0.9F);
         long var18 = SectionPos.blockToSection(var14.asLong());
         if (var12.add(var18)) {
            DebugRenderer.renderFloatingText(var1, var2, ((Level)var10).getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of(var18)), (double)SectionPos.sectionToBlockCoord(SectionPos.x(var18), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.y(var18), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.z(var18), 8), -65536, 0.3F);
         }

         if (var15 != 15) {
            DebugRenderer.renderFloatingText(var1, var2, String.valueOf(var15), (double)var14.getX() + 0.5, (double)var14.getY() + 0.25, (double)var14.getZ() + 0.5, var17);
         }
      }

   }
}
