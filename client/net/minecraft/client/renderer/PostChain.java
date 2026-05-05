package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.shaders.UniformType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

public class PostChain implements AutoCloseable {
   public static final Identifier MAIN_TARGET_ID = Identifier.withDefaultNamespace("main");
   private final List<PostPass> passes;
   private final Map<Identifier, PostChainConfig.InternalTarget> internalTargets;
   private final Set<Identifier> externalTargets;
   private final Map<Identifier, RenderTarget> persistentTargets = new HashMap();
   private final Projection projection;
   private final ProjectionMatrixBuffer projectionMatrixBuffer;

   private PostChain(final List<PostPass> passes, final Map<Identifier, PostChainConfig.InternalTarget> internalTargets, final Set<Identifier> externalTargets, final Projection projection, final ProjectionMatrixBuffer projectionMatrixBuffer) {
      super();
      this.passes = passes;
      this.internalTargets = internalTargets;
      this.externalTargets = externalTargets;
      this.projection = projection;
      this.projectionMatrixBuffer = projectionMatrixBuffer;
   }

   public static PostChain load(final PostChainConfig config, final TextureManager textureManager, final Set<Identifier> allowedExternalTargets, final Identifier id, final Projection projection, final ProjectionMatrixBuffer projectionMatrixBuffer) throws ShaderManager.CompilationException {
      Stream<Identifier> referencedTargets = config.passes().stream().flatMap(PostChainConfig.Pass::referencedTargets);
      Set<Identifier> referencedExternalTargets = (Set)referencedTargets.filter((targetId) -> !config.internalTargets().containsKey(targetId)).collect(Collectors.toSet());
      Set<Identifier> invalidExternalTargets = Sets.difference(referencedExternalTargets, allowedExternalTargets);
      if (!invalidExternalTargets.isEmpty()) {
         throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + String.valueOf(invalidExternalTargets));
      } else {
         ImmutableList.Builder<PostPass> passes = ImmutableList.builder();

         for(int i = 0; i < config.passes().size(); ++i) {
            PostChainConfig.Pass pass = (PostChainConfig.Pass)config.passes().get(i);
            passes.add(createPass(textureManager, pass, id.withSuffix("/" + i)));
         }

         return new PostChain(passes.build(), config.internalTargets(), referencedExternalTargets, projection, projectionMatrixBuffer);
      }
   }

   private static PostPass createPass(final TextureManager textureManager, final PostChainConfig.Pass config, final Identifier id) throws ShaderManager.CompilationException {
      RenderPipeline.Builder pipelineBuilder = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET).withFragmentShader(config.fragmentShaderId()).withVertexShader(config.vertexShaderId()).withLocation(id);
      BindGroupLayout.Builder bindGroupLayoutBuilder = BindGroupLayout.builder();

      for(PostChainConfig.Input input : config.inputs()) {
         bindGroupLayoutBuilder.withSampler(input.samplerName() + "Sampler");
      }

      bindGroupLayoutBuilder.withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER);

      for(String uniformGroupName : config.uniforms().keySet()) {
         bindGroupLayoutBuilder.withUniform(uniformGroupName, UniformType.UNIFORM_BUFFER);
      }

      pipelineBuilder.withBindGroupLayout(bindGroupLayoutBuilder.build());
      RenderPipeline pipeline = pipelineBuilder.build();
      List<PostPass.Input> inputs = new ArrayList();

      label113:
      for(PostChainConfig.Input input : config.inputs()) {
         Objects.requireNonNull(input);
         PostChainConfig.Input var9 = input;
         byte var10 = 0;

         while(true) {
            //$FF: var10->value
            //0->net/minecraft/client/renderer/PostChainConfig$TextureInput
            //1->net/minecraft/client/renderer/PostChainConfig$TargetInput
            switch (var9.typeSwitch<invokedynamic>(var9, var10)) {
               case 0:
                  PostChainConfig.TextureInput var11 = (PostChainConfig.TextureInput)var9;
                  PostChainConfig.TextureInput var53 = var11;

                  try {
                     var54 = var53.samplerName();
                  } catch (Throwable var32) {
                     throw new MatchException(var32.toString(), var32);
                  }

                  String var37 = var54;
                  String samplerName = var37;
                  var53 = var11;

                  try {
                     var56 = var53.location();
                  } catch (Throwable var31) {
                     throw new MatchException(var31.toString(), var31);
                  }

                  Identifier var38 = var56;
                  Identifier location = var38;
                  var53 = var11;

                  try {
                     var58 = var53.width();
                  } catch (Throwable var30) {
                     throw new MatchException(var30.toString(), var30);
                  }

                  int var39 = var58;
                  if (true) {
                     int width = var39;
                     var53 = var11;

                     try {
                        var60 = var53.height();
                     } catch (Throwable var29) {
                        throw new MatchException(var29.toString(), var29);
                     }

                     var39 = var60;
                     if (true) {
                        int height = var39;
                        var53 = var11;

                        try {
                           var62 = var53.bilinear();
                        } catch (Throwable var28) {
                           throw new MatchException(var28.toString(), var28);
                        }

                        var39 = var62;
                        if (true) {
                           boolean bilinear = (boolean)var39;
                           AbstractTexture var42 = textureManager.getTexture(location.withPath((UnaryOperator)((path) -> "textures/effect/" + path + ".png")));
                           inputs.add(new PostPass.TextureInput(samplerName, var42, width, height, bilinear));
                           continue label113;
                        }
                     }
                  }

                  var10 = 1;
                  break;
               case 1:
                  PostChainConfig.TargetInput texture = (PostChainConfig.TargetInput)var9;
                  PostChainConfig.TargetInput var10000 = texture;

                  try {
                     var46 = var10000.samplerName();
                  } catch (Throwable var27) {
                     throw new MatchException(var27.toString(), var27);
                  }

                  String bilinear = var46;
                  String samplerName = bilinear;
                  var10000 = texture;

                  try {
                     var48 = var10000.targetId();
                  } catch (Throwable var26) {
                     throw new MatchException(var26.toString(), var26);
                  }

                  Identifier bilinear = var48;
                  Identifier targetId = bilinear;
                  var10000 = texture;

                  try {
                     var50 = var10000.useDepthBuffer();
                  } catch (Throwable var25) {
                     throw new MatchException(var25.toString(), var25);
                  }

                  boolean bilinear = var50;
                  if (true) {
                     boolean useDepthBuffer = bilinear;
                     var10000 = texture;

                     try {
                        var52 = var10000.bilinear();
                     } catch (Throwable var24) {
                        throw new MatchException(var24.toString(), var24);
                     }

                     bilinear = var52;
                     if (true) {
                        inputs.add(new PostPass.TargetInput(samplerName, targetId, useDepthBuffer, bilinear));
                        continue label113;
                     }
                  }

                  var10 = 2;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         }
      }

      return new PostPass(pipeline, config.outputTarget(), config.uniforms(), inputs);
   }

   public void addToFrame(final FrameGraphBuilder frame, final int screenWidth, final int screenHeight, final TargetBundle providedTargets) {
      this.projection.setSize((float)screenWidth, (float)screenHeight);
      GpuBufferSlice projectionBuffer = this.projectionMatrixBuffer.getBuffer(this.projection);
      Map<Identifier, ResourceHandle<RenderTarget>> targets = new HashMap(this.internalTargets.size() + this.externalTargets.size());

      for(Identifier id : this.externalTargets) {
         targets.put(id, providedTargets.getOrThrow(id));
      }

      for(Map.Entry<Identifier, PostChainConfig.InternalTarget> entry : this.internalTargets.entrySet()) {
         Identifier id = (Identifier)entry.getKey();
         PostChainConfig.InternalTarget target = (PostChainConfig.InternalTarget)entry.getValue();
         RenderTargetDescriptor descriptor = new RenderTargetDescriptor((Integer)target.width().orElse(screenWidth), (Integer)target.height().orElse(screenHeight), true, ARGB.vector4fFromARGB32(target.clearColor()), GpuFormat.RGBA8_UNORM);
         if (target.persistent()) {
            RenderTarget persistentTarget = this.getOrCreatePersistentTarget(id, descriptor);
            targets.put(id, frame.importExternal(id.toString(), persistentTarget));
         } else {
            targets.put(id, frame.createInternal(id.toString(), descriptor));
         }
      }

      for(PostPass pass : this.passes) {
         pass.addToFrame(frame, targets, projectionBuffer);
      }

      for(Identifier id : this.externalTargets) {
         providedTargets.replace(id, (ResourceHandle)targets.get(id));
      }

   }

   /** @deprecated */
   @Deprecated
   public void process(final RenderTarget mainTarget, final GraphicsResourceAllocator resourceAllocator) {
      FrameGraphBuilder frame = new FrameGraphBuilder();
      TargetBundle targets = PostChain.TargetBundle.of(MAIN_TARGET_ID, frame.importExternal("main", mainTarget));
      this.addToFrame(frame, mainTarget.width, mainTarget.height, targets);
      frame.execute(resourceAllocator);
   }

   private RenderTarget getOrCreatePersistentTarget(final Identifier id, final RenderTargetDescriptor descriptor) {
      RenderTarget target = (RenderTarget)this.persistentTargets.get(id);
      if (target == null || target.width != descriptor.width() || target.height != descriptor.height()) {
         if (target != null) {
            target.destroyBuffers();
         }

         target = descriptor.allocate();
         descriptor.prepare(target);
         this.persistentTargets.put(id, target);
      }

      return target;
   }

   public void close() {
      this.persistentTargets.values().forEach(RenderTarget::destroyBuffers);
      this.persistentTargets.clear();

      for(PostPass pass : this.passes) {
         pass.close();
      }

   }

   public interface TargetBundle {
      static TargetBundle of(final Identifier targetId, final ResourceHandle<RenderTarget> target) {
         return new TargetBundle() {
            private ResourceHandle<RenderTarget> handle = target;

            public void replace(final Identifier id, final ResourceHandle<RenderTarget> handle) {
               if (id.equals(targetId)) {
                  this.handle = handle;
               } else {
                  throw new IllegalArgumentException("No target with id " + String.valueOf(id));
               }
            }

            public @Nullable ResourceHandle<RenderTarget> get(final Identifier id) {
               return id.equals(targetId) ? this.handle : null;
            }
         };
      }

      void replace(Identifier id, ResourceHandle<RenderTarget> handle);

      @Nullable ResourceHandle<RenderTarget> get(Identifier id);

      default ResourceHandle<RenderTarget> getOrThrow(final Identifier id) {
         ResourceHandle<RenderTarget> handle = this.get(id);
         if (handle == null) {
            throw new IllegalArgumentException("Missing target with id " + String.valueOf(id));
         } else {
            return handle;
         }
      }
   }
}
