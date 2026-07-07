package com.mojang.renderpearl.api.pipeline;

import com.mojang.renderpearl.api.GpuFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public class BindGroupLayout {
   private final List<String> samplers;
   private final List<UniformDescription> uniforms;

   private BindGroupLayout(final List<String> samplers, final List<UniformDescription> uniforms) {
      super();
      this.samplers = samplers;
      this.uniforms = uniforms;
   }

   public static Builder builder() {
      return new Builder();
   }

   public List<String> getSamplers() {
      return this.samplers;
   }

   public List<UniformDescription> getUniforms() {
      return this.uniforms;
   }

   public static List<String> flattenSamplers(final List<BindGroupLayout> bindGroupLayouts) {
      List<String> flattened = new ArrayList();

      for(BindGroupLayout bindGroupLayout : bindGroupLayouts) {
         flattened.addAll(bindGroupLayout.getSamplers());
      }

      return flattened;
   }

   public static List<UniformDescription> flattenUniforms(final List<BindGroupLayout> bindGroupLayouts) {
      List<UniformDescription> flattened = new ArrayList();

      for(BindGroupLayout bindGroupLayout : bindGroupLayouts) {
         flattened.addAll(bindGroupLayout.getUniforms());
      }

      return flattened;
   }

   public static void ensureCompatible(final List<BindGroupLayout> bindGroupLayouts) {
      Set<String> names = new HashSet();

      for(int layoutIndex = 0; layoutIndex < bindGroupLayouts.size(); ++layoutIndex) {
         BindGroupLayout bindGroupLayout = (BindGroupLayout)bindGroupLayouts.get(layoutIndex);

         for(UniformDescription uniform : bindGroupLayout.getUniforms()) {
            if (!names.add(uniform.name())) {
               String var10002 = uniform.name();
               throw new IllegalArgumentException("Duplicate bind name '" + var10002 + "' in bind group layout " + layoutIndex);
            }
         }

         for(String sampler : bindGroupLayout.getSamplers()) {
            if (!names.add(sampler)) {
               throw new IllegalArgumentException("Duplicate bind name '" + sampler + "' in bind group layout " + layoutIndex);
            }
         }
      }

   }

   public static class Builder {
      private final List<String> samplers = new ArrayList();
      private final List<UniformDescription> uniforms = new ArrayList();

      private Builder() {
         super();
      }

      public Builder withSampler(final String sampler) {
         this.samplers.add(sampler);
         return this;
      }

      public Builder withUniform(final String name, final UniformType type) {
         if (type == UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Cannot use texel buffer without specifying texture format");
         } else {
            this.uniforms.add(new UniformDescription(name, type));
            return this;
         }
      }

      public Builder withUniform(final String name, final UniformType type, final GpuFormat format) {
         if (type != UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Only texel buffer can specify texture format");
         } else {
            this.uniforms.add(new UniformDescription(name, format));
            return this;
         }
      }

      public BindGroupLayout build() {
         return new BindGroupLayout(List.copyOf(this.samplers), List.copyOf(this.uniforms));
      }
   }

   public static record UniformDescription(String name, UniformType type, @Nullable GpuFormat gpuFormat) {
      public UniformDescription(final String name, final UniformType type) {
         this(name, type, (GpuFormat)null);
         if (type == UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Texel buffer needs a texture format");
         }
      }

      public UniformDescription(final String name, final GpuFormat gpuFormat) {
         this(name, UniformType.TEXEL_BUFFER, gpuFormat);
      }

      public UniformDescription {
         super();
      }
   }
}
