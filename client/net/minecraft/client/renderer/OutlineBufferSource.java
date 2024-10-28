package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import java.util.Optional;
import net.minecraft.util.ARGB;

public class OutlineBufferSource implements MultiBufferSource {
   private final MultiBufferSource.BufferSource bufferSource;
   private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
   private int teamR = 255;
   private int teamG = 255;
   private int teamB = 255;
   private int teamA = 255;

   public OutlineBufferSource(MultiBufferSource.BufferSource var1) {
      super();
      this.bufferSource = var1;
   }

   public VertexConsumer getBuffer(RenderType var1) {
      VertexConsumer var2;
      if (var1.isOutline()) {
         var2 = this.outlineBufferSource.getBuffer(var1);
         return new EntityOutlineGenerator(var2, this.teamR, this.teamG, this.teamB, this.teamA);
      } else {
         var2 = this.bufferSource.getBuffer(var1);
         Optional var3 = var1.outline();
         if (var3.isPresent()) {
            VertexConsumer var4 = this.outlineBufferSource.getBuffer((RenderType)var3.get());
            EntityOutlineGenerator var5 = new EntityOutlineGenerator(var4, this.teamR, this.teamG, this.teamB, this.teamA);
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

   static record EntityOutlineGenerator(VertexConsumer delegate, int color) implements VertexConsumer {
      public EntityOutlineGenerator(VertexConsumer var1, int var2, int var3, int var4, int var5) {
         this(var1, ARGB.color(var5, var2, var3, var4));
      }

      private EntityOutlineGenerator(VertexConsumer var1, int var2) {
         super();
         this.delegate = var1;
         this.color = var2;
      }

      public VertexConsumer addVertex(float var1, float var2, float var3) {
         this.delegate.addVertex(var1, var2, var3).setColor(this.color);
         return this;
      }

      public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
         return this;
      }

      public VertexConsumer setUv(float var1, float var2) {
         this.delegate.setUv(var1, var2);
         return this;
      }

      public VertexConsumer setUv1(int var1, int var2) {
         return this;
      }

      public VertexConsumer setUv2(int var1, int var2) {
         return this;
      }

      public VertexConsumer setNormal(float var1, float var2, float var3) {
         return this;
      }

      public VertexConsumer delegate() {
         return this.delegate;
      }

      public int color() {
         return this.color;
      }
   }
}
