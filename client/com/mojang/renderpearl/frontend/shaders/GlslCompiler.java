package com.mojang.renderpearl.frontend.shaders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
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
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.util.shaderc.ShadercIncludeResolve;
import org.lwjgl.util.shaderc.ShadercIncludeResultRelease;

public class GlslCompiler implements UncheckedAutoCloseable {
   private final boolean isZeroToOne;
   private final boolean shaderDrawParameters;
   private final ShadercIncludeResultRelease includeResultRelease;
   private final ShaderSource.CachedIncludeSource missingIncludeResult;
   private final ShaderSource.CachedIncludeSource malformedIdResult;
   private final LongArrayList compilers = new LongArrayList();

   public GlslCompiler(final boolean isZeroToOne, final boolean shaderDrawParameters) {
      super();
      this.isZeroToOne = isZeroToOne;
      this.shaderDrawParameters = shaderDrawParameters;
      this.includeResultRelease = ShadercIncludeResultRelease.create(GlslCompiler::releaseIncludeResult);
      this.missingIncludeResult = ShaderSource.CachedIncludeSource.createError("not found");
      this.malformedIdResult = ShaderSource.CachedIncludeSource.createError("malformed id");
   }

   private ShaderSource.CachedIncludeSource processInclude(final ShaderSource shaderSource, final String requestedShader) {
      Identifier id = Identifier.tryParse(requestedShader);
      if (id == null) {
         return this.malformedIdResult;
      } else {
         ShaderSource.CachedIncludeSource shaderContents = shaderSource.getInclude(id);
         return shaderContents == null ? this.missingIncludeResult : shaderContents;
      }
   }

   private ShadercIncludeResolve createIncludeResolver(final ShaderSource shaderSource) {
      return ShadercIncludeResolve.create((var2, requested_source, var6, var7, var9) -> {
         String requestedShader = MemoryUtil.memASCII(requested_source);
         return this.processInclude(shaderSource, requestedShader).includeResultPtr();
      });
   }

   private static void releaseIncludeResult(final long user_data, final long include_result) {
   }

   public void close() {
      this.includeResultRelease.close();
      this.malformedIdResult.close();
      this.missingIncludeResult.close();
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

      ShadercIncludeResolve includeResolver = this.createIncludeResolver(shaderSource);

      SPIRVModule var20;
      try {
         Shaderc.shaderc_compile_options_set_include_callbacks(shaderOptions, includeResolver, this.includeResultRelease, 0L);
         long compiler = this.acquireCompiler();

         long result;
         try {
            Zone tracyZone = TracyClient.beginZone("Compile to SPV", false);

            try {
               tracyZone.addText(name);
               result = Shaderc.shaderc_compile_into_spv(compiler, sourceBuffer, shaderType, filenameBuffer, entrypointBuffer, shaderOptions);
            } catch (Throwable var36) {
               if (tracyZone != null) {
                  try {
                     tracyZone.close();
                  } catch (Throwable var35) {
                     var36.addSuppressed(var35);
                  }
               }

               throw var36;
            }

            if (tracyZone != null) {
               tracyZone.close();
            }
         } finally {
            this.releaseCompiler(compiler);
         }

         try {
            int status = Shaderc.shaderc_result_get_compilation_status(result);
            if (status != 0) {
               throw new ShaderCompileException("Couldn't parse GLSL: " + Shaderc.shaderc_result_get_error_message(result));
            }

            ByteBuffer spirv = Shaderc.shaderc_result_get_bytes(result);
            ByteBuffer copy = MemoryUtil.memCalloc(spirv.remaining());
            MemoryUtil.memCopy(spirv, copy);
            var20 = new SPIRVModule(copy, type);
         } finally {
            Shaderc.shaderc_result_release(result);
            Shaderc.shaderc_compile_options_release(shaderOptions);
            MemoryUtil.memFree(entrypointBuffer);
            MemoryUtil.memFree(filenameBuffer);
            MemoryUtil.memFree(sourceBuffer);
         }
      } catch (Throwable var39) {
         if (includeResolver != null) {
            try {
               includeResolver.close();
            } catch (Throwable var34) {
               var39.addSuppressed(var34);
            }
         }

         throw var39;
      }

      if (includeResolver != null) {
         includeResolver.close();
      }

      return var20;
   }
}
