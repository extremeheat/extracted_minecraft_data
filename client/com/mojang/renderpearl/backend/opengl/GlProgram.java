package com.mojang.renderpearl.backend.opengl;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.util.ShaderCompileException;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL33C;
import org.slf4j.Logger;

public class GlProgram implements UncheckedAutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final List<@Nullable Uniform> uniforms = new ReferenceArrayList();
   private Uniform.@Nullable Ubo pushConstant = null;
   private final int programId;
   private final String debugLabel;

   private GlProgram(final int programId, final String debugLabel) {
      super();
      this.programId = programId;
      this.debugLabel = debugLabel;
   }

   public static GlProgram link(final List<GlShaderModule> compiledShaders, final String debugLabel) throws ShaderCompileException {
      int programId = GlStateManager.glCreateProgram();
      if (programId <= 0) {
         throw new ShaderCompileException("Could not create shader program (returned program ID " + programId + ")");
      } else {
         for(GlShaderModule shaderModule : compiledShaders) {
            GlStateManager.glAttachShader(programId, shaderModule.getShaderId());
         }

         GlStateManager.glLinkProgram(programId);
         int linkStatus = GlStateManager.glGetProgrami(programId, 35714);
         String linkMessage = GlStateManager.glGetProgramInfoLog(programId, 32768);
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

   public void setupBindGroupLayouts(final List<BindGroupLayout.UniformDescription> uniforms) {
      int nextUboBinding = 0;
      int nextSamplerIndex = 0;
      GlStateManager._glUseProgram(this.programId);
      int pushConstantBlock = GL33C.glGetUniformBlockIndex(this.programId, "_push_constants");
      if (pushConstantBlock != -1) {
         int uboBinding = nextUboBinding++;
         GL33C.glUniformBlockBinding(this.programId, pushConstantBlock, uboBinding);
         this.pushConstant = new Uniform.Ubo(uboBinding);
      }

      for(int i = 0; i < uniforms.size(); ++i) {
         BindGroupLayout.UniformDescription uniformDescription = (BindGroupLayout.UniformDescription)uniforms.get(i);
         String uniformName = String.format(Locale.ROOT, "_uniform_%02d_%02d", 0, i);
         Object var16;
         switch (uniformDescription.type()) {
            case UNIFORM_BUFFER:
               int index = GL33C.glGetUniformBlockIndex(this.programId, uniformName);
               if (index == -1) {
                  var16 = null;
               } else {
                  int uboBinding = nextUboBinding++;
                  GL33C.glUniformBlockBinding(this.programId, index, uboBinding);
                  var16 = new Uniform.Ubo(uboBinding);
               }
               break;
            case TEXEL_BUFFER:
               int location = GlStateManager._glGetUniformLocation(this.programId, uniformName);
               if (location == -1) {
                  var16 = null;
               } else {
                  int samplerIndex = nextSamplerIndex++;
                  GL33C.glUniform1i(location, samplerIndex);
                  var16 = new Uniform.Utb(samplerIndex, (GpuFormat)Objects.requireNonNull(uniformDescription.gpuFormat()));
               }
               break;
            case COMBINED_IMAGE_SAMPLER:
               int location = GlStateManager._glGetUniformLocation(this.programId, uniformName);
               if (location == -1) {
                  var16 = null;
               } else {
                  int samplerIndex = nextSamplerIndex++;
                  GL33C.glUniform1i(location, samplerIndex);
                  var16 = new Uniform.Sampler(samplerIndex);
               }
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         Uniform uniform = (Uniform)var16;
         this.uniforms.add(uniform);
      }

      GlStateManager._glUseProgram(0);
   }

   public void close() {
      this.uniforms.forEach(UncheckedAutoCloseable::safeClose);
      GlStateManager.glDeleteProgram(this.programId);
   }

   public @Nullable Uniform getUniform(final int index) {
      return index >= this.uniforms.size() ? null : (Uniform)this.uniforms.get(index);
   }

   public int uniformCount() {
      return this.uniforms.size();
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
