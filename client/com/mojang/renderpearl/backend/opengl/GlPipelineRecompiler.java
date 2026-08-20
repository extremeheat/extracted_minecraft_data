package com.mojang.renderpearl.backend.opengl;

import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.backend.api.BackendRenderPipeline;
import com.mojang.renderpearl.backend.api.SpvModule;
import com.mojang.renderpearl.frontend.shaders.SpvUtil;
import com.mojang.renderpearl.util.ShaderCompileException;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Locale;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.spvc.Spvc;
import org.lwjgl.util.spvc.SpvcReflectedResource;
import org.slf4j.Logger;

public class GlPipelineRecompiler {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MAX_LOG_LENGTH = 32768;
   private static final boolean IS_MACOS;
   public static final String UNIFORM_FORMAT_STRING = "_uniform_%02d_%02d";
   public static final String PUSH_CONSTANT_BLOCK_NAME = "_push_constants";
   private static final boolean SHADER_DEBUG_MODE;
   private final GlDebugLabel debugLabels;
   private final boolean drawParametersSupported;

   public GlPipelineRecompiler(final GlDebugLabel debugLabels, final boolean drawParametersSupported) {
      super();
      this.debugLabels = debugLabels;
      this.drawParametersSupported = drawParametersSupported;
   }

   private String decompileShader(final BackendRenderPipeline.CreateInfo.Shader shaderCreateInfo) throws ShaderCompileException {
      SpvModule spvModule = shaderCreateInfo.module();
      IntBuffer spirv = spvModule.spv().asIntBuffer();
      long spvcContext = 0L;

      String var25;
      try {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            PointerBuffer pointerReturnBuffer = stack.callocPointer(1);
            SpvUtil.throwIfError(Spvc.spvc_context_create(pointerReturnBuffer), "Couldn't create spvc context");
            spvcContext = pointerReturnBuffer.get(0);
            SpvUtil.throwIfError(Spvc.spvc_context_parse_spirv(spvcContext, spirv, (long)spirv.remaining(), pointerReturnBuffer), "Couldn't parse spirv");
            long ir = pointerReturnBuffer.get(0);
            SpvUtil.throwIfError(Spvc.spvc_context_create_compiler(spvcContext, 1, ir, 0, pointerReturnBuffer), "Couldn't create compiler");
            long compiler = pointerReturnBuffer.get(0);
            Spvc.spvc_compiler_create_compiler_options(compiler, pointerReturnBuffer);
            long options = pointerReturnBuffer.get(0);
            Spvc.spvc_compiler_options_set_uint(options, 33554440, 330);
            Spvc.spvc_compiler_options_set_bool(options, 33554439, false);
            Spvc.spvc_compiler_options_set_bool(options, 33554465, true);
            Spvc.spvc_compiler_options_set_bool(options, 33554437, this.drawParametersSupported);
            if (!SHADER_DEBUG_MODE) {
               Spvc.spvc_compiler_options_set_bool(options, 16777270, true);
               Spvc.spvc_compiler_options_set_bool(options, 16777218, true);
            }

            SpvUtil.throwIfError(Spvc.spvc_compiler_create_shader_resources(compiler, pointerReturnBuffer), "Couldn't create resource list");
            long spvcResources = pointerReturnBuffer.get(0);
            if (!SHADER_DEBUG_MODE) {
               if (spvModule.type() == ShaderType.VERTEX) {
                  this.renameInterfaceVariables(compiler, spvcResources, 3, "_vert_input_%02d");
               }

               if (spvModule.type() == ShaderType.FRAGMENT) {
                  this.renameInterfaceVariables(compiler, spvcResources, 4, "_frag_output_%02d");
               }
            }

            if (SHADER_DEBUG_MODE && !IS_MACOS) {
               Spvc.spvc_compiler_options_set_bool(options, 33554438, true);
            } else if (spvModule.type() == ShaderType.VERTEX || spvModule.type() == ShaderType.FRAGMENT) {
               this.renameInterfaceVariables(compiler, spvcResources, spvModule.type() == ShaderType.FRAGMENT ? 3 : 4, "_interface_variable_%02d");
            }

            for(int i = 0; i < SpvUtil.DESCRIPTOR_TYPES.size(); ++i) {
               int descriptorType = SpvUtil.DESCRIPTOR_TYPES.getInt(i);
               this.renameDescriptors(compiler, spvcResources, descriptorType);
            }

            this.renameDescriptors(compiler, spvcResources, 9);
            String var10001 = shaderCreateInfo.entryPoint();
            byte var10002;
            switch (spvModule.type()) {
               case VERTEX -> var10002 = 0;
               case FRAGMENT -> var10002 = 4;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            Spvc.spvc_compiler_set_entry_point(compiler, var10001, var10002);
            Spvc.spvc_compiler_install_compiler_options(compiler, options);
            Spvc.spvc_compiler_compile(compiler, pointerReturnBuffer);
            var25 = MemoryUtil.memASCII(pointerReturnBuffer.get(0));
         } catch (Throwable var23) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var22) {
                  var23.addSuppressed(var22);
               }
            }

            throw var23;
         }

         if (stack != null) {
            stack.close();
         }
      } finally {
         if (spvcContext != 0L) {
            Spvc.spvc_context_destroy(spvcContext);
         }

      }

      return var25;
   }

   private void renameInterfaceVariables(final long compiler, final long spvcResources, final int resourceType, final String formatString) throws ShaderCompileException {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         PointerBuffer countPointer = stack.callocPointer(1);
         IntBuffer intReturnBuffer = stack.callocInt(1);
         PointerBuffer pointerReturnBuffer = stack.callocPointer(1);
         SpvUtil.throwIfError(Spvc.spvc_resources_get_resource_list_for_type(spvcResources, resourceType, pointerReturnBuffer, countPointer), "Couldn't get resource list");
         long interfaceVariableCount = countPointer.get(0);
         SpvcReflectedResource.Buffer interfaceVariables = SpvcReflectedResource.create(pointerReturnBuffer.get(0), (int)interfaceVariableCount);

         for(int i = 0; (long)i < interfaceVariableCount; ++i) {
            SpvcReflectedResource interfaceVariable = (SpvcReflectedResource)interfaceVariables.get(i);
            int location = Spvc.spvc_compiler_get_decoration(compiler, interfaceVariable.id(), 30);
            Spvc.spvc_compiler_set_name(compiler, interfaceVariable.id(), String.format(Locale.ROOT, formatString, location));
         }
      } catch (Throwable var18) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var17) {
               var18.addSuppressed(var17);
            }
         }

         throw var18;
      }

      if (stack != null) {
         stack.close();
      }

   }

   private void renameDescriptors(final long compiler, final long spvcResources, final int resourceType) throws ShaderCompileException {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         PointerBuffer pointerReturnBuffer = stack.callocPointer(1);
         IntBuffer intReturnBuffer = stack.callocInt(1);
         PointerBuffer countPointer = stack.callocPointer(1);
         SpvUtil.throwIfError(Spvc.spvc_resources_get_resource_list_for_type(spvcResources, resourceType, pointerReturnBuffer, countPointer), "Couldn't list input variables");
         long resourceCount = countPointer.get(0);
         SpvcReflectedResource.Buffer resources = SpvcReflectedResource.create(pointerReturnBuffer.get(0), (int)resourceCount);

         for(int i = 0; (long)i < resourceCount; ++i) {
            SpvcReflectedResource resource = (SpvcReflectedResource)resources.get(i);
            int descriptorSet = Spvc.spvc_compiler_get_decoration(compiler, resource.id(), 34);
            int binding = Spvc.spvc_compiler_get_decoration(compiler, resource.id(), 33);
            switch (resourceType) {
               case 1:
               case 2:
                  String blockName = String.format(Locale.ROOT, "_uniform_instance_%02d_%02d", descriptorSet, binding);
                  if (SHADER_DEBUG_MODE) {
                     String existingBlockName = Spvc.spvc_compiler_get_name(compiler, resource.base_type_id());
                     if (existingBlockName != null) {
                        blockName = existingBlockName;
                     }
                  }

                  Spvc.spvc_compiler_set_name(compiler, resource.id(), blockName);
                  Spvc.spvc_compiler_set_name(compiler, resource.base_type_id(), String.format(Locale.ROOT, "_uniform_%02d_%02d", descriptorSet, binding));
               case 3:
               case 4:
               case 5:
               case 8:
               default:
                  break;
               case 6:
               case 7:
               case 10:
               case 11:
                  Spvc.spvc_compiler_set_name(compiler, resource.id(), String.format(Locale.ROOT, "_uniform_%02d_%02d", descriptorSet, binding));
                  break;
               case 9:
                  String blockName = "_push_constants_instance";
                  if (SHADER_DEBUG_MODE) {
                     String existingBlockName = Spvc.spvc_compiler_get_name(compiler, resource.base_type_id());
                     if (existingBlockName != null) {
                        blockName = existingBlockName;
                     }
                  }

                  Spvc.spvc_compiler_set_name(compiler, resource.id(), blockName);
                  Spvc.spvc_compiler_set_name(compiler, resource.base_type_id(), "_push_constants");
            }
         }
      } catch (Throwable var20) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var19) {
               var20.addSuppressed(var19);
            }
         }

         throw var20;
      }

      if (stack != null) {
         stack.close();
      }

   }

   private GlShaderModule compileShader(final String name, final ShaderType type, final String source) {
      int shaderId = GlStateManager.glCreateShader(GlConst.toGl(type));
      GlStateManager.glShaderSource(shaderId, source);
      GlStateManager.glCompileShader(shaderId);
      if (GlStateManager.glGetShaderi(shaderId, 35713) == 0) {
         String logInfo = StringUtils.trim(GlStateManager.glGetShaderInfoLog(shaderId, 32768));
         LOGGER.error("Couldn't compile {} shader for pipeline ({}): {}", new Object[]{type.getName(), name, logInfo});
         return GlShaderModule.INVALID_SHADER;
      } else {
         GlShaderModule module = new GlShaderModule(shaderId, name, type);
         this.debugLabels.applyLabel(module);
         return module;
      }
   }

   public @Nullable GlProgram compileProgram(final BackendRenderPipeline.CreateInfo createInfo) {
      List<GlShaderModule> compiledShaders = new ReferenceArrayList();

      try {
         for(BackendRenderPipeline.CreateInfo.Shader shader : createInfo.shaders()) {
            String decompiledSource = this.decompileShader(shader);
            GlShaderModule compiledShader = this.compileShader(shader.name(), shader.module().type(), decompiledSource);
            if (compiledShader == GlShaderModule.INVALID_SHADER) {
               Object var7 = null;
               return (GlProgram)var7;
            }

            compiledShaders.add(compiledShader);
         }

         GlProgram compiled = GlProgram.link(compiledShaders, createInfo.name());
         compiled.setupBindGroupLayouts(createInfo.uniforms());
         this.debugLabels.applyLabel(compiled);
         GlProgram var15 = compiled;
         return var15;
      } catch (ShaderCompileException | IllegalArgumentException e) {
         LOGGER.error("Couldn't compile program for pipeline {}", createInfo.name(), e);
         Object shader = null;
         return (GlProgram)shader;
      } finally {
         compiledShaders.forEach(UncheckedAutoCloseable::close);
      }
   }

   static {
      IS_MACOS = Util.getPlatform() == Util.OS.OSX;
      SHADER_DEBUG_MODE = SharedConstants.IS_RENDERDOC_ATTACHED;
   }
}
