package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public record ShaderProgram(ResourceLocation configId, VertexFormat vertexFormat, ShaderDefines defines) {
   public ShaderProgram(ResourceLocation var1, VertexFormat var2, ShaderDefines var3) {
      super();
      this.configId = var1;
      this.vertexFormat = var2;
      this.defines = var3;
   }

   public String toString() {
      String var10000 = String.valueOf(this.configId);
      String var1 = var10000 + " (" + String.valueOf(this.vertexFormat) + ")";
      return !this.defines.isEmpty() ? var1 + " with " + String.valueOf(this.defines) : var1;
   }
}
