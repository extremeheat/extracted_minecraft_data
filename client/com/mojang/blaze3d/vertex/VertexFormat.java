package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

public class VertexFormat {
   public static final int UNKNOWN_ELEMENT = -1;
   private final List<VertexFormatElement> elements;
   private final List<String> names;
   private final int vertexSize;
   private final int elementsMask;
   private final int[] offsetsByElement = new int[32];
   @Nullable
   private VertexBuffer immediateDrawVertexBuffer;

   VertexFormat(List<VertexFormatElement> var1, List<String> var2, IntList var3, int var4) {
      super();
      this.elements = var1;
      this.names = var2;
      this.vertexSize = var4;
      this.elementsMask = var1.stream().mapToInt(VertexFormatElement::mask).reduce(0, (var0, var1x) -> var0 | var1x);

      for (int var5 = 0; var5 < this.offsetsByElement.length; var5++) {
         VertexFormatElement var6 = VertexFormatElement.byId(var5);
         int var7 = var6 != null ? var1.indexOf(var6) : -1;
         this.offsetsByElement[var5] = var7 != -1 ? var3.getInt(var7) : -1;
      }
   }

   public static VertexFormat.Builder builder() {
      return new VertexFormat.Builder();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("Vertex format (").append(this.vertexSize).append(" bytes):\n");

      for (int var2 = 0; var2 < this.elements.size(); var2++) {
         VertexFormatElement var3 = this.elements.get(var2);
         var1.append(var2).append(". ").append(this.names.get(var2)).append(": ").append(var3).append(" @ ").append(this.getOffset(var3)).append('\n');
      }

      return var1.toString();
   }

   public int getVertexSize() {
      return this.vertexSize;
   }

   public List<VertexFormatElement> getElements() {
      return this.elements;
   }

   public List<String> getElementAttributeNames() {
      return this.names;
   }

   public int[] getOffsetsByElement() {
      return this.offsetsByElement;
   }

   public int getOffset(VertexFormatElement var1) {
      return this.offsetsByElement[var1.id()];
   }

   public boolean contains(VertexFormatElement var1) {
      return (this.elementsMask & var1.mask()) != 0;
   }

   public int getElementsMask() {
      return this.elementsMask;
   }

   public String getElementName(VertexFormatElement var1) {
      int var2 = this.elements.indexOf(var1);
      if (var2 == -1) {
         throw new IllegalArgumentException(var1 + " is not contained in format");
      } else {
         return this.names.get(var2);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof VertexFormat var2
            && this.elementsMask == var2.elementsMask
            && this.vertexSize == var2.vertexSize
            && this.names.equals(var2.names)
            && Arrays.equals(this.offsetsByElement, var2.offsetsByElement)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.elementsMask * 31 + Arrays.hashCode(this.offsetsByElement);
   }

   public void setupBufferState() {
      RenderSystem.assertOnRenderThread();
      int var1 = this.getVertexSize();

      for (int var2 = 0; var2 < this.elements.size(); var2++) {
         GlStateManager._enableVertexAttribArray(var2);
         VertexFormatElement var3 = this.elements.get(var2);
         var3.setupBufferState(var2, (long)this.getOffset(var3), var1);
      }
   }

   public void clearBufferState() {
      RenderSystem.assertOnRenderThread();

      for (int var1 = 0; var1 < this.elements.size(); var1++) {
         GlStateManager._disableVertexAttribArray(var1);
      }
   }

   public VertexBuffer getImmediateDrawVertexBuffer() {
      VertexBuffer var1 = this.immediateDrawVertexBuffer;
      if (var1 == null) {
         this.immediateDrawVertexBuffer = var1 = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
      }

      return var1;
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
      private final IntList offsets = new IntArrayList();
      private int offset;

      Builder() {
         super();
      }

      public VertexFormat.Builder add(String var1, VertexFormatElement var2) {
         this.elements.put(var1, var2);
         this.offsets.add(this.offset);
         this.offset = this.offset + var2.byteSize();
         return this;
      }

      public VertexFormat.Builder padding(int var1) {
         this.offset += var1;
         return this;
      }

      public VertexFormat build() {
         ImmutableMap var1 = this.elements.buildOrThrow();
         ImmutableList var2 = var1.values().asList();
         ImmutableList var3 = var1.keySet().asList();
         return new VertexFormat(var2, var3, this.offsets, this.offset);
      }
   }

   public static enum IndexType {
      SHORT(5123, 2),
      INT(5125, 4);

      public final int asGLType;
      public final int bytes;

      private IndexType(final int nullxx, final int nullxxx) {
         this.asGLType = nullxx;
         this.bytes = nullxxx;
      }

      public static VertexFormat.IndexType least(int var0) {
         return (var0 & -65536) != 0 ? INT : SHORT;
      }
   }

   public static enum Mode {
      LINES(4, 2, 2, false),
      LINE_STRIP(5, 2, 1, true),
      DEBUG_LINES(1, 2, 2, false),
      DEBUG_LINE_STRIP(3, 2, 1, true),
      TRIANGLES(4, 3, 3, false),
      TRIANGLE_STRIP(5, 3, 1, true),
      TRIANGLE_FAN(6, 3, 1, true),
      QUADS(4, 4, 4, false);

      public final int asGLMode;
      public final int primitiveLength;
      public final int primitiveStride;
      public final boolean connectedPrimitives;

      private Mode(final int nullxx, final int nullxxx, final int nullxxxx, final boolean nullxxxxx) {
         this.asGLMode = nullxx;
         this.primitiveLength = nullxxx;
         this.primitiveStride = nullxxxx;
         this.connectedPrimitives = nullxxxxx;
      }

      public int indexCount(int var1) {
         return switch (this) {
            case LINES, QUADS -> var1 / 4 * 6;
            case LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN -> var1;
            default -> 0;
         };
      }
   }
}
