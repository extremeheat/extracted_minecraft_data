package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import org.joml.Matrix4f;

public class PostChain implements AutoCloseable {
   private static final String MAIN_RENDER_TARGET = "minecraft:main";
   private final RenderTarget screenTarget;
   private final ResourceProvider resourceProvider;
   private final String name;
   private final List<PostPass> passes = Lists.newArrayList();
   private final Map<String, RenderTarget> customRenderTargets = Maps.newHashMap();
   private final List<RenderTarget> fullSizedTargets = Lists.newArrayList();
   private Matrix4f shaderOrthoMatrix;
   private int screenWidth;
   private int screenHeight;
   private float time;
   private float lastStamp;

   public PostChain(TextureManager var1, ResourceProvider var2, RenderTarget var3, ResourceLocation var4) throws IOException, JsonSyntaxException {
      super();
      this.resourceProvider = var2;
      this.screenTarget = var3;
      this.time = 0.0F;
      this.lastStamp = 0.0F;
      this.screenWidth = var3.viewWidth;
      this.screenHeight = var3.viewHeight;
      this.name = var4.toString();
      this.updateOrthoMatrix();
      this.load(var1, var4);
   }

   private void load(TextureManager var1, ResourceLocation var2) throws IOException, JsonSyntaxException {
      Resource var3 = this.resourceProvider.getResourceOrThrow(var2);

      try {
         BufferedReader var4 = var3.openAsReader();

         try {
            JsonObject var17 = GsonHelper.parse((Reader)var4);
            JsonArray var6;
            int var7;
            Iterator var8;
            JsonElement var9;
            ChainedJsonException var11;
            if (GsonHelper.isArrayNode(var17, "targets")) {
               var6 = var17.getAsJsonArray("targets");
               var7 = 0;

               for(var8 = var6.iterator(); var8.hasNext(); ++var7) {
                  var9 = (JsonElement)var8.next();

                  try {
                     this.parseTargetNode(var9);
                  } catch (Exception var14) {
                     var11 = ChainedJsonException.forException(var14);
                     var11.prependJsonKey("targets[" + var7 + "]");
                     throw var11;
                  }
               }
            }

            if (GsonHelper.isArrayNode(var17, "passes")) {
               var6 = var17.getAsJsonArray("passes");
               var7 = 0;

               for(var8 = var6.iterator(); var8.hasNext(); ++var7) {
                  var9 = (JsonElement)var8.next();

                  try {
                     this.parsePassNode(var1, var9);
                  } catch (Exception var13) {
                     var11 = ChainedJsonException.forException(var13);
                     var11.prependJsonKey("passes[" + var7 + "]");
                     throw var11;
                  }
               }
            }
         } catch (Throwable var15) {
            if (var4 != null) {
               try {
                  ((Reader)var4).close();
               } catch (Throwable var12) {
                  var15.addSuppressed(var12);
               }
            }

            throw var15;
         }

         if (var4 != null) {
            ((Reader)var4).close();
         }

      } catch (Exception var16) {
         ChainedJsonException var5 = ChainedJsonException.forException(var16);
         String var10001 = var2.getPath();
         var5.setFilenameAndFlush(var10001 + " (" + var3.sourcePackId() + ")");
         throw var5;
      }
   }

   private void parseTargetNode(JsonElement var1) throws ChainedJsonException {
      if (GsonHelper.isStringValue(var1)) {
         this.addTempTarget(var1.getAsString(), this.screenWidth, this.screenHeight);
      } else {
         JsonObject var2 = GsonHelper.convertToJsonObject(var1, "target");
         String var3 = GsonHelper.getAsString(var2, "name");
         int var4 = GsonHelper.getAsInt(var2, "width", this.screenWidth);
         int var5 = GsonHelper.getAsInt(var2, "height", this.screenHeight);
         if (this.customRenderTargets.containsKey(var3)) {
            throw new ChainedJsonException(var3 + " is already defined");
         }

         this.addTempTarget(var3, var4, var5);
      }

   }

   private void parsePassNode(TextureManager var1, JsonElement var2) throws IOException {
      JsonObject var3 = GsonHelper.convertToJsonObject(var2, "pass");
      String var4 = GsonHelper.getAsString(var3, "name");
      String var5 = GsonHelper.getAsString(var3, "intarget");
      String var6 = GsonHelper.getAsString(var3, "outtarget");
      RenderTarget var7 = this.getRenderTarget(var5);
      RenderTarget var8 = this.getRenderTarget(var6);
      boolean var9 = GsonHelper.getAsBoolean(var3, "use_linear_filter", false);
      if (var7 == null) {
         throw new ChainedJsonException("Input target '" + var5 + "' does not exist");
      } else if (var8 == null) {
         throw new ChainedJsonException("Output target '" + var6 + "' does not exist");
      } else {
         PostPass var10 = this.addPass(var4, var7, var8, var9);
         JsonArray var11 = GsonHelper.getAsJsonArray(var3, "auxtargets", (JsonArray)null);
         if (var11 != null) {
            int var12 = 0;

            for(Iterator var13 = var11.iterator(); var13.hasNext(); ++var12) {
               JsonElement var14 = (JsonElement)var13.next();

               try {
                  JsonObject var15 = GsonHelper.convertToJsonObject(var14, "auxtarget");
                  String var32 = GsonHelper.getAsString(var15, "name");
                  String var17 = GsonHelper.getAsString(var15, "id");
                  boolean var18;
                  String var19;
                  if (var17.endsWith(":depth")) {
                     var18 = true;
                     var19 = var17.substring(0, var17.lastIndexOf(58));
                  } else {
                     var18 = false;
                     var19 = var17;
                  }

                  RenderTarget var20 = this.getRenderTarget(var19);
                  if (var20 == null) {
                     if (var18) {
                        throw new ChainedJsonException("Render target '" + var19 + "' can't be used as depth buffer");
                     }

                     ResourceLocation var21 = new ResourceLocation("textures/effect/" + var19 + ".png");
                     this.resourceProvider.getResource(var21).orElseThrow(() -> {
                        return new ChainedJsonException("Render target or texture '" + var19 + "' does not exist");
                     });
                     RenderSystem.setShaderTexture(0, var21);
                     var1.bindForSetup(var21);
                     AbstractTexture var22 = var1.getTexture(var21);
                     int var23 = GsonHelper.getAsInt(var15, "width");
                     int var24 = GsonHelper.getAsInt(var15, "height");
                     boolean var25 = GsonHelper.getAsBoolean(var15, "bilinear");
                     if (var25) {
                        RenderSystem.texParameter(3553, 10241, 9729);
                        RenderSystem.texParameter(3553, 10240, 9729);
                     } else {
                        RenderSystem.texParameter(3553, 10241, 9728);
                        RenderSystem.texParameter(3553, 10240, 9728);
                     }

                     Objects.requireNonNull(var22);
                     var10.addAuxAsset(var32, var22::getId, var23, var24);
                  } else if (var18) {
                     Objects.requireNonNull(var20);
                     var10.addAuxAsset(var32, var20::getDepthTextureId, var20.width, var20.height);
                  } else {
                     Objects.requireNonNull(var20);
                     var10.addAuxAsset(var32, var20::getColorTextureId, var20.width, var20.height);
                  }
               } catch (Exception var27) {
                  ChainedJsonException var16 = ChainedJsonException.forException(var27);
                  var16.prependJsonKey("auxtargets[" + var12 + "]");
                  throw var16;
               }
            }
         }

         JsonArray var28 = GsonHelper.getAsJsonArray(var3, "uniforms", (JsonArray)null);
         if (var28 != null) {
            int var29 = 0;

            for(Iterator var30 = var28.iterator(); var30.hasNext(); ++var29) {
               JsonElement var31 = (JsonElement)var30.next();

               try {
                  this.parseUniformNode(var31);
               } catch (Exception var26) {
                  ChainedJsonException var33 = ChainedJsonException.forException(var26);
                  var33.prependJsonKey("uniforms[" + var29 + "]");
                  throw var33;
               }
            }
         }

      }
   }

   private void parseUniformNode(JsonElement var1) throws ChainedJsonException {
      JsonObject var2 = GsonHelper.convertToJsonObject(var1, "uniform");
      String var3 = GsonHelper.getAsString(var2, "name");
      Uniform var4 = ((PostPass)this.passes.get(this.passes.size() - 1)).getEffect().getUniform(var3);
      if (var4 == null) {
         throw new ChainedJsonException("Uniform '" + var3 + "' does not exist");
      } else {
         float[] var5 = new float[4];
         int var6 = 0;
         JsonArray var7 = GsonHelper.getAsJsonArray(var2, "values");

         for(Iterator var8 = var7.iterator(); var8.hasNext(); ++var6) {
            JsonElement var9 = (JsonElement)var8.next();

            try {
               var5[var6] = GsonHelper.convertToFloat(var9, "value");
            } catch (Exception var12) {
               ChainedJsonException var11 = ChainedJsonException.forException(var12);
               var11.prependJsonKey("values[" + var6 + "]");
               throw var11;
            }
         }

         switch (var6) {
            case 0:
            default:
               break;
            case 1:
               var4.set(var5[0]);
               break;
            case 2:
               var4.set(var5[0], var5[1]);
               break;
            case 3:
               var4.set(var5[0], var5[1], var5[2]);
               break;
            case 4:
               var4.set(var5[0], var5[1], var5[2], var5[3]);
         }

      }
   }

   public RenderTarget getTempTarget(String var1) {
      return (RenderTarget)this.customRenderTargets.get(var1);
   }

   public void addTempTarget(String var1, int var2, int var3) {
      TextureTarget var4 = new TextureTarget(var2, var3, true, Minecraft.ON_OSX);
      ((RenderTarget)var4).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.customRenderTargets.put(var1, var4);
      if (var2 == this.screenWidth && var3 == this.screenHeight) {
         this.fullSizedTargets.add(var4);
      }

   }

   public void close() {
      Iterator var1 = this.customRenderTargets.values().iterator();

      while(var1.hasNext()) {
         RenderTarget var2 = (RenderTarget)var1.next();
         var2.destroyBuffers();
      }

      var1 = this.passes.iterator();

      while(var1.hasNext()) {
         PostPass var3 = (PostPass)var1.next();
         var3.close();
      }

      this.passes.clear();
   }

   public PostPass addPass(String var1, RenderTarget var2, RenderTarget var3, boolean var4) throws IOException {
      PostPass var5 = new PostPass(this.resourceProvider, var1, var2, var3, var4);
      this.passes.add(this.passes.size(), var5);
      return var5;
   }

   private void updateOrthoMatrix() {
      this.shaderOrthoMatrix = (new Matrix4f()).setOrtho(0.0F, (float)this.screenTarget.width, 0.0F, (float)this.screenTarget.height, 0.1F, 1000.0F);
   }

   public void resize(int var1, int var2) {
      this.screenWidth = this.screenTarget.width;
      this.screenHeight = this.screenTarget.height;
      this.updateOrthoMatrix();
      Iterator var3 = this.passes.iterator();

      while(var3.hasNext()) {
         PostPass var4 = (PostPass)var3.next();
         var4.setOrthoMatrix(this.shaderOrthoMatrix);
      }

      var3 = this.fullSizedTargets.iterator();

      while(var3.hasNext()) {
         RenderTarget var5 = (RenderTarget)var3.next();
         var5.resize(var1, var2, Minecraft.ON_OSX);
      }

   }

   private void setFilterMode(int var1) {
      this.screenTarget.setFilterMode(var1);
      Iterator var2 = this.customRenderTargets.values().iterator();

      while(var2.hasNext()) {
         RenderTarget var3 = (RenderTarget)var2.next();
         var3.setFilterMode(var1);
      }

   }

   public void process(float var1) {
      if (var1 < this.lastStamp) {
         this.time += 1.0F - this.lastStamp;
         this.time += var1;
      } else {
         this.time += var1 - this.lastStamp;
      }

      for(this.lastStamp = var1; this.time > 20.0F; this.time -= 20.0F) {
      }

      int var2 = 9728;

      PostPass var4;
      for(Iterator var3 = this.passes.iterator(); var3.hasNext(); var4.process(this.time / 20.0F)) {
         var4 = (PostPass)var3.next();
         int var5 = var4.getFilterMode();
         if (var2 != var5) {
            this.setFilterMode(var5);
            var2 = var5;
         }
      }

      this.setFilterMode(9728);
   }

   public void setUniform(String var1, float var2) {
      Iterator var3 = this.passes.iterator();

      while(var3.hasNext()) {
         PostPass var4 = (PostPass)var3.next();
         var4.getEffect().safeGetUniform(var1).set(var2);
      }

   }

   public final String getName() {
      return this.name;
   }

   @Nullable
   private RenderTarget getRenderTarget(@Nullable String var1) {
      if (var1 == null) {
         return null;
      } else {
         return var1.equals("minecraft:main") ? this.screenTarget : (RenderTarget)this.customRenderTargets.get(var1);
      }
   }
}
