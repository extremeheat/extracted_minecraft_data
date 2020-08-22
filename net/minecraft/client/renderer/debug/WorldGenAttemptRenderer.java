package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

public class WorldGenAttemptRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final List toRender = Lists.newArrayList();
   private final List scales = Lists.newArrayList();
   private final List alphas = Lists.newArrayList();
   private final List reds = Lists.newArrayList();
   private final List greens = Lists.newArrayList();
   private final List blues = Lists.newArrayList();

   public void addPos(BlockPos var1, float var2, float var3, float var4, float var5, float var6) {
      this.toRender.add(var1);
      this.scales.add(var2);
      this.alphas.add(var6);
      this.reds.add(var3);
      this.greens.add(var4);
      this.blues.add(var5);
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      Tesselator var9 = Tesselator.getInstance();
      BufferBuilder var10 = var9.getBuilder();
      var10.begin(5, DefaultVertexFormat.POSITION_COLOR);

      for(int var11 = 0; var11 < this.toRender.size(); ++var11) {
         BlockPos var12 = (BlockPos)this.toRender.get(var11);
         Float var13 = (Float)this.scales.get(var11);
         float var14 = var13 / 2.0F;
         LevelRenderer.addChainedFilledBoxVertices(var10, (double)((float)var12.getX() + 0.5F - var14) - var3, (double)((float)var12.getY() + 0.5F - var14) - var5, (double)((float)var12.getZ() + 0.5F - var14) - var7, (double)((float)var12.getX() + 0.5F + var14) - var3, (double)((float)var12.getY() + 0.5F + var14) - var5, (double)((float)var12.getZ() + 0.5F + var14) - var7, (Float)this.reds.get(var11), (Float)this.greens.get(var11), (Float)this.blues.get(var11), (Float)this.alphas.get(var11));
      }

      var9.end();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
