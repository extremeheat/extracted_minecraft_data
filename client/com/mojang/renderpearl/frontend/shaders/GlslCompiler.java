package com.mojang.renderpearl.frontend.shaders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.pipeline.ShaderSource;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.backend.api.SpvModule;
import com.mojang.renderpearl.util.ShaderCompileException;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.util.shaderc.ShadercIncludeResolve;
import org.lwjgl.util.shaderc.ShadercIncludeResult;
import org.lwjgl.util.shaderc.ShadercIncludeResultRelease;
import org.slf4j.Logger;

public class GlslCompiler implements UncheckedAutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long shaderCompiler;
   private final boolean isZeroToOne;
   private final boolean shaderDrawParameters;
   private final ShadercIncludeResolve includeResolver;
   private final ShadercIncludeResultRelease includeResultRelease;
   private @Nullable ShaderSource currentShaderSource;

   public GlslCompiler(final boolean isZeroToOne, final boolean shaderDrawParameters) {
      super();
      this.isZeroToOne = isZeroToOne;
      this.shaderDrawParameters = shaderDrawParameters;
      this.shaderCompiler = Shaderc.shaderc_compiler_initialize();
      this.includeResolver = ShadercIncludeResolve.create((user_data, requested_source, type, requesting_source, include_depth) -> {
         assert this.currentShaderSource != null;

         String requestedShader = MemoryUtil.memASCII(requested_source);
         String requestingShader = MemoryUtil.memASCII(requesting_source);
         ShadercIncludeResult result = ShadercIncludeResult.calloc();

         try {
            String shaderContents = this.currentShaderSource.get(Identifier.parse(requestedShader).withPrefix("shaders/include/"), (ShaderType)null);
            if (shaderContents != null) {
               result.source_name(MemoryUtil.memASCII(requestedShader, false));
               result.content(MemoryUtil.memASCII(shaderContents, false));
               return result.address();
            }
         } catch (Throwable throwable) {
            LOGGER.error("Shader \"{}\" include of \"{}\" failed", new Object[]{requestingShader, requestedShader, throwable});
         }

         result.source_name(MemoryUtil.memASCII("", false));
         result.content(MemoryUtil.memASCII("", false));
         return result.address();
      });
      this.includeResultRelease = ShadercIncludeResultRelease.create((user_data, include_result) -> {
         MemoryUtil.nmemFree(MemoryUtil.memGetAddress(include_result + (long)ShadercIncludeResult.SOURCE_NAME));
         MemoryUtil.nmemFree(MemoryUtil.memGetAddress(include_result + (long)ShadercIncludeResult.CONTENT));
         MemoryUtil.nmemFree(include_result);
      });
   }

   public void close() {
      this.includeResultRelease.close();
      this.includeResolver.close();
      Shaderc.shaderc_compiler_release(this.shaderCompiler);
   }

   private long createBaseShaderOptions() {
      long shaderOptions = Shaderc.shaderc_compile_options_initialize();
      Shaderc.shaderc_compile_options_set_target_env(shaderOptions, 0, 4202496);
      Shaderc.shaderc_compile_options_set_auto_bind_uniforms(shaderOptions, true);
      Shaderc.shaderc_compile_options_set_preserve_bindings(shaderOptions, false);
      Shaderc.shaderc_compile_options_set_generate_debug_info(shaderOptions);
      Shaderc.shaderc_compile_options_set_optimization_level(shaderOptions, 0);
      if (this.isZeroToOne) {
         Shaderc.shaderc_compile_options_add_macro_definition(shaderOptions, "RENDERPEARL_DEPTH_IS_ZERO_TO_ONE", "");
      }

      if (RenderSystem.getDevice().getDeviceInfo().hintsAndWorkarounds().isExplicitDepthRequired()) {
         Shaderc.shaderc_compile_options_add_macro_definition(shaderOptions, "RENDERPEARL_EXPLICIT_DEPTH_INVARIANCE", "");
      }

      if (this.shaderDrawParameters) {
         Shaderc.shaderc_compile_options_add_macro_definition(shaderOptions, "RENDERPEARL_INSTANCE_INDEX_INCLUDES_BASE_INSTANCE", "");
      }

      return shaderOptions;
   }

   public SpvModule compileToSpv(final String name, final String source, final ShaderType type, final ShaderDefines shaderDefines, final ShaderSource shaderSource) throws ShaderCompileException {
      int shaderType = type == ShaderType.FRAGMENT ? 1 : 0;
      ByteBuffer sourceBuffer = MemoryUtil.memUTF8(source, false);
      ByteBuffer filenameBuffer = MemoryUtil.memUTF8(name);
      ByteBuffer entrypointBuffer = MemoryUtil.memUTF8("main");
      long shaderOptions = this.createBaseShaderOptions();

      for(Map.Entry<String, String> macro : shaderDefines.values().entrySet()) {
         Shaderc.shaderc_compile_options_add_macro_definition(shaderOptions, (CharSequence)macro.getKey(), (CharSequence)macro.getValue());
      }

      for(String flag : shaderDefines.flags()) {
         Shaderc.shaderc_compile_options_add_macro_definition(shaderOptions, flag, "");
      }

      this.currentShaderSource = shaderSource;
      Shaderc.shaderc_compile_options_set_include_callbacks(shaderOptions, this.includeResolver, this.includeResultRelease, 0L);
      long result = Shaderc.shaderc_compile_into_spv(this.shaderCompiler, sourceBuffer, shaderType, filenameBuffer, entrypointBuffer, shaderOptions);

      SPIRVModule var17;
      try {
         int status = Shaderc.shaderc_result_get_compilation_status(result);
         if (status != 0) {
            throw new ShaderCompileException("Couldn't parse GLSL: " + Shaderc.shaderc_result_get_error_message(result));
         }

         ByteBuffer spirv = Shaderc.shaderc_result_get_bytes(result);
         ByteBuffer copy = MemoryUtil.memCalloc(spirv.remaining());
         MemoryUtil.memCopy(spirv, copy);
         var17 = new SPIRVModule(copy, type);
      } finally {
         Shaderc.shaderc_result_release(result);
         Shaderc.shaderc_compile_options_release(shaderOptions);
         MemoryUtil.memFree(entrypointBuffer);
         MemoryUtil.memFree(filenameBuffer);
         MemoryUtil.memFree(sourceBuffer);
         this.currentShaderSource = null;
      }

      return var17;
   }
}
