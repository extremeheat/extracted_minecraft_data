package com.mojang.renderpearl.backend.opengl;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.util.ShaderCompileException;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL33C;
import org.slf4j.Logger;

public class GlProgram implements UncheckedAutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MAX_LOG_LENGTH = 32768;
   private final Int2ReferenceMap<@Nullable Uniform> uniforms = new Int2ReferenceOpenHashMap();
   private int maxUniformBinding = 0;
   private Uniform.@Nullable Ubo pushConstant = null;
   private final int programId;
   private final String debugLabel;

   private GlProgram(final int programId, final String debugLabel) {
      super();
      this.programId = programId;
      this.debugLabel = debugLabel;
   }

   public static GlProgram link(final List<GlShaderModule> compiledShaders, final String debugLabel) throws ShaderCompileException {
      int programId = GL33C.glCreateProgram();
      if (programId <= 0) {
         throw new ShaderCompileException("Could not create shader program (returned program ID " + programId + ")");
      } else {
         for(GlShaderModule shaderModule : compiledShaders) {
            GL33C.glAttachShader(programId, shaderModule.getShaderId());
         }

         GL33C.glLinkProgram(programId);
         int linkStatus = GL33C.glGetProgrami(programId, 35714);
         String linkMessage = GL33C.glGetProgramInfoLog(programId, 32768);
         if (linkStatus != 0 && !linkMessage.contains("Failed for unknown reason")) {
            if (!linkMessage.isEmpty()) {
               LOGGER.info("Info log when linking program containing {}. Log output: {}", shaderList(compiledShaders), linkMessage);
            }

            return new GlProgram(programId, debugLabel);
         } else {
            String var10002 = shaderList(compiledShaders);
            throw new ShaderCompileException("Error encountered when linking program containing " + var10002 + ". Log output: " + linkMessage);
         }
      }
   }

   private static String shaderList(final List<GlShaderModule> compiledShaders) {
      return (String)compiledShaders.stream().map((shader) -> {
         String var10000 = String.valueOf(shader.getType());
         return var10000 + " " + shader.getLabel();
      }).collect(Collectors.joining(", "));
   }

   public void setupBindGroupLayouts(final GlStateManager stateManager, final List<CompiledRenderPipeline.CreateInfo.Uniform> uniforms) {
      int nextUboBinding = 0;
      int nextSamplerIndex = 0;
      GL33C.glUseProgram(this.programId);
      int pushConstantBlock = GL33C.glGetUniformBlockIndex(this.programId, "_push_constants");
      if (pushConstantBlock != -1) {
         int uboBinding = nextUboBinding++;
         GL33C.glUniformBlockBinding(this.programId, pushConstantBlock, uboBinding);
         this.pushConstant = new Uniform.Ubo(uboBinding);
      }

      for(int i = 0; i < uniforms.size(); ++i) {
         CompiledRenderPipeline.CreateInfo.Uniform uniformDescription = (CompiledRenderPipeline.CreateInfo.Uniform)uniforms.get(i);
         String uniformName = String.format(Locale.ROOT, "_uniform_%02d_%02d", 0, uniformDescription.binding());
         Object var20;
         switch (uniformDescription.type()) {
            case UNIFORM_BUFFER:
               int index = GL33C.glGetUniformBlockIndex(this.programId, uniformName);
               if (index == -1) {
                  var20 = null;
               } else {
                  int uboBinding = nextUboBinding++;
                  GL33C.glUniformBlockBinding(this.programId, index, uboBinding);
                  var20 = new Uniform.Ubo(uboBinding);
               }
               break;
            case TEXEL_BUFFER:
               int location = GL33C.glGetUniformLocation(this.programId, uniformName);
               if (location == -1) {
                  var20 = null;
               } else {
                  int samplerIndex = nextSamplerIndex++;
                  GL33C.glUniform1i(location, samplerIndex);
                  var20 = new Uniform.Utb(stateManager, samplerIndex, (GpuFormat)Objects.requireNonNull(uniformDescription.gpuFormat()));
               }
               break;
            case COMBINED_IMAGE_SAMPLER:
               int location = GL33C.glGetUniformLocation(this.programId, uniformName);
               if (location == -1) {
                  var20 = null;
               } else {
                  int samplerIndex = nextSamplerIndex++;
                  GL33C.glUniform1i(location, samplerIndex);
                  var20 = new Uniform.Sampler(samplerIndex);
               }
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         Uniform uniform = (Uniform)var20;
         this.uniforms.put(uniformDescription.binding(), uniform);
      }

      GL33C.glUseProgram(0);
      int maxBinding = 0;

      for(CompiledRenderPipeline.CreateInfo.Uniform uniform : uniforms) {
         maxBinding = Math.max(maxBinding, uniform.binding());
      }

      this.maxUniformBinding = maxBinding;
   }

   public void close() {
      this.uniforms.values().forEach(UncheckedAutoCloseable::safeClose);
      GL33C.glDeleteProgram(this.programId);
   }

   public @Nullable Uniform getUniform(final int binding) {
      return (Uniform)this.uniforms.get(binding);
   }

   public int maxUniformBinding() {
      return this.maxUniformBinding;
   }

   @VisibleForTesting
   public int getProgramId() {
      return this.programId;
   }

   public String toString() {
      return this.debugLabel;
   }

   public String getDebugLabel() {
      return this.debugLabel;
   }

   public Uniform.@Nullable Ubo pushConstant() {
      return this.pushConstant;
   }
}
