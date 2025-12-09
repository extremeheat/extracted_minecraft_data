package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import net.minecraft.client.renderer.rendertype.RenderType;

public class OutlineBufferSource implements MultiBufferSource {
   private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
   private int outlineColor = -1;

   public OutlineBufferSource() {
      super();
   }

   public VertexConsumer getBuffer(RenderType var1) {
      if (var1.isOutline()) {
         VertexConsumer var4 = this.outlineBufferSource.getBuffer(var1);
         return new EntityOutlineGenerator(var4, this.outlineColor);
      } else {
         Optional var2 = var1.outline();
         if (var2.isPresent()) {
            VertexConsumer var3 = this.outlineBufferSource.getBuffer((RenderType)var2.get());
            return new EntityOutlineGenerator(var3, this.outlineColor);
         } else {
            throw new IllegalStateException("Can't render an outline for this rendertype!");
         }
      }
   }

   public void setColor(int var1) {
      this.outlineColor = var1;
   }

   public void endOutlineBatch() {
      this.outlineBufferSource.endBatch();
   }

   static record EntityOutlineGenerator(VertexConsumer delegate, int color) implements VertexConsumer {
      EntityOutlineGenerator(VertexConsumer var1, int var2) {
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

      public VertexConsumer setColor(int var1) {
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

      public VertexConsumer setLineWidth(float var1) {
         return this;
      }
   }
}
