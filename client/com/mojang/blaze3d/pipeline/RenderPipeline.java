package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RenderPipeline {
   private final Identifier location;
   private final Identifier vertexShader;
   private final Identifier fragmentShader;
   private final ShaderDefines shaderDefines;
   private final List<String> samplers;
   private final List<UniformDescription> uniforms;
   private final @Nullable DepthStencilState depthStencilState;
   private final PolygonMode polygonMode;
   private final boolean cull;
   private final ColorTargetState colorTargetState;
   private final VertexFormat vertexFormat;
   private final VertexFormat.Mode vertexFormatMode;
   private final int sortKey;
   private static int sortKeySeed;

   protected RenderPipeline(final Identifier location, final Identifier vertexShader, final Identifier fragmentShader, final ShaderDefines shaderDefines, final List<String> samplers, final List<UniformDescription> uniforms, final ColorTargetState colorTargetState, final @Nullable DepthStencilState depthStencilState, final PolygonMode polygonMode, final boolean cull, final VertexFormat vertexFormat, final VertexFormat.Mode vertexFormatMode, final int sortKey) {
      super();
      this.location = location;
      this.vertexShader = vertexShader;
      this.fragmentShader = fragmentShader;
      this.shaderDefines = shaderDefines;
      this.samplers = samplers;
      this.uniforms = uniforms;
      this.depthStencilState = depthStencilState;
      this.polygonMode = polygonMode;
      this.cull = cull;
      this.colorTargetState = colorTargetState;
      this.vertexFormat = vertexFormat;
      this.vertexFormatMode = vertexFormatMode;
      this.sortKey = sortKey;
   }

   public int getSortKey() {
      return SharedConstants.DEBUG_SHUFFLE_UI_RENDERING_ORDER ? super.hashCode() * (sortKeySeed + 1) : this.sortKey;
   }

   public static void updateSortKeySeed() {
      sortKeySeed = Math.round(100000.0F * (float)Math.random());
   }

   public String toString() {
      return this.location.toString();
   }

   public PolygonMode getPolygonMode() {
      return this.polygonMode;
   }

   public boolean isCull() {
      return this.cull;
   }

   public ColorTargetState getColorTargetState() {
      return this.colorTargetState;
   }

   public @Nullable DepthStencilState getDepthStencilState() {
      return this.depthStencilState;
   }

   public Identifier getLocation() {
      return this.location;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public VertexFormat.Mode getVertexFormatMode() {
      return this.vertexFormatMode;
   }

   public Identifier getVertexShader() {
      return this.vertexShader;
   }

   public Identifier getFragmentShader() {
      return this.fragmentShader;
   }

   public ShaderDefines getShaderDefines() {
      return this.shaderDefines;
   }

   public List<String> getSamplers() {
      return this.samplers;
   }

   public List<UniformDescription> getUniforms() {
      return this.uniforms;
   }

   public boolean wantsDepthTexture() {
      return this.depthStencilState != null;
   }

   public static Builder builder(final Snippet... snippets) {
      Builder builder = new Builder();

      for(Snippet snippet : snippets) {
         builder.withSnippet(snippet);
      }

      return builder;
   }

   public static class Builder {
      private static int nextPipelineSortKey;
      private Optional<Identifier> location = Optional.empty();
      private Optional<Identifier> fragmentShader = Optional.empty();
      private Optional<Identifier> vertexShader = Optional.empty();
      private Optional<ShaderDefines.Builder> definesBuilder = Optional.empty();
      private Optional<List<String>> samplers = Optional.empty();
      private Optional<List<UniformDescription>> uniforms = Optional.empty();
      private Optional<DepthStencilState> depthStencilState = Optional.empty();
      private Optional<PolygonMode> polygonMode = Optional.empty();
      private Optional<Boolean> cull = Optional.empty();
      private Optional<ColorTargetState> colorTargetState = Optional.empty();
      private Optional<VertexFormat> vertexFormat = Optional.empty();
      private Optional<VertexFormat.Mode> vertexFormatMode = Optional.empty();

      private Builder() {
         super();
      }

      public Builder withLocation(final String location) {
         this.location = Optional.of(Identifier.withDefaultNamespace(location));
         return this;
      }

      public Builder withLocation(final Identifier location) {
         this.location = Optional.of(location);
         return this;
      }

      public Builder withFragmentShader(final String fragmentShader) {
         this.fragmentShader = Optional.of(Identifier.withDefaultNamespace(fragmentShader));
         return this;
      }

      public Builder withFragmentShader(final Identifier fragmentShader) {
         this.fragmentShader = Optional.of(fragmentShader);
         return this;
      }

      public Builder withVertexShader(final String vertexShader) {
         this.vertexShader = Optional.of(Identifier.withDefaultNamespace(vertexShader));
         return this;
      }

      public Builder withVertexShader(final Identifier vertexShader) {
         this.vertexShader = Optional.of(vertexShader);
         return this;
      }

      public Builder withShaderDefine(final String key) {
         if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(ShaderDefines.builder());
         }

         ((ShaderDefines.Builder)this.definesBuilder.get()).define(key);
         return this;
      }

      public Builder withShaderDefine(final String key, final int value) {
         if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(ShaderDefines.builder());
         }

         ((ShaderDefines.Builder)this.definesBuilder.get()).define(key, value);
         return this;
      }

      public Builder withShaderDefine(final String key, final float value) {
         if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(ShaderDefines.builder());
         }

         ((ShaderDefines.Builder)this.definesBuilder.get()).define(key, value);
         return this;
      }

      public Builder withSampler(final String sampler) {
         if (this.samplers.isEmpty()) {
            this.samplers = Optional.of(new ArrayList());
         }

         ((List)this.samplers.get()).add(sampler);
         return this;
      }

      public Builder withUniform(final String name, final UniformType type) {
         if (this.uniforms.isEmpty()) {
            this.uniforms = Optional.of(new ArrayList());
         }

         if (type == UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Cannot use texel buffer without specifying texture format");
         } else {
            ((List)this.uniforms.get()).add(new UniformDescription(name, type));
            return this;
         }
      }

      public Builder withUniform(final String name, final UniformType type, final GpuFormat format) {
         if (this.uniforms.isEmpty()) {
            this.uniforms = Optional.of(new ArrayList());
         }

         if (type != UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Only texel buffer can specify texture format");
         } else {
            ((List)this.uniforms.get()).add(new UniformDescription(name, format));
            return this;
         }
      }

      public Builder withPolygonMode(final PolygonMode polygonMode) {
         this.polygonMode = Optional.of(polygonMode);
         return this;
      }

      public Builder withCull(final boolean cull) {
         this.cull = Optional.of(cull);
         return this;
      }

      public Builder withColorTargetState(final ColorTargetState colorTargetState) {
         this.colorTargetState = Optional.of(colorTargetState);
         return this;
      }

      public Builder withDepthStencilState(final DepthStencilState depthStencilState) {
         this.depthStencilState = Optional.of(depthStencilState);
         return this;
      }

      public Builder withDepthStencilState(final Optional<DepthStencilState> depthStencilState) {
         this.depthStencilState = depthStencilState;
         return this;
      }

      public Builder withVertexFormat(final VertexFormat vertexFormat, final VertexFormat.Mode vertexFormatMode) {
         this.vertexFormat = Optional.of(vertexFormat);
         this.vertexFormatMode = Optional.of(vertexFormatMode);
         return this;
      }

      private void withSnippet(final Snippet snippet) {
         if (snippet.vertexShader.isPresent()) {
            this.vertexShader = snippet.vertexShader;
         }

         if (snippet.fragmentShader.isPresent()) {
            this.fragmentShader = snippet.fragmentShader;
         }

         if (snippet.shaderDefines.isPresent()) {
            if (this.definesBuilder.isEmpty()) {
               this.definesBuilder = Optional.of(ShaderDefines.builder());
            }

            ShaderDefines snippetDefines = (ShaderDefines)snippet.shaderDefines.get();

            for(Map.Entry<String, String> snippetValue : snippetDefines.values().entrySet()) {
               ((ShaderDefines.Builder)this.definesBuilder.get()).define((String)snippetValue.getKey(), (String)snippetValue.getValue());
            }

            for(String flag : snippetDefines.flags()) {
               ((ShaderDefines.Builder)this.definesBuilder.get()).define(flag);
            }
         }

         snippet.samplers.ifPresent((builderSamplers) -> {
            if (this.samplers.isPresent()) {
               ((List)this.samplers.get()).addAll(builderSamplers);
            } else {
               this.samplers = Optional.of(new ArrayList(builderSamplers));
            }

         });
         snippet.uniforms.ifPresent((builderUniforms) -> {
            if (this.uniforms.isPresent()) {
               ((List)this.uniforms.get()).addAll(builderUniforms);
            } else {
               this.uniforms = Optional.of(new ArrayList(builderUniforms));
            }

         });
         if (snippet.depthStencilState.isPresent()) {
            this.depthStencilState = snippet.depthStencilState;
         }

         if (snippet.cull.isPresent()) {
            this.cull = snippet.cull;
         }

         if (snippet.colorTargetState.isPresent()) {
            this.colorTargetState = snippet.colorTargetState;
         }

         if (snippet.vertexFormat.isPresent()) {
            this.vertexFormat = snippet.vertexFormat;
         }

         if (snippet.vertexFormatMode.isPresent()) {
            this.vertexFormatMode = snippet.vertexFormatMode;
         }

         if (snippet.polygonMode.isPresent()) {
            this.polygonMode = snippet.polygonMode;
         }

      }

      public Snippet buildSnippet() {
         return new Snippet(this.vertexShader, this.fragmentShader, this.definesBuilder.map(ShaderDefines.Builder::build), this.samplers.map(Collections::unmodifiableList), this.uniforms.map(Collections::unmodifiableList), this.colorTargetState, this.depthStencilState, this.polygonMode, this.cull, this.vertexFormat, this.vertexFormatMode);
      }

      public RenderPipeline build() {
         if (this.location.isEmpty()) {
            throw new IllegalStateException("Missing location");
         } else if (this.vertexShader.isEmpty()) {
            throw new IllegalStateException("Missing vertex shader");
         } else if (this.fragmentShader.isEmpty()) {
            throw new IllegalStateException("Missing fragment shader");
         } else if (this.vertexFormat.isEmpty()) {
            throw new IllegalStateException("Missing vertex buffer format");
         } else if (this.vertexFormatMode.isEmpty()) {
            throw new IllegalStateException("Missing vertex mode");
         } else {
            return new RenderPipeline((Identifier)this.location.get(), (Identifier)this.vertexShader.get(), (Identifier)this.fragmentShader.get(), ((ShaderDefines.Builder)this.definesBuilder.orElse(ShaderDefines.builder())).build(), List.copyOf((Collection)this.samplers.orElse(new ArrayList())), (List)this.uniforms.orElse(Collections.emptyList()), (ColorTargetState)this.colorTargetState.orElse(ColorTargetState.DEFAULT), (DepthStencilState)this.depthStencilState.orElse((Object)null), (PolygonMode)this.polygonMode.orElse(PolygonMode.FILL), (Boolean)this.cull.orElse(true), (VertexFormat)this.vertexFormat.get(), (VertexFormat.Mode)this.vertexFormatMode.get(), nextPipelineSortKey++);
         }
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

   public static record Snippet(Optional<Identifier> vertexShader, Optional<Identifier> fragmentShader, Optional<ShaderDefines> shaderDefines, Optional<List<String>> samplers, Optional<List<UniformDescription>> uniforms, Optional<ColorTargetState> colorTargetState, Optional<DepthStencilState> depthStencilState, Optional<PolygonMode> polygonMode, Optional<Boolean> cull, Optional<VertexFormat> vertexFormat, Optional<VertexFormat.Mode> vertexFormatMode) {
      public Snippet {
         super();
      }
   }
}
