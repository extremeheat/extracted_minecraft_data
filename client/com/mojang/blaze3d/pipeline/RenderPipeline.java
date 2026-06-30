package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RenderPipeline {
   private final Identifier location;
   private final Identifier vertexShader;
   private final Identifier fragmentShader;
   private final ShaderDefines shaderDefines;
   private final List<BindGroupLayout> bindGroupLayouts;
   private final @Nullable DepthStencilState depthStencilState;
   private final PolygonMode polygonMode;
   private final boolean cull;
   private final @Nullable ColorTargetState[] colorTargetStates;
   private final @Nullable VertexFormat[] vertexFormatPerBuffer = new VertexFormat[16];
   private final PrimitiveTopology primitiveTopology;
   private final int sortKey;
   private static int sortKeySeed;

   protected RenderPipeline(final Identifier location, final Identifier vertexShader, final Identifier fragmentShader, final ShaderDefines shaderDefines, final List<BindGroupLayout> bindGroupLayouts, final @Nullable ColorTargetState[] colorTargetStates, final @Nullable DepthStencilState depthStencilState, final PolygonMode polygonMode, final boolean cull, final @Nullable VertexFormat[] vertexFormatPerBuffer, final PrimitiveTopology primitiveTopology, final int sortKey) {
      super();
      this.location = location;
      this.vertexShader = vertexShader;
      this.fragmentShader = fragmentShader;
      this.shaderDefines = shaderDefines;
      this.bindGroupLayouts = bindGroupLayouts;
      this.depthStencilState = depthStencilState;
      this.polygonMode = polygonMode;
      this.cull = cull;
      this.colorTargetStates = colorTargetStates;
      this.primitiveTopology = primitiveTopology;
      this.sortKey = sortKey;
      System.arraycopy(vertexFormatPerBuffer, 0, this.vertexFormatPerBuffer, 0, this.vertexFormatPerBuffer.length);
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

   public @Nullable ColorTargetState[] getColorTargetStates() {
      return this.colorTargetStates;
   }

   public @Nullable DepthStencilState getDepthStencilState() {
      return this.depthStencilState;
   }

   public Identifier getLocation() {
      return this.location;
   }

   public @Nullable VertexFormat[] getVertexFormatBindings() {
      return this.vertexFormatPerBuffer;
   }

   public @Nullable VertexFormat getVertexFormatBinding(final int bindingIndex) {
      return this.vertexFormatPerBuffer[bindingIndex];
   }

   public PrimitiveTopology getPrimitiveTopology() {
      return this.primitiveTopology;
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

   public List<BindGroupLayout> getBindGroupLayouts() {
      return this.bindGroupLayouts;
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
      private Optional<Set<BindGroupLayout>> bindGroupLayouts = Optional.empty();
      private Optional<DepthStencilState> depthStencilState = Optional.empty();
      private Optional<PolygonMode> polygonMode = Optional.empty();
      private Optional<Boolean> cull = Optional.empty();
      private final @Nullable ColorTargetState[] colorTargetStates = new ColorTargetState[8];
      private int activeColorTargetStateCount;
      private final @Nullable VertexFormat[] vertexFormatPerBuffer = new VertexFormat[16];
      private Optional<PrimitiveTopology> primitiveTopology = Optional.empty();

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

      public Builder withBindGroupLayout(final BindGroupLayout bindGroupLayout) {
         if (this.bindGroupLayouts.isEmpty()) {
            this.bindGroupLayouts = Optional.of(new ObjectOpenHashSet());
         }

         ((Set)this.bindGroupLayouts.get()).add(bindGroupLayout);
         return this;
      }

      public Builder withPolygonMode(final PolygonMode polygonMode) {
         this.polygonMode = Optional.of(polygonMode);
         return this;
      }

      public Builder withCull(final boolean cull) {
         this.cull = Optional.of(cull);
         return this;
      }

      public Builder withColorTargetState(final int index, final ColorTargetState colorTargetState) {
         this.colorTargetStates[index] = colorTargetState;
         this.activeColorTargetStateCount = Math.max(this.activeColorTargetStateCount, index + 1);
         return this;
      }

      public Builder withColorTargetStates(final int startIindex, final int endIndex, final Supplier<ColorTargetState> colorTargetState) {
         for(int i = startIindex; i <= endIndex; ++i) {
            this.colorTargetStates[i] = (ColorTargetState)colorTargetState.get();
            this.activeColorTargetStateCount = Math.max(this.activeColorTargetStateCount, i + 1);
         }

         return this;
      }

      public Builder withUnusedColorTargetState(final int index) {
         this.colorTargetStates[index] = null;
         this.activeColorTargetStateCount = Math.max(this.activeColorTargetStateCount, index + 1);
         return this;
      }

      public Builder withColorTargetState(final ColorTargetState colorTargetState) {
         return this.withColorTargetState(0, colorTargetState);
      }

      public Builder withDepthStencilState(final DepthStencilState depthStencilState) {
         this.depthStencilState = Optional.of(depthStencilState);
         return this;
      }

      public Builder withDepthStencilState(final Optional<DepthStencilState> depthStencilState) {
         this.depthStencilState = depthStencilState;
         return this;
      }

      public Builder withVertexBinding(final int bindingIndex, final VertexFormat vertexFormat) {
         this.vertexFormatPerBuffer[bindingIndex] = vertexFormat;
         return this;
      }

      public Builder withPrimitiveTopology(final PrimitiveTopology primitiveTopology) {
         this.primitiveTopology = Optional.of(primitiveTopology);
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

         snippet.bindGroupLayouts.ifPresent((snippetLayouts) -> {
            if (this.bindGroupLayouts.isPresent()) {
               ((Set)this.bindGroupLayouts.get()).addAll(snippetLayouts);
            } else {
               this.bindGroupLayouts = Optional.of(new ObjectOpenHashSet(snippetLayouts));
            }

         });
         if (snippet.depthStencilState.isPresent()) {
            this.depthStencilState = snippet.depthStencilState;
         }

         if (snippet.cull.isPresent()) {
            this.cull = snippet.cull;
         }

         for(int i = 0; i < snippet.activeColorTargetStateCount; ++i) {
            if (this.colorTargetStates[i] == null && snippet.colorTargetStates[i] != null) {
               this.colorTargetStates[i] = snippet.colorTargetStates[i];
            }
         }

         this.activeColorTargetStateCount = Math.max(this.activeColorTargetStateCount, snippet.activeColorTargetStateCount);

         for(int i = 0; i < snippet.vertexFormatPerBuffer.length; ++i) {
            VertexFormat vertexFormat = snippet.vertexFormatPerBuffer[i];
            if (vertexFormat != null) {
               this.vertexFormatPerBuffer[i] = vertexFormat;
            }
         }

         if (snippet.vertexFormatMode.isPresent()) {
            this.primitiveTopology = snippet.vertexFormatMode;
         }

         if (snippet.polygonMode.isPresent()) {
            this.polygonMode = snippet.polygonMode;
         }

      }

      public Snippet buildSnippet() {
         return new Snippet(this.vertexShader, this.fragmentShader, this.definesBuilder.map(ShaderDefines.Builder::build), this.bindGroupLayouts.map(List::copyOf), this.colorTargetStates, this.activeColorTargetStateCount, this.depthStencilState, this.polygonMode, this.cull, this.vertexFormatPerBuffer, this.primitiveTopology);
      }

      public RenderPipeline build() {
         if (this.location.isEmpty()) {
            throw new IllegalStateException("Missing location");
         } else if (this.vertexShader.isEmpty()) {
            throw new IllegalStateException("Missing vertex shader");
         } else if (this.fragmentShader.isEmpty()) {
            throw new IllegalStateException("Missing fragment shader");
         } else if (this.primitiveTopology.isEmpty()) {
            throw new IllegalStateException("Missing primitive topology");
         } else {
            ColorTargetState[] activeColorTargetStates;
            if (this.activeColorTargetStateCount == 0) {
               activeColorTargetStates = new ColorTargetState[0];
            } else {
               activeColorTargetStates = (ColorTargetState[])Arrays.copyOf(this.colorTargetStates, this.activeColorTargetStateCount);
               Optional<BlendFunction> lastBlend = Optional.empty();

               for(ColorTargetState activeColorTargetState : activeColorTargetStates) {
                  if (activeColorTargetState != null) {
                     Optional<BlendFunction> currentBlend = activeColorTargetState.blendFunction();
                     if (currentBlend.isPresent()) {
                        if (lastBlend.isEmpty()) {
                           lastBlend = currentBlend;
                        } else if (!currentBlend.equals(lastBlend)) {
                           throw new IllegalStateException("Blend functions must currently be the same for all color targets");
                        }
                     }
                  }
               }
            }

            int boundVertexAttribCount = 0;

            for(VertexFormat bindings : this.vertexFormatPerBuffer) {
               if (bindings != null) {
                  boundVertexAttribCount += bindings.getElements().size();
               }
            }

            if (boundVertexAttribCount > 16) {
               throw new IllegalStateException("Binding more than 16 vertex attributes is not supported");
            } else {
               return new RenderPipeline((Identifier)this.location.get(), (Identifier)this.vertexShader.get(), (Identifier)this.fragmentShader.get(), ((ShaderDefines.Builder)this.definesBuilder.orElse(ShaderDefines.builder())).build(), List.copyOf((Collection)this.bindGroupLayouts.orElse(Collections.emptySet())), activeColorTargetStates, (DepthStencilState)this.depthStencilState.orElse((Object)null), (PolygonMode)this.polygonMode.orElse(PolygonMode.FILL), (Boolean)this.cull.orElse(true), this.vertexFormatPerBuffer, (PrimitiveTopology)this.primitiveTopology.get(), nextPipelineSortKey++);
            }
         }
      }
   }

   public static record Snippet(Optional<Identifier> vertexShader, Optional<Identifier> fragmentShader, Optional<ShaderDefines> shaderDefines, Optional<List<BindGroupLayout>> bindGroupLayouts, @Nullable ColorTargetState[] colorTargetStates, int activeColorTargetStateCount, Optional<DepthStencilState> depthStencilState, Optional<PolygonMode> polygonMode, Optional<Boolean> cull, @Nullable VertexFormat[] vertexFormatPerBuffer, Optional<PrimitiveTopology> vertexFormatMode) {
      public Snippet {
         super();
      }
   }
}
