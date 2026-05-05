package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.GpuFormat;
import java.util.Locale;

public record VertexFormatElement(String name, int offset, GpuFormat format) {
   public VertexFormatElement {
      super();
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s %s offset:%d", this.name, this.format, this.offset);
   }
}
