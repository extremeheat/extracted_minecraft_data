package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.GpuOutOfMemoryException;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;

public class GlDevice implements GpuDevice {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected static boolean USE_GL_ARB_vertex_attrib_binding = true;
   protected static boolean USE_GL_KHR_debug = true;
   protected static boolean USE_GL_EXT_debug_label = true;
   protected static boolean USE_GL_ARB_debug_output = true;
   protected static boolean USE_GL_ARB_direct_state_access = true;
   protected static boolean USE_GL_ARB_buffer_storage = true;
   private final CommandEncoder encoder;
   @Nullable
   private final GlDebug debugLog;
   private final GlDebugLabel debugLabels;
   private final int maxSupportedTextureSize;
   private final DirectStateAccess directStateAccess;
   private final BiFunction<ResourceLocation, ShaderType, String> defaultShaderSource;
   private final Map<RenderPipeline, GlRenderPipeline> pipelineCache = new IdentityHashMap();
   private final Map<ShaderCompilationKey, GlShaderModule> shaderCache = new HashMap();
   private final VertexArrayCache vertexArrayCache;
   private final BufferStorage bufferStorage;
   private final Set<String> enabledExtensions = new HashSet();
   private final int uniformOffsetAlignment;

   public GlDevice(long var1, int var3, boolean var4, BiFunction<ResourceLocation, ShaderType, String> var5, boolean var6) {
      super();
      GLFW.glfwMakeContextCurrent(var1);
      GLCapabilities var7 = GL.createCapabilities();
      int var8 = getMaxSupportedTextureSize();
      GLFW.glfwSetWindowSizeLimits(var1, -1, -1, var8, var8);
      this.debugLog = GlDebug.enableDebugCallback(var3, var4, this.enabledExtensions);
      this.debugLabels = GlDebugLabel.create(var7, var6, this.enabledExtensions);
      this.vertexArrayCache = VertexArrayCache.create(var7, this.debugLabels, this.enabledExtensions);
      this.bufferStorage = BufferStorage.create(var7, this.enabledExtensions);
      this.directStateAccess = DirectStateAccess.create(var7, this.enabledExtensions);
      this.maxSupportedTextureSize = var8;
      this.defaultShaderSource = var5;
      this.encoder = new GlCommandEncoder(this);
      this.uniformOffsetAlignment = GL11.glGetInteger(35380);
   }

   public GlDebugLabel debugLabels() {
      return this.debugLabels;
   }

   public CommandEncoder createCommandEncoder() {
      return this.encoder;
   }

   public GpuTexture createTexture(@Nullable Supplier<String> var1, int var2, TextureFormat var3, int var4, int var5, int var6) {
      return this.createTexture(this.debugLabels.exists() && var1 != null ? (String)var1.get() : null, var2, var3, var4, var5, var6);
   }

   public GpuTexture createTexture(@Nullable String var1, int var2, TextureFormat var3, int var4, int var5, int var6) {
      if (var6 < 1) {
         throw new IllegalArgumentException("mipLevels must be at least 1");
      } else {
         GlStateManager.clearGlErrors();
         int var7 = GlStateManager._genTexture();
         if (var1 == null) {
            var1 = String.valueOf(var7);
         }

         GlStateManager._bindTexture(var7);
         GlStateManager._texParameter(3553, 33085, var6 - 1);
         GlStateManager._texParameter(3553, 33082, 0);
         GlStateManager._texParameter(3553, 33083, var6 - 1);
         if (var3.hasDepthAspect()) {
            GlStateManager._texParameter(3553, 34892, 0);
         }

         for(int var8 = 0; var8 < var6; ++var8) {
            GlStateManager._texImage2D(3553, var8, GlConst.toGlInternalId(var3), var4 >> var8, var5 >> var8, 0, GlConst.toGlExternalId(var3), GlConst.toGlType(var3), (IntBuffer)null);
         }

         int var10 = GlStateManager._getError();
         if (var10 == 1285) {
            throw new GpuOutOfMemoryException("Could not allocate texture of " + var4 + "x" + var5 + " for " + var1);
         } else if (var10 != 0) {
            throw new IllegalStateException("OpenGL error " + var10);
         } else {
            GlTexture var9 = new GlTexture(var2, var1, var3, var4, var5, var6, var7);
            this.debugLabels.applyLabel(var9);
            return var9;
         }
      }
   }

   public GpuBuffer createBuffer(@Nullable Supplier<String> var1, int var2, int var3) {
      if (var3 <= 0) {
         throw new IllegalArgumentException("Buffer size must be greater than zero");
      } else {
         GlBuffer var4 = this.bufferStorage.createBuffer(this.directStateAccess, var1, var2, var3);
         this.debugLabels.applyLabel(var4);
         return var4;
      }
   }

   public GpuBuffer createBuffer(@Nullable Supplier<String> var1, int var2, ByteBuffer var3) {
      if (!var3.hasRemaining()) {
         throw new IllegalArgumentException("Buffer source must not be empty");
      } else {
         GlBuffer var4 = this.bufferStorage.createBuffer(this.directStateAccess, var1, var2, var3);
         this.debugLabels.applyLabel(var4);
         return var4;
      }
   }

   public String getImplementationInformation() {
      if (GLFW.glfwGetCurrentContext() == 0L) {
         return "NO CONTEXT";
      } else {
         String var10000 = GlStateManager._getString(7937);
         return var10000 + " GL version " + GlStateManager._getString(7938) + ", " + GlStateManager._getString(7936);
      }
   }

   public List<String> getLastDebugMessages() {
      return this.debugLog == null ? Collections.emptyList() : this.debugLog.getLastOpenGlDebugMessages();
   }

   public boolean isDebuggingEnabled() {
      return this.debugLog != null;
   }

   public String getRenderer() {
      return GlStateManager._getString(7937);
   }

   public String getVendor() {
      return GlStateManager._getString(7936);
   }

   public String getBackendName() {
      return "OpenGL";
   }

   public String getVersion() {
      return GlStateManager._getString(7938);
   }

   private static int getMaxSupportedTextureSize() {
      int var0 = GlStateManager._getInteger(3379);

      for(int var1 = Math.max(32768, var0); var1 >= 1024; var1 >>= 1) {
         GlStateManager._texImage2D(32868, 0, 6408, var1, var1, 0, 6408, 5121, (IntBuffer)null);
         int var2 = GlStateManager._getTexLevelParameter(32868, 0, 4096);
         if (var2 != 0) {
            return var1;
         }
      }

      int var3 = Math.max(var0, 1024);
      LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", var3);
      return var3;
   }

   public int getMaxTextureSize() {
      return this.maxSupportedTextureSize;
   }

   public int getUniformOffsetAlignment() {
      return this.uniformOffsetAlignment;
   }

   public void clearPipelineCache() {
      for(GlRenderPipeline var2 : this.pipelineCache.values()) {
         if (var2.program() != GlProgram.INVALID_PROGRAM) {
            var2.program().close();
         }
      }

      this.pipelineCache.clear();

      for(GlShaderModule var4 : this.shaderCache.values()) {
         if (var4 != GlShaderModule.INVALID_SHADER) {
            var4.close();
         }
      }

      this.shaderCache.clear();
   }

   public List<String> getEnabledExtensions() {
      return new ArrayList(this.enabledExtensions);
   }

   public void close() {
      this.clearPipelineCache();
   }

   public DirectStateAccess directStateAccess() {
      return this.directStateAccess;
   }

   protected GlRenderPipeline getOrCompilePipeline(RenderPipeline var1) {
      return (GlRenderPipeline)this.pipelineCache.computeIfAbsent(var1, (var2) -> this.compilePipeline(var1, this.defaultShaderSource));
   }

   protected GlShaderModule getOrCompileShader(ResourceLocation var1, ShaderType var2, ShaderDefines var3, BiFunction<ResourceLocation, ShaderType, String> var4) {
      ShaderCompilationKey var5 = new ShaderCompilationKey(var1, var2, var3);
      return (GlShaderModule)this.shaderCache.computeIfAbsent(var5, (var3x) -> this.compileShader(var5, var4));
   }

   public GlRenderPipeline precompilePipeline(RenderPipeline var1, @Nullable BiFunction<ResourceLocation, ShaderType, String> var2) {
      BiFunction var3 = var2 == null ? this.defaultShaderSource : var2;
      return (GlRenderPipeline)this.pipelineCache.computeIfAbsent(var1, (var3x) -> this.compilePipeline(var1, var3));
   }

   private GlShaderModule compileShader(ShaderCompilationKey var1, BiFunction<ResourceLocation, ShaderType, String> var2) {
      String var3 = (String)var2.apply(var1.id, var1.type);
      if (var3 == null) {
         LOGGER.error("Couldn't find source for {} shader ({})", var1.type, var1.id);
         return GlShaderModule.INVALID_SHADER;
      } else {
         String var4 = GlslPreprocessor.injectDefines(var3, var1.defines);
         int var5 = GlStateManager.glCreateShader(GlConst.toGl(var1.type));
         GlStateManager.glShaderSource(var5, var4);
         GlStateManager.glCompileShader(var5);
         if (GlStateManager.glGetShaderi(var5, 35713) == 0) {
            String var7 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(var5, 32768));
            LOGGER.error("Couldn't compile {} shader ({}): {}", new Object[]{var1.type.getName(), var1.id, var7});
            return GlShaderModule.INVALID_SHADER;
         } else {
            GlShaderModule var6 = new GlShaderModule(var5, var1.id, var1.type);
            this.debugLabels.applyLabel(var6);
            return var6;
         }
      }
   }

   private GlRenderPipeline compilePipeline(RenderPipeline var1, BiFunction<ResourceLocation, ShaderType, String> var2) {
      GlShaderModule var3 = this.getOrCompileShader(var1.getVertexShader(), ShaderType.VERTEX, var1.getShaderDefines(), var2);
      GlShaderModule var4 = this.getOrCompileShader(var1.getFragmentShader(), ShaderType.FRAGMENT, var1.getShaderDefines(), var2);
      if (var3 == GlShaderModule.INVALID_SHADER) {
         LOGGER.error("Couldn't compile pipeline {}: vertex shader {} was invalid", var1.getLocation(), var1.getVertexShader());
         return new GlRenderPipeline(var1, GlProgram.INVALID_PROGRAM);
      } else if (var4 == GlShaderModule.INVALID_SHADER) {
         LOGGER.error("Couldn't compile pipeline {}: fragment shader {} was invalid", var1.getLocation(), var1.getFragmentShader());
         return new GlRenderPipeline(var1, GlProgram.INVALID_PROGRAM);
      } else {
         GlProgram var5;
         try {
            var5 = GlProgram.link(var3, var4, var1.getVertexFormat(), var1.getLocation().toString());
         } catch (ShaderManager.CompilationException var7) {
            LOGGER.error("Couldn't compile program for pipeline {}: {}", var1.getLocation(), var7);
            return new GlRenderPipeline(var1, GlProgram.INVALID_PROGRAM);
         }

         var5.setupUniforms(var1.getUniforms(), var1.getSamplers());
         this.debugLabels.applyLabel(var5);
         return new GlRenderPipeline(var1, var5);
      }
   }

   public VertexArrayCache vertexArrayCache() {
      return this.vertexArrayCache;
   }

   public BufferStorage getBufferStorage() {
      return this.bufferStorage;
   }

   // $FF: synthetic method
   public CompiledRenderPipeline precompilePipeline(final RenderPipeline var1, @Nullable final BiFunction var2) {
      return this.precompilePipeline(var1, var2);
   }

   static record ShaderCompilationKey(ResourceLocation id, ShaderType type, ShaderDefines defines) {
      final ResourceLocation id;
      final ShaderType type;
      final ShaderDefines defines;

      ShaderCompilationKey(ResourceLocation var1, ShaderType var2, ShaderDefines var3) {
         super();
         this.id = var1;
         this.type = var2;
         this.defines = var3;
      }

      public String toString() {
         String var10000 = String.valueOf(this.id);
         String var1 = var10000 + " (" + String.valueOf(this.type) + ")";
         return !this.defines.isEmpty() ? var1 + " with " + String.valueOf(this.defines) : var1;
      }
   }
}
