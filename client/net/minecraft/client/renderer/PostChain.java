package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.shaders.Uniform;
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
import org.joml.Matrix4f;

public class PostChain {
   public static final ResourceLocation MAIN_TARGET_ID = ResourceLocation.withDefaultNamespace("main");
   private final List<PostPass> passes;
   private final Map<ResourceLocation, PostChainConfig.InternalTarget> internalTargets;
   private final Set<ResourceLocation> externalTargets;

   private PostChain(List<PostPass> var1, Map<ResourceLocation, PostChainConfig.InternalTarget> var2, Set<ResourceLocation> var3) {
      super();
      this.passes = var1;
      this.internalTargets = var2;
      this.externalTargets = var3;
   }

   public static PostChain load(PostChainConfig var0, TextureManager var1, ShaderManager var2, Set<ResourceLocation> var3, ResourceLocation var4) throws ShaderManager.CompilationException {
      Stream var5 = var0.passes().stream().flatMap(PostChainConfig.Pass::referencedTargets);
      Set var6 = (Set)var5.filter((var1x) -> !var0.internalTargets().containsKey(var1x)).collect(Collectors.toSet());
      Sets.SetView var7 = Sets.difference(var6, var3);
      if (!var7.isEmpty()) {
         throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + String.valueOf(var7));
      } else {
         ImmutableList.Builder var8 = ImmutableList.builder();

         for(int var9 = 0; var9 < var0.passes().size(); ++var9) {
            PostChainConfig.Pass var10 = (PostChainConfig.Pass)var0.passes().get(var9);
            var8.add(createPass(var1, var2, var10, var4.withSuffix("/" + var9)));
         }

         return new PostChain(var8.build(), var0.internalTargets(), var6);
      }
   }

   private static PostPass createPass(TextureManager var0, ShaderManager var1, PostChainConfig.Pass var2, ResourceLocation var3) throws ShaderManager.CompilationException {
      RenderPipeline.Builder var4 = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET).withFragmentShader(var2.fragmentShaderId()).withVertexShader(var2.vertexShaderId()).withLocation(var3);

      for(PostChainConfig.Input var6 : var2.inputs()) {
         var4.withSampler(var6.samplerName() + "Sampler");
         var4.withUniform(var6.samplerName() + "Size", Uniform.Type.VEC2);
      }

      for(PostChainConfig.Uniform var35 : var2.uniforms()) {
         var4.withUniform(var35.name(), (Uniform.Type)Objects.requireNonNull(Uniform.Type.CODEC.byName(var35.type())));
      }

      RenderPipeline var34 = var4.build();
      CompiledShaderProgram var36 = var1.getProgramForLoading(var34);

      for(PostChainConfig.Uniform var8 : var2.uniforms()) {
         String var9 = var8.name();
         if (var36.getUniform(var9) == null) {
            throw new ShaderManager.CompilationException("Uniform '" + var9 + "' does not exist for " + String.valueOf(var3));
         }
      }

      PostPass var37 = new PostPass(var34, var36, var2.outputTarget(), var2.uniforms());

      for(PostChainConfig.Input var39 : var2.inputs()) {
         Objects.requireNonNull(var39);
         byte var11 = 0;
         //$FF: var11->value
         //0->net/minecraft/client/renderer/PostChainConfig$TextureInput
         //1->net/minecraft/client/renderer/PostChainConfig$TargetInput
         switch (var39.typeSwitch<invokedynamic>(var39, var11)) {
            case 0:
               PostChainConfig.TextureInput var12 = (PostChainConfig.TextureInput)var39;
               PostChainConfig.TextureInput var56 = var12;

               try {
                  var57 = var56.samplerName();
               } catch (Throwable var32) {
                  throw new MatchException(var32.toString(), var32);
               }

               String var40 = var57;
               String var13 = var40;
               var56 = var12;

               try {
                  var59 = var56.location();
               } catch (Throwable var31) {
                  throw new MatchException(var31.toString(), var31);
               }

               ResourceLocation var41 = var59;
               ResourceLocation var14 = var41;
               var56 = var12;

               try {
                  var61 = var56.width();
               } catch (Throwable var30) {
                  throw new MatchException(var30.toString(), var30);
               }

               int var42 = var61;
               int var15 = var42;
               var56 = var12;

               try {
                  var63 = var56.height();
               } catch (Throwable var29) {
                  throw new MatchException(var29.toString(), var29);
               }

               var42 = var63;
               int var16 = var42;
               var56 = var12;

               try {
                  var65 = var56.bilinear();
               } catch (Throwable var28) {
                  throw new MatchException(var28.toString(), var28);
               }

               var42 = var65;
               boolean var17 = (boolean)var42;
               AbstractTexture var45 = var0.getTexture(var14.withPath((UnaryOperator)((var0x) -> "textures/effect/" + var0x + ".png")));
               var45.setFilter(var17, false);
               var37.addInput(new PostPass.TextureInput(var13, var45, var15, var16));
               break;
            case 1:
               PostChainConfig.TargetInput var18 = (PostChainConfig.TargetInput)var39;
               PostChainConfig.TargetInput var10000 = var18;

               try {
                  var49 = var10000.samplerName();
               } catch (Throwable var27) {
                  throw new MatchException(var27.toString(), var27);
               }

               String var23 = var49;
               String var19 = var23;
               var10000 = var18;

               try {
                  var51 = var10000.targetId();
               } catch (Throwable var26) {
                  throw new MatchException(var26.toString(), var26);
               }

               ResourceLocation var46 = var51;
               ResourceLocation var20 = var46;
               var10000 = var18;

               try {
                  var53 = var10000.useDepthBuffer();
               } catch (Throwable var25) {
                  throw new MatchException(var25.toString(), var25);
               }

               boolean var47 = var53;
               boolean var21 = var47;
               var10000 = var18;

               try {
                  var55 = var10000.bilinear();
               } catch (Throwable var24) {
                  throw new MatchException(var24.toString(), var24);
               }

               var47 = var55;
               var37.addInput(new PostPass.TargetInput(var19, var20, var21, var47));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

      return var37;
   }

   public void addToFrame(FrameGraphBuilder var1, int var2, int var3, TargetBundle var4) {
      Matrix4f var5 = (new Matrix4f()).setOrtho(0.0F, (float)var2, 0.0F, (float)var3, 0.1F, 1000.0F);
      HashMap var6 = new HashMap(this.internalTargets.size() + this.externalTargets.size());

      for(ResourceLocation var8 : this.externalTargets) {
         var6.put(var8, var4.getOrThrow(var8));
      }

      for(Map.Entry var22 : this.internalTargets.entrySet()) {
         ResourceLocation var9 = (ResourceLocation)var22.getKey();
         PostChainConfig.InternalTarget var10000 = (PostChainConfig.InternalTarget)var22.getValue();
         Objects.requireNonNull(var10000);
         PostChainConfig.InternalTarget var11 = var10000;
         byte var12 = 0;
         RenderTargetDescriptor var27;
         //$FF: var12->value
         //0->net/minecraft/client/renderer/PostChainConfig$FixedSizedTarget
         //1->net/minecraft/client/renderer/PostChainConfig$FullScreenTarget
         switch (var11.typeSwitch<invokedynamic>(var11, var12)) {
            case 0:
               PostChainConfig.FixedSizedTarget var13 = (PostChainConfig.FixedSizedTarget)var11;
               PostChainConfig.FixedSizedTarget var28 = var13;

               try {
                  var29 = var28.width();
               } catch (Throwable var18) {
                  throw new MatchException(var18.toString(), var18);
               }

               int var25 = var29;
               int var14 = var25;
               var28 = var13;

               try {
                  var31 = var28.height();
               } catch (Throwable var17) {
                  throw new MatchException(var17.toString(), var17);
               }

               var25 = var31;
               var27 = new RenderTargetDescriptor(var14, var25, true, 0);
               break;
            case 1:
               PostChainConfig.FullScreenTarget var16 = (PostChainConfig.FullScreenTarget)var11;
               var27 = new RenderTargetDescriptor(var2, var3, true, 0);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         RenderTargetDescriptor var10 = var27;
         var6.put(var9, var1.createInternal(var9.toString(), var10));
      }

      for(PostPass var23 : this.passes) {
         var23.addToFrame(var1, var6, var5);
      }

      for(ResourceLocation var24 : this.externalTargets) {
         var4.replace(var24, (ResourceHandle)var6.get(var24));
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

   public void setUniform(String var1, float var2) {
      for(PostPass var4 : this.passes) {
         var4.getShader().safeGetUniform(var1).set(var2);
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
