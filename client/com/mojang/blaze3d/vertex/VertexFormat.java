package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.stream.Collectors;

public class VertexFormat {
   private final ImmutableList<VertexFormatElement> elements;
   private final IntList offsets = new IntArrayList();
   private final int vertexSize;

   public VertexFormat(ImmutableList<VertexFormatElement> var1) {
      super();
      this.elements = var1;
      int var2 = 0;

      VertexFormatElement var4;
      for(UnmodifiableIterator var3 = var1.iterator(); var3.hasNext(); var2 += var4.getByteSize()) {
         var4 = (VertexFormatElement)var3.next();
         this.offsets.add(var2);
      }

      this.vertexSize = var2;
   }

   public String toString() {
      return "format: " + this.elements.size() + " elements: " + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(" "));
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

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VertexFormat var2 = (VertexFormat)var1;
         return this.vertexSize != var2.vertexSize ? false : this.elements.equals(var2.elements);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.elements.hashCode();
   }

   public void setupBufferState(long var1) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.setupBufferState(var1);
         });
      } else {
         int var3 = this.getVertexSize();
         ImmutableList var4 = this.getElements();

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            ((VertexFormatElement)var4.get(var5)).setupBufferState(var1 + (long)this.offsets.getInt(var5), var3);
         }

      }
   }

   public void clearBufferState() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::clearBufferState);
      } else {
         UnmodifiableIterator var1 = this.getElements().iterator();

         while(var1.hasNext()) {
            VertexFormatElement var2 = (VertexFormatElement)var1.next();
            var2.clearBufferState();
         }

      }
   }
}
