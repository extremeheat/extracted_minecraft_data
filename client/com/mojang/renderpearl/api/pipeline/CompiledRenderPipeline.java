package com.mojang.renderpearl.api.pipeline;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.util.List;
import org.jspecify.annotations.Nullable;

public interface CompiledRenderPipeline extends UncheckedAutoCloseable {
   boolean isClosed();

   @FunctionalInterface
   public interface Pending {
      Pending NULL = () -> null;

      @Nullable CompiledRenderPipeline finishCompile();
   }

   public static record CreateInfo(String name, List<SpvModule> shaders, List<VertexBuffer> vertexBuffers, List<AttribBinding> attribBindings, List<Uniform> uniforms, int pushConstantsSize, @Nullable DepthStencilState depthStencilState, GpuFormat depthStencilFormat, PolygonMode polygonMode, boolean cull, List<@Nullable ColorTargetState> colorTargetStates, PrimitiveTopology primitiveTopology) {
      public static int MIN_VERTEX_STRIDE_ALIGNMENT = 4;
      public static int MAX_VERTEX_BUFFERS = 8;
      public static int MAX_VERTEX_ATTRIBS = 16;

      public CreateInfo {
         super();
      }

      public static record VertexBuffer(int bufferSlot, int stride, int stepRate) {
         public VertexBuffer {
            super();
         }
      }

      public static record AttribBinding(int bufferSlot, int location, int offset, GpuFormat format) {
         public AttribBinding {
            super();
         }
      }

      public static record Uniform(String name, int binding, UniformType type, @Nullable GpuFormat gpuFormat) {
         public Uniform {
            super();
         }
      }
   }
}
