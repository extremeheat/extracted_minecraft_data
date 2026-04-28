package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Mth;

public class VertexFormat {
   public static final int UNKNOWN_ELEMENT = -1;
   private static final int VERTEX_ALIGNMENT = 4;
   private final List<VertexFormatElement> elements;
   private final List<String> names;
   private final int vertexSize;
   private final int elementsMask;
   private final int[] offsetsByElement = new int[32];

   private VertexFormat(final List<VertexFormatElement> elements, final List<String> names, final IntList offsets, final int vertexSize) {
      super();
      this.elements = elements;
      this.names = names;
      this.vertexSize = vertexSize;
      this.elementsMask = elements.stream().mapToInt(VertexFormatElement::mask).reduce(0, (left, right) -> left | right);

      for(int id = 0; id < this.offsetsByElement.length; ++id) {
         VertexFormatElement element = VertexFormatElement.byId(id);
         int index = element != null ? elements.indexOf(element) : -1;
         this.offsetsByElement[id] = index != -1 ? offsets.getInt(index) : -1;
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

   public int getOffset(final VertexFormatElement element) {
      return this.offsetsByElement[element.id()];
   }

   public boolean contains(final VertexFormatElement element) {
      return (this.elementsMask & element.mask()) != 0;
   }

   public int getElementsMask() {
      return this.elementsMask;
   }

   public String getElementName(final VertexFormatElement element) {
      int index = this.elements.indexOf(element);
      if (index == -1) {
         throw new IllegalArgumentException(String.valueOf(element) + " is not contained in format");
      } else {
         return (String)this.names.get(index);
      }
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof VertexFormat) {
            VertexFormat format = (VertexFormat)o;
            if (this.elementsMask == format.elementsMask && this.vertexSize == format.vertexSize && this.names.equals(format.names) && Arrays.equals(this.offsetsByElement, format.offsetsByElement)) {
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

   public static class Builder {
      private final ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
      private final IntList offsets = new IntArrayList();
      private int offset;

      private Builder() {
         super();
      }

      public Builder add(final String name, final VertexFormatElement element) {
         if (!Mth.isMultipleOf(this.offset, element.format().byteAlignment())) {
            throw new IllegalArgumentException(name + " is not aligned to " + element.format().byteAlignment() + " as required by " + String.valueOf(element.format()));
         } else {
            this.elements.put(name, element);
            this.offsets.add(this.offset);
            this.offset += element.byteSize();
            return this;
         }
      }

      public Builder padding(final int bytes) {
         this.offset += bytes;
         return this;
      }

      public VertexFormat build() {
         ImmutableMap<String, VertexFormatElement> elementMap = this.elements.buildOrThrow();
         ImmutableList<VertexFormatElement> elements = elementMap.values().asList();
         ImmutableList<String> names = elementMap.keySet().asList();
         int vertexSize = this.offset;
         if (!Mth.isMultipleOf(vertexSize, 4)) {
            throw new IllegalStateException("Vertex size must be a multiple of 4, was " + vertexSize);
         } else {
            return new VertexFormat(elements, names, this.offsets, vertexSize);
         }
      }
   }

   public static enum IndexType {
      SHORT(2),
      INT(4);

      public final int bytes;

      private IndexType(final int bytes) {
         this.bytes = bytes;
      }

      public static IndexType least(final int length) {
         return (length & -65536) != 0 ? INT : SHORT;
      }

      // $FF: synthetic method
      private static IndexType[] $values() {
         return new IndexType[]{SHORT, INT};
      }
   }

   public static enum Mode {
      LINES(2, 2, false),
      DEBUG_LINES(2, 2, false),
      DEBUG_LINE_STRIP(2, 1, true),
      POINTS(1, 1, false),
      TRIANGLES(3, 3, false),
      TRIANGLE_STRIP(3, 1, true),
      TRIANGLE_FAN(3, 1, true),
      QUADS(4, 4, false);

      public final int primitiveLength;
      public final int primitiveStride;
      public final boolean connectedPrimitives;

      private Mode(final int primitiveLength, final int primitiveStride, final boolean connectedPrimitives) {
         this.primitiveLength = primitiveLength;
         this.primitiveStride = primitiveStride;
         this.connectedPrimitives = connectedPrimitives;
      }

      public int indexCount(final int vertexCount) {
         int var10000;
         switch (this.ordinal()) {
            case 0:
            case 7:
               var10000 = vertexCount / 4 * 6;
               break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
               var10000 = vertexCount;
               break;
            default:
               var10000 = 0;
         }

         int indexCount = var10000;
         return indexCount;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{LINES, DEBUG_LINES, DEBUG_LINE_STRIP, POINTS, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS};
      }
   }
}
