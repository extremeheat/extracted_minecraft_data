package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectInstance implements Effect, AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
   private static EffectInstance lastAppliedEffect;
   private static int lastProgramId = -1;
   private final Map<String, Object> samplerMap = Maps.newHashMap();
   private final List<String> samplerNames = Lists.newArrayList();
   private final List<Integer> samplerLocations = Lists.newArrayList();
   private final List<Uniform> uniforms = Lists.newArrayList();
   private final List<Integer> uniformLocations = Lists.newArrayList();
   private final Map<String, Uniform> uniformMap = Maps.newHashMap();
   private final int programId;
   private final String name;
   private final boolean cull;
   private boolean dirty;
   private final BlendMode blend;
   private final List<Integer> attributes;
   private final List<String> attributeNames;
   private final Program vertexProgram;
   private final Program fragmentProgram;

   public EffectInstance(ResourceManager var1, String var2) throws IOException {
      super();
      ResourceLocation var3 = new ResourceLocation("shaders/program/" + var2 + ".json");
      this.name = var2;
      Resource var4 = null;

      try {
         var4 = var1.getResource(var3);
         JsonObject var5 = GsonHelper.parse((Reader)(new InputStreamReader(var4.getInputStream(), StandardCharsets.UTF_8)));
         String var6 = GsonHelper.getAsString(var5, "vertex");
         String var27 = GsonHelper.getAsString(var5, "fragment");
         JsonArray var8 = GsonHelper.getAsJsonArray(var5, "samplers", (JsonArray)null);
         if (var8 != null) {
            int var9 = 0;

            for(Iterator var10 = var8.iterator(); var10.hasNext(); ++var9) {
               JsonElement var11 = (JsonElement)var10.next();

               try {
                  this.parseSamplerNode(var11);
               } catch (Exception var24) {
                  ChainedJsonException var13 = ChainedJsonException.forException(var24);
                  var13.prependJsonKey("samplers[" + var9 + "]");
                  throw var13;
               }
            }
         }

         JsonArray var28 = GsonHelper.getAsJsonArray(var5, "attributes", (JsonArray)null);
         Iterator var31;
         if (var28 != null) {
            int var29 = 0;
            this.attributes = Lists.newArrayListWithCapacity(var28.size());
            this.attributeNames = Lists.newArrayListWithCapacity(var28.size());

            for(var31 = var28.iterator(); var31.hasNext(); ++var29) {
               JsonElement var12 = (JsonElement)var31.next();

               try {
                  this.attributeNames.add(GsonHelper.convertToString(var12, "attribute"));
               } catch (Exception var23) {
                  ChainedJsonException var14 = ChainedJsonException.forException(var23);
                  var14.prependJsonKey("attributes[" + var29 + "]");
                  throw var14;
               }
            }
         } else {
            this.attributes = null;
            this.attributeNames = null;
         }

         JsonArray var30 = GsonHelper.getAsJsonArray(var5, "uniforms", (JsonArray)null);
         if (var30 != null) {
            int var32 = 0;

            for(Iterator var33 = var30.iterator(); var33.hasNext(); ++var32) {
               JsonElement var35 = (JsonElement)var33.next();

               try {
                  this.parseUniformNode(var35);
               } catch (Exception var22) {
                  ChainedJsonException var15 = ChainedJsonException.forException(var22);
                  var15.prependJsonKey("uniforms[" + var32 + "]");
                  throw var15;
               }
            }
         }

         this.blend = parseBlendNode(GsonHelper.getAsJsonObject(var5, "blend", (JsonObject)null));
         this.cull = GsonHelper.getAsBoolean(var5, "cull", true);
         this.vertexProgram = getOrCreate(var1, Program.Type.VERTEX, var6);
         this.fragmentProgram = getOrCreate(var1, Program.Type.FRAGMENT, var27);
         this.programId = ProgramManager.getInstance().createProgram();
         ProgramManager.getInstance().linkProgram(this);
         this.updateLocations();
         if (this.attributeNames != null) {
            var31 = this.attributeNames.iterator();

            while(var31.hasNext()) {
               String var34 = (String)var31.next();
               int var36 = GLX.glGetAttribLocation(this.programId, var34);
               this.attributes.add(var36);
            }
         }
      } catch (Exception var25) {
         ChainedJsonException var7 = ChainedJsonException.forException(var25);
         var7.setFilenameAndFlush(var3.getPath());
         throw var7;
      } finally {
         IOUtils.closeQuietly(var4);
      }

      this.markDirty();
   }

   public static Program getOrCreate(ResourceManager var0, Program.Type var1, String var2) throws IOException {
      Program var3 = (Program)var1.getPrograms().get(var2);
      if (var3 == null) {
         ResourceLocation var4 = new ResourceLocation("shaders/program/" + var2 + var1.getExtension());
         Resource var5 = var0.getResource(var4);

         try {
            var3 = Program.compileShader(var1, var2, var5.getInputStream());
         } finally {
            IOUtils.closeQuietly(var5);
         }
      }

      return var3;
   }

   public static BlendMode parseBlendNode(JsonObject var0) {
      if (var0 == null) {
         return new BlendMode();
      } else {
         int var1 = 32774;
         int var2 = 1;
         int var3 = 0;
         int var4 = 1;
         int var5 = 0;
         boolean var6 = true;
         boolean var7 = false;
         if (GsonHelper.isStringValue(var0, "func")) {
            var1 = BlendMode.stringToBlendFunc(var0.get("func").getAsString());
            if (var1 != 32774) {
               var6 = false;
            }
         }

         if (GsonHelper.isStringValue(var0, "srcrgb")) {
            var2 = BlendMode.stringToBlendFactor(var0.get("srcrgb").getAsString());
            if (var2 != 1) {
               var6 = false;
            }
         }

         if (GsonHelper.isStringValue(var0, "dstrgb")) {
            var3 = BlendMode.stringToBlendFactor(var0.get("dstrgb").getAsString());
            if (var3 != 0) {
               var6 = false;
            }
         }

         if (GsonHelper.isStringValue(var0, "srcalpha")) {
            var4 = BlendMode.stringToBlendFactor(var0.get("srcalpha").getAsString());
            if (var4 != 1) {
               var6 = false;
            }

            var7 = true;
         }

         if (GsonHelper.isStringValue(var0, "dstalpha")) {
            var5 = BlendMode.stringToBlendFactor(var0.get("dstalpha").getAsString());
            if (var5 != 0) {
               var6 = false;
            }

            var7 = true;
         }

         if (var6) {
            return new BlendMode();
         } else {
            return var7 ? new BlendMode(var2, var3, var4, var5, var1) : new BlendMode(var2, var3, var1);
         }
      }
   }

   public void close() {
      Iterator var1 = this.uniforms.iterator();

      while(var1.hasNext()) {
         Uniform var2 = (Uniform)var1.next();
         var2.close();
      }

      ProgramManager.getInstance().releaseProgram(this);
   }

   public void clear() {
      GLX.glUseProgram(0);
      lastProgramId = -1;
      lastAppliedEffect = null;

      for(int var1 = 0; var1 < this.samplerLocations.size(); ++var1) {
         if (this.samplerMap.get(this.samplerNames.get(var1)) != null) {
            GlStateManager.activeTexture(GLX.GL_TEXTURE0 + var1);
            GlStateManager.bindTexture(0);
         }
      }

   }

   public void apply() {
      this.dirty = false;
      lastAppliedEffect = this;
      this.blend.apply();
      if (this.programId != lastProgramId) {
         GLX.glUseProgram(this.programId);
         lastProgramId = this.programId;
      }

      if (this.cull) {
         GlStateManager.enableCull();
      } else {
         GlStateManager.disableCull();
      }

      for(int var1 = 0; var1 < this.samplerLocations.size(); ++var1) {
         if (this.samplerMap.get(this.samplerNames.get(var1)) != null) {
            GlStateManager.activeTexture(GLX.GL_TEXTURE0 + var1);
            GlStateManager.enableTexture();
            Object var2 = this.samplerMap.get(this.samplerNames.get(var1));
            int var3 = -1;
            if (var2 instanceof RenderTarget) {
               var3 = ((RenderTarget)var2).colorTextureId;
            } else if (var2 instanceof TextureObject) {
               var3 = ((TextureObject)var2).getId();
            } else if (var2 instanceof Integer) {
               var3 = (Integer)var2;
            }

            if (var3 != -1) {
               GlStateManager.bindTexture(var3);
               GLX.glUniform1i(GLX.glGetUniformLocation(this.programId, (CharSequence)this.samplerNames.get(var1)), var1);
            }
         }
      }

      Iterator var4 = this.uniforms.iterator();

      while(var4.hasNext()) {
         Uniform var5 = (Uniform)var4.next();
         var5.upload();
      }

   }

   public void markDirty() {
      this.dirty = true;
   }

   @Nullable
   public Uniform getUniform(String var1) {
      return (Uniform)this.uniformMap.get(var1);
   }

   public AbstractUniform safeGetUniform(String var1) {
      Uniform var2 = this.getUniform(var1);
      return (AbstractUniform)(var2 == null ? DUMMY_UNIFORM : var2);
   }

   private void updateLocations() {
      int var1 = 0;

      String var3;
      int var4;
      for(int var2 = 0; var1 < this.samplerNames.size(); ++var2) {
         var3 = (String)this.samplerNames.get(var1);
         var4 = GLX.glGetUniformLocation(this.programId, var3);
         if (var4 == -1) {
            LOGGER.warn("Shader {}could not find sampler named {} in the specified shader program.", this.name, var3);
            this.samplerMap.remove(var3);
            this.samplerNames.remove(var2);
            --var2;
         } else {
            this.samplerLocations.add(var4);
         }

         ++var1;
      }

      Iterator var5 = this.uniforms.iterator();

      while(var5.hasNext()) {
         Uniform var6 = (Uniform)var5.next();
         var3 = var6.getName();
         var4 = GLX.glGetUniformLocation(this.programId, var3);
         if (var4 == -1) {
            LOGGER.warn("Could not find uniform named {} in the specified shader program.", var3);
         } else {
            this.uniformLocations.add(var4);
            var6.setLocation(var4);
            this.uniformMap.put(var3, var6);
         }
      }

   }

   private void parseSamplerNode(JsonElement var1) {
      JsonObject var2 = GsonHelper.convertToJsonObject(var1, "sampler");
      String var3 = GsonHelper.getAsString(var2, "name");
      if (!GsonHelper.isStringValue(var2, "file")) {
         this.samplerMap.put(var3, (Object)null);
         this.samplerNames.add(var3);
      } else {
         this.samplerNames.add(var3);
      }
   }

   public void setSampler(String var1, Object var2) {
      if (this.samplerMap.containsKey(var1)) {
         this.samplerMap.remove(var1);
      }

      this.samplerMap.put(var1, var2);
      this.markDirty();
   }

   private void parseUniformNode(JsonElement var1) throws ChainedJsonException {
      JsonObject var2 = GsonHelper.convertToJsonObject(var1, "uniform");
      String var3 = GsonHelper.getAsString(var2, "name");
      int var4 = Uniform.getTypeFromString(GsonHelper.getAsString(var2, "type"));
      int var5 = GsonHelper.getAsInt(var2, "count");
      float[] var6 = new float[Math.max(var5, 16)];
      JsonArray var7 = GsonHelper.getAsJsonArray(var2, "values");
      if (var7.size() != var5 && var7.size() > 1) {
         throw new ChainedJsonException("Invalid amount of values specified (expected " + var5 + ", found " + var7.size() + ")");
      } else {
         int var8 = 0;

         for(Iterator var9 = var7.iterator(); var9.hasNext(); ++var8) {
            JsonElement var10 = (JsonElement)var9.next();

            try {
               var6[var8] = GsonHelper.convertToFloat(var10, "value");
            } catch (Exception var13) {
               ChainedJsonException var12 = ChainedJsonException.forException(var13);
               var12.prependJsonKey("values[" + var8 + "]");
               throw var12;
            }
         }

         if (var5 > 1 && var7.size() == 1) {
            while(var8 < var5) {
               var6[var8] = var6[0];
               ++var8;
            }
         }

         int var14 = var5 > 1 && var5 <= 4 && var4 < 8 ? var5 - 1 : 0;
         Uniform var15 = new Uniform(var3, var4 + var14, var5, this);
         if (var4 <= 3) {
            var15.setSafe((int)var6[0], (int)var6[1], (int)var6[2], (int)var6[3]);
         } else if (var4 <= 7) {
            var15.setSafe(var6[0], var6[1], var6[2], var6[3]);
         } else {
            var15.set(var6);
         }

         this.uniforms.add(var15);
      }
   }

   public Program getVertexProgram() {
      return this.vertexProgram;
   }

   public Program getFragmentProgram() {
      return this.fragmentProgram;
   }

   public int getId() {
      return this.programId;
   }
}
