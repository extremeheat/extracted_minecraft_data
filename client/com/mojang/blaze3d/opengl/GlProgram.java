package com.mojang.blaze3d.opengl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.renderer.ShaderManager;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL31;
import org.slf4j.Logger;

public class GlProgram implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Set<String> BUILT_IN_UNIFORMS = Sets.newHashSet(new String[]{"Projection", "Lighting", "Fog", "Globals"});
   public static final GlProgram INVALID_PROGRAM = new GlProgram(-1, "invalid");
   private final Map<String, Uniform> uniformsByName = new HashMap();
   private final int programId;
   private final String debugLabel;

   private GlProgram(final int programId, final String debugLabel) {
      super();
      this.programId = programId;
      this.debugLabel = debugLabel;
   }

   public static GlProgram link(final GlShaderModule vertexShader, final GlShaderModule fragmentShader, final VertexFormat vertexFormat, final String debugLabel) throws ShaderManager.CompilationException {
      int programId = GlStateManager.glCreateProgram();
      if (programId <= 0) {
         throw new ShaderManager.CompilationException("Could not create shader program (returned program ID " + programId + ")");
      } else {
         int attributeLocation = 0;

         for(String attributeName : vertexFormat.getElementAttributeNames()) {
            GlStateManager._glBindAttribLocation(programId, attributeLocation, attributeName);
            ++attributeLocation;
         }

         GlStateManager.glAttachShader(programId, vertexShader.getShaderId());
         GlStateManager.glAttachShader(programId, fragmentShader.getShaderId());
         GlStateManager.glLinkProgram(programId);
         int linkStatus = GlStateManager.glGetProgrami(programId, 35714);
         String linkMessage = GlStateManager.glGetProgramInfoLog(programId, 32768);
         if (linkStatus != 0 && !linkMessage.contains("Failed for unknown reason")) {
            if (!linkMessage.isEmpty()) {
               LOGGER.info("Info log when linking program containing VS {} and FS {}. Log output: {}", new Object[]{vertexShader.getId(), fragmentShader.getId(), linkMessage});
            }

            return new GlProgram(programId, debugLabel);
         } else {
            String var10002 = String.valueOf(vertexShader.getId());
            throw new ShaderManager.CompilationException("Error encountered when linking program containing VS " + var10002 + " and FS " + String.valueOf(fragmentShader.getId()) + ". Log output: " + linkMessage);
         }
      }
   }

   public void setupUniforms(final List<RenderPipeline.UniformDescription> uniforms, final List<String> samplers) {
      int nextUboBinding = 0;
      int nextSamplerIndex = 0;

      for(RenderPipeline.UniformDescription uniformDescription : uniforms) {
         String uniformName = uniformDescription.name();
         Object var10000;
         switch (uniformDescription.type()) {
            case UNIFORM_BUFFER:
               int index = GL31.glGetUniformBlockIndex(this.programId, uniformName);
               if (index == -1) {
                  var10000 = null;
               } else {
                  int uboBinding = nextUboBinding++;
                  GL31.glUniformBlockBinding(this.programId, index, uboBinding);
                  var10000 = new Uniform.Ubo(uboBinding);
               }
               break;
            case TEXEL_BUFFER:
               int location = GlStateManager._glGetUniformLocation(this.programId, uniformName);
               if (location == -1) {
                  LOGGER.warn("{} shader program does not use utb {} defined in the pipeline. This might be a bug.", this.debugLabel, uniformName);
                  var10000 = null;
               } else {
                  int samplerIndex = nextSamplerIndex++;
                  var10000 = new Uniform.Utb(location, samplerIndex, (GpuFormat)Objects.requireNonNull(uniformDescription.gpuFormat()));
               }
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         Uniform uniform = (Uniform)var10000;
         if (uniform != null) {
            this.uniformsByName.put(uniformName, uniform);
         }
      }

      for(String sampler : samplers) {
         int location = GlStateManager._glGetUniformLocation(this.programId, sampler);
         if (location == -1) {
            LOGGER.warn("{} shader program does not use sampler {} defined in the pipeline. This might be a bug.", this.debugLabel, sampler);
         } else {
            int samplerIndex = nextSamplerIndex++;
            this.uniformsByName.put(sampler, new Uniform.Sampler(location, samplerIndex));
         }
      }

      int totalDefinedBlocks = GlStateManager.glGetProgrami(this.programId, 35382);

      for(int i = 0; i < totalDefinedBlocks; ++i) {
         String name = GL31.glGetActiveUniformBlockName(this.programId, i);
         if (!this.uniformsByName.containsKey(name)) {
            if (!samplers.contains(name) && BUILT_IN_UNIFORMS.contains(name)) {
               int uboBinding = nextUboBinding++;
               GL31.glUniformBlockBinding(this.programId, i, uboBinding);
               this.uniformsByName.put(name, new Uniform.Ubo(uboBinding));
            } else {
               LOGGER.warn("Found unknown and unsupported uniform {} in {}", name, this.debugLabel);
            }
         }
      }

   }

   public void close() {
      this.uniformsByName.values().forEach(Uniform::close);
      GlStateManager.glDeleteProgram(this.programId);
   }

   public @Nullable Uniform getUniform(final String name) {
      RenderSystem.assertOnRenderThread();
      return (Uniform)this.uniformsByName.get(name);
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

   public Map<String, Uniform> getUniforms() {
      return this.uniformsByName;
   }
}
