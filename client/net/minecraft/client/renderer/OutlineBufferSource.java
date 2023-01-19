package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import java.util.Optional;

public class OutlineBufferSource implements MultiBufferSource {
   private final MultiBufferSource.BufferSource bufferSource;
   private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
   private int teamR = 255;
   private int teamG = 255;
   private int teamB = 255;
   private int teamA = 255;

   public OutlineBufferSource(MultiBufferSource.BufferSource var1) {
      super();
      this.bufferSource = var1;
   }

   @Override
   public VertexConsumer getBuffer(RenderType var1) {
      if (var1.isOutline()) {
         VertexConsumer var6 = this.outlineBufferSource.getBuffer(var1);
         return new OutlineBufferSource.EntityOutlineGenerator(var6, this.teamR, this.teamG, this.teamB, this.teamA);
      } else {
         VertexConsumer var2 = this.bufferSource.getBuffer(var1);
         Optional var3 = var1.outline();
         if (var3.isPresent()) {
            VertexConsumer var4 = this.outlineBufferSource.getBuffer((RenderType)var3.get());
            OutlineBufferSource.EntityOutlineGenerator var5 = new OutlineBufferSource.EntityOutlineGenerator(
               var4, this.teamR, this.teamG, this.teamB, this.teamA
            );
            return VertexMultiConsumer.create(var5, var2);
         } else {
            return var2;
         }
      }
   }

   public void setColor(int var1, int var2, int var3, int var4) {
      this.teamR = var1;
      this.teamG = var2;
      this.teamB = var3;
      this.teamA = var4;
   }

   public void endOutlineBatch() {
      this.outlineBufferSource.endBatch();
   }

   static class EntityOutlineGenerator extends DefaultedVertexConsumer {
      private final VertexConsumer delegate;
      private double x;
      private double y;
      private double z;
      private float u;
      private float v;

      EntityOutlineGenerator(VertexConsumer var1, int var2, int var3, int var4, int var5) {
         super();
         this.delegate = var1;
         super.defaultColor(var2, var3, var4, var5);
      }

      @Override
      public void defaultColor(int var1, int var2, int var3, int var4) {
      }

      @Override
      public void unsetDefaultColor() {
      }

      @Override
      public VertexConsumer vertex(double var1, double var3, double var5) {
         this.x = var1;
         this.y = var3;
         this.z = var5;
         return this;
      }

      @Override
      public VertexConsumer color(int var1, int var2, int var3, int var4) {
         return this;
      }

      @Override
      public VertexConsumer uv(float var1, float var2) {
         this.u = var1;
         this.v = var2;
         return this;
      }

      @Override
      public VertexConsumer overlayCoords(int var1, int var2) {
         return this;
      }

      @Override
      public VertexConsumer uv2(int var1, int var2) {
         return this;
      }

      @Override
      public VertexConsumer normal(float var1, float var2, float var3) {
         return this;
      }

      @Override
      public void vertex(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         int var10,
         int var11,
         float var12,
         float var13,
         float var14
      ) {
         this.delegate
            .vertex((double)var1, (double)var2, (double)var3)
            .color(this.defaultR, this.defaultG, this.defaultB, this.defaultA)
            .uv(var8, var9)
            .endVertex();
      }

      @Override
      public void endVertex() {
         this.delegate.vertex(this.x, this.y, this.z).color(this.defaultR, this.defaultG, this.defaultB, this.defaultA).uv(this.u, this.v).endVertex();
      }
   }
}
