package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      VertexConsumer var9 = var2.getBuffer(RenderType.debugFilledBox());

      for(int var10 = 0; var10 < this.toRender.size(); ++var10) {
         BlockPos var11 = (BlockPos)this.toRender.get(var10);
         Float var12 = (Float)this.scales.get(var10);
         float var13 = var12 / 2.0F;
         LevelRenderer.addChainedFilledBoxVertices(var1, var9, (double)((float)var11.getX() + 0.5F - var13) - var3, (double)((float)var11.getY() + 0.5F - var13) - var5, (double)((float)var11.getZ() + 0.5F - var13) - var7, (double)((float)var11.getX() + 0.5F + var13) - var3, (double)((float)var11.getY() + 0.5F + var13) - var5, (double)((float)var11.getZ() + 0.5F + var13) - var7, (Float)this.reds.get(var10), (Float)this.greens.get(var10), (Float)this.blues.get(var10), (Float)this.alphas.get(var10));
      }

   }
}
