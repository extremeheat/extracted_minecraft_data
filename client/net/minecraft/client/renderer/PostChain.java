package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
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
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class PostChain implements AutoCloseable {
   public static final ResourceLocation MAIN_TARGET_ID = ResourceLocation.withDefaultNamespace("main");
   private final List<PostPass> passes;
   private final Map<ResourceLocation, PostChainConfig.InternalTarget> internalTargets;
   private final Set<ResourceLocation> externalTargets;
   private final Map<ResourceLocation, RenderTarget> persistentTargets = new HashMap();
   private final CachedOrthoProjectionMatrixBuffer projectionMatrixBuffer;

   private PostChain(List<PostPass> var1, Map<ResourceLocation, PostChainConfig.InternalTarget> var2, Set<ResourceLocation> var3, CachedOrthoProjectionMatrixBuffer var4) {
      super();
      this.passes = var1;
      this.internalTargets = var2;
      this.externalTargets = var3;
      this.projectionMatrixBuffer = var4;
   }

   public static PostChain load(PostChainConfig var0, TextureManager var1, Set<ResourceLocation> var2, ResourceLocation var3, CachedOrthoProjectionMatrixBuffer var4) throws ShaderManager.CompilationException {
      Stream var5 = var0.passes().stream().flatMap(PostChainConfig.Pass::referencedTargets);
      Set var6 = (Set)var5.filter((var1x) -> !var0.internalTargets().containsKey(var1x)).collect(Collectors.toSet());
      Sets.SetView var7 = Sets.difference(var6, var2);
      if (!var7.isEmpty()) {
         throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + String.valueOf(var7));
      } else {
         ImmutableList.Builder var8 = ImmutableList.builder();

         for(int var9 = 0; var9 < var0.passes().size(); ++var9) {
            PostChainConfig.Pass var10 = (PostChainConfig.Pass)var0.passes().get(var9);
            var8.add(createPass(var1, var10, var3.withSuffix("/" + var9)));
         }

         return new PostChain(var8.build(), var0.internalTargets(), var6, var4);
      }
   }

   private static PostPass createPass(TextureManager var0, PostChainConfig.Pass var1, ResourceLocation var2) throws ShaderManager.CompilationException {
      RenderPipeline.Builder var3 = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET).withFragmentShader(var1.fragmentShaderId()).withVertexShader(var1.vertexShaderId()).withLocation(var2);

      for(PostChainConfig.Input var5 : var1.inputs()) {
         var3.withSampler(var5.samplerName() + "Sampler");
      }

      var3.withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER);

      for(String var33 : var1.uniforms().keySet()) {
         var3.withUniform(var33, UniformType.UNIFORM_BUFFER);
      }

      RenderPipeline var32 = var3.build();
      ArrayList var34 = new ArrayList();

      for(PostChainConfig.Input var7 : var1.inputs()) {
         Objects.requireNonNull(var7);
         byte var9 = 0;
         //$FF: var9->value
         //0->net/minecraft/client/renderer/PostChainConfig$TextureInput
         //1->net/minecraft/client/renderer/PostChainConfig$TargetInput
         switch (var7.typeSwitch<invokedynamic>(var7, var9)) {
            case 0:
               PostChainConfig.TextureInput var10 = (PostChainConfig.TextureInput)var7;
               PostChainConfig.TextureInput var51 = var10;

               try {
                  var52 = var51.samplerName();
               } catch (Throwable var30) {
                  throw new MatchException(var30.toString(), var30);
               }

               String var35 = var52;
               String var11 = var35;
               var51 = var10;

               try {
                  var54 = var51.location();
               } catch (Throwable var29) {
                  throw new MatchException(var29.toString(), var29);
               }

               ResourceLocation var36 = var54;
               ResourceLocation var12 = var36;
               var51 = var10;

               try {
                  var56 = var51.width();
               } catch (Throwable var28) {
                  throw new MatchException(var28.toString(), var28);
               }

               int var37 = var56;
               int var13 = var37;
               var51 = var10;

               try {
                  var58 = var51.height();
               } catch (Throwable var27) {
                  throw new MatchException(var27.toString(), var27);
               }

               var37 = var58;
               int var14 = var37;
               var51 = var10;

               try {
                  var60 = var51.bilinear();
               } catch (Throwable var26) {
                  throw new MatchException(var26.toString(), var26);
               }

               var37 = var60;
               boolean var15 = (boolean)var37;
               AbstractTexture var40 = var0.getTexture(var12.withPath((UnaryOperator)((var0x) -> "textures/effect/" + var0x + ".png")));
               var40.setFilter(var15, false);
               var34.add(new PostPass.TextureInput(var11, var40, var13, var14));
               break;
            case 1:
               PostChainConfig.TargetInput var16 = (PostChainConfig.TargetInput)var7;
               PostChainConfig.TargetInput var10000 = var16;

               try {
                  var44 = var10000.samplerName();
               } catch (Throwable var25) {
                  throw new MatchException(var25.toString(), var25);
               }

               String var21 = var44;
               String var17 = var21;
               var10000 = var16;

               try {
                  var46 = var10000.targetId();
               } catch (Throwable var24) {
                  throw new MatchException(var24.toString(), var24);
               }

               ResourceLocation var41 = var46;
               ResourceLocation var18 = var41;
               var10000 = var16;

               try {
                  var48 = var10000.useDepthBuffer();
               } catch (Throwable var23) {
                  throw new MatchException(var23.toString(), var23);
               }

               boolean var42 = var48;
               boolean var19 = var42;
               var10000 = var16;

               try {
                  var50 = var10000.bilinear();
               } catch (Throwable var22) {
                  throw new MatchException(var22.toString(), var22);
               }

               var42 = var50;
               var34.add(new PostPass.TargetInput(var17, var18, var19, var42));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

      return new PostPass(var32, var1.outputTarget(), var1.uniforms(), var34);
   }

   public void addToFrame(FrameGraphBuilder var1, int var2, int var3, TargetBundle var4) {
      GpuBufferSlice var5 = this.projectionMatrixBuffer.getBuffer((float)var2, (float)var3);
      HashMap var6 = new HashMap(this.internalTargets.size() + this.externalTargets.size());

      for(ResourceLocation var8 : this.externalTargets) {
         var6.put(var8, var4.getOrThrow(var8));
      }

      for(Map.Entry var16 : this.internalTargets.entrySet()) {
         ResourceLocation var9 = (ResourceLocation)var16.getKey();
         PostChainConfig.InternalTarget var10 = (PostChainConfig.InternalTarget)var16.getValue();
         RenderTargetDescriptor var11 = new RenderTargetDescriptor((Integer)var10.width().orElse(var2), (Integer)var10.height().orElse(var3), true, var10.clearColor());
         if (var10.persistent()) {
            RenderTarget var12 = this.getOrCreatePersistentTarget(var9, var11);
            var6.put(var9, var1.importExternal(var9.toString(), var12));
         } else {
            var6.put(var9, var1.createInternal(var9.toString(), var11));
         }
      }

      for(PostPass var17 : this.passes) {
         var17.addToFrame(var1, var6, var5);
      }

      for(ResourceLocation var18 : this.externalTargets) {
         var4.replace(var18, (ResourceHandle)var6.get(var18));
      }

   }

   /** @deprecated */
   @Deprecated
   public void process(RenderTarget var1, GraphicsResourceAllocator var2) {
      FrameGraphBuilder var3 = new FrameGraphBuilder();
      TargetBundle var4 = PostChain.TargetBundle.of(MAIN_TARGET_ID, var3.importExternal("main", var1));
      this.addToFrame(var3, var1.width, var1.height, var4);
      var3.execute(var2);
   }

   private RenderTarget getOrCreatePersistentTarget(ResourceLocation var1, RenderTargetDescriptor var2) {
      RenderTarget var3 = (RenderTarget)this.persistentTargets.get(var1);
      if (var3 == null || var3.width != var2.width() || var3.height != var2.height()) {
         if (var3 != null) {
            var3.destroyBuffers();
         }

         var3 = var2.allocate();
         var2.prepare(var3);
         this.persistentTargets.put(var1, var3);
      }

      return var3;
   }

   public void close() {
      this.persistentTargets.values().forEach(RenderTarget::destroyBuffers);
      this.persistentTargets.clear();

      for(PostPass var2 : this.passes) {
         var2.close();
      }

   }

   public interface TargetBundle {
      static TargetBundle of(final ResourceLocation var0, final ResourceHandle<RenderTarget> var1) {
         return new TargetBundle() {
            private ResourceHandle<RenderTarget> handle = var1;

            public void replace(ResourceLocation var1x, ResourceHandle<RenderTarget> var2) {
               if (var1x.equals(var0)) {
                  this.handle = var2;
               } else {
                  throw new IllegalArgumentException("No target with id " + String.valueOf(var1x));
               }
            }

            @Nullable
            public ResourceHandle<RenderTarget> get(ResourceLocation var1x) {
               return var1x.equals(var0) ? this.handle : null;
            }
         };
      }

      void replace(ResourceLocation var1, ResourceHandle<RenderTarget> var2);

      @Nullable
      ResourceHandle<RenderTarget> get(ResourceLocation var1);

      default ResourceHandle<RenderTarget> getOrThrow(ResourceLocation var1) {
         ResourceHandle var2 = this.get(var1);
         if (var2 == null) {
            throw new IllegalArgumentException("Missing target with id " + String.valueOf(var1));
         } else {
            return var2;
         }
      }
   }
}
