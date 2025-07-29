package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.GraphicsWorkarounds;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;

@DontObfuscate
public class VertexFormat {
   public static final int UNKNOWN_ELEMENT = -1;
   private final List<VertexFormatElement> elements;
   private final List<String> names;
   private final int vertexSize;
   private final int elementsMask;
   private final int[] offsetsByElement = new int[32];
   @Nullable
   private GpuBuffer immediateDrawVertexBuffer;
   @Nullable
   private GpuBuffer immediateDrawIndexBuffer;

   VertexFormat(List<VertexFormatElement> var1, List<String> var2, IntList var3, int var4) {
      super();
      this.elements = var1;
      this.names = var2;
      this.vertexSize = var4;
      this.elementsMask = var1.stream().mapToInt(VertexFormatElement::mask).reduce(0, (var0, var1x) -> var0 | var1x);

      for(int var5 = 0; var5 < this.offsetsByElement.length; ++var5) {
         VertexFormatElement var6 = VertexFormatElement.byId(var5);
         int var7 = var6 != null ? var1.indexOf(var6) : -1;
         this.offsetsByElement[var5] = var7 != -1 ? var3.getInt(var7) : -1;
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   public String toString() {
      return "VertexFormat" + String.valueOf(this.names);
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
         throw new IllegalArgumentException(String.valueOf(var1) + " is not contained in format");
      } else {
         return (String)this.names.get(var2);
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof VertexFormat) {
            VertexFormat var2 = (VertexFormat)var1;
            if (this.elementsMask == var2.elementsMask && this.vertexSize == var2.vertexSize && this.names.equals(var2.names) && Arrays.equals(this.offsetsByElement, var2.offsetsByElement)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.elementsMask * 31 + Arrays.hashCode(this.offsetsByElement);
   }

   private static GpuBuffer uploadToBuffer(@Nullable GpuBuffer var0, ByteBuffer var1, int var2, Supplier<String> var3) {
      GpuDevice var4 = RenderSystem.getDevice();
      if (GraphicsWorkarounds.get(var4).alwaysCreateFreshImmediateBuffer()) {
         if (var0 != null) {
            var0.close();
         }

         return var4.createBuffer(var3, var2, var1);
      } else {
         if (var0 == null) {
            var0 = var4.createBuffer(var3, var2, var1);
         } else {
            CommandEncoder var5 = var4.createCommandEncoder();
            if (var0.size() < var1.remaining()) {
               var0.close();
               var0 = var4.createBuffer(var3, var2, var1);
            } else {
               var5.writeToBuffer(var0.slice(), var1);
            }
         }

         return var0;
      }
   }

   public GpuBuffer uploadImmediateVertexBuffer(ByteBuffer var1) {
      this.immediateDrawVertexBuffer = uploadToBuffer(this.immediateDrawVertexBuffer, var1, 40, () -> "Immediate vertex buffer for " + String.valueOf(this));
      return this.immediateDrawVertexBuffer;
   }

   public GpuBuffer uploadImmediateIndexBuffer(ByteBuffer var1) {
      this.immediateDrawIndexBuffer = uploadToBuffer(this.immediateDrawIndexBuffer, var1, 72, () -> "Immediate index buffer for " + String.valueOf(this));
      return this.immediateDrawIndexBuffer;
   }

   @DontObfuscate
   public static class Builder {
      private final ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
      private final IntList offsets = new IntArrayList();
      private int offset;

      Builder() {
         super();
      }

      public Builder add(String var1, VertexFormatElement var2) {
         this.elements.put(var1, var2);
         this.offsets.add(this.offset);
         this.offset += var2.byteSize();
         return this;
      }

      public Builder padding(int var1) {
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
      SHORT(2),
      INT(4);

      public final int bytes;

      private IndexType(final int var3) {
         this.bytes = var3;
      }

      public static IndexType least(int var0) {
         return (var0 & -65536) != 0 ? INT : SHORT;
      }

      // $FF: synthetic method
      private static IndexType[] $values() {
         return new IndexType[]{SHORT, INT};
      }
   }

   public static enum Mode {
      LINES(2, 2, false),
      LINE_STRIP(2, 1, true),
      DEBUG_LINES(2, 2, false),
      DEBUG_LINE_STRIP(2, 1, true),
      TRIANGLES(3, 3, false),
      TRIANGLE_STRIP(3, 1, true),
      TRIANGLE_FAN(3, 1, true),
      QUADS(4, 4, false);

      public final int primitiveLength;
      public final int primitiveStride;
      public final boolean connectedPrimitives;

      private Mode(final int var3, final int var4, final boolean var5) {
         this.primitiveLength = var3;
         this.primitiveStride = var4;
         this.connectedPrimitives = var5;
      }

      public int indexCount(int var1) {
         int var2;
         switch (this.ordinal()) {
            case 0:
            case 7:
               var2 = var1 / 4 * 6;
               break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
               var2 = var1;
               break;
            default:
               var2 = 0;
         }

         return var2;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{LINES, LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS};
      }
   }
}
