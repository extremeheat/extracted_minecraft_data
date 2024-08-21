package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets.SetView;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
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
      Stream var4 = var0.passes().stream().flatMap(var0x -> var0x.inputs().stream()).flatMap(var0x -> var0x.referencedTargets().stream());
      Set var5 = var4.filter(var1x -> !var0.internalTargets().containsKey(var1x)).collect(Collectors.toSet());
      SetView var6 = Sets.difference(var5, var3);
      if (!var6.isEmpty()) {
         throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + var6);
      } else {
         Builder var7 = ImmutableList.builder();

         for (PostChainConfig.Pass var9 : var0.passes()) {
            var7.add(createPass(var1, var2, var9));
         }

         return new PostChain(var7.build(), var0.internalTargets(), var5);
      }
   }

   // $VF: Inserted dummy exception handlers to handle obfuscated exceptions
   private static PostPass createPass(TextureManager var0, ShaderManager var1, PostChainConfig.Pass var2) throws ShaderManager.CompilationException {
      ResourceLocation var3 = var2.program();
      CompiledShaderProgram var4 = var1.getProgram(new ShaderProgram(var3, DefaultVertexFormat.POSITION, ShaderDefines.EMPTY));
      if (var4 == null) {
         throw new ShaderManager.CompilationException("Shader '" + var3 + "' could not be loaded");
      } else {
         for (PostChainConfig.Uniform var6 : var2.uniforms()) {
            String var7 = var6.name();
            if (var4.getUniform(var7) == null) {
               throw new ShaderManager.CompilationException("Uniform '" + var7 + "' does not exist for " + var3);
            }
         }

         String var32 = var3.toString();
         PostPass var33 = new PostPass(var32, var4, var2.outputTarget(), var2.uniforms());

         for (Object var8 : var2.inputs()) {
            Objects.requireNonNull(var8);
            Throwable var45;
            switch (var8) {
               case PostChainConfig.TextureInput var11:
                  PostChainConfig.TextureInput var53 = var11;

                  try {
                     var54 = var53.samplerName();
                  } catch (Throwable var31) {
                     var45 = var31;
                     boolean var66 = false;
                     break;
                  }

                  String var36 = var54;
                  PostChainConfig.TextureInput var55 = var11;

                  try {
                     var56 = var55.location();
                  } catch (Throwable var30) {
                     var45 = var30;
                     boolean var67 = false;
                     break;
                  }

                  ResourceLocation var37 = var56;
                  PostChainConfig.TextureInput var57 = var11;

                  try {
                     var58 = var57.width();
                  } catch (Throwable var29) {
                     var45 = var29;
                     boolean var68 = false;
                     break;
                  }

                  int var38 = var58;
                  PostChainConfig.TextureInput var59 = var11;

                  try {
                     var60 = var59.height();
                  } catch (Throwable var28) {
                     var45 = var28;
                     boolean var69 = false;
                     break;
                  }

                  int var39 = var60;
                  PostChainConfig.TextureInput var61 = var11;

                  try {
                     var62 = var61.bilinear();
                  } catch (Throwable var27) {
                     var45 = var27;
                     boolean var70 = false;
                     break;
                  }

                  boolean var40 = var62;
                  AbstractTexture var41 = var0.getTexture(var37.withPath(var0x -> "textures/effect/" + var0x + ".png"));
                  var41.setFilter(var40, false);
                  var33.addInput(new PostPass.TextureInput(var36, var41, var38, var39));
                  continue;
               case PostChainConfig.TargetInput var17:
                  PostChainConfig.TargetInput var10000 = var17;

                  try {
                     var46 = var10000.samplerName();
                  } catch (Throwable var26) {
                     var45 = var26;
                     boolean var10001 = false;
                     break;
                  }

                  String var22 = var46;
                  PostChainConfig.TargetInput var47 = var17;

                  try {
                     var48 = var47.targetId();
                  } catch (Throwable var25) {
                     var45 = var25;
                     boolean var63 = false;
                     break;
                  }

                  ResourceLocation var42 = var48;
                  PostChainConfig.TargetInput var49 = var17;

                  try {
                     var50 = var49.useDepthBuffer();
                  } catch (Throwable var24) {
                     var45 = var24;
                     boolean var64 = false;
                     break;
                  }

                  boolean var43 = var50;
                  PostChainConfig.TargetInput var51 = var17;

                  try {
                     var52 = var51.bilinear();
                  } catch (Throwable var23) {
                     var45 = var23;
                     boolean var65 = false;
                     break;
                  }

                  boolean var44 = var52;
                  var33.addInput(new PostPass.TargetInput(var22, var42, var43, var44));
                  continue;
               default:
                  throw new MatchException(null, null);
            }

            Throwable var35 = var45;
            throw new MatchException(var35.toString(), var35);
         }

         return var33;
      }
   }

   // $VF: Inserted dummy exception handlers to handle obfuscated exceptions
   public void addToFrame(FrameGraphBuilder var1, int var2, int var3, PostChain.TargetBundle var4) {
      Matrix4f var5 = new Matrix4f().setOrtho(0.0F, (float)var2, 0.0F, (float)var3, 0.1F, 1000.0F);
      HashMap var6 = new HashMap(this.internalTargets.size() + this.externalTargets.size());

      for (ResourceLocation var8 : this.externalTargets) {
         var6.put(var8, var4.getOrThrow(var8));
      }

      for (Entry var23 : this.internalTargets.entrySet()) {
         ResourceLocation var9 = (ResourceLocation)var23.getKey();
         PostChainConfig.InternalTarget var35;
         Objects.requireNonNull(var35);
         Object var11 = var35;

         var35 = (PostChainConfig.InternalTarget)var23.getValue();
         RenderTargetDescriptor var10 = switch (var11) {
            case PostChainConfig.FixedSizedTarget var13 -> {
               PostChainConfig.FixedSizedTarget var29 = var13;

               int var26;
               label56: {
                  label76: {
                     try {
                        var31 = var29.width();
                     } catch (Throwable var18) {
                        var30 = var18;
                        boolean var10001 = false;
                        break label76;
                     }

                     var26 = var31;
                     PostChainConfig.FixedSizedTarget var32 = var13;

                     try {
                        var33 = var32.height();
                        break label56;
                     } catch (Throwable var17) {
                        var30 = var17;
                        boolean var34 = false;
                     }
                  }

                  Throwable var20 = var30;
                  throw new MatchException(var20.toString(), var20);
               }

               int var27 = var33;
               yield new RenderTargetDescriptor(var26, var27, true);
            }
            case PostChainConfig.FullScreenTarget var16 -> new RenderTargetDescriptor(var2, var3, true);
            default -> throw new MatchException(null, null);
         };
         var6.put(var9, var1.createInternal(var9.toString(), var10));
      }

      for (PostPass var24 : this.passes) {
         var24.addToFrame(var1, var6, var5);
      }

      for (ResourceLocation var25 : this.externalTargets) {
         var4.replace(var25, (ResourceHandle<RenderTarget>)var6.get(var25));
      }
   }

   @Deprecated
   public void process(RenderTarget var1, GraphicsResourceAllocator var2) {
      FrameGraphBuilder var3 = new FrameGraphBuilder();
      PostChain.TargetBundle var4 = PostChain.TargetBundle.of(MAIN_TARGET_ID, var3.importExternal("main", var1));
      this.addToFrame(var3, var1.width, var1.height, var4);
      var3.execute(var2);
   }

   public void setUniform(String var1, float var2) {
      for (PostPass var4 : this.passes) {
         var4.getShader().safeGetUniform(var1).set(var2);
      }
   }

   public interface TargetBundle {
      static PostChain.TargetBundle of(final ResourceLocation var0, final ResourceHandle<RenderTarget> var1) {
         return new PostChain.TargetBundle() {
            private ResourceHandle<RenderTarget> handle = var1;

            @Override
            public void replace(ResourceLocation var1x, ResourceHandle<RenderTarget> var2) {
               if (var1x.equals(var0)) {
                  this.handle = var2;
               } else {
                  throw new IllegalArgumentException("No target with id " + var1x);
               }
            }

            @Nullable
            @Override
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
            throw new IllegalArgumentException("Missing target with id " + var1);
         } else {
            return var2;
         }
      }
   }
}
