package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets.SetView;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import org.joml.Matrix4f;

public class PostChain implements AutoCloseable {
   public static final ResourceLocation MAIN_TARGET_ID = ResourceLocation.withDefaultNamespace("main");
   private final ResourceLocation id;
   private final List<PostPass> passes;
   private final Map<ResourceLocation, PostChainConfig.InternalTarget> internalTargets;
   private final Set<ResourceLocation> externalTargets;
   private float time;

   private PostChain(ResourceLocation var1, List<PostPass> var2, Map<ResourceLocation, PostChainConfig.InternalTarget> var3, Set<ResourceLocation> var4) {
      super();
      this.id = var1;
      this.passes = var2;
      this.internalTargets = var3;
      this.externalTargets = var4;
   }

   public static PostChain load(ResourceProvider var0, TextureManager var1, ResourceLocation var2, Set<ResourceLocation> var3) throws IOException, JsonSyntaxException {
      Resource var4 = var0.getResourceOrThrow(var2);

      try {
         PostChain var18;
         try (BufferedReader var5 = var4.openAsReader()) {
            JsonObject var17 = GsonHelper.parse(var5);
            PostChainConfig var7 = (PostChainConfig)PostChainConfig.CODEC.parse(JsonOps.INSTANCE, var17).getOrThrow(JsonSyntaxException::new);
            Stream var8 = var7.passes().stream().flatMap(var0x -> var0x.inputs().stream()).flatMap(var0x -> var0x.referencedTargets().stream());
            Set var9 = var8.filter(var1x -> !var7.internalTargets().containsKey(var1x)).collect(Collectors.toSet());
            SetView var10 = Sets.difference(var9, var3);
            if (!var10.isEmpty()) {
               throw new ChainedJsonException("Referenced external targets are not available in this context: " + var10);
            }

            Builder var11 = ImmutableList.builder();

            for (PostChainConfig.Pass var13 : var7.passes()) {
               var11.add(createPass(var0, var1, var13));
            }

            var18 = new PostChain(var2, var11.build(), var7.internalTargets(), var9);
         }

         return var18;
      } catch (Exception var16) {
         ChainedJsonException var6 = ChainedJsonException.forException(var16);
         var6.setFilenameAndFlush(var2.getPath() + " (" + var4.sourcePackId() + ")");
         throw var6;
      }
   }

   // $VF: Inserted dummy exception handlers to handle obfuscated exceptions
   private static PostPass createPass(ResourceProvider var0, TextureManager var1, PostChainConfig.Pass var2) throws IOException {
      PostPass var3 = new PostPass(var0, var2.name(), var2.outputTarget());

      for (Object var5 : var2.inputs()) {
         Objects.requireNonNull(var5);
         Throwable var43;
         switch (var5) {
            case PostChainConfig.TextureInput var8:
               PostChainConfig.TextureInput var51 = var8;

               try {
                  var52 = var51.samplerName();
               } catch (Throwable var28) {
                  var43 = var28;
                  boolean var64 = false;
                  break;
               }

               String var33 = var52;
               PostChainConfig.TextureInput var53 = var8;

               try {
                  var54 = var53.location();
               } catch (Throwable var27) {
                  var43 = var27;
                  boolean var65 = false;
                  break;
               }

               ResourceLocation var34 = var54;
               ResourceLocation var10 = var34;
               PostChainConfig.TextureInput var55 = var8;

               try {
                  var56 = var55.width();
               } catch (Throwable var26) {
                  var43 = var26;
                  boolean var66 = false;
                  break;
               }

               int var35 = var56;
               PostChainConfig.TextureInput var57 = var8;

               try {
                  var58 = var57.height();
               } catch (Throwable var25) {
                  var43 = var25;
                  boolean var67 = false;
                  break;
               }

               int var36 = var58;
               PostChainConfig.TextureInput var59 = var8;

               try {
                  var60 = var59.bilinear();
               } catch (Throwable var24) {
                  var43 = var24;
                  boolean var68 = false;
                  break;
               }

               boolean var37 = var60;
               ResourceLocation var38 = var10.withPath(var0x -> "textures/effect/" + var0x + ".png");
               var0.getResource(var38).orElseThrow(() -> new ChainedJsonException("Texture '" + var10 + "' does not exist"));
               RenderSystem.setShaderTexture(0, var38);
               var1.bindForSetup(var38);
               AbstractTexture var39 = var1.getTexture(var38);
               if (var37) {
                  RenderSystem.texParameter(3553, 10241, 9729);
                  RenderSystem.texParameter(3553, 10240, 9729);
               } else {
                  RenderSystem.texParameter(3553, 10241, 9728);
                  RenderSystem.texParameter(3553, 10240, 9728);
               }

               var3.addInput(new PostPass.TextureInput(var33, var39, var35, var36));
               continue;
            case PostChainConfig.TargetInput var14:
               PostChainConfig.TargetInput var10000 = var14;

               try {
                  var44 = var10000.samplerName();
               } catch (Throwable var23) {
                  var43 = var23;
                  boolean var10001 = false;
                  break;
               }

               String var19 = var44;
               PostChainConfig.TargetInput var45 = var14;

               try {
                  var46 = var45.targetId();
               } catch (Throwable var22) {
                  var43 = var22;
                  boolean var61 = false;
                  break;
               }

               ResourceLocation var40 = var46;
               PostChainConfig.TargetInput var47 = var14;

               try {
                  var48 = var47.useDepthBuffer();
               } catch (Throwable var21) {
                  var43 = var21;
                  boolean var62 = false;
                  break;
               }

               boolean var41 = var48;
               PostChainConfig.TargetInput var49 = var14;

               try {
                  var50 = var49.bilinear();
               } catch (Throwable var20) {
                  var43 = var20;
                  boolean var63 = false;
                  break;
               }

               boolean var42 = var50;
               var3.addInput(new PostPass.TargetInput(var19, var40, var41, var42));
               continue;
            default:
               throw new MatchException(null, null);
         }

         Throwable var29 = var43;
         throw new MatchException(var29.toString(), var29);
      }

      for (PostChainConfig.Uniform var31 : var2.uniforms()) {
         String var6 = var31.name();
         Uniform var32 = var3.getEffect().getUniform(var6);
         if (var32 == null) {
            throw new ChainedJsonException("Uniform '" + var6 + "' does not exist");
         }

         storeUniform(var32, var31.values());
      }

      return var3;
   }

   private static void storeUniform(Uniform var0, List<Float> var1) {
      switch (var1.size()) {
         case 0:
         default:
            break;
         case 1:
            var0.set((Float)var1.getFirst());
            break;
         case 2:
            var0.set((Float)var1.get(0), (Float)var1.get(1));
            break;
         case 3:
            var0.set((Float)var1.get(0), (Float)var1.get(1), (Float)var1.get(2));
            break;
         case 4:
            var0.set((Float)var1.get(0), (Float)var1.get(1), (Float)var1.get(2), (Float)var1.get(3));
      }
   }

   @Override
   public void close() {
      for (PostPass var2 : this.passes) {
         var2.close();
      }
   }

   // $VF: Inserted dummy exception handlers to handle obfuscated exceptions
   public void addToFrame(FrameGraphBuilder var1, DeltaTracker var2, int var3, int var4, PostChain.TargetBundle var5) {
      Matrix4f var6 = new Matrix4f().setOrtho(0.0F, (float)var3, 0.0F, (float)var4, 0.1F, 1000.0F);
      this.time = this.time + var2.getRealtimeDeltaTicks();

      while (this.time > 20.0F) {
         this.time -= 20.0F;
      }

      HashMap var7 = new HashMap(this.internalTargets.size() + this.externalTargets.size());

      for (ResourceLocation var9 : this.externalTargets) {
         var7.put(var9, var5.getOrThrow(var9));
      }

      for (Entry var24 : this.internalTargets.entrySet()) {
         ResourceLocation var10 = (ResourceLocation)var24.getKey();
         PostChainConfig.InternalTarget var36;
         Objects.requireNonNull(var36);
         Object var12 = var36;

         var36 = (PostChainConfig.InternalTarget)var24.getValue();
         RenderTargetDescriptor var11 = switch (var12) {
            case PostChainConfig.FixedSizedTarget var14 -> {
               PostChainConfig.FixedSizedTarget var30 = var14;

               int var27;
               label59: {
                  label85: {
                     try {
                        var32 = var30.width();
                     } catch (Throwable var19) {
                        var31 = var19;
                        boolean var10001 = false;
                        break label85;
                     }

                     var27 = var32;
                     PostChainConfig.FixedSizedTarget var33 = var14;

                     try {
                        var34 = var33.height();
                        break label59;
                     } catch (Throwable var18) {
                        var31 = var18;
                        boolean var35 = false;
                     }
                  }

                  Throwable var21 = var31;
                  throw new MatchException(var21.toString(), var21);
               }

               int var28 = var34;
               yield new RenderTargetDescriptor(var27, var28, true);
            }
            case PostChainConfig.FullScreenTarget var17 -> new RenderTargetDescriptor(var3, var4, true);
            default -> throw new MatchException(null, null);
         };
         var7.put(var10, var1.createInternal(var10.toString(), var11));
      }

      for (PostPass var25 : this.passes) {
         var25.addToFrame(var1, var7, var6, this.time / 20.0F);
      }

      for (ResourceLocation var26 : this.externalTargets) {
         var5.replace(var26, (ResourceHandle<RenderTarget>)var7.get(var26));
      }
   }

   @Deprecated
   public void process(RenderTarget var1, GraphicsResourceAllocator var2, DeltaTracker var3) {
      FrameGraphBuilder var4 = new FrameGraphBuilder();
      PostChain.TargetBundle var5 = PostChain.TargetBundle.of(MAIN_TARGET_ID, var4.importExternal("main", var1));
      this.addToFrame(var4, var3, var1.width, var1.height, var5);
      var4.execute(var2);
   }

   public void setUniform(String var1, float var2) {
      for (PostPass var4 : this.passes) {
         var4.getEffect().safeGetUniform(var1).set(var2);
      }
   }

   public final ResourceLocation getId() {
      return this.id;
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
