package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class VertexFormat {
   private final ImmutableList<VertexFormatElement> elements;
   private final ImmutableMap<String, VertexFormatElement> elementMapping;
   private final IntList offsets = new IntArrayList();
   private final int vertexSize;
   @Nullable
   private VertexBuffer immediateDrawVertexBuffer;

   public VertexFormat(ImmutableMap<String, VertexFormatElement> var1) {
      super();
      this.elementMapping = var1;
      this.elements = var1.values().asList();
      int var2 = 0;

      VertexFormatElement var4;
      for(UnmodifiableIterator var3 = var1.values().iterator(); var3.hasNext(); var2 += var4.getByteSize()) {
         var4 = (VertexFormatElement)var3.next();
         this.offsets.add(var2);
      }

      this.vertexSize = var2;
   }

   @Override
   public String toString() {
      return "format: "
         + this.elementMapping.size()
         + " elements: "
         + (String)this.elementMapping.entrySet().stream().map(Object::toString).collect(Collectors.joining(" "));
   }

   public int getIntegerSize() {
      return this.getVertexSize() / 4;
   }

   public int getVertexSize() {
      return this.vertexSize;
   }

   public ImmutableList<VertexFormatElement> getElements() {
      return this.elements;
   }

   public ImmutableList<String> getElementAttributeNames() {
      return this.elementMapping.keySet().asList();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VertexFormat var2 = (VertexFormat)var1;
         return this.vertexSize != var2.vertexSize ? false : this.elementMapping.equals(var2.elementMapping);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.elementMapping.hashCode();
   }

   public void setupBufferState() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::_setupBufferState);
      } else {
         this._setupBufferState();
      }
   }

   private void _setupBufferState() {
      int var1 = this.getVertexSize();
      ImmutableList var2 = this.getElements();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ((VertexFormatElement)var2.get(var3)).setupBufferState(var3, (long)this.offsets.getInt(var3), var1);
      }
   }

   public void clearBufferState() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::_clearBufferState);
      } else {
         this._clearBufferState();
      }
   }

   private void _clearBufferState() {
      ImmutableList var1 = this.getElements();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         VertexFormatElement var3 = (VertexFormatElement)var1.get(var2);
         var3.clearBufferState(var2);
      }
   }

   public VertexBuffer getImmediateDrawVertexBuffer() {
      VertexBuffer var1 = this.immediateDrawVertexBuffer;
      if (var1 == null) {
         this.immediateDrawVertexBuffer = var1 = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
      }

      return var1;
   }

   public static enum IndexType {
      SHORT(5123, 2),
      INT(5125, 4);

      public final int asGLType;
      public final int bytes;

      private IndexType(int var3, int var4) {
         this.asGLType = var3;
         this.bytes = var4;
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

      private Mode(int var3, int var4, int var5, boolean var6) {
         this.asGLMode = var3;
         this.primitiveLength = var4;
         this.primitiveStride = var5;
         this.connectedPrimitives = var6;
      }

      public int indexCount(int var1) {
         return switch(this) {
            case LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN -> var1;
            case LINES, QUADS -> var1 / 4 * 6;
            default -> 0;
         };
      }
   }
}
