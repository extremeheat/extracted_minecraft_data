package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;
import oshi.SystemInfo;
import oshi.hardware.Processor;

public class GLX {
   private static final Logger LOGGER = LogManager.getLogger();
   public static boolean isNvidia;
   public static boolean isAmd;
   public static int GL_FRAMEBUFFER;
   public static int GL_RENDERBUFFER;
   public static int GL_COLOR_ATTACHMENT0;
   public static int GL_DEPTH_ATTACHMENT;
   public static int GL_FRAMEBUFFER_COMPLETE;
   public static int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
   public static int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
   public static int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
   public static int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
   private static GLX.FboMode fboMode;
   public static final boolean useFbo = true;
   private static boolean hasShaders;
   private static boolean useShaderArb;
   public static int GL_LINK_STATUS;
   public static int GL_COMPILE_STATUS;
   public static int GL_VERTEX_SHADER;
   public static int GL_FRAGMENT_SHADER;
   private static boolean useMultitextureArb;
   public static int GL_TEXTURE0;
   public static int GL_TEXTURE1;
   public static int GL_TEXTURE2;
   private static boolean useTexEnvCombineArb;
   public static int GL_COMBINE;
   public static int GL_INTERPOLATE;
   public static int GL_PRIMARY_COLOR;
   public static int GL_CONSTANT;
   public static int GL_PREVIOUS;
   public static int GL_COMBINE_RGB;
   public static int GL_SOURCE0_RGB;
   public static int GL_SOURCE1_RGB;
   public static int GL_SOURCE2_RGB;
   public static int GL_OPERAND0_RGB;
   public static int GL_OPERAND1_RGB;
   public static int GL_OPERAND2_RGB;
   public static int GL_COMBINE_ALPHA;
   public static int GL_SOURCE0_ALPHA;
   public static int GL_SOURCE1_ALPHA;
   public static int GL_SOURCE2_ALPHA;
   public static int GL_OPERAND0_ALPHA;
   public static int GL_OPERAND1_ALPHA;
   public static int GL_OPERAND2_ALPHA;
   private static boolean separateBlend;
   public static boolean useSeparateBlendExt;
   public static boolean isOpenGl21;
   public static boolean usePostProcess;
   private static String capsString = "";
   private static String cpuInfo;
   public static final boolean useVbo = true;
   public static boolean needVbo;
   private static boolean useVboArb;
   public static int GL_ARRAY_BUFFER;
   public static int GL_STATIC_DRAW;
   private static final Map<Integer, String> LOOKUP_MAP = (Map)make(Maps.newHashMap(), (var0) -> {
      var0.put(0, "No error");
      var0.put(1280, "Enum parameter is invalid for this function");
      var0.put(1281, "Parameter is invalid for this function");
      var0.put(1282, "Current state is invalid for this function");
      var0.put(1283, "Stack overflow");
      var0.put(1284, "Stack underflow");
      var0.put(1285, "Out of memory");
      var0.put(1286, "Operation on incomplete framebuffer");
      var0.put(1286, "Operation on incomplete framebuffer");
   });

   public GLX() {
      super();
   }

   public static void populateSnooperWithOpenGL(SnooperAccess var0) {
      var0.setFixedData("opengl_version", GlStateManager.getString(7938));
      var0.setFixedData("opengl_vendor", GlStateManager.getString(7936));
      GLCapabilities var1 = GL.getCapabilities();
      var0.setFixedData("gl_caps[ARB_arrays_of_arrays]", var1.GL_ARB_arrays_of_arrays);
      var0.setFixedData("gl_caps[ARB_base_instance]", var1.GL_ARB_base_instance);
      var0.setFixedData("gl_caps[ARB_blend_func_extended]", var1.GL_ARB_blend_func_extended);
      var0.setFixedData("gl_caps[ARB_clear_buffer_object]", var1.GL_ARB_clear_buffer_object);
      var0.setFixedData("gl_caps[ARB_color_buffer_float]", var1.GL_ARB_color_buffer_float);
      var0.setFixedData("gl_caps[ARB_compatibility]", var1.GL_ARB_compatibility);
      var0.setFixedData("gl_caps[ARB_compressed_texture_pixel_storage]", var1.GL_ARB_compressed_texture_pixel_storage);
      var0.setFixedData("gl_caps[ARB_compute_shader]", var1.GL_ARB_compute_shader);
      var0.setFixedData("gl_caps[ARB_copy_buffer]", var1.GL_ARB_copy_buffer);
      var0.setFixedData("gl_caps[ARB_copy_image]", var1.GL_ARB_copy_image);
      var0.setFixedData("gl_caps[ARB_depth_buffer_float]", var1.GL_ARB_depth_buffer_float);
      var0.setFixedData("gl_caps[ARB_compute_shader]", var1.GL_ARB_compute_shader);
      var0.setFixedData("gl_caps[ARB_copy_buffer]", var1.GL_ARB_copy_buffer);
      var0.setFixedData("gl_caps[ARB_copy_image]", var1.GL_ARB_copy_image);
      var0.setFixedData("gl_caps[ARB_depth_buffer_float]", var1.GL_ARB_depth_buffer_float);
      var0.setFixedData("gl_caps[ARB_depth_clamp]", var1.GL_ARB_depth_clamp);
      var0.setFixedData("gl_caps[ARB_depth_texture]", var1.GL_ARB_depth_texture);
      var0.setFixedData("gl_caps[ARB_draw_buffers]", var1.GL_ARB_draw_buffers);
      var0.setFixedData("gl_caps[ARB_draw_buffers_blend]", var1.GL_ARB_draw_buffers_blend);
      var0.setFixedData("gl_caps[ARB_draw_elements_base_vertex]", var1.GL_ARB_draw_elements_base_vertex);
      var0.setFixedData("gl_caps[ARB_draw_indirect]", var1.GL_ARB_draw_indirect);
      var0.setFixedData("gl_caps[ARB_draw_instanced]", var1.GL_ARB_draw_instanced);
      var0.setFixedData("gl_caps[ARB_explicit_attrib_location]", var1.GL_ARB_explicit_attrib_location);
      var0.setFixedData("gl_caps[ARB_explicit_uniform_location]", var1.GL_ARB_explicit_uniform_location);
      var0.setFixedData("gl_caps[ARB_fragment_layer_viewport]", var1.GL_ARB_fragment_layer_viewport);
      var0.setFixedData("gl_caps[ARB_fragment_program]", var1.GL_ARB_fragment_program);
      var0.setFixedData("gl_caps[ARB_fragment_shader]", var1.GL_ARB_fragment_shader);
      var0.setFixedData("gl_caps[ARB_fragment_program_shadow]", var1.GL_ARB_fragment_program_shadow);
      var0.setFixedData("gl_caps[ARB_framebuffer_object]", var1.GL_ARB_framebuffer_object);
      var0.setFixedData("gl_caps[ARB_framebuffer_sRGB]", var1.GL_ARB_framebuffer_sRGB);
      var0.setFixedData("gl_caps[ARB_geometry_shader4]", var1.GL_ARB_geometry_shader4);
      var0.setFixedData("gl_caps[ARB_gpu_shader5]", var1.GL_ARB_gpu_shader5);
      var0.setFixedData("gl_caps[ARB_half_float_pixel]", var1.GL_ARB_half_float_pixel);
      var0.setFixedData("gl_caps[ARB_half_float_vertex]", var1.GL_ARB_half_float_vertex);
      var0.setFixedData("gl_caps[ARB_instanced_arrays]", var1.GL_ARB_instanced_arrays);
      var0.setFixedData("gl_caps[ARB_map_buffer_alignment]", var1.GL_ARB_map_buffer_alignment);
      var0.setFixedData("gl_caps[ARB_map_buffer_range]", var1.GL_ARB_map_buffer_range);
      var0.setFixedData("gl_caps[ARB_multisample]", var1.GL_ARB_multisample);
      var0.setFixedData("gl_caps[ARB_multitexture]", var1.GL_ARB_multitexture);
      var0.setFixedData("gl_caps[ARB_occlusion_query2]", var1.GL_ARB_occlusion_query2);
      var0.setFixedData("gl_caps[ARB_pixel_buffer_object]", var1.GL_ARB_pixel_buffer_object);
      var0.setFixedData("gl_caps[ARB_seamless_cube_map]", var1.GL_ARB_seamless_cube_map);
      var0.setFixedData("gl_caps[ARB_shader_objects]", var1.GL_ARB_shader_objects);
      var0.setFixedData("gl_caps[ARB_shader_stencil_export]", var1.GL_ARB_shader_stencil_export);
      var0.setFixedData("gl_caps[ARB_shader_texture_lod]", var1.GL_ARB_shader_texture_lod);
      var0.setFixedData("gl_caps[ARB_shadow]", var1.GL_ARB_shadow);
      var0.setFixedData("gl_caps[ARB_shadow_ambient]", var1.GL_ARB_shadow_ambient);
      var0.setFixedData("gl_caps[ARB_stencil_texturing]", var1.GL_ARB_stencil_texturing);
      var0.setFixedData("gl_caps[ARB_sync]", var1.GL_ARB_sync);
      var0.setFixedData("gl_caps[ARB_tessellation_shader]", var1.GL_ARB_tessellation_shader);
      var0.setFixedData("gl_caps[ARB_texture_border_clamp]", var1.GL_ARB_texture_border_clamp);
      var0.setFixedData("gl_caps[ARB_texture_buffer_object]", var1.GL_ARB_texture_buffer_object);
      var0.setFixedData("gl_caps[ARB_texture_cube_map]", var1.GL_ARB_texture_cube_map);
      var0.setFixedData("gl_caps[ARB_texture_cube_map_array]", var1.GL_ARB_texture_cube_map_array);
      var0.setFixedData("gl_caps[ARB_texture_non_power_of_two]", var1.GL_ARB_texture_non_power_of_two);
      var0.setFixedData("gl_caps[ARB_uniform_buffer_object]", var1.GL_ARB_uniform_buffer_object);
      var0.setFixedData("gl_caps[ARB_vertex_blend]", var1.GL_ARB_vertex_blend);
      var0.setFixedData("gl_caps[ARB_vertex_buffer_object]", var1.GL_ARB_vertex_buffer_object);
      var0.setFixedData("gl_caps[ARB_vertex_program]", var1.GL_ARB_vertex_program);
      var0.setFixedData("gl_caps[ARB_vertex_shader]", var1.GL_ARB_vertex_shader);
      var0.setFixedData("gl_caps[EXT_bindable_uniform]", var1.GL_EXT_bindable_uniform);
      var0.setFixedData("gl_caps[EXT_blend_equation_separate]", var1.GL_EXT_blend_equation_separate);
      var0.setFixedData("gl_caps[EXT_blend_func_separate]", var1.GL_EXT_blend_func_separate);
      var0.setFixedData("gl_caps[EXT_blend_minmax]", var1.GL_EXT_blend_minmax);
      var0.setFixedData("gl_caps[EXT_blend_subtract]", var1.GL_EXT_blend_subtract);
      var0.setFixedData("gl_caps[EXT_draw_instanced]", var1.GL_EXT_draw_instanced);
      var0.setFixedData("gl_caps[EXT_framebuffer_multisample]", var1.GL_EXT_framebuffer_multisample);
      var0.setFixedData("gl_caps[EXT_framebuffer_object]", var1.GL_EXT_framebuffer_object);
      var0.setFixedData("gl_caps[EXT_framebuffer_sRGB]", var1.GL_EXT_framebuffer_sRGB);
      var0.setFixedData("gl_caps[EXT_geometry_shader4]", var1.GL_EXT_geometry_shader4);
      var0.setFixedData("gl_caps[EXT_gpu_program_parameters]", var1.GL_EXT_gpu_program_parameters);
      var0.setFixedData("gl_caps[EXT_gpu_shader4]", var1.GL_EXT_gpu_shader4);
      var0.setFixedData("gl_caps[EXT_packed_depth_stencil]", var1.GL_EXT_packed_depth_stencil);
      var0.setFixedData("gl_caps[EXT_separate_shader_objects]", var1.GL_EXT_separate_shader_objects);
      var0.setFixedData("gl_caps[EXT_shader_image_load_store]", var1.GL_EXT_shader_image_load_store);
      var0.setFixedData("gl_caps[EXT_shadow_funcs]", var1.GL_EXT_shadow_funcs);
      var0.setFixedData("gl_caps[EXT_shared_texture_palette]", var1.GL_EXT_shared_texture_palette);
      var0.setFixedData("gl_caps[EXT_stencil_clear_tag]", var1.GL_EXT_stencil_clear_tag);
      var0.setFixedData("gl_caps[EXT_stencil_two_side]", var1.GL_EXT_stencil_two_side);
      var0.setFixedData("gl_caps[EXT_stencil_wrap]", var1.GL_EXT_stencil_wrap);
      var0.setFixedData("gl_caps[EXT_texture_array]", var1.GL_EXT_texture_array);
      var0.setFixedData("gl_caps[EXT_texture_buffer_object]", var1.GL_EXT_texture_buffer_object);
      var0.setFixedData("gl_caps[EXT_texture_integer]", var1.GL_EXT_texture_integer);
      var0.setFixedData("gl_caps[EXT_texture_sRGB]", var1.GL_EXT_texture_sRGB);
      var0.setFixedData("gl_caps[ARB_vertex_shader]", var1.GL_ARB_vertex_shader);
      var0.setFixedData("gl_caps[gl_max_vertex_uniforms]", GlStateManager.getInteger(35658));
      GlStateManager.getError();
      var0.setFixedData("gl_caps[gl_max_fragment_uniforms]", GlStateManager.getInteger(35657));
      GlStateManager.getError();
      var0.setFixedData("gl_caps[gl_max_vertex_attribs]", GlStateManager.getInteger(34921));
      GlStateManager.getError();
      var0.setFixedData("gl_caps[gl_max_vertex_texture_image_units]", GlStateManager.getInteger(35660));
      GlStateManager.getError();
      var0.setFixedData("gl_caps[gl_max_texture_image_units]", GlStateManager.getInteger(34930));
      GlStateManager.getError();
      var0.setFixedData("gl_caps[gl_max_array_texture_layers]", GlStateManager.getInteger(35071));
      GlStateManager.getError();
   }

   public static String getOpenGLVersionString() {
      return GLFW.glfwGetCurrentContext() == 0L ? "NO CONTEXT" : GlStateManager.getString(7937) + " GL version " + GlStateManager.getString(7938) + ", " + GlStateManager.getString(7936);
   }

   public static int getRefreshRate(Window var0) {
      long var1 = GLFW.glfwGetWindowMonitor(var0.getWindow());
      if (var1 == 0L) {
         var1 = GLFW.glfwGetPrimaryMonitor();
      }

      GLFWVidMode var3 = var1 == 0L ? null : GLFW.glfwGetVideoMode(var1);
      return var3 == null ? 0 : var3.refreshRate();
   }

   public static String getLWJGLVersion() {
      return Version.getVersion();
   }

   public static LongSupplier initGlfw() {
      Window.checkGlfwError((var0x, var1x) -> {
         throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", var0x, var1x));
      });
      ArrayList var0 = Lists.newArrayList();
      GLFWErrorCallback var1 = GLFW.glfwSetErrorCallback((var1x, var2x) -> {
         var0.add(String.format("GLFW error during init: [0x%X]%s", var1x, var2x));
      });
      if (!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(var0));
      } else {
         LongSupplier var2 = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9D);
         };
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            LOGGER.error("GLFW error collected during initialization: {}", var4);
         }

         setGlfwErrorCallback(var1);
         return var2;
      }
   }

   public static void setGlfwErrorCallback(GLFWErrorCallbackI var0) {
      GLFW.glfwSetErrorCallback(var0).free();
   }

   public static boolean shouldClose(Window var0) {
      return GLFW.glfwWindowShouldClose(var0.getWindow());
   }

   public static void pollEvents() {
      GLFW.glfwPollEvents();
   }

   public static String getOpenGLVersion() {
      return GlStateManager.getString(7938);
   }

   public static String getRenderer() {
      return GlStateManager.getString(7937);
   }

   public static String getVendor() {
      return GlStateManager.getString(7936);
   }

   public static void setupNvFogDistance() {
      if (GL.getCapabilities().GL_NV_fog_distance) {
         GlStateManager.fogi(34138, 34139);
      }

   }

   public static boolean supportsOpenGL2() {
      return GL.getCapabilities().OpenGL20;
   }

   public static void withTextureRestore(Runnable var0) {
      GL11.glPushAttrib(270336);

      try {
         var0.run();
      } finally {
         GL11.glPopAttrib();
      }

   }

   public static ByteBuffer allocateMemory(int var0) {
      return MemoryUtil.memAlloc(var0);
   }

   public static void freeMemory(Buffer var0) {
      MemoryUtil.memFree(var0);
   }

   public static void init() {
      GLCapabilities var0 = GL.getCapabilities();
      useMultitextureArb = var0.GL_ARB_multitexture && !var0.OpenGL13;
      useTexEnvCombineArb = var0.GL_ARB_texture_env_combine && !var0.OpenGL13;
      if (useMultitextureArb) {
         capsString = capsString + "Using ARB_multitexture.\n";
         GL_TEXTURE0 = 33984;
         GL_TEXTURE1 = 33985;
         GL_TEXTURE2 = 33986;
      } else {
         capsString = capsString + "Using GL 1.3 multitexturing.\n";
         GL_TEXTURE0 = 33984;
         GL_TEXTURE1 = 33985;
         GL_TEXTURE2 = 33986;
      }

      if (useTexEnvCombineArb) {
         capsString = capsString + "Using ARB_texture_env_combine.\n";
         GL_COMBINE = 34160;
         GL_INTERPOLATE = 34165;
         GL_PRIMARY_COLOR = 34167;
         GL_CONSTANT = 34166;
         GL_PREVIOUS = 34168;
         GL_COMBINE_RGB = 34161;
         GL_SOURCE0_RGB = 34176;
         GL_SOURCE1_RGB = 34177;
         GL_SOURCE2_RGB = 34178;
         GL_OPERAND0_RGB = 34192;
         GL_OPERAND1_RGB = 34193;
         GL_OPERAND2_RGB = 34194;
         GL_COMBINE_ALPHA = 34162;
         GL_SOURCE0_ALPHA = 34184;
         GL_SOURCE1_ALPHA = 34185;
         GL_SOURCE2_ALPHA = 34186;
         GL_OPERAND0_ALPHA = 34200;
         GL_OPERAND1_ALPHA = 34201;
         GL_OPERAND2_ALPHA = 34202;
      } else {
         capsString = capsString + "Using GL 1.3 texture combiners.\n";
         GL_COMBINE = 34160;
         GL_INTERPOLATE = 34165;
         GL_PRIMARY_COLOR = 34167;
         GL_CONSTANT = 34166;
         GL_PREVIOUS = 34168;
         GL_COMBINE_RGB = 34161;
         GL_SOURCE0_RGB = 34176;
         GL_SOURCE1_RGB = 34177;
         GL_SOURCE2_RGB = 34178;
         GL_OPERAND0_RGB = 34192;
         GL_OPERAND1_RGB = 34193;
         GL_OPERAND2_RGB = 34194;
         GL_COMBINE_ALPHA = 34162;
         GL_SOURCE0_ALPHA = 34184;
         GL_SOURCE1_ALPHA = 34185;
         GL_SOURCE2_ALPHA = 34186;
         GL_OPERAND0_ALPHA = 34200;
         GL_OPERAND1_ALPHA = 34201;
         GL_OPERAND2_ALPHA = 34202;
      }

      useSeparateBlendExt = var0.GL_EXT_blend_func_separate && !var0.OpenGL14;
      separateBlend = var0.OpenGL14 || var0.GL_EXT_blend_func_separate;
      capsString = capsString + "Using framebuffer objects because ";
      if (var0.OpenGL30) {
         capsString = capsString + "OpenGL 3.0 is supported and separate blending is supported.\n";
         fboMode = GLX.FboMode.BASE;
         GL_FRAMEBUFFER = 36160;
         GL_RENDERBUFFER = 36161;
         GL_COLOR_ATTACHMENT0 = 36064;
         GL_DEPTH_ATTACHMENT = 36096;
         GL_FRAMEBUFFER_COMPLETE = 36053;
         GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
      } else if (var0.GL_ARB_framebuffer_object) {
         capsString = capsString + "ARB_framebuffer_object is supported and separate blending is supported.\n";
         fboMode = GLX.FboMode.ARB;
         GL_FRAMEBUFFER = 36160;
         GL_RENDERBUFFER = 36161;
         GL_COLOR_ATTACHMENT0 = 36064;
         GL_DEPTH_ATTACHMENT = 36096;
         GL_FRAMEBUFFER_COMPLETE = 36053;
         GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
      } else {
         if (!var0.GL_EXT_framebuffer_object) {
            throw new IllegalStateException("The driver does not appear to support framebuffer objects");
         }

         capsString = capsString + "EXT_framebuffer_object is supported.\n";
         fboMode = GLX.FboMode.EXT;
         GL_FRAMEBUFFER = 36160;
         GL_RENDERBUFFER = 36161;
         GL_COLOR_ATTACHMENT0 = 36064;
         GL_DEPTH_ATTACHMENT = 36096;
         GL_FRAMEBUFFER_COMPLETE = 36053;
         GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
      }

      isOpenGl21 = var0.OpenGL21;
      hasShaders = isOpenGl21 || var0.GL_ARB_vertex_shader && var0.GL_ARB_fragment_shader && var0.GL_ARB_shader_objects;
      capsString = capsString + "Shaders are " + (hasShaders ? "" : "not ") + "available because ";
      if (hasShaders) {
         if (var0.OpenGL21) {
            capsString = capsString + "OpenGL 2.1 is supported.\n";
            useShaderArb = false;
            GL_LINK_STATUS = 35714;
            GL_COMPILE_STATUS = 35713;
            GL_VERTEX_SHADER = 35633;
            GL_FRAGMENT_SHADER = 35632;
         } else {
            capsString = capsString + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
            useShaderArb = true;
            GL_LINK_STATUS = 35714;
            GL_COMPILE_STATUS = 35713;
            GL_VERTEX_SHADER = 35633;
            GL_FRAGMENT_SHADER = 35632;
         }
      } else {
         capsString = capsString + "OpenGL 2.1 is " + (var0.OpenGL21 ? "" : "not ") + "supported, ";
         capsString = capsString + "ARB_shader_objects is " + (var0.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
         capsString = capsString + "ARB_vertex_shader is " + (var0.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
         capsString = capsString + "ARB_fragment_shader is " + (var0.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
      }

      usePostProcess = hasShaders;
      String var1 = GL11.glGetString(7936).toLowerCase(Locale.ROOT);
      isNvidia = var1.contains("nvidia");
      useVboArb = !var0.OpenGL15 && var0.GL_ARB_vertex_buffer_object;
      capsString = capsString + "VBOs are available because ";
      if (useVboArb) {
         capsString = capsString + "ARB_vertex_buffer_object is supported.\n";
         GL_STATIC_DRAW = 35044;
         GL_ARRAY_BUFFER = 34962;
      } else {
         capsString = capsString + "OpenGL 1.5 is supported.\n";
         GL_STATIC_DRAW = 35044;
         GL_ARRAY_BUFFER = 34962;
      }

      isAmd = var1.contains("ati");
      if (isAmd) {
         needVbo = true;
      }

      try {
         Processor[] var2 = (new SystemInfo()).getHardware().getProcessors();
         cpuInfo = String.format("%dx %s", var2.length, var2[0]).replaceAll("\\s+", " ");
      } catch (Throwable var3) {
      }

   }

   public static boolean isNextGen() {
      return usePostProcess;
   }

   public static String getCapsString() {
      return capsString;
   }

   public static int glGetProgrami(int var0, int var1) {
      return useShaderArb ? ARBShaderObjects.glGetObjectParameteriARB(var0, var1) : GL20.glGetProgrami(var0, var1);
   }

   public static void glAttachShader(int var0, int var1) {
      if (useShaderArb) {
         ARBShaderObjects.glAttachObjectARB(var0, var1);
      } else {
         GL20.glAttachShader(var0, var1);
      }

   }

   public static void glDeleteShader(int var0) {
      if (useShaderArb) {
         ARBShaderObjects.glDeleteObjectARB(var0);
      } else {
         GL20.glDeleteShader(var0);
      }

   }

   public static int glCreateShader(int var0) {
      return useShaderArb ? ARBShaderObjects.glCreateShaderObjectARB(var0) : GL20.glCreateShader(var0);
   }

   public static void glShaderSource(int var0, CharSequence var1) {
      if (useShaderArb) {
         ARBShaderObjects.glShaderSourceARB(var0, var1);
      } else {
         GL20.glShaderSource(var0, var1);
      }

   }

   public static void glCompileShader(int var0) {
      if (useShaderArb) {
         ARBShaderObjects.glCompileShaderARB(var0);
      } else {
         GL20.glCompileShader(var0);
      }

   }

   public static int glGetShaderi(int var0, int var1) {
      return useShaderArb ? ARBShaderObjects.glGetObjectParameteriARB(var0, var1) : GL20.glGetShaderi(var0, var1);
   }

   public static String glGetShaderInfoLog(int var0, int var1) {
      return useShaderArb ? ARBShaderObjects.glGetInfoLogARB(var0, var1) : GL20.glGetShaderInfoLog(var0, var1);
   }

   public static String glGetProgramInfoLog(int var0, int var1) {
      return useShaderArb ? ARBShaderObjects.glGetInfoLogARB(var0, var1) : GL20.glGetProgramInfoLog(var0, var1);
   }

   public static void glUseProgram(int var0) {
      if (useShaderArb) {
         ARBShaderObjects.glUseProgramObjectARB(var0);
      } else {
         GL20.glUseProgram(var0);
      }

   }

   public static int glCreateProgram() {
      return useShaderArb ? ARBShaderObjects.glCreateProgramObjectARB() : GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int var0) {
      if (useShaderArb) {
         ARBShaderObjects.glDeleteObjectARB(var0);
      } else {
         GL20.glDeleteProgram(var0);
      }

   }

   public static void glLinkProgram(int var0) {
      if (useShaderArb) {
         ARBShaderObjects.glLinkProgramARB(var0);
      } else {
         GL20.glLinkProgram(var0);
      }

   }

   public static int glGetUniformLocation(int var0, CharSequence var1) {
      return useShaderArb ? ARBShaderObjects.glGetUniformLocationARB(var0, var1) : GL20.glGetUniformLocation(var0, var1);
   }

   public static void glUniform1(int var0, IntBuffer var1) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform1ivARB(var0, var1);
      } else {
         GL20.glUniform1iv(var0, var1);
      }

   }

   public static void glUniform1i(int var0, int var1) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform1iARB(var0, var1);
      } else {
         GL20.glUniform1i(var0, var1);
      }

   }

   public static void glUniform1(int var0, FloatBuffer var1) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform1fvARB(var0, var1);
      } else {
         GL20.glUniform1fv(var0, var1);
      }

   }

   public static void glUniform2(int var0, IntBuffer var1) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform2ivARB(var0, var1);
      } else {
         GL20.glUniform2iv(var0, var1);
      }

   }

   public static void glUniform2(int var0, FloatBuffer var1) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform2fvARB(var0, var1);
      } else {
         GL20.glUniform2fv(var0, var1);
      }

   }

   public static void glUniform3(int var0, IntBuffer var1) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform3ivARB(var0, var1);
      } else {
         GL20.glUniform3iv(var0, var1);
      }

   }

   public static void glUniform3(int var0, FloatBuffer var1) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform3fvARB(var0, var1);
      } else {
         GL20.glUniform3fv(var0, var1);
      }

   }

   public static void glUniform4(int var0, IntBuffer var1) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform4ivARB(var0, var1);
      } else {
         GL20.glUniform4iv(var0, var1);
      }

   }

   public static void glUniform4(int var0, FloatBuffer var1) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform4fvARB(var0, var1);
      } else {
         GL20.glUniform4fv(var0, var1);
      }

   }

   public static void glUniformMatrix2(int var0, boolean var1, FloatBuffer var2) {
      if (useShaderArb) {
         ARBShaderObjects.glUniformMatrix2fvARB(var0, var1, var2);
      } else {
         GL20.glUniformMatrix2fv(var0, var1, var2);
      }

   }

   public static void glUniformMatrix3(int var0, boolean var1, FloatBuffer var2) {
      if (useShaderArb) {
         ARBShaderObjects.glUniformMatrix3fvARB(var0, var1, var2);
      } else {
         GL20.glUniformMatrix3fv(var0, var1, var2);
      }

   }

   public static void glUniformMatrix4(int var0, boolean var1, FloatBuffer var2) {
      if (useShaderArb) {
         ARBShaderObjects.glUniformMatrix4fvARB(var0, var1, var2);
      } else {
         GL20.glUniformMatrix4fv(var0, var1, var2);
      }

   }

   public static int glGetAttribLocation(int var0, CharSequence var1) {
      return useShaderArb ? ARBVertexShader.glGetAttribLocationARB(var0, var1) : GL20.glGetAttribLocation(var0, var1);
   }

   public static int glGenBuffers() {
      return useVboArb ? ARBVertexBufferObject.glGenBuffersARB() : GL15.glGenBuffers();
   }

   public static void glGenBuffers(IntBuffer var0) {
      if (useVboArb) {
         ARBVertexBufferObject.glGenBuffersARB(var0);
      } else {
         GL15.glGenBuffers(var0);
      }

   }

   public static void glBindBuffer(int var0, int var1) {
      if (useVboArb) {
         ARBVertexBufferObject.glBindBufferARB(var0, var1);
      } else {
         GL15.glBindBuffer(var0, var1);
      }

   }

   public static void glBufferData(int var0, ByteBuffer var1, int var2) {
      if (useVboArb) {
         ARBVertexBufferObject.glBufferDataARB(var0, var1, var2);
      } else {
         GL15.glBufferData(var0, var1, var2);
      }

   }

   public static void glDeleteBuffers(int var0) {
      if (useVboArb) {
         ARBVertexBufferObject.glDeleteBuffersARB(var0);
      } else {
         GL15.glDeleteBuffers(var0);
      }

   }

   public static void glDeleteBuffers(IntBuffer var0) {
      if (useVboArb) {
         ARBVertexBufferObject.glDeleteBuffersARB(var0);
      } else {
         GL15.glDeleteBuffers(var0);
      }

   }

   public static boolean useVbo() {
      return true;
   }

   public static void glBindFramebuffer(int var0, int var1) {
      switch(fboMode) {
      case BASE:
         GL30.glBindFramebuffer(var0, var1);
         break;
      case ARB:
         ARBFramebufferObject.glBindFramebuffer(var0, var1);
         break;
      case EXT:
         EXTFramebufferObject.glBindFramebufferEXT(var0, var1);
      }

   }

   public static void glBindRenderbuffer(int var0, int var1) {
      switch(fboMode) {
      case BASE:
         GL30.glBindRenderbuffer(var0, var1);
         break;
      case ARB:
         ARBFramebufferObject.glBindRenderbuffer(var0, var1);
         break;
      case EXT:
         EXTFramebufferObject.glBindRenderbufferEXT(var0, var1);
      }

   }

   public static void glDeleteRenderbuffers(int var0) {
      switch(fboMode) {
      case BASE:
         GL30.glDeleteRenderbuffers(var0);
         break;
      case ARB:
         ARBFramebufferObject.glDeleteRenderbuffers(var0);
         break;
      case EXT:
         EXTFramebufferObject.glDeleteRenderbuffersEXT(var0);
      }

   }

   public static void glDeleteFramebuffers(int var0) {
      switch(fboMode) {
      case BASE:
         GL30.glDeleteFramebuffers(var0);
         break;
      case ARB:
         ARBFramebufferObject.glDeleteFramebuffers(var0);
         break;
      case EXT:
         EXTFramebufferObject.glDeleteFramebuffersEXT(var0);
      }

   }

   public static int glGenFramebuffers() {
      switch(fboMode) {
      case BASE:
         return GL30.glGenFramebuffers();
      case ARB:
         return ARBFramebufferObject.glGenFramebuffers();
      case EXT:
         return EXTFramebufferObject.glGenFramebuffersEXT();
      default:
         return -1;
      }
   }

   public static int glGenRenderbuffers() {
      switch(fboMode) {
      case BASE:
         return GL30.glGenRenderbuffers();
      case ARB:
         return ARBFramebufferObject.glGenRenderbuffers();
      case EXT:
         return EXTFramebufferObject.glGenRenderbuffersEXT();
      default:
         return -1;
      }
   }

   public static void glRenderbufferStorage(int var0, int var1, int var2, int var3) {
      switch(fboMode) {
      case BASE:
         GL30.glRenderbufferStorage(var0, var1, var2, var3);
         break;
      case ARB:
         ARBFramebufferObject.glRenderbufferStorage(var0, var1, var2, var3);
         break;
      case EXT:
         EXTFramebufferObject.glRenderbufferStorageEXT(var0, var1, var2, var3);
      }

   }

   public static void glFramebufferRenderbuffer(int var0, int var1, int var2, int var3) {
      switch(fboMode) {
      case BASE:
         GL30.glFramebufferRenderbuffer(var0, var1, var2, var3);
         break;
      case ARB:
         ARBFramebufferObject.glFramebufferRenderbuffer(var0, var1, var2, var3);
         break;
      case EXT:
         EXTFramebufferObject.glFramebufferRenderbufferEXT(var0, var1, var2, var3);
      }

   }

   public static int glCheckFramebufferStatus(int var0) {
      switch(fboMode) {
      case BASE:
         return GL30.glCheckFramebufferStatus(var0);
      case ARB:
         return ARBFramebufferObject.glCheckFramebufferStatus(var0);
      case EXT:
         return EXTFramebufferObject.glCheckFramebufferStatusEXT(var0);
      default:
         return -1;
      }
   }

   public static void glFramebufferTexture2D(int var0, int var1, int var2, int var3, int var4) {
      switch(fboMode) {
      case BASE:
         GL30.glFramebufferTexture2D(var0, var1, var2, var3, var4);
         break;
      case ARB:
         ARBFramebufferObject.glFramebufferTexture2D(var0, var1, var2, var3, var4);
         break;
      case EXT:
         EXTFramebufferObject.glFramebufferTexture2DEXT(var0, var1, var2, var3, var4);
      }

   }

   public static int getBoundFramebuffer() {
      switch(fboMode) {
      case BASE:
         return GlStateManager.getInteger(36006);
      case ARB:
         return GlStateManager.getInteger(36006);
      case EXT:
         return GlStateManager.getInteger(36006);
      default:
         return 0;
      }
   }

   public static void glActiveTexture(int var0) {
      if (useMultitextureArb) {
         ARBMultitexture.glActiveTextureARB(var0);
      } else {
         GL13.glActiveTexture(var0);
      }

   }

   public static void glClientActiveTexture(int var0) {
      if (useMultitextureArb) {
         ARBMultitexture.glClientActiveTextureARB(var0);
      } else {
         GL13.glClientActiveTexture(var0);
      }

   }

   public static void glMultiTexCoord2f(int var0, float var1, float var2) {
      if (useMultitextureArb) {
         ARBMultitexture.glMultiTexCoord2fARB(var0, var1, var2);
      } else {
         GL13.glMultiTexCoord2f(var0, var1, var2);
      }

   }

   public static void glBlendFuncSeparate(int var0, int var1, int var2, int var3) {
      if (separateBlend) {
         if (useSeparateBlendExt) {
            EXTBlendFuncSeparate.glBlendFuncSeparateEXT(var0, var1, var2, var3);
         } else {
            GL14.glBlendFuncSeparate(var0, var1, var2, var3);
         }
      } else {
         GL11.glBlendFunc(var0, var1);
      }

   }

   public static boolean isUsingFBOs() {
      return true;
   }

   public static String getCpuInfo() {
      return cpuInfo == null ? "<unknown>" : cpuInfo;
   }

   public static void renderCrosshair(int var0) {
      renderCrosshair(var0, true, true, true);
   }

   public static void renderCrosshair(int var0, boolean var1, boolean var2, boolean var3) {
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);
      Tesselator var4 = Tesselator.getInstance();
      BufferBuilder var5 = var4.getBuilder();
      GL11.glLineWidth(4.0F);
      var5.begin(1, DefaultVertexFormat.POSITION_COLOR);
      if (var1) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var5.vertex((double)var0, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (var2) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var5.vertex(0.0D, (double)var0, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (var3) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var5.vertex(0.0D, 0.0D, (double)var0).color(0, 0, 0, 255).endVertex();
      }

      var4.end();
      GL11.glLineWidth(2.0F);
      var5.begin(1, DefaultVertexFormat.POSITION_COLOR);
      if (var1) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
         var5.vertex((double)var0, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
      }

      if (var2) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
         var5.vertex(0.0D, (double)var0, 0.0D).color(0, 255, 0, 255).endVertex();
      }

      if (var3) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
         var5.vertex(0.0D, 0.0D, (double)var0).color(127, 127, 255, 255).endVertex();
      }

      var4.end();
      GL11.glLineWidth(1.0F);
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
   }

   public static String getErrorString(int var0) {
      return (String)LOOKUP_MAP.get(var0);
   }

   public static <T> T make(Supplier<T> var0) {
      return var0.get();
   }

   public static <T> T make(T var0, Consumer<T> var1) {
      var1.accept(var0);
      return var0;
   }

   static enum FboMode {
      BASE,
      ARB,
      EXT;

      private FboMode() {
      }
   }
}
