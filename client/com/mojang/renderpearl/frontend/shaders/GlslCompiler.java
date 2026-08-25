package com.mojang.renderpearl.frontend.shaders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.pipeline.ShaderSource;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.backend.api.SpvModule;
import com.mojang.renderpearl.util.ShaderCompileException;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.longs.LongArrayList;
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
   private static final ScopedValue<ShaderSource> SHADER_SOURCE = ScopedValue.newInstance();
   private final boolean isZeroToOne;
   private final boolean shaderDrawParameters;
   private final ShadercIncludeResolve includeResolver;
   private final ShadercIncludeResultRelease includeResultRelease;
   private final LongArrayList compilers = new LongArrayList();

   public GlslCompiler(final boolean isZeroToOne, final boolean shaderDrawParameters) {
      super();
      this.isZeroToOne = isZeroToOne;
      this.shaderDrawParameters = shaderDrawParameters;
      this.includeResolver = ShadercIncludeResolve.create(GlslCompiler::processInclude);
      this.includeResultRelease = ShadercIncludeResultRelease.create(GlslCompiler::releaseIncludeResult);
   }

   private static long processInclude(final long user_data, final long requested_source, final int type, final long requesting_source, final long include_depth) {
      ShaderSource shaderSource = (ShaderSource)SHADER_SOURCE.get();
      String requestedShader = MemoryUtil.memASCII(requested_source);
      String requestingShader = MemoryUtil.memASCII(requesting_source);
      ShadercIncludeResult result = tryInclude(shaderSource, requestingShader, requestedShader);
      if (result != null) {
         return result.address();
      } else {
         ShadercIncludeResult failedResult = ShadercIncludeResult.calloc();
         failedResult.source_name(MemoryUtil.memASCII("", false));
         failedResult.content(MemoryUtil.memASCII("", false));
         return failedResult.address();
      }
   }

   private static @Nullable ShadercIncludeResult tryInclude(final @Nullable ShaderSource shaderSource, final String requestingShader, final String requestedShader) {
      try {
         if (shaderSource == null) {
            LOGGER.error("Shader \"{}\" include of \"{}\" failed, ShaderSource not set", requestingShader, requestedShader);
            return null;
         } else {
            String shaderContents = shaderSource.get(Identifier.parse(requestedShader).withPrefix("shaders/include/"), (ShaderType)null);
            if (shaderContents == null) {
               LOGGER.error("Shader \"{}\" include of \"{}\" failed, contents not found", requestingShader, requestedShader);
               return null;
            } else {
               ShadercIncludeResult result = ShadercIncludeResult.calloc();
               result.source_name(MemoryUtil.memASCII(requestedShader, false));
               result.content(MemoryUtil.memASCII(shaderContents, false));
               return result;
            }
         }
      } catch (Throwable throwable) {
         LOGGER.error("Shader \"{}\" include of \"{}\" failed", new Object[]{requestingShader, requestedShader, throwable});
         return null;
      }
   }

   private static void releaseIncludeResult(final long user_data, final long include_result) {
      MemoryUtil.nmemFree(MemoryUtil.memGetAddress(include_result + (long)ShadercIncludeResult.SOURCE_NAME));
      MemoryUtil.nmemFree(MemoryUtil.memGetAddress(include_result + (long)ShadercIncludeResult.CONTENT));
      MemoryUtil.nmemFree(include_result);
   }

   public void close() {
      this.includeResultRelease.close();
      this.includeResolver.close();
      this.compilers.forEach(Shaderc::shaderc_compiler_release);
      this.compilers.clear();
   }

   private synchronized long acquireCompiler() {
      return this.compilers.isEmpty() ? Shaderc.shaderc_compiler_initialize() : this.compilers.popLong();
   }

   private synchronized void releaseCompiler(final long compiler) {
      this.compilers.add(compiler);
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
      return (SpvModule)ScopedValue.where(SHADER_SOURCE, shaderSource).call(() -> this.compileToSpv(name, source, type, shaderDefines));
   }

   private SpvModule compileToSpv(final String name, final String source, final ShaderType type, final ShaderDefines shaderDefines) throws ShaderCompileException {
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

      Shaderc.shaderc_compile_options_set_include_callbacks(shaderOptions, this.includeResolver, this.includeResultRelease, 0L);
      long compiler = this.acquireCompiler();

      long result;
      try {
         Zone tracyZone = TracyClient.beginZone("Compile to SPV", false);

         try {
            tracyZone.addText(name);
            result = Shaderc.shaderc_compile_into_spv(compiler, sourceBuffer, shaderType, filenameBuffer, entrypointBuffer, shaderOptions);
         } catch (Throwable var29) {
            if (tracyZone != null) {
               try {
                  tracyZone.close();
               } catch (Throwable var28) {
                  var29.addSuppressed(var28);
               }
            }

            throw var29;
         }

         if (tracyZone != null) {
            tracyZone.close();
         }
      } finally {
         this.releaseCompiler(compiler);
      }

      SPIRVModule var18;
      try {
         int status = Shaderc.shaderc_result_get_compilation_status(result);
         if (status != 0) {
            throw new ShaderCompileException("Couldn't parse GLSL: " + Shaderc.shaderc_result_get_error_message(result));
         }

         ByteBuffer spirv = Shaderc.shaderc_result_get_bytes(result);
         ByteBuffer copy = MemoryUtil.memCalloc(spirv.remaining());
         MemoryUtil.memCopy(spirv, copy);
         var18 = new SPIRVModule(copy, type);
      } finally {
         Shaderc.shaderc_result_release(result);
         Shaderc.shaderc_compile_options_release(shaderOptions);
         MemoryUtil.memFree(entrypointBuffer);
         MemoryUtil.memFree(filenameBuffer);
         MemoryUtil.memFree(sourceBuffer);
      }

      return var18;
   }
}
