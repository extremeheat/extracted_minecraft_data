package net.minecraft.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.CompiledShader;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.joml.Matrix4f;

public class CompiledShaderProgram implements AutoCloseable {
   private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
   private final List<String> samplers = new ArrayList();
   private final Object2ObjectMap<String, GpuTexture> samplerTextures = new Object2ObjectOpenHashMap();
   private final IntList samplerLocations = new IntArrayList();
   private final List<Uniform> uniforms = new ArrayList();
   private final Map<String, Uniform> uniformsByName = new HashMap();
   private final int programId;
   private final String shaderName;
   @Nullable
   public Uniform MODEL_VIEW_MATRIX;
   @Nullable
   public Uniform PROJECTION_MATRIX;
   @Nullable
   public Uniform TEXTURE_MATRIX;
   @Nullable
   public Uniform SCREEN_SIZE;
   @Nullable
   public Uniform COLOR_MODULATOR;
   @Nullable
   public Uniform LIGHT0_DIRECTION;
   @Nullable
   public Uniform LIGHT1_DIRECTION;
   @Nullable
   public Uniform GLINT_ALPHA;
   @Nullable
   public Uniform FOG_START;
   @Nullable
   public Uniform FOG_END;
   @Nullable
   public Uniform FOG_COLOR;
   @Nullable
   public Uniform FOG_SHAPE;
   @Nullable
   public Uniform LINE_WIDTH;
   @Nullable
   public Uniform GAME_TIME;
   @Nullable
   public Uniform MODEL_OFFSET;

   private CompiledShaderProgram(int var1, String var2) {
      super();
      this.programId = var1;
      this.shaderName = var2;
   }

   public static CompiledShaderProgram link(CompiledShader var0, CompiledShader var1, VertexFormat var2, String var3) throws ShaderManager.CompilationException {
      int var4 = GlStateManager.glCreateProgram();
      if (var4 <= 0) {
         throw new ShaderManager.CompilationException("Could not create shader program (returned program ID " + var4 + ")");
      } else {
         var2.bindAttributes(var4);
         GlStateManager.glAttachShader(var4, var0.getShaderId());
         GlStateManager.glAttachShader(var4, var1.getShaderId());
         GlStateManager.glLinkProgram(var4);
         int var5 = GlStateManager.glGetProgrami(var4, 35714);
         if (var5 == 0) {
            String var6 = GlStateManager.glGetProgramInfoLog(var4, 32768);
            String var10002 = String.valueOf(var0.getId());
            throw new ShaderManager.CompilationException("Error encountered when linking program containing VS " + var10002 + " and FS " + String.valueOf(var1.getId()) + ". Log output: " + var6);
         } else {
            return new CompiledShaderProgram(var4, var3);
         }
      }
   }

   public void setupUniforms(List<RenderPipeline.UniformDescription> var1, List<String> var2) {
      RenderSystem.assertOnRenderThread();

      for(RenderPipeline.UniformDescription var4 : var1) {
         String var5 = var4.name();
         int var6 = Uniform.glGetUniformLocation(this.programId, var5);
         if (var6 != -1) {
            Uniform var7 = this.createUniform(var4);
            var7.setLocation(var6);
            this.uniforms.add(var7);
            this.uniformsByName.put(var5, var7);
         }
      }

      for(String var9 : var2) {
         int var10 = Uniform.glGetUniformLocation(this.programId, var9);
         if (var10 != -1) {
            this.samplers.add(var9);
            this.samplerLocations.add(var10);
         }
      }

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
      this.MODEL_OFFSET = this.getUniform("ModelOffset");
   }

   private Uniform createUniform(RenderPipeline.UniformDescription var1) {
      return new Uniform(var1.name(), var1.type());
   }

   public void close() {
      this.uniforms.forEach(Uniform::close);
      GlStateManager.glDeleteProgram(this.programId);
   }

   public void clear() {
      RenderSystem.assertOnRenderThread();
      GlStateManager._glUseProgram(0);
      int var1 = GlStateManager._getActiveTexture();

      for(int var2 = 0; var2 < this.samplerLocations.size(); ++var2) {
         String var3 = (String)this.samplers.get(var2);
         if (!this.samplerTextures.containsKey(var3)) {
            GlStateManager._activeTexture('\u84c0' + var2);
            GlStateManager._bindTexture(0);
         }
      }

      GlStateManager._activeTexture(var1);
   }

   public void apply() {
      RenderSystem.assertOnRenderThread();
      GlStateManager._glUseProgram(this.programId);
      int var1 = GlStateManager._getActiveTexture();

      for(int var2 = 0; var2 < this.samplerLocations.size(); ++var2) {
         String var3 = (String)this.samplers.get(var2);
         GpuTexture var4 = (GpuTexture)this.samplerTextures.get(var3);
         if (var4 != null) {
            int var5 = this.samplerLocations.getInt(var2);
            Uniform.uploadInteger(var5, var2);
            RenderSystem.activeTexture('\u84c0' + var2);
            var4.bind();
         }
      }

      GlStateManager._activeTexture(var1);

      for(Uniform var7 : this.uniforms) {
         var7.upload();
      }

   }

   @Nullable
   public Uniform getUniform(String var1) {
      RenderSystem.assertOnRenderThread();
      return (Uniform)this.uniformsByName.get(var1);
   }

   public AbstractUniform safeGetUniform(String var1) {
      Uniform var2 = this.getUniform(var1);
      return (AbstractUniform)(var2 == null ? DUMMY_UNIFORM : var2);
   }

   public void bindSampler(String var1, @Nullable GpuTexture var2) {
      this.samplerTextures.put(var1, var2);
   }

   public void setDefaultUniforms(VertexFormat.Mode var1, Matrix4f var2, Matrix4f var3, Window var4) {
      for(int var5 = 0; var5 < 12; ++var5) {
         GpuTexture var6 = RenderSystem.getShaderTexture(var5);
         this.bindSampler("Sampler" + var5, var6);
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

      FogParameters var7 = RenderSystem.getShaderFog();
      if (this.FOG_START != null) {
         this.FOG_START.set(var7.start());
      }

      if (this.FOG_END != null) {
         this.FOG_END.set(var7.end());
      }

      if (this.FOG_COLOR != null) {
         this.FOG_COLOR.set(var7.red(), var7.green(), var7.blue(), var7.alpha());
      }

      if (this.FOG_SHAPE != null) {
         this.FOG_SHAPE.set(var7.shape().getIndex());
      }

      if (this.TEXTURE_MATRIX != null) {
         this.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
      }

      if (this.GAME_TIME != null) {
         this.GAME_TIME.set(RenderSystem.getShaderGameTime());
      }

      if (this.MODEL_OFFSET != null) {
         this.MODEL_OFFSET.set(RenderSystem.getModelOffset());
      }

      if (this.SCREEN_SIZE != null) {
         this.SCREEN_SIZE.set((float)var4.getWidth(), (float)var4.getHeight());
      }

      if (this.LINE_WIDTH != null && (var1 == VertexFormat.Mode.LINES || var1 == VertexFormat.Mode.LINE_STRIP)) {
         this.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
      }

      RenderSystem.setupShaderLights(this);
   }

   @VisibleForTesting
   public void registerUniform(Uniform var1) {
      this.uniforms.add(var1);
      this.uniformsByName.put(var1.getName(), var1);
   }

   @VisibleForTesting
   public int getProgramId() {
      return this.programId;
   }

   public String toString() {
      return this.shaderName;
   }
}
