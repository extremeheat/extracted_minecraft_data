package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.slf4j.Logger;

public class ShaderInstance implements Shader, AutoCloseable {
   public static final String SHADER_PATH = "shaders";
   private static final String SHADER_CORE_PATH = "shaders/core/";
   private static final String SHADER_INCLUDE_PATH = "shaders/include/";
   static final Logger LOGGER = LogUtils.getLogger();
   private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
   private static final boolean ALWAYS_REAPPLY = true;
   private static ShaderInstance lastAppliedShader;
   private static int lastProgramId = -1;
   private final Map<String, Object> samplerMap = Maps.newHashMap();
   private final List<String> samplerNames = Lists.newArrayList();
   private final List<Integer> samplerLocations = Lists.newArrayList();
   private final List<Uniform> uniforms = Lists.newArrayList();
   private final List<Integer> uniformLocations = Lists.newArrayList();
   private final Map<String, Uniform> uniformMap = Maps.newHashMap();
   private final int programId;
   private final String name;
   private boolean dirty;
   private final Program vertexProgram;
   private final Program fragmentProgram;
   private final VertexFormat vertexFormat;
   @Nullable
   public final Uniform MODEL_VIEW_MATRIX;
   @Nullable
   public final Uniform PROJECTION_MATRIX;
   @Nullable
   public final Uniform TEXTURE_MATRIX;
   @Nullable
   public final Uniform SCREEN_SIZE;
   @Nullable
   public final Uniform COLOR_MODULATOR;
   @Nullable
   public final Uniform LIGHT0_DIRECTION;
   @Nullable
   public final Uniform LIGHT1_DIRECTION;
   @Nullable
   public final Uniform GLINT_ALPHA;
   @Nullable
   public final Uniform FOG_START;
   @Nullable
   public final Uniform FOG_END;
   @Nullable
   public final Uniform FOG_COLOR;
   @Nullable
   public final Uniform FOG_SHAPE;
   @Nullable
   public final Uniform LINE_WIDTH;
   @Nullable
   public final Uniform GAME_TIME;
   @Nullable
   public final Uniform CHUNK_OFFSET;

   public ShaderInstance(ResourceProvider var1, String var2, VertexFormat var3) throws IOException {
      super();
      this.name = var2;
      this.vertexFormat = var3;
      ResourceLocation var4 = ResourceLocation.withDefaultNamespace("shaders/core/" + var2 + ".json");

      try {
         BufferedReader var6 = var1.openAsReader(var4);

         try {
            JsonObject var5 = GsonHelper.parse((Reader)var6);
            String var21 = GsonHelper.getAsString(var5, "vertex");
            String var8 = GsonHelper.getAsString(var5, "fragment");
            JsonArray var9 = GsonHelper.getAsJsonArray(var5, "samplers", (JsonArray)null);
            if (var9 != null) {
               int var10 = 0;

               for(Iterator var11 = var9.iterator(); var11.hasNext(); ++var10) {
                  JsonElement var12 = (JsonElement)var11.next();

                  try {
                     this.parseSamplerNode(var12);
                  } catch (Exception var18) {
                     ChainedJsonException var14 = ChainedJsonException.forException(var18);
                     var14.prependJsonKey("samplers[" + var10 + "]");
                     throw var14;
                  }
               }
            }

            JsonArray var22 = GsonHelper.getAsJsonArray(var5, "uniforms", (JsonArray)null);
            int var23;
            Iterator var24;
            if (var22 != null) {
               var23 = 0;

               for(var24 = var22.iterator(); var24.hasNext(); ++var23) {
                  JsonElement var13 = (JsonElement)var24.next();

                  try {
                     this.parseUniformNode(var13);
                  } catch (Exception var17) {
                     ChainedJsonException var15 = ChainedJsonException.forException(var17);
                     var15.prependJsonKey("uniforms[" + var23 + "]");
                     throw var15;
                  }
               }
            }

            this.vertexProgram = getOrCreate(var1, Program.Type.VERTEX, var21);
            this.fragmentProgram = getOrCreate(var1, Program.Type.FRAGMENT, var8);
            this.programId = ProgramManager.createProgram();
            var23 = 0;

            for(var24 = var3.getElementAttributeNames().iterator(); var24.hasNext(); ++var23) {
               String var25 = (String)var24.next();
               Uniform.glBindAttribLocation(this.programId, var23, var25);
            }

            ProgramManager.linkShader(this);
            this.updateLocations();
         } catch (Throwable var19) {
            if (var6 != null) {
               try {
                  ((Reader)var6).close();
               } catch (Throwable var16) {
                  var19.addSuppressed(var16);
               }
            }

            throw var19;
         }

         if (var6 != null) {
            ((Reader)var6).close();
         }
      } catch (Exception var20) {
         ChainedJsonException var7 = ChainedJsonException.forException(var20);
         var7.setFilenameAndFlush(var4.getPath());
         throw var7;
      }

      this.markDirty();
      this.MODEL_VIEW_MATRIX = this.getUniform("ModelViewMat");
      this.PROJECTION_MATRIX = this.getUniform("ProjMat");
      this.TEXTURE_MATRIX = this.getUniform("TextureMat");
      this.SCREEN_SIZE = this.getUniform("ScreenSize");
      this.COLOR_MODULATOR = this.getUniform("ColorModulator");
      this.LIGHT0_DIRECTION = this.getUniform("Light0_Direction");
      this.LIGHT1_DIRECTION = this.getUniform("Light1_Direction");
      this.GLINT_ALPHA = this.getUniform("GlintAlpha");
      this.FOG_START = this.getUniform("FogStart");
      this.FOG_END = this.getUniform("FogEnd");
      this.FOG_COLOR = this.getUniform("FogColor");
      this.FOG_SHAPE = this.getUniform("FogShape");
      this.LINE_WIDTH = this.getUniform("LineWidth");
      this.GAME_TIME = this.getUniform("GameTime");
      this.CHUNK_OFFSET = this.getUniform("ChunkOffset");
   }

   private static Program getOrCreate(final ResourceProvider var0, Program.Type var1, String var2) throws IOException {
      Program var4 = (Program)var1.getPrograms().get(var2);
      Program var3;
      if (var4 == null) {
         String var5 = "shaders/core/" + var2 + var1.getExtension();
         Resource var6 = var0.getResourceOrThrow(ResourceLocation.withDefaultNamespace(var5));
         InputStream var7 = var6.open();

         try {
            final String var8 = FileUtil.getFullResourcePath(var5);
            var3 = Program.compileShader(var1, var2, var7, var6.sourcePackId(), new GlslPreprocessor() {
               private final Set<String> importedPaths = Sets.newHashSet();

               public String applyImport(boolean var1, String var2) {
                  String var10000 = var1 ? var8 : "shaders/include/";
                  var2 = FileUtil.normalizeResourcePath(var10000 + var2);
                  if (!this.importedPaths.add(var2)) {
                     return null;
                  } else {
                     ResourceLocation var3 = ResourceLocation.parse(var2);

                     try {
                        BufferedReader var4 = var0.openAsReader(var3);

                        String var5;
                        try {
                           var5 = IOUtils.toString(var4);
                        } catch (Throwable var8x) {
                           if (var4 != null) {
                              try {
                                 ((Reader)var4).close();
                              } catch (Throwable var7) {
                                 var8x.addSuppressed(var7);
                              }
                           }

                           throw var8x;
                        }

                        if (var4 != null) {
                           ((Reader)var4).close();
                        }

                        return var5;
                     } catch (IOException var9) {
                        ShaderInstance.LOGGER.error("Could not open GLSL import {}: {}", var2, var9.getMessage());
                        return "#error " + var9.getMessage();
                     }
                  }
               }
            });
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
         var3 = var4;
      }

      return var3;
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
      lastAppliedShader = null;
      int var1 = GlStateManager._getActiveTexture();

      for(int var2 = 0; var2 < this.samplerLocations.size(); ++var2) {
         if (this.samplerMap.get(this.samplerNames.get(var2)) != null) {
            GlStateManager._activeTexture('\u84c0' + var2);
            GlStateManager._bindTexture(0);
         }
      }

      GlStateManager._activeTexture(var1);
   }

   public void apply() {
      RenderSystem.assertOnRenderThread();
      this.dirty = false;
      lastAppliedShader = this;
      if (this.programId != lastProgramId) {
         ProgramManager.glUseProgram(this.programId);
         lastProgramId = this.programId;
      }

      int var1 = GlStateManager._getActiveTexture();

      for(int var2 = 0; var2 < this.samplerLocations.size(); ++var2) {
         String var3 = (String)this.samplerNames.get(var2);
         if (this.samplerMap.get(var3) != null) {
            int var4 = Uniform.glGetUniformLocation(this.programId, var3);
            Uniform.uploadInteger(var4, var2);
            RenderSystem.activeTexture('\u84c0' + var2);
            Object var5 = this.samplerMap.get(var3);
            int var6 = -1;
            if (var5 instanceof RenderTarget) {
               var6 = ((RenderTarget)var5).getColorTextureId();
            } else if (var5 instanceof AbstractTexture) {
               var6 = ((AbstractTexture)var5).getId();
            } else if (var5 instanceof Integer) {
               var6 = (Integer)var5;
            }

            if (var6 != -1) {
               RenderSystem.bindTexture(var6);
            }
         }
      }

      GlStateManager._activeTexture(var1);
      Iterator var7 = this.uniforms.iterator();

      while(var7.hasNext()) {
         Uniform var8 = (Uniform)var7.next();
         var8.upload();
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
         int var7 = var1.getInt(var2);
         this.samplerNames.remove(var7);
      }

      Iterator var6 = this.uniforms.iterator();

      while(var6.hasNext()) {
         Uniform var8 = (Uniform)var6.next();
         String var9 = var8.getName();
         int var5 = Uniform.glGetUniformLocation(this.programId, var9);
         if (var5 == -1) {
            LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", this.name, var9);
         } else {
            this.uniformLocations.add(var5);
            var8.setLocation(var5);
            this.uniformMap.put(var9, var8);
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
            var15.set(Arrays.copyOfRange(var6, 0, var5));
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
      this.fragmentProgram.attachToShader(this);
      this.vertexProgram.attachToShader(this);
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public String getName() {
      return this.name;
   }

   public int getId() {
      return this.programId;
   }

   public void setDefaultUniforms(VertexFormat.Mode var1, Matrix4f var2, Matrix4f var3, Window var4) {
      for(int var5 = 0; var5 < 12; ++var5) {
         int var6 = RenderSystem.getShaderTexture(var5);
         this.setSampler("Sampler" + var5, var6);
      }

      if (this.MODEL_VIEW_MATRIX != null) {
         this.MODEL_VIEW_MATRIX.set(var2);
      }

      if (this.PROJECTION_MATRIX != null) {
         this.PROJECTION_MATRIX.set(var3);
      }

      if (this.COLOR_MODULATOR != null) {
         this.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
      }

      if (this.GLINT_ALPHA != null) {
         this.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
      }

      if (this.FOG_START != null) {
         this.FOG_START.set(RenderSystem.getShaderFogStart());
      }

      if (this.FOG_END != null) {
         this.FOG_END.set(RenderSystem.getShaderFogEnd());
      }

      if (this.FOG_COLOR != null) {
         this.FOG_COLOR.set(RenderSystem.getShaderFogColor());
      }

      if (this.FOG_SHAPE != null) {
         this.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
      }

      if (this.TEXTURE_MATRIX != null) {
         this.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
      }

      if (this.GAME_TIME != null) {
         this.GAME_TIME.set(RenderSystem.getShaderGameTime());
      }

      if (this.SCREEN_SIZE != null) {
         this.SCREEN_SIZE.set((float)var4.getWidth(), (float)var4.getHeight());
      }

      if (this.LINE_WIDTH != null && (var1 == VertexFormat.Mode.LINES || var1 == VertexFormat.Mode.LINE_STRIP)) {
         this.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
      }

      RenderSystem.setupShaderLights(this);
   }
}
