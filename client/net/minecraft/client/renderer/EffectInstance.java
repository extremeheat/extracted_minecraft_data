package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.shaders.EffectProgram;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public class EffectInstance implements Effect, AutoCloseable {
   private static final String EFFECT_SHADER_PATH = "shaders/program/";
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
   private static final boolean ALWAYS_REAPPLY = true;
   private static EffectInstance lastAppliedEffect;
   private static int lastProgramId = -1;
   private final Map<String, IntSupplier> samplerMap = Maps.newHashMap();
   private final List<String> samplerNames = Lists.newArrayList();
   private final List<Integer> samplerLocations = Lists.newArrayList();
   private final List<Uniform> uniforms = Lists.newArrayList();
   private final List<Integer> uniformLocations = Lists.newArrayList();
   private final Map<String, Uniform> uniformMap = Maps.newHashMap();
   private final int programId;
   private final String name;
   private boolean dirty;
   private final BlendMode blend;
   private final List<Integer> attributes;
   private final List<String> attributeNames;
   private final EffectProgram vertexProgram;
   private final EffectProgram fragmentProgram;

   public EffectInstance(ResourceProvider var1, String var2) throws IOException {
      super();
      ResourceLocation var3 = ResourceLocation.withDefaultNamespace("shaders/program/" + var2 + ".json");
      this.name = var2;
      Resource var4 = var1.getResourceOrThrow(var3);

      try {
         BufferedReader var5 = var4.openAsReader();

         try {
            JsonObject var23 = GsonHelper.parse((Reader)var5);
            String var7 = GsonHelper.getAsString(var23, "vertex");
            String var8 = GsonHelper.getAsString(var23, "fragment");
            JsonArray var9 = GsonHelper.getAsJsonArray(var23, "samplers", (JsonArray)null);
            if (var9 != null) {
               int var10 = 0;

               for(Iterator var11 = var9.iterator(); var11.hasNext(); ++var10) {
                  JsonElement var12 = (JsonElement)var11.next();

                  try {
                     this.parseSamplerNode(var12);
                  } catch (Exception var20) {
                     ChainedJsonException var14 = ChainedJsonException.forException(var20);
                     var14.prependJsonKey("samplers[" + var10 + "]");
                     throw var14;
                  }
               }
            }

            JsonArray var24 = GsonHelper.getAsJsonArray(var23, "attributes", (JsonArray)null);
            Iterator var27;
            if (var24 != null) {
               int var25 = 0;
               this.attributes = Lists.newArrayListWithCapacity(var24.size());
               this.attributeNames = Lists.newArrayListWithCapacity(var24.size());

               for(var27 = var24.iterator(); var27.hasNext(); ++var25) {
                  JsonElement var13 = (JsonElement)var27.next();

                  try {
                     this.attributeNames.add(GsonHelper.convertToString(var13, "attribute"));
                  } catch (Exception var19) {
                     ChainedJsonException var15 = ChainedJsonException.forException(var19);
                     var15.prependJsonKey("attributes[" + var25 + "]");
                     throw var15;
                  }
               }
            } else {
               this.attributes = null;
               this.attributeNames = null;
            }

            JsonArray var26 = GsonHelper.getAsJsonArray(var23, "uniforms", (JsonArray)null);
            if (var26 != null) {
               int var28 = 0;

               for(Iterator var29 = var26.iterator(); var29.hasNext(); ++var28) {
                  JsonElement var31 = (JsonElement)var29.next();

                  try {
                     this.parseUniformNode(var31);
                  } catch (Exception var18) {
                     ChainedJsonException var16 = ChainedJsonException.forException(var18);
                     var16.prependJsonKey("uniforms[" + var28 + "]");
                     throw var16;
                  }
               }
            }

            this.blend = parseBlendNode(GsonHelper.getAsJsonObject(var23, "blend", (JsonObject)null));
            this.vertexProgram = getOrCreate(var1, Program.Type.VERTEX, var7);
            this.fragmentProgram = getOrCreate(var1, Program.Type.FRAGMENT, var8);
            this.programId = ProgramManager.createProgram();
            ProgramManager.linkShader(this);
            this.updateLocations();
            if (this.attributeNames != null) {
               var27 = this.attributeNames.iterator();

               while(var27.hasNext()) {
                  String var30 = (String)var27.next();
                  int var32 = Uniform.glGetAttribLocation(this.programId, var30);
                  this.attributes.add(var32);
               }
            }
         } catch (Throwable var21) {
            if (var5 != null) {
               try {
                  ((Reader)var5).close();
               } catch (Throwable var17) {
                  var21.addSuppressed(var17);
               }
            }

            throw var21;
         }

         if (var5 != null) {
            ((Reader)var5).close();
         }
      } catch (Exception var22) {
         ChainedJsonException var6 = ChainedJsonException.forException(var22);
         String var10001 = var3.getPath();
         var6.setFilenameAndFlush(var10001 + " (" + var4.sourcePackId() + ")");
         throw var6;
      }

      this.markDirty();
   }

   public static EffectProgram getOrCreate(ResourceProvider var0, Program.Type var1, String var2) throws IOException {
      Program var3 = (Program)var1.getPrograms().get(var2);
      if (var3 != null && !(var3 instanceof EffectProgram)) {
         throw new InvalidClassException("Program is not of type EffectProgram");
      } else {
         EffectProgram var4;
         if (var3 == null) {
            ResourceLocation var5 = ResourceLocation.withDefaultNamespace("shaders/program/" + var2 + var1.getExtension());
            Resource var6 = var0.getResourceOrThrow(var5);
            InputStream var7 = var6.open();

            try {
               var4 = EffectProgram.compileShader(var1, var2, var7, var6.sourcePackId());
            } catch (Throwable var11) {
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (var7 != null) {
               var7.close();
            }
         } else {
            var4 = (EffectProgram)var3;
         }

         return var4;
      }
   }

   public static BlendMode parseBlendNode(@Nullable JsonObject var0) {
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

      ProgramManager.releaseProgram(this);
   }

   public void clear() {
      RenderSystem.assertOnRenderThread();
      ProgramManager.glUseProgram(0);
      lastProgramId = -1;
      lastAppliedEffect = null;

      for(int var1 = 0; var1 < this.samplerLocations.size(); ++var1) {
         if (this.samplerMap.get(this.samplerNames.get(var1)) != null) {
            GlStateManager._activeTexture('\u84c0' + var1);
            GlStateManager._bindTexture(0);
         }
      }

   }

   public void apply() {
      this.dirty = false;
      lastAppliedEffect = this;
      this.blend.apply();
      if (this.programId != lastProgramId) {
         ProgramManager.glUseProgram(this.programId);
         lastProgramId = this.programId;
      }

      for(int var1 = 0; var1 < this.samplerLocations.size(); ++var1) {
         String var2 = (String)this.samplerNames.get(var1);
         IntSupplier var3 = (IntSupplier)this.samplerMap.get(var2);
         if (var3 != null) {
            RenderSystem.activeTexture('\u84c0' + var1);
            int var4 = var3.getAsInt();
            if (var4 != -1) {
               RenderSystem.bindTexture(var4);
               Uniform.uploadInteger((Integer)this.samplerLocations.get(var1), var1);
            }
         }
      }

      Iterator var5 = this.uniforms.iterator();

      while(var5.hasNext()) {
         Uniform var6 = (Uniform)var5.next();
         var6.upload();
      }

   }

   public void markDirty() {
      this.dirty = true;
   }

   @Nullable
   public Uniform getUniform(String var1) {
      RenderSystem.assertOnRenderThread();
      return (Uniform)this.uniformMap.get(var1);
   }

   public AbstractUniform safeGetUniform(String var1) {
      Uniform var2 = this.getUniform(var1);
      return (AbstractUniform)(var2 == null ? DUMMY_UNIFORM : var2);
   }

   private void updateLocations() {
      RenderSystem.assertOnRenderThread();
      IntArrayList var1 = new IntArrayList();

      int var2;
      for(var2 = 0; var2 < this.samplerNames.size(); ++var2) {
         String var3 = (String)this.samplerNames.get(var2);
         int var4 = Uniform.glGetUniformLocation(this.programId, var3);
         if (var4 == -1) {
            LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", this.name, var3);
            this.samplerMap.remove(var3);
            var1.add(var2);
         } else {
            this.samplerLocations.add(var4);
         }
      }

      for(var2 = var1.size() - 1; var2 >= 0; --var2) {
         this.samplerNames.remove(var1.getInt(var2));
      }

      Iterator var6 = this.uniforms.iterator();

      while(var6.hasNext()) {
         Uniform var7 = (Uniform)var6.next();
         String var8 = var7.getName();
         int var5 = Uniform.glGetUniformLocation(this.programId, var8);
         if (var5 == -1) {
            LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", this.name, var8);
         } else {
            this.uniformLocations.add(var5);
            var7.setLocation(var5);
            this.uniformMap.put(var8, var7);
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

   public void setSampler(String var1, IntSupplier var2) {
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

   public void attachToProgram() {
      this.fragmentProgram.attachToEffect(this);
      this.vertexProgram.attachToEffect(this);
   }

   public String getName() {
      return this.name;
   }

   public int getId() {
      return this.programId;
   }
}
