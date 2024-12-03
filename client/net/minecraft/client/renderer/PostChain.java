package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
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

   public static PostChain load(PostChainConfig var0, TextureManager var1, ShaderManager var2, Set<ResourceLocation> var3) throws ShaderManager.CompilationException {
      Stream var4 = var0.passes().stream().flatMap((var0x) -> var0x.inputs().stream()).flatMap((var0x) -> var0x.referencedTargets().stream());
      Set var5 = (Set)var4.filter((var1x) -> !var0.internalTargets().containsKey(var1x)).collect(Collectors.toSet());
      Sets.SetView var6 = Sets.difference(var5, var3);
      if (!var6.isEmpty()) {
         throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + String.valueOf(var6));
      } else {
         ImmutableList.Builder var7 = ImmutableList.builder();

         for(PostChainConfig.Pass var9 : var0.passes()) {
            var7.add(createPass(var1, var2, var9));
         }

         return new PostChain(var7.build(), var0.internalTargets(), var5);
      }
   }

   private static PostPass createPass(TextureManager var0, ShaderManager var1, PostChainConfig.Pass var2) throws ShaderManager.CompilationException {
      CompiledShaderProgram var3 = var1.getProgramForLoading(var2.program());

      for(PostChainConfig.Uniform var5 : var2.uniforms()) {
         String var6 = var5.name();
         if (var3.getUniform(var6) == null) {
            throw new ShaderManager.CompilationException("Uniform '" + var6 + "' does not exist for " + String.valueOf(var2.programId()));
         }
      }

      String var31 = var2.programId().toString();
      PostPass var32 = new PostPass(var31, var3, var2.outputTarget(), var2.uniforms());

      for(PostChainConfig.Input var7 : var2.inputs()) {
         Objects.requireNonNull(var7);
         byte var9 = 0;
         //$FF: var9->value
         //0->net/minecraft/client/renderer/PostChainConfig$TextureInput
         //1->net/minecraft/client/renderer/PostChainConfig$TargetInput
         switch (var7.typeSwitch<invokedynamic>(var7, var9)) {
            case 0:
               PostChainConfig.TextureInput var10 = (PostChainConfig.TextureInput)var7;
               PostChainConfig.TextureInput var50 = var10;

               try {
                  var51 = var50.samplerName();
               } catch (Throwable var30) {
                  throw new MatchException(var30.toString(), var30);
               }

               String var34 = var51;
               String var11 = var34;
               var50 = var10;

               try {
                  var53 = var50.location();
               } catch (Throwable var29) {
                  throw new MatchException(var29.toString(), var29);
               }

               ResourceLocation var35 = var53;
               ResourceLocation var12 = var35;
               var50 = var10;

               try {
                  var55 = var50.width();
               } catch (Throwable var28) {
                  throw new MatchException(var28.toString(), var28);
               }

               int var36 = var55;
               int var13 = var36;
               var50 = var10;

               try {
                  var57 = var50.height();
               } catch (Throwable var27) {
                  throw new MatchException(var27.toString(), var27);
               }

               var36 = var57;
               int var14 = var36;
               var50 = var10;

               try {
                  var59 = var50.bilinear();
               } catch (Throwable var26) {
                  throw new MatchException(var26.toString(), var26);
               }

               var36 = var59;
               boolean var15 = (boolean)var36;
               AbstractTexture var39 = var0.getTexture(var12.withPath((UnaryOperator)((var0x) -> "textures/effect/" + var0x + ".png")));
               var39.setFilter(var15, false);
               var32.addInput(new PostPass.TextureInput(var11, var39, var13, var14));
               break;
            case 1:
               PostChainConfig.TargetInput var16 = (PostChainConfig.TargetInput)var7;
               PostChainConfig.TargetInput var10000 = var16;

               try {
                  var43 = var10000.samplerName();
               } catch (Throwable var25) {
                  throw new MatchException(var25.toString(), var25);
               }

               String var21 = var43;
               String var17 = var21;
               var10000 = var16;

               try {
                  var45 = var10000.targetId();
               } catch (Throwable var24) {
                  throw new MatchException(var24.toString(), var24);
               }

               ResourceLocation var40 = var45;
               ResourceLocation var18 = var40;
               var10000 = var16;

               try {
                  var47 = var10000.useDepthBuffer();
               } catch (Throwable var23) {
                  throw new MatchException(var23.toString(), var23);
               }

               boolean var41 = var47;
               boolean var19 = var41;
               var10000 = var16;

               try {
                  var49 = var10000.bilinear();
               } catch (Throwable var22) {
                  throw new MatchException(var22.toString(), var22);
               }

               var41 = var49;
               var32.addInput(new PostPass.TargetInput(var17, var18, var19, var41));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

      return var32;
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
               var27 = new RenderTargetDescriptor(var14, var25, true);
               break;
            case 1:
               PostChainConfig.FullScreenTarget var16 = (PostChainConfig.FullScreenTarget)var11;
               var27 = new RenderTargetDescriptor(var2, var3, true);
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
