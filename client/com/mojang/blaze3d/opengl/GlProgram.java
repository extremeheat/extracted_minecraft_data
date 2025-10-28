package com.mojang.blaze3d.opengl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
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
   public static Set<String> BUILT_IN_UNIFORMS = Sets.newHashSet(new String[]{"Projection", "Lighting", "Fog", "Globals"});
   public static GlProgram INVALID_PROGRAM = new GlProgram(-1, "invalid");
   private final Map<String, Uniform> uniformsByName = new HashMap();
   private final int programId;
   private final String debugLabel;

   private GlProgram(int var1, String var2) {
      super();
      this.programId = var1;
      this.debugLabel = var2;
   }

   public static GlProgram link(GlShaderModule var0, GlShaderModule var1, VertexFormat var2, String var3) throws ShaderManager.CompilationException {
      int var4 = GlStateManager.glCreateProgram();
      if (var4 <= 0) {
         throw new ShaderManager.CompilationException("Could not create shader program (returned program ID " + var4 + ")");
      } else {
         int var5 = 0;

         for(String var7 : var2.getElementAttributeNames()) {
            GlStateManager._glBindAttribLocation(var4, var5, var7);
            ++var5;
         }

         GlStateManager.glAttachShader(var4, var0.getShaderId());
         GlStateManager.glAttachShader(var4, var1.getShaderId());
         GlStateManager.glLinkProgram(var4);
         int var8 = GlStateManager.glGetProgrami(var4, 35714);
         String var9 = GlStateManager.glGetProgramInfoLog(var4, 32768);
         if (var8 != 0 && !var9.contains("Failed for unknown reason")) {
            if (!var9.isEmpty()) {
               LOGGER.info("Info log when linking program containing VS {} and FS {}. Log output: {}", new Object[]{var0.getId(), var1.getId(), var9});
            }

            return new GlProgram(var4, var3);
         } else {
            String var10002 = String.valueOf(var0.getId());
            throw new ShaderManager.CompilationException("Error encountered when linking program containing VS " + var10002 + " and FS " + String.valueOf(var1.getId()) + ". Log output: " + var9);
         }
      }
   }

   public void setupUniforms(List<RenderPipeline.UniformDescription> var1, List<String> var2) {
      int var3 = 0;
      int var4 = 0;

      for(RenderPipeline.UniformDescription var6 : var1) {
         String var7 = var6.name();
         Object var10000;
         switch (var6.type()) {
            case UNIFORM_BUFFER:
               int var19 = GL31.glGetUniformBlockIndex(this.programId, var7);
               if (var19 == -1) {
                  var10000 = null;
               } else {
                  int var20 = var3++;
                  GL31.glUniformBlockBinding(this.programId, var19, var20);
                  var10000 = new Uniform.Ubo(var20);
               }
               break;
            case TEXEL_BUFFER:
               int var9 = GlStateManager._glGetUniformLocation(this.programId, var7);
               if (var9 == -1) {
                  LOGGER.warn("{} shader program does not use utb {} defined in the pipeline. This might be a bug.", this.debugLabel, var7);
                  var10000 = null;
               } else {
                  int var10 = var4++;
                  var10000 = new Uniform.Utb(var9, var10, (TextureFormat)Objects.requireNonNull(var6.textureFormat()));
               }
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         Object var8 = var10000;
         if (var8 != null) {
            this.uniformsByName.put(var7, var8);
         }
      }

      for(String var13 : var2) {
         int var15 = GlStateManager._glGetUniformLocation(this.programId, var13);
         if (var15 == -1) {
            LOGGER.warn("{} shader program does not use sampler {} defined in the pipeline. This might be a bug.", this.debugLabel, var13);
         } else {
            int var17 = var4++;
            this.uniformsByName.put(var13, new Uniform.Sampler(var15, var17));
         }
      }

      int var12 = GlStateManager.glGetProgrami(this.programId, 35382);

      for(int var14 = 0; var14 < var12; ++var14) {
         String var16 = GL31.glGetActiveUniformBlockName(this.programId, var14);
         if (!this.uniformsByName.containsKey(var16)) {
            if (!var2.contains(var16) && BUILT_IN_UNIFORMS.contains(var16)) {
               int var18 = var3++;
               GL31.glUniformBlockBinding(this.programId, var14, var18);
               this.uniformsByName.put(var16, new Uniform.Ubo(var18));
            } else {
               LOGGER.warn("Found unknown and unsupported uniform {} in {}", var16, this.debugLabel);
            }
         }
      }

   }

   public void close() {
      this.uniformsByName.values().forEach(Uniform::close);
      GlStateManager.glDeleteProgram(this.programId);
   }

   public @Nullable Uniform getUniform(String var1) {
      RenderSystem.assertOnRenderThread();
      return (Uniform)this.uniformsByName.get(var1);
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
