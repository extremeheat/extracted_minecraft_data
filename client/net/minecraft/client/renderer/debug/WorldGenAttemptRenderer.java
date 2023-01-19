package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

public class WorldGenAttemptRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final List<BlockPos> toRender = Lists.newArrayList();
   private final List<Float> scales = Lists.newArrayList();
   private final List<Float> alphas = Lists.newArrayList();
   private final List<Float> reds = Lists.newArrayList();
   private final List<Float> greens = Lists.newArrayList();
   private final List<Float> blues = Lists.newArrayList();

   public WorldGenAttemptRenderer() {
      super();
   }

   public void addPos(BlockPos var1, float var2, float var3, float var4, float var5, float var6) {
      this.toRender.add(var1);
      this.scales.add(var2);
      this.alphas.add(var6);
      this.reds.add(var3);
      this.greens.add(var4);
      this.blues.add(var5);
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      Tesselator var9 = Tesselator.getInstance();
      BufferBuilder var10 = var9.getBuilder();
      var10.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

      for(int var11 = 0; var11 < this.toRender.size(); ++var11) {
         BlockPos var12 = this.toRender.get(var11);
         Float var13 = this.scales.get(var11);
         float var14 = var13 / 2.0F;
         LevelRenderer.addChainedFilledBoxVertices(
            var10,
            (double)((float)var12.getX() + 0.5F - var14) - var3,
            (double)((float)var12.getY() + 0.5F - var14) - var5,
            (double)((float)var12.getZ() + 0.5F - var14) - var7,
            (double)((float)var12.getX() + 0.5F + var14) - var3,
            (double)((float)var12.getY() + 0.5F + var14) - var5,
            (double)((float)var12.getZ() + 0.5F + var14) - var7,
            this.reds.get(var11),
            this.greens.get(var11),
            this.blues.get(var11),
            this.alphas.get(var11)
         );
      }

      var9.end();
   }
}
