package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RenderType {
   private static final int MEGABYTE = 1048576;
   public static final int BIG_BUFFER_SIZE = 4194304;
   public static final int SMALL_BUFFER_SIZE = 786432;
   public static final int TRANSIENT_BUFFER_SIZE = 1536;
   private final RenderSetup state;
   private final Optional<RenderType> outline;
   protected final String name;

   private RenderType(String var1, RenderSetup var2) {
      super();
      this.name = var1;
      this.state = var2;
      this.outline = var2.outlineProperty == RenderSetup.OutlineProperty.AFFECTS_OUTLINE ? var2.textures.values().stream().findFirst().map((var1x) -> (RenderType)RenderTypes.OUTLINE.apply(var1x, var2.pipeline.isCull())) : Optional.empty();
   }

   static RenderType create(String var0, RenderSetup var1) {
      return new RenderType(var0, var1);
   }

   public String toString() {
      String var10000 = this.name;
      return "RenderType[" + var10000 + ":" + String.valueOf(this.state) + "]";
   }

   public void draw(MeshData var1) {
      Matrix4fStack var2 = RenderSystem.getModelViewStack();
      Consumer var3 = this.state.layeringTransform.getModifier();
      if (var3 != null) {
         var2.pushMatrix();
         var3.accept(var2);
      }

      GpuBufferSlice var4 = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), this.state.textureTransform.getMatrix());
      Map var5 = this.state.getTextures();
      MeshData var6 = var1;

      try {
         GpuBuffer var7 = this.state.pipeline.getVertexFormat().uploadImmediateVertexBuffer(var1.vertexBuffer());
         GpuBuffer var8;
         VertexFormat.IndexType var9;
         if (var1.indexBuffer() == null) {
            RenderSystem.AutoStorageIndexBuffer var10 = RenderSystem.getSequentialBuffer(var1.drawState().mode());
            var8 = var10.getBuffer(var1.drawState().indexCount());
            var9 = var10.type();
         } else {
            var8 = this.state.pipeline.getVertexFormat().uploadImmediateIndexBuffer(var1.indexBuffer());
            var9 = var1.drawState().indexType();
         }

         RenderTarget var21 = this.state.outputTarget.getRenderTarget();
         GpuTextureView var11 = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : var21.getColorTextureView();
         GpuTextureView var12 = var21.useDepth ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : var21.getDepthTextureView()) : null;

         try (RenderPass var13 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Immediate draw for " + this.name, var11, OptionalInt.empty(), var12, OptionalDouble.empty())) {
            var13.setPipeline(this.state.pipeline);
            ScissorState var14 = RenderSystem.getScissorStateForRenderTypeDraws();
            if (var14.enabled()) {
               var13.enableScissor(var14.x(), var14.y(), var14.width(), var14.height());
            }

            RenderSystem.bindDefaultUniforms(var13);
            var13.setUniform("DynamicTransforms", var4);
            var13.setVertexBuffer(0, var7);
            if (this.state.useOverlay) {
               var13.bindTexture("Sampler1", Minecraft.getInstance().gameRenderer.overlayTexture().getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
            }

            if (this.state.useLightmap) {
               var13.bindTexture("Sampler2", Minecraft.getInstance().gameRenderer.lightTexture().getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
            }

            for(Map.Entry var16 : var5.entrySet()) {
               var13.bindTexture((String)var16.getKey(), ((AbstractTexture)var16.getValue()).getTextureView(), ((AbstractTexture)var16.getValue()).getSampler());
            }

            var13.setIndexBuffer(var8, var9);
            var13.drawIndexed(0, 0, var1.drawState().indexCount(), 1);
         }
      } catch (Throwable var20) {
         if (var1 != null) {
            try {
               var6.close();
            } catch (Throwable var17) {
               var20.addSuppressed(var17);
            }
         }

         throw var20;
      }

      if (var1 != null) {
         var1.close();
      }

      if (var3 != null) {
         var2.popMatrix();
      }

   }

   public int bufferSize() {
      return this.state.bufferSize;
   }

   public VertexFormat format() {
      return this.state.pipeline.getVertexFormat();
   }

   public VertexFormat.Mode mode() {
      return this.state.pipeline.getVertexFormatMode();
   }

   public Optional<RenderType> outline() {
      return this.outline;
   }

   public boolean isOutline() {
      return this.state.outlineProperty == RenderSetup.OutlineProperty.IS_OUTLINE;
   }

   public RenderPipeline pipeline() {
      return this.state.pipeline;
   }

   public boolean affectsCrumbling() {
      return this.state.affectsCrumbling;
   }

   public boolean canConsolidateConsecutiveGeometry() {
      return !this.mode().connectedPrimitives;
   }

   public boolean sortOnUpload() {
      return this.state.sortOnUpload;
   }
}
