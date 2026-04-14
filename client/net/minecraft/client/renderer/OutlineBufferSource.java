package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import net.minecraft.client.renderer.rendertype.RenderType;

public class OutlineBufferSource implements MultiBufferSource, AutoCloseable {
   private final MultiBufferSource.BufferSource outlineBufferSource;
   private int outlineColor = -1;

   public OutlineBufferSource(final MultiBufferSource.BufferSource outlineBufferSource) {
      super();
      this.outlineBufferSource = outlineBufferSource;
   }

   public VertexConsumer getBuffer(final RenderType renderType) {
      if (renderType.isOutline()) {
         VertexConsumer delegate = this.outlineBufferSource.getBuffer(renderType);
         return new EntityOutlineGenerator(delegate, this.outlineColor);
      } else {
         Optional<RenderType> outline = renderType.outline();
         if (outline.isPresent()) {
            VertexConsumer delegate = this.outlineBufferSource.getBuffer((RenderType)outline.get());
            return new EntityOutlineGenerator(delegate, this.outlineColor);
         } else {
            throw new IllegalStateException("Can't render an outline for this rendertype!");
         }
      }
   }

   public void setColor(final int color) {
      this.outlineColor = color;
   }

   public void endOutlineBatch() {
      this.outlineBufferSource.uploadAndDraw();
   }

   public void endFrame() {
      this.outlineBufferSource.endFrame();
   }

   public void close() {
      this.outlineBufferSource.close();
   }

   private static record EntityOutlineGenerator(VertexConsumer delegate, int color) implements VertexConsumer {
      private EntityOutlineGenerator {
         super();
      }

      public VertexConsumer addVertex(final float x, final float y, final float z) {
         this.delegate.addVertex(x, y, z).setColor(this.color);
         return this;
      }

      public VertexConsumer setColor(final int r, final int g, final int b, final int a) {
         return this;
      }

      public VertexConsumer setColor(final int color) {
         return this;
      }

      public VertexConsumer setUv(final float u, final float v) {
         this.delegate.setUv(u, v);
         return this;
      }

      public VertexConsumer setUv1(final int u, final int v) {
         return this;
      }

      public VertexConsumer setUv2(final int u, final int v) {
         return this;
      }

      public VertexConsumer setNormal(final float x, final float y, final float z) {
         return this;
      }

      public VertexConsumer setLineWidth(final float width) {
         return this;
      }
   }
}
