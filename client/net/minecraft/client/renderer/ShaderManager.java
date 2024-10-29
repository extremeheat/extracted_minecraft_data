package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.CompiledShader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ShaderManager extends SimplePreparableReloadListener<Configs> implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final String SHADER_PATH = "shaders";
   public static final String SHADER_INCLUDE_PATH = "shaders/include/";
   private static final FileToIdConverter PROGRAM_ID_CONVERTER = FileToIdConverter.json("shaders");
   private static final FileToIdConverter POST_CHAIN_ID_CONVERTER = FileToIdConverter.json("post_effect");
   public static final int MAX_LOG_LENGTH = 32768;
   final TextureManager textureManager;
   private final Consumer<Exception> recoveryHandler;
   private CompilationCache compilationCache;

   public ShaderManager(TextureManager var1, Consumer<Exception> var2) {
      super();
      this.compilationCache = new CompilationCache(ShaderManager.Configs.EMPTY);
      this.textureManager = var1;
      this.recoveryHandler = var2;
   }

   protected Configs prepare(ResourceManager var1, ProfilerFiller var2) {
      ImmutableMap.Builder var3 = ImmutableMap.builder();
      ImmutableMap.Builder var4 = ImmutableMap.builder();
      Map var5 = var1.listResources("shaders", (var0) -> {
         return isProgram(var0) || isShader(var0);
      });
      Iterator var6 = var5.entrySet().iterator();

      while(var6.hasNext()) {
         Map.Entry var7 = (Map.Entry)var6.next();
         ResourceLocation var8 = (ResourceLocation)var7.getKey();
         CompiledShader.Type var9 = CompiledShader.Type.byLocation(var8);
         if (var9 != null) {
            loadShader(var8, (Resource)var7.getValue(), var9, var5, var4);
         } else if (isProgram(var8)) {
            loadProgram(var8, (Resource)var7.getValue(), var3);
         }
      }

      ImmutableMap.Builder var10 = ImmutableMap.builder();
      Iterator var11 = POST_CHAIN_ID_CONVERTER.listMatchingResources(var1).entrySet().iterator();

      while(var11.hasNext()) {
         Map.Entry var12 = (Map.Entry)var11.next();
         loadPostChain((ResourceLocation)var12.getKey(), (Resource)var12.getValue(), var10);
      }

      return new Configs(var3.build(), var4.build(), var10.build());
   }

   private static void loadShader(ResourceLocation var0, Resource var1, CompiledShader.Type var2, Map<ResourceLocation, Resource> var3, ImmutableMap.Builder<ShaderSourceKey, String> var4) {
      ResourceLocation var5 = var2.idConverter().fileToId(var0);
      GlslPreprocessor var6 = createPreprocessor(var3, var0);

      try {
         BufferedReader var7 = var1.openAsReader();

         try {
            String var8 = IOUtils.toString(var7);
            var4.put(new ShaderSourceKey(var5, var2), String.join("", var6.process(var8)));
         } catch (Throwable var11) {
            if (var7 != null) {
               try {
                  ((Reader)var7).close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (var7 != null) {
            ((Reader)var7).close();
         }
      } catch (IOException var12) {
         LOGGER.error("Failed to load shader source at {}", var0, var12);
      }

   }

   private static GlslPreprocessor createPreprocessor(final Map<ResourceLocation, Resource> var0, ResourceLocation var1) {
      final ResourceLocation var2 = var1.withPath(FileUtil::getFullResourcePath);
      return new GlslPreprocessor() {
         private final Set<ResourceLocation> importedLocations = new ObjectArraySet();

         public String applyImport(boolean var1, String var2x) {
            ResourceLocation var3;
            try {
               if (var1) {
                  var3 = var2.withPath((var1x) -> {
                     return FileUtil.normalizeResourcePath(var1x + var2x);
                  });
               } else {
                  var3 = ResourceLocation.parse(var2x).withPrefix("shaders/include/");
               }
            } catch (ResourceLocationException var8) {
               ShaderManager.LOGGER.error("Malformed GLSL import {}: {}", var2x, var8.getMessage());
               return "#error " + var8.getMessage();
            }

            if (!this.importedLocations.add(var3)) {
               return null;
            } else {
               try {
                  BufferedReader var4 = ((Resource)var0.get(var3)).openAsReader();

                  String var5;
                  try {
                     var5 = IOUtils.toString(var4);
                  } catch (Throwable var9) {
                     if (var4 != null) {
                        try {
                           ((Reader)var4).close();
                        } catch (Throwable var7) {
                           var9.addSuppressed(var7);
                        }
                     }

                     throw var9;
                  }

                  if (var4 != null) {
                     ((Reader)var4).close();
                  }

                  return var5;
               } catch (IOException var10) {
                  ShaderManager.LOGGER.error("Could not open GLSL import {}: {}", var3, var10.getMessage());
                  return "#error " + var10.getMessage();
               }
            }
         }
      };
   }

   private static void loadProgram(ResourceLocation var0, Resource var1, ImmutableMap.Builder<ResourceLocation, ShaderProgramConfig> var2) {
      ResourceLocation var3 = PROGRAM_ID_CONVERTER.fileToId(var0);

      try {
         BufferedReader var4 = var1.openAsReader();

         try {
            JsonElement var5 = JsonParser.parseReader(var4);
            ShaderProgramConfig var6 = (ShaderProgramConfig)ShaderProgramConfig.CODEC.parse(JsonOps.INSTANCE, var5).getOrThrow(JsonSyntaxException::new);
            var2.put(var3, var6);
         } catch (Throwable var8) {
            if (var4 != null) {
               try {
                  ((Reader)var4).close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var4 != null) {
            ((Reader)var4).close();
         }
      } catch (JsonParseException | IOException var9) {
         LOGGER.error("Failed to parse shader config at {}", var0, var9);
      }

   }

   private static void loadPostChain(ResourceLocation var0, Resource var1, ImmutableMap.Builder<ResourceLocation, PostChainConfig> var2) {
      ResourceLocation var3 = POST_CHAIN_ID_CONVERTER.fileToId(var0);

      try {
         BufferedReader var4 = var1.openAsReader();

         try {
            JsonElement var5 = JsonParser.parseReader(var4);
            var2.put(var3, (PostChainConfig)PostChainConfig.CODEC.parse(JsonOps.INSTANCE, var5).getOrThrow(JsonSyntaxException::new));
         } catch (Throwable var8) {
            if (var4 != null) {
               try {
                  ((Reader)var4).close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var4 != null) {
            ((Reader)var4).close();
         }
      } catch (JsonParseException | IOException var9) {
         LOGGER.error("Failed to parse post chain at {}", var0, var9);
      }

   }

   private static boolean isProgram(ResourceLocation var0) {
      return var0.getPath().endsWith(".json");
   }

   private static boolean isShader(ResourceLocation var0) {
      return CompiledShader.Type.byLocation(var0) != null || var0.getPath().endsWith(".glsl");
   }

   protected void apply(Configs var1, ResourceManager var2, ProfilerFiller var3) {
      CompilationCache var4 = new CompilationCache(var1);
      HashMap var5 = new HashMap();
      Iterator var6 = CoreShaders.getProgramsToPreload().iterator();

      while(var6.hasNext()) {
         ShaderProgram var7 = (ShaderProgram)var6.next();

         try {
            var4.programs.put(var7, Optional.of(var4.compileProgram(var7)));
         } catch (CompilationException var9) {
            var5.put(var7, var9);
         }
      }

      if (!var5.isEmpty()) {
         var4.close();
         Stream var10002 = var5.entrySet().stream().map((var0) -> {
            String var10000 = String.valueOf(var0.getKey());
            return " - " + var10000 + ": " + ((CompilationException)var0.getValue()).getMessage();
         });
         throw new RuntimeException("Failed to load required shader programs:\n" + (String)var10002.collect(Collectors.joining("\n")));
      } else {
         this.compilationCache.close();
         this.compilationCache = var4;
      }
   }

   public String getName() {
      return "Shader Loader";
   }

   public void preloadForStartup(ResourceProvider var1, ShaderProgram... var2) throws IOException, CompilationException {
      ShaderProgram[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ShaderProgram var6 = var3[var5];
         Resource var7 = var1.getResourceOrThrow(PROGRAM_ID_CONVERTER.idToFile(var6.configId()));
         BufferedReader var8 = var7.openAsReader();

         try {
            JsonElement var9 = JsonParser.parseReader(var8);
            ShaderProgramConfig var10 = (ShaderProgramConfig)ShaderProgramConfig.CODEC.parse(JsonOps.INSTANCE, var9).getOrThrow(JsonSyntaxException::new);
            ShaderDefines var11 = var10.defines().withOverrides(var6.defines());
            CompiledShader var12 = this.preloadShader(var1, var10.vertex(), CompiledShader.Type.VERTEX, var11);
            CompiledShader var13 = this.preloadShader(var1, var10.fragment(), CompiledShader.Type.FRAGMENT, var11);
            CompiledShaderProgram var14 = linkProgram(var6, var10, var12, var13);
            this.compilationCache.programs.put(var6, Optional.of(var14));
         } catch (Throwable var16) {
            if (var8 != null) {
               try {
                  ((Reader)var8).close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }
            }

            throw var16;
         }

         if (var8 != null) {
            ((Reader)var8).close();
         }
      }

   }

   private CompiledShader preloadShader(ResourceProvider var1, ResourceLocation var2, CompiledShader.Type var3, ShaderDefines var4) throws IOException, CompilationException {
      ResourceLocation var5 = var3.idConverter().idToFile(var2);
      BufferedReader var6 = var1.getResourceOrThrow(var5).openAsReader();

      CompiledShader var10;
      try {
         String var7 = IOUtils.toString(var6);
         String var8 = GlslPreprocessor.injectDefines(var7, var4);
         CompiledShader var9 = CompiledShader.compile(var2, var3, var8);
         this.compilationCache.shaders.put(new ShaderCompilationKey(var2, var3, var4), var9);
         var10 = var9;
      } catch (Throwable var12) {
         if (var6 != null) {
            try {
               ((Reader)var6).close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (var6 != null) {
         ((Reader)var6).close();
      }

      return var10;
   }

   @Nullable
   public CompiledShaderProgram getProgram(ShaderProgram var1) {
      try {
         return this.compilationCache.getOrCompileProgram(var1);
      } catch (CompilationException var3) {
         LOGGER.error("Failed to load shader program: {}", var1, var3);
         this.compilationCache.programs.put(var1, Optional.empty());
         this.recoveryHandler.accept(var3);
         return null;
      }
   }

   public CompiledShaderProgram getProgramForLoading(ShaderProgram var1) throws CompilationException {
      CompiledShaderProgram var2 = this.compilationCache.getOrCompileProgram(var1);
      if (var2 == null) {
         throw new CompilationException("Shader '" + String.valueOf(var1) + "' could not be found");
      } else {
         return var2;
      }
   }

   static CompiledShaderProgram linkProgram(ShaderProgram var0, ShaderProgramConfig var1, CompiledShader var2, CompiledShader var3) throws CompilationException {
      CompiledShaderProgram var4 = CompiledShaderProgram.link(var2, var3, var0.vertexFormat());
      var4.setupUniforms(var1.uniforms(), var1.samplers());
      return var4;
   }

   @Nullable
   public PostChain getPostChain(ResourceLocation var1, Set<ResourceLocation> var2) {
      try {
         return this.compilationCache.getOrLoadPostChain(var1, var2);
      } catch (CompilationException var4) {
         LOGGER.error("Failed to load post chain: {}", var1, var4);
         this.compilationCache.postChains.put(var1, Optional.empty());
         this.recoveryHandler.accept(var4);
         return null;
      }
   }

   public void close() {
      this.compilationCache.close();
   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }

   class CompilationCache implements AutoCloseable {
      private final Configs configs;
      final Map<ShaderProgram, Optional<CompiledShaderProgram>> programs = new HashMap();
      final Map<ShaderCompilationKey, CompiledShader> shaders = new HashMap();
      final Map<ResourceLocation, Optional<PostChain>> postChains = new HashMap();

      CompilationCache(final Configs var2) {
         super();
         this.configs = var2;
      }

      @Nullable
      public CompiledShaderProgram getOrCompileProgram(ShaderProgram var1) throws CompilationException {
         Optional var2 = (Optional)this.programs.get(var1);
         if (var2 != null) {
            return (CompiledShaderProgram)var2.orElse((Object)null);
         } else {
            CompiledShaderProgram var3 = this.compileProgram(var1);
            this.programs.put(var1, Optional.of(var3));
            return var3;
         }
      }

      CompiledShaderProgram compileProgram(ShaderProgram var1) throws CompilationException {
         ShaderProgramConfig var2 = (ShaderProgramConfig)this.configs.programs.get(var1.configId());
         if (var2 == null) {
            throw new CompilationException("Could not find program with id: " + String.valueOf(var1.configId()));
         } else {
            ShaderDefines var3 = var2.defines().withOverrides(var1.defines());
            CompiledShader var4 = this.getOrCompileShader(var2.vertex(), CompiledShader.Type.VERTEX, var3);
            CompiledShader var5 = this.getOrCompileShader(var2.fragment(), CompiledShader.Type.FRAGMENT, var3);
            return ShaderManager.linkProgram(var1, var2, var4, var5);
         }
      }

      private CompiledShader getOrCompileShader(ResourceLocation var1, CompiledShader.Type var2, ShaderDefines var3) throws CompilationException {
         ShaderCompilationKey var4 = new ShaderCompilationKey(var1, var2, var3);
         CompiledShader var5 = (CompiledShader)this.shaders.get(var4);
         if (var5 == null) {
            var5 = this.compileShader(var4);
            this.shaders.put(var4, var5);
         }

         return var5;
      }

      private CompiledShader compileShader(ShaderCompilationKey var1) throws CompilationException {
         String var2 = (String)this.configs.shaderSources.get(new ShaderSourceKey(var1.id, var1.type));
         if (var2 == null) {
            throw new CompilationException("Could not find shader: " + String.valueOf(var1));
         } else {
            String var3 = GlslPreprocessor.injectDefines(var2, var1.defines);
            return CompiledShader.compile(var1.id, var1.type, var3);
         }
      }

      @Nullable
      public PostChain getOrLoadPostChain(ResourceLocation var1, Set<ResourceLocation> var2) throws CompilationException {
         Optional var3 = (Optional)this.postChains.get(var1);
         if (var3 != null) {
            return (PostChain)var3.orElse((Object)null);
         } else {
            PostChain var4 = this.loadPostChain(var1, var2);
            this.postChains.put(var1, Optional.of(var4));
            return var4;
         }
      }

      private PostChain loadPostChain(ResourceLocation var1, Set<ResourceLocation> var2) throws CompilationException {
         PostChainConfig var3 = (PostChainConfig)this.configs.postChains.get(var1);
         if (var3 == null) {
            throw new CompilationException("Could not find post chain with id: " + String.valueOf(var1));
         } else {
            return PostChain.load(var3, ShaderManager.this.textureManager, ShaderManager.this, var2);
         }
      }

      public void close() {
         RenderSystem.assertOnRenderThread();
         this.programs.values().forEach((var0) -> {
            var0.ifPresent(CompiledShaderProgram::close);
         });
         this.shaders.values().forEach(CompiledShader::close);
         this.programs.clear();
         this.shaders.clear();
         this.postChains.clear();
      }
   }

   public static record Configs(Map<ResourceLocation, ShaderProgramConfig> programs, Map<ShaderSourceKey, String> shaderSources, Map<ResourceLocation, PostChainConfig> postChains) {
      final Map<ResourceLocation, ShaderProgramConfig> programs;
      final Map<ShaderSourceKey, String> shaderSources;
      final Map<ResourceLocation, PostChainConfig> postChains;
      public static final Configs EMPTY = new Configs(Map.of(), Map.of(), Map.of());

      public Configs(Map<ResourceLocation, ShaderProgramConfig> var1, Map<ShaderSourceKey, String> var2, Map<ResourceLocation, PostChainConfig> var3) {
         super();
         this.programs = var1;
         this.shaderSources = var2;
         this.postChains = var3;
      }

      public Map<ResourceLocation, ShaderProgramConfig> programs() {
         return this.programs;
      }

      public Map<ShaderSourceKey, String> shaderSources() {
         return this.shaderSources;
      }

      public Map<ResourceLocation, PostChainConfig> postChains() {
         return this.postChains;
      }
   }

   static record ShaderSourceKey(ResourceLocation id, CompiledShader.Type type) {
      ShaderSourceKey(ResourceLocation var1, CompiledShader.Type var2) {
         super();
         this.id = var1;
         this.type = var2;
      }

      public String toString() {
         String var10000 = String.valueOf(this.id);
         return var10000 + " (" + String.valueOf(this.type) + ")";
      }

      public ResourceLocation id() {
         return this.id;
      }

      public CompiledShader.Type type() {
         return this.type;
      }
   }

   public static class CompilationException extends Exception {
      public CompilationException(String var1) {
         super(var1);
      }
   }

   static record ShaderCompilationKey(ResourceLocation id, CompiledShader.Type type, ShaderDefines defines) {
      final ResourceLocation id;
      final CompiledShader.Type type;
      final ShaderDefines defines;

      ShaderCompilationKey(ResourceLocation var1, CompiledShader.Type var2, ShaderDefines var3) {
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

      public ResourceLocation id() {
         return this.id;
      }

      public CompiledShader.Type type() {
         return this.type;
      }

      public ShaderDefines defines() {
         return this.defines;
      }
   }
}
