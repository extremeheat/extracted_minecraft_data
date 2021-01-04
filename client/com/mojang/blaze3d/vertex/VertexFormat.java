package com.mojang.blaze3d.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<VertexFormatElement> elements;
   private final List<Integer> offsets;
   private int vertexSize;
   private int colorOffset;
   private final List<Integer> texOffset;
   private int normalOffset;

   public VertexFormat(VertexFormat var1) {
      this();

      for(int var2 = 0; var2 < var1.getElementCount(); ++var2) {
         this.addElement(var1.getElement(var2));
      }

      this.vertexSize = var1.getVertexSize();
   }

   public VertexFormat() {
      super();
      this.elements = Lists.newArrayList();
      this.offsets = Lists.newArrayList();
      this.colorOffset = -1;
      this.texOffset = Lists.newArrayList();
      this.normalOffset = -1;
   }

   public void clear() {
      this.elements.clear();
      this.offsets.clear();
      this.colorOffset = -1;
      this.texOffset.clear();
      this.normalOffset = -1;
      this.vertexSize = 0;
   }

   public VertexFormat addElement(VertexFormatElement var1) {
      if (var1.isPosition() && this.hasPositionElement()) {
         LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      } else {
         this.elements.add(var1);
         this.offsets.add(this.vertexSize);
         switch(var1.getUsage()) {
         case NORMAL:
            this.normalOffset = this.vertexSize;
            break;
         case COLOR:
            this.colorOffset = this.vertexSize;
            break;
         case UV:
            this.texOffset.add(var1.getIndex(), this.vertexSize);
         }

         this.vertexSize += var1.getByteSize();
         return this;
      }
   }

   public boolean hasNormal() {
      return this.normalOffset >= 0;
   }

   public int getNormalOffset() {
      return this.normalOffset;
   }

   public boolean hasColor() {
      return this.colorOffset >= 0;
   }

   public int getColorOffset() {
      return this.colorOffset;
   }

   public boolean hasUv(int var1) {
      return this.texOffset.size() - 1 >= var1;
   }

   public int getUvOffset(int var1) {
      return (Integer)this.texOffset.get(var1);
   }

   public String toString() {
      String var1 = "format: " + this.elements.size() + " elements: ";

      for(int var2 = 0; var2 < this.elements.size(); ++var2) {
         var1 = var1 + ((VertexFormatElement)this.elements.get(var2)).toString();
         if (var2 != this.elements.size() - 1) {
            var1 = var1 + " ";
         }
      }

      return var1;
   }

   private boolean hasPositionElement() {
      int var1 = 0;

      for(int var2 = this.elements.size(); var1 < var2; ++var1) {
         VertexFormatElement var3 = (VertexFormatElement)this.elements.get(var1);
         if (var3.isPosition()) {
            return true;
         }
      }

      return false;
   }

   public int getIntegerSize() {
      return this.getVertexSize() / 4;
   }

   public int getVertexSize() {
      return this.vertexSize;
   }

   public List<VertexFormatElement> getElements() {
      return this.elements;
   }

   public int getElementCount() {
      return this.elements.size();
   }

   public VertexFormatElement getElement(int var1) {
      return (VertexFormatElement)this.elements.get(var1);
   }

   public int getOffset(int var1) {
      return (Integer)this.offsets.get(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VertexFormat var2 = (VertexFormat)var1;
         if (this.vertexSize != var2.vertexSize) {
            return false;
         } else {
            return !this.elements.equals(var2.elements) ? false : this.offsets.equals(var2.offsets);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.elements.hashCode();
      var1 = 31 * var1 + this.offsets.hashCode();
      var1 = 31 * var1 + this.vertexSize;
      return var1;
   }
}
