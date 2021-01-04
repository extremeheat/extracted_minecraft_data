package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Iterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;

public class HeightMapRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public HeightMapRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(long var1) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      MultiPlayerLevel var4 = this.minecraft.level;
      double var5 = var3.getPosition().x;
      double var7 = var3.getPosition().y;
      double var9 = var3.getPosition().z;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      BlockPos var11 = new BlockPos(var3.getPosition().x, 0.0D, var3.getPosition().z);
      Tesselator var12 = Tesselator.getInstance();
      BufferBuilder var13 = var12.getBuilder();
      var13.begin(5, DefaultVertexFormat.POSITION_COLOR);
      Iterator var14 = BlockPos.betweenClosed(var11.offset(-40, 0, -40), var11.offset(40, 0, 40)).iterator();

      while(var14.hasNext()) {
         BlockPos var15 = (BlockPos)var14.next();
         int var16 = var4.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var15.getX(), var15.getZ());
         if (var4.getBlockState(var15.offset(0, var16, 0).below()).isAir()) {
            LevelRenderer.addChainedFilledBoxVertices(var13, (double)((float)var15.getX() + 0.25F) - var5, (double)var16 - var7, (double)((float)var15.getZ() + 0.25F) - var9, (double)((float)var15.getX() + 0.75F) - var5, (double)var16 + 0.09375D - var7, (double)((float)var15.getZ() + 0.75F) - var9, 0.0F, 0.0F, 1.0F, 0.5F);
         } else {
            LevelRenderer.addChainedFilledBoxVertices(var13, (double)((float)var15.getX() + 0.25F) - var5, (double)var16 - var7, (double)((float)var15.getZ() + 0.25F) - var9, (double)((float)var15.getX() + 0.75F) - var5, (double)var16 + 0.09375D - var7, (double)((float)var15.getZ() + 0.75F) - var9, 0.0F, 1.0F, 0.0F, 0.5F);
         }
      }

      var12.end();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }
}
