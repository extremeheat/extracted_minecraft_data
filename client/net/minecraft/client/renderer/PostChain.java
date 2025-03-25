package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
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

   public static PostChain load(PostChainConfig var0, TextureManager var1, Set<ResourceLocation> var2, ResourceLocation var3) throws ShaderManager.CompilationException {
      Stream var4 = var0.passes().stream().flatMap(PostChainConfig.Pass::referencedTargets);
      Set var5 = (Set)var4.filter((var1x) -> !var0.internalTargets().containsKey(var1x)).collect(Collectors.toSet());
      Sets.SetView var6 = Sets.difference(var5, var2);
      if (!var6.isEmpty()) {
         throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + String.valueOf(var6));
      } else {
         ImmutableList.Builder var7 = ImmutableList.builder();

         for(int var8 = 0; var8 < var0.passes().size(); ++var8) {
            PostChainConfig.Pass var9 = (PostChainConfig.Pass)var0.passes().get(var8);
            var7.add(createPass(var1, var9, var3.withSuffix("/" + var8)));
         }

         return new PostChain(var7.build(), var0.internalTargets(), var5);
      }
   }

   private static PostPass createPass(TextureManager var0, PostChainConfig.Pass var1, ResourceLocation var2) throws ShaderManager.CompilationException {
      RenderPipeline.Builder var3 = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET).withFragmentShader(var1.fragmentShaderId()).withVertexShader(var1.vertexShaderId()).withLocation(var2);

      for(PostChainConfig.Input var5 : var1.inputs()) {
         var3.withSampler(var5.samplerName() + "Sampler");
         var3.withUniform(var5.samplerName() + "Size", UniformType.VEC2);
      }

      for(PostChainConfig.Uniform var34 : var1.uniforms()) {
         var3.withUniform(var34.name(), (UniformType)Objects.requireNonNull(UniformType.CODEC.byName(var34.type())));
      }

      RenderPipeline var33 = var3.build();
      CompiledRenderPipeline var35 = RenderSystem.getDevice().precompilePipeline(var33);

      for(PostChainConfig.Uniform var7 : var1.uniforms()) {
         String var8 = var7.name();
         if (!var35.containsUniform(var8)) {
            throw new ShaderManager.CompilationException("Uniform '" + var8 + "' does not exist for " + String.valueOf(var2));
         }
      }

      PostPass var36 = new PostPass(var33, var1.outputTarget(), var1.uniforms());

      for(PostChainConfig.Input var38 : var1.inputs()) {
         Objects.requireNonNull(var38);
         byte var10 = 0;
         //$FF: var10->value
         //0->net/minecraft/client/renderer/PostChainConfig$TextureInput
         //1->net/minecraft/client/renderer/PostChainConfig$TargetInput
         switch (var38.typeSwitch<invokedynamic>(var38, var10)) {
            case 0:
               PostChainConfig.TextureInput var11 = (PostChainConfig.TextureInput)var38;
               PostChainConfig.TextureInput var55 = var11;

               try {
                  var56 = var55.samplerName();
               } catch (Throwable var31) {
                  throw new MatchException(var31.toString(), var31);
               }

               String var39 = var56;
               String var12 = var39;
               var55 = var11;

               try {
                  var58 = var55.location();
               } catch (Throwable var30) {
                  throw new MatchException(var30.toString(), var30);
               }

               ResourceLocation var40 = var58;
               ResourceLocation var13 = var40;
               var55 = var11;

               try {
                  var60 = var55.width();
               } catch (Throwable var29) {
                  throw new MatchException(var29.toString(), var29);
               }

               int var41 = var60;
               int var14 = var41;
               var55 = var11;

               try {
                  var62 = var55.height();
               } catch (Throwable var28) {
                  throw new MatchException(var28.toString(), var28);
               }

               var41 = var62;
               int var15 = var41;
               var55 = var11;

               try {
                  var64 = var55.bilinear();
               } catch (Throwable var27) {
                  throw new MatchException(var27.toString(), var27);
               }

               var41 = var64;
               boolean var16 = (boolean)var41;
               AbstractTexture var44 = var0.getTexture(var13.withPath((UnaryOperator)((var0x) -> "textures/effect/" + var0x + ".png")));
               var44.setFilter(var16, false);
               var36.addInput(new PostPass.TextureInput(var12, var44, var14, var15));
               break;
            case 1:
               PostChainConfig.TargetInput var17 = (PostChainConfig.TargetInput)var38;
               PostChainConfig.TargetInput var10000 = var17;

               try {
                  var48 = var10000.samplerName();
               } catch (Throwable var26) {
                  throw new MatchException(var26.toString(), var26);
               }

               String var22 = var48;
               String var18 = var22;
               var10000 = var17;

               try {
                  var50 = var10000.targetId();
               } catch (Throwable var25) {
                  throw new MatchException(var25.toString(), var25);
               }

               ResourceLocation var45 = var50;
               ResourceLocation var19 = var45;
               var10000 = var17;

               try {
                  var52 = var10000.useDepthBuffer();
               } catch (Throwable var24) {
                  throw new MatchException(var24.toString(), var24);
               }

               boolean var46 = var52;
               boolean var20 = var46;
               var10000 = var17;

               try {
                  var54 = var10000.bilinear();
               } catch (Throwable var23) {
                  throw new MatchException(var23.toString(), var23);
               }

               var46 = var54;
               var36.addInput(new PostPass.TargetInput(var18, var19, var20, var46));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

      return var36;
   }

   public void addToFrame(FrameGraphBuilder var1, int var2, int var3, TargetBundle var4, @Nullable Consumer<RenderPass> var5) {
      Matrix4f var6 = (new Matrix4f()).setOrtho(0.0F, (float)var2, 0.0F, (float)var3, 0.1F, 1000.0F);
      HashMap var7 = new HashMap(this.internalTargets.size() + this.externalTargets.size());

      for(ResourceLocation var9 : this.externalTargets) {
         var7.put(var9, var4.getOrThrow(var9));
      }

      for(Map.Entry var23 : this.internalTargets.entrySet()) {
         ResourceLocation var10 = (ResourceLocation)var23.getKey();
         PostChainConfig.InternalTarget var10000 = (PostChainConfig.InternalTarget)var23.getValue();
         Objects.requireNonNull(var10000);
         PostChainConfig.InternalTarget var12 = var10000;
         byte var13 = 0;
         RenderTargetDescriptor var28;
         //$FF: var13->value
         //0->net/minecraft/client/renderer/PostChainConfig$FixedSizedTarget
         //1->net/minecraft/client/renderer/PostChainConfig$FullScreenTarget
         switch (var12.typeSwitch<invokedynamic>(var12, var13)) {
            case 0:
               PostChainConfig.FixedSizedTarget var14 = (PostChainConfig.FixedSizedTarget)var12;
               PostChainConfig.FixedSizedTarget var29 = var14;

               try {
                  var30 = var29.width();
               } catch (Throwable var19) {
                  throw new MatchException(var19.toString(), var19);
               }

               int var26 = var30;
               int var15 = var26;
               var29 = var14;

               try {
                  var32 = var29.height();
               } catch (Throwable var18) {
                  throw new MatchException(var18.toString(), var18);
               }

               var26 = var32;
               var28 = new RenderTargetDescriptor(var15, var26, true, 0);
               break;
            case 1:
               PostChainConfig.FullScreenTarget var17 = (PostChainConfig.FullScreenTarget)var12;
               var28 = new RenderTargetDescriptor(var2, var3, true, 0);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         RenderTargetDescriptor var11 = var28;
         var7.put(var10, var1.createInternal(var10.toString(), var11));
      }

      for(PostPass var24 : this.passes) {
         var24.addToFrame(var1, var7, var6, var5);
      }

      for(ResourceLocation var25 : this.externalTargets) {
         var4.replace(var25, (ResourceHandle)var7.get(var25));
      }

   }

   /** @deprecated */
   @Deprecated
   public void process(RenderTarget var1, GraphicsResourceAllocator var2, @Nullable Consumer<RenderPass> var3) {
      FrameGraphBuilder var4 = new FrameGraphBuilder();
      TargetBundle var5 = PostChain.TargetBundle.of(MAIN_TARGET_ID, var4.importExternal("main", var1));
      this.addToFrame(var4, var1.width, var1.height, var5, var3);
      var4.execute(var2);
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
