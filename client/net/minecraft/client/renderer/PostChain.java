package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
      Stream var4 = var0.passes().stream().flatMap((var0x) -> {
         return var0x.inputs().stream();
      }).flatMap((var0x) -> {
         return var0x.referencedTargets().stream();
      });
      Set var5 = (Set)var4.filter((var1x) -> {
         return !var0.internalTargets().containsKey(var1x);
      }).collect(Collectors.toSet());
      Sets.SetView var6 = Sets.difference(var5, var3);
      if (!var6.isEmpty()) {
         throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + String.valueOf(var6));
      } else {
         ImmutableList.Builder var7 = ImmutableList.builder();
         Iterator var8 = var0.passes().iterator();

         while(var8.hasNext()) {
            PostChainConfig.Pass var9 = (PostChainConfig.Pass)var8.next();
            var7.add(createPass(var1, var2, var9));
         }

         return new PostChain(var7.build(), var0.internalTargets(), var5);
      }
   }

   private static PostPass createPass(TextureManager var0, ShaderManager var1, PostChainConfig.Pass var2) throws ShaderManager.CompilationException {
      ResourceLocation var3 = var2.program();
      CompiledShaderProgram var4 = var1.getProgramForLoading(new ShaderProgram(var3, DefaultVertexFormat.POSITION, ShaderDefines.EMPTY));
      Iterator var5 = var2.uniforms().iterator();

      String var7;
      do {
         if (!var5.hasNext()) {
            String var32 = var3.toString();
            PostPass var33 = new PostPass(var32, var4, var2.outputTarget(), var2.uniforms());
            Iterator var34 = var2.inputs().iterator();

            while(var34.hasNext()) {
               PostChainConfig.Input var8 = (PostChainConfig.Input)var34.next();
               Objects.requireNonNull(var8);
               byte var10 = 0;
               String var35;
               ResourceLocation var36;
               boolean var39;
               //$FF: var10->value
               //0->net/minecraft/client/renderer/PostChainConfig$TextureInput
               //1->net/minecraft/client/renderer/PostChainConfig$TargetInput
               switch (var8.typeSwitch<invokedynamic>(var8, var10)) {
                  case 0:
                     PostChainConfig.TextureInput var11 = (PostChainConfig.TextureInput)var8;
                     PostChainConfig.TextureInput var41 = var11;

                     try {
                        var35 = var41.samplerName();
                     } catch (Throwable var31) {
                        throw new MatchException(var31.toString(), var31);
                     }

                     String var37 = var35;
                     String var12 = var37;
                     var41 = var11;

                     try {
                        var36 = var41.location();
                     } catch (Throwable var30) {
                        throw new MatchException(var30.toString(), var30);
                     }

                     ResourceLocation var38 = var36;
                     ResourceLocation var13 = var38;
                     var41 = var11;

                     int var44;
                     try {
                        var44 = var41.width();
                     } catch (Throwable var29) {
                        throw new MatchException(var29.toString(), var29);
                     }

                     int var40 = var44;
                     int var14 = var40;
                     var41 = var11;

                     try {
                        var44 = var41.height();
                     } catch (Throwable var28) {
                        throw new MatchException(var28.toString(), var28);
                     }

                     var40 = var44;
                     int var15 = var40;
                     var41 = var11;

                     try {
                        var39 = var41.bilinear();
                     } catch (Throwable var27) {
                        throw new MatchException(var27.toString(), var27);
                     }

                     boolean var42 = var39;
                     boolean var16 = var42;
                     AbstractTexture var43 = var0.getTexture(var13.withPath((var0x) -> {
                        return "textures/effect/" + var0x + ".png";
                     }));
                     var43.setFilter(var16, false);
                     var33.addInput(new PostPass.TextureInput(var12, var43, var14, var15));
                     break;
                  case 1:
                     PostChainConfig.TargetInput var17 = (PostChainConfig.TargetInput)var8;
                     PostChainConfig.TargetInput var10000 = var17;

                     try {
                        var35 = var10000.samplerName();
                     } catch (Throwable var26) {
                        throw new MatchException(var26.toString(), var26);
                     }

                     String var22 = var35;
                     String var18 = var22;
                     var10000 = var17;

                     try {
                        var36 = var10000.targetId();
                     } catch (Throwable var25) {
                        throw new MatchException(var25.toString(), var25);
                     }

                     ResourceLocation var45 = var36;
                     ResourceLocation var19 = var45;
                     var10000 = var17;

                     try {
                        var39 = var10000.useDepthBuffer();
                     } catch (Throwable var24) {
                        throw new MatchException(var24.toString(), var24);
                     }

                     boolean var46 = var39;
                     boolean var20 = var46;
                     var10000 = var17;

                     try {
                        var39 = var10000.bilinear();
                     } catch (Throwable var23) {
                        throw new MatchException(var23.toString(), var23);
                     }

                     var46 = var39;
                     var33.addInput(new PostPass.TargetInput(var18, var19, var20, var46));
                     break;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
               }
            }

            return var33;
         }

         PostChainConfig.Uniform var6 = (PostChainConfig.Uniform)var5.next();
         var7 = var6.name();
      } while(var4.getUniform(var7) != null);

      throw new ShaderManager.CompilationException("Uniform '" + var7 + "' does not exist for " + String.valueOf(var3));
   }

   public void addToFrame(FrameGraphBuilder var1, int var2, int var3, TargetBundle var4) {
      Matrix4f var5 = (new Matrix4f()).setOrtho(0.0F, (float)var2, 0.0F, (float)var3, 0.1F, 1000.0F);
      HashMap var6 = new HashMap(this.internalTargets.size() + this.externalTargets.size());
      Iterator var7 = this.externalTargets.iterator();

      ResourceLocation var8;
      while(var7.hasNext()) {
         var8 = (ResourceLocation)var7.next();
         var6.put(var8, var4.getOrThrow(var8));
      }

      var7 = this.internalTargets.entrySet().iterator();

      while(var7.hasNext()) {
         Map.Entry var19 = (Map.Entry)var7.next();
         ResourceLocation var9 = (ResourceLocation)var19.getKey();
         PostChainConfig.InternalTarget var10000 = (PostChainConfig.InternalTarget)var19.getValue();
         Objects.requireNonNull(var10000);
         PostChainConfig.InternalTarget var11 = var10000;
         byte var12 = 0;
         RenderTargetDescriptor var21;
         //$FF: var12->value
         //0->net/minecraft/client/renderer/PostChainConfig$FixedSizedTarget
         //1->net/minecraft/client/renderer/PostChainConfig$FullScreenTarget
         switch (var11.typeSwitch<invokedynamic>(var11, var12)) {
            case 0:
               PostChainConfig.FixedSizedTarget var13 = (PostChainConfig.FixedSizedTarget)var11;
               PostChainConfig.FixedSizedTarget var23 = var13;

               int var24;
               try {
                  var24 = var23.width();
               } catch (Throwable var18) {
                  throw new MatchException(var18.toString(), var18);
               }

               int var22 = var24;
               int var14 = var22;
               var23 = var13;

               try {
                  var24 = var23.height();
               } catch (Throwable var17) {
                  throw new MatchException(var17.toString(), var17);
               }

               var22 = var24;
               var21 = new RenderTargetDescriptor(var14, var22, true);
               break;
            case 1:
               PostChainConfig.FullScreenTarget var16 = (PostChainConfig.FullScreenTarget)var11;
               var21 = new RenderTargetDescriptor(var2, var3, true);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         RenderTargetDescriptor var10 = var21;
         var6.put(var9, var1.createInternal(var9.toString(), var10));
      }

      var7 = this.passes.iterator();

      while(var7.hasNext()) {
         PostPass var20 = (PostPass)var7.next();
         var20.addToFrame(var1, var6, var5);
      }

      var7 = this.externalTargets.iterator();

      while(var7.hasNext()) {
         var8 = (ResourceLocation)var7.next();
         var4.replace(var8, (ResourceHandle)var6.get(var8));
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
      Iterator var3 = this.passes.iterator();

      while(var3.hasNext()) {
         PostPass var4 = (PostPass)var3.next();
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
