package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;

public class HeightMapRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public HeightMapRenderer(Minecraft var1) {
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      ClientLevel var9 = this.minecraft.level;
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos var10 = new BlockPos(var3, 0.0D, var7);
      Tesselator var11 = Tesselator.getInstance();
      BufferBuilder var12 = var11.getBuilder();
      var12.begin(5, DefaultVertexFormat.POSITION_COLOR);
      Iterator var13 = BlockPos.betweenClosed(var10.offset(-40, 0, -40), var10.offset(40, 0, 40)).iterator();

      while(var13.hasNext()) {
         BlockPos var14 = (BlockPos)var13.next();
         int var15 = var9.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var14.getX(), var14.getZ());
         if (var9.getBlockState(var14.offset(0, var15, 0).below()).isAir()) {
            LevelRenderer.addChainedFilledBoxVertices(var12, (double)((float)var14.getX() + 0.25F) - var3, (double)var15 - var5, (double)((float)var14.getZ() + 0.25F) - var7, (double)((float)var14.getX() + 0.75F) - var3, (double)var15 + 0.09375D - var5, (double)((float)var14.getZ() + 0.75F) - var7, 0.0F, 0.0F, 1.0F, 0.5F);
         } else {
            LevelRenderer.addChainedFilledBoxVertices(var12, (double)((float)var14.getX() + 0.25F) - var3, (double)var15 - var5, (double)((float)var14.getZ() + 0.25F) - var7, (double)((float)var14.getX() + 0.75F) - var3, (double)var15 + 0.09375D - var5, (double)((float)var14.getZ() + 0.75F) - var7, 0.0F, 1.0F, 0.0F, 0.5F);
         }
      }

      var11.end();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
