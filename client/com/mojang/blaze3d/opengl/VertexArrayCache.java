package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.VisibleForDebug;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBVertexAttribBinding;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GLCapabilities;

public abstract class VertexArrayCache {
   public VertexArrayCache() {
      super();
   }

   public static VertexArrayCache create(final GLCapabilities capabilities, final GlDebugLabel debugLabels, final Set<String> enabledExtensions) {
      if (capabilities.GL_ARB_vertex_attrib_binding && GlDevice.USE_GL_ARB_vertex_attrib_binding) {
         enabledExtensions.add("GL_ARB_vertex_attrib_binding");
         return new Separate(debugLabels);
      } else {
         return new Emulated(debugLabels);
      }
   }

   public abstract VertexArray bindVertexArray(final @Nullable VertexFormat[] vertexBindings, final @Nullable GpuBufferSlice[] vertexBuffers, final @Nullable VertexArray lastBoundVertexArray);

   private static class Emulated extends VertexArrayCache {
      private final Map<List<@Nullable VertexFormat>, VertexArray> cache = new HashMap();
      private final GlDebugLabel debugLabels;

      public Emulated(final GlDebugLabel debugLabels) {
         super();
         this.debugLabels = debugLabels;
      }

      public VertexArray bindVertexArray(final VertexFormat[] vertexBindings, final GpuBufferSlice[] vertexBuffers, final VertexArray lastBoundVertexArray) {
         List<VertexFormat> listBindings = Arrays.asList(vertexBindings);
         VertexArray vertexArray = (VertexArray)this.cache.get(listBindings);
         if (vertexArray == null) {
            int id = GlStateManager._glGenVertexArrays();
            GlStateManager._glBindVertexArray(id);
            setupCombinedAttributes(vertexBindings, true, vertexBuffers);
            VertexArray vao = new VertexArray(id, vertexBindings);
            this.debugLabels.applyLabel(vao);
            this.cache.put(listBindings, vao);
            return vao;
         } else {
            GlStateManager._glBindVertexArray(vertexArray.id);
            if (vertexArray != lastBoundVertexArray) {
               setupCombinedAttributes(vertexBindings, false, vertexBuffers);
            }

            return vertexArray;
         }
      }

      private static void setupCombinedAttributes(final @Nullable VertexFormat[] vertexBindings, final boolean enable, final @Nullable GpuBufferSlice[] vertexBuffers) {
         int attributeIndex = 0;

         for(int i = 0; i < vertexBindings.length; ++i) {
            VertexFormat vertexBinding = vertexBindings[i];
            if (vertexBinding != null) {
               GlBuffer buffer = (GlBuffer)vertexBuffers[i].buffer();
               GlStateManager._glBindBuffer(34962, buffer.handle());
               int vertexSize = vertexBinding.getVertexSize();

               for(VertexFormatElement element : vertexBinding.getElements()) {
                  long totalOffset = vertexBuffers[i].offset() + (long)element.offset();
                  int glExternalId = GlConst.toGlExternalId(element.format());
                  int glType = GlConst.toGlType(element.format());
                  boolean isIntegerFormat = GlConst.isGlFormatInteger(glExternalId);
                  boolean isNormalizedFormat = GlConst.isFormatNormalized(element.format());
                  int channelCount = GlConst.glFormatChannelCount(glExternalId);
                  if (enable) {
                     GlStateManager._enableVertexAttribArray(attributeIndex);
                  }

                  if (isIntegerFormat) {
                     GlStateManager._vertexAttribIPointer(attributeIndex, channelCount, glType, vertexSize, totalOffset);
                  } else {
                     GlStateManager._vertexAttribPointer(attributeIndex, channelCount, glType, isNormalizedFormat, vertexSize, totalOffset);
                  }

                  GL33C.glVertexAttribDivisor(attributeIndex, vertexBinding.getStepRate());
                  ++attributeIndex;
               }
            }
         }

      }
   }

   private static class Separate extends VertexArrayCache {
      private final Map<List<@Nullable VertexFormat>, VertexArray> cache = new HashMap();
      private final GlDebugLabel debugLabels;
      private final boolean needsMesaWorkaround;

      public Separate(final GlDebugLabel debugLabels) {
         super();
         this.debugLabels = debugLabels;
         if ("Mesa".equals(GlStateManager._getString(7936))) {
            String version = GlStateManager._getString(7938);
            this.needsMesaWorkaround = version.contains("25.0.0") || version.contains("25.0.1") || version.contains("25.0.2");
         } else {
            this.needsMesaWorkaround = false;
         }

      }

      public VertexArray bindVertexArray(final VertexFormat[] vertexBindings, final GpuBufferSlice[] vertexBuffers, final VertexArray lastBoundVertexArray) {
         List<VertexFormat> listBindings = Arrays.asList(vertexBindings);
         VertexArray vertexArray = (VertexArray)this.cache.get(listBindings);
         if (vertexArray == null) {
            int id = GlStateManager._glGenVertexArrays();
            GlStateManager._glBindVertexArray(id);
            int attribLocation = 0;

            for(int i = 0; i < vertexBindings.length; ++i) {
               VertexFormat vertexBinding = vertexBindings[i];
               if (vertexBinding != null) {
                  for(VertexFormatElement element : vertexBinding.getElements()) {
                     if (element != null) {
                        GlStateManager._enableVertexAttribArray(attribLocation);
                        int glExternalId = GlConst.toGlExternalId(element.format());
                        int glType = GlConst.toGlType(element.format());
                        boolean isIntegerFormat = GlConst.isGlFormatInteger(glExternalId);
                        boolean isNormalizedFormat = GlConst.isFormatNormalized(element.format());
                        int channelCount = GlConst.glFormatChannelCount(glExternalId);
                        if (isIntegerFormat) {
                           ARBVertexAttribBinding.glVertexAttribIFormat(attribLocation, channelCount, glType, element.offset());
                        } else {
                           ARBVertexAttribBinding.glVertexAttribFormat(attribLocation, channelCount, glType, isNormalizedFormat, element.offset());
                        }

                        ARBVertexAttribBinding.glVertexAttribBinding(attribLocation, i);
                        ++attribLocation;
                     }
                  }

                  ARBVertexAttribBinding.glVertexBindingDivisor(i, vertexBinding.getStepRate());
               }
            }

            for(int i = 0; i < vertexBuffers.length; ++i) {
               GpuBufferSlice vertexBufferSlice = vertexBuffers[i];
               if (vertexBufferSlice != null) {
                  GlBuffer vertexBuffer = (GlBuffer)vertexBufferSlice.buffer();
                  ARBVertexAttribBinding.glBindVertexBuffer(i, vertexBuffer.handle(), vertexBufferSlice.offset(), vertexBindings[i].getVertexSize());
               }
            }

            VertexArray vao = new VertexArray(id, vertexBindings);
            this.debugLabels.applyLabel(vao);
            this.cache.put(listBindings, vao);
            return vao;
         } else {
            GlStateManager._glBindVertexArray(vertexArray.id);
            if (vertexArray != lastBoundVertexArray) {
               for(int i = 0; i < vertexBuffers.length; ++i) {
                  GpuBufferSlice vertexBufferSlice = vertexBuffers[i];
                  if (vertexBufferSlice != null) {
                     GlBuffer vertexBuffer = (GlBuffer)vertexBufferSlice.buffer();
                     if (this.needsMesaWorkaround) {
                        ARBVertexAttribBinding.glBindVertexBuffer(i, 0, 0L, 0);
                     }

                     ARBVertexAttribBinding.glBindVertexBuffer(i, vertexBuffer.handle(), vertexBufferSlice.offset(), vertexBindings[i].getVertexSize());
                  }
               }
            }

            return vertexArray;
         }
      }
   }

   public static class VertexArray {
      @VisibleForDebug
      final int id;
      @VisibleForDebug
      final String formatName;

      private VertexArray(final int id, final @Nullable VertexFormat[] vertexBindings) {
         super();
         this.id = id;
         this.formatName = (String)Arrays.stream(vertexBindings).filter(Objects::nonNull).map(VertexFormat::toString).collect(Collectors.joining(", "));
      }
   }
}
