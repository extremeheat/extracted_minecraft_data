package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
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

public class ShaderManager extends SimplePreparableReloadListener<ShaderManager.Configs> implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final String SHADER_PATH = "shaders";
   public static final String SHADER_INCLUDE_PATH = "shaders/include/";
   private static final FileToIdConverter PROGRAM_ID_CONVERTER = FileToIdConverter.json("shaders");
   private static final FileToIdConverter POST_CHAIN_ID_CONVERTER = FileToIdConverter.json("post_effect");
   public static final int MAX_LOG_LENGTH = 32768;
   private final TextureManager textureManager;
   private ShaderManager.Configs configs = ShaderManager.Configs.EMPTY;
   private final Map<ShaderProgram, Optional<CompiledShaderProgram>> compiledPrograms = new HashMap<>();
   private final Map<ShaderManager.ShaderCompilationKey, CompiledShader> compiledShaders = new HashMap<>();
   private final Map<ResourceLocation, Optional<PostChain>> postChains = new HashMap<>();

   public ShaderManager(TextureManager var1) {
      super();
      this.textureManager = var1;
   }

   protected ShaderManager.Configs prepare(ResourceManager var1, ProfilerFiller var2) {
      Builder var3 = ImmutableMap.builder();
      Builder var4 = ImmutableMap.builder();
      Map var5 = var1.listResources("shaders", var0 -> isProgram(var0) || isShader(var0));

      for (Entry var7 : var5.entrySet()) {
         ResourceLocation var8 = (ResourceLocation)var7.getKey();
         CompiledShader.Type var9 = CompiledShader.Type.byLocation(var8);
         if (var9 != null) {
            loadShader(var8, (Resource)var7.getValue(), var9, var5, var4);
         } else if (isProgram(var8)) {
            loadProgram(var8, (Resource)var7.getValue(), var3);
         }
      }

      Builder var10 = ImmutableMap.builder();

      for (Entry var12 : POST_CHAIN_ID_CONVERTER.listMatchingResources(var1).entrySet()) {
         loadPostChain((ResourceLocation)var12.getKey(), (Resource)var12.getValue(), var10);
      }

      return new ShaderManager.Configs(var3.build(), var4.build(), var10.build());
   }

   private static void loadShader(
      ResourceLocation var0, Resource var1, CompiledShader.Type var2, Map<ResourceLocation, Resource> var3, Builder<ShaderManager.ShaderSourceKey, String> var4
   ) {
      ResourceLocation var5 = var2.idConverter().fileToId(var0);
      GlslPreprocessor var6 = createPreprocessor(var3, var0);

      try (BufferedReader var7 = var1.openAsReader()) {
         String var8 = IOUtils.toString(var7);
         var4.put(new ShaderManager.ShaderSourceKey(var5, var2), String.join("", var6.process(var8)));
      } catch (IOException var12) {
         LOGGER.error("Failed to load shader source at {}", var0, var12);
      }
   }

   private static GlslPreprocessor createPreprocessor(final Map<ResourceLocation, Resource> var0, ResourceLocation var1) {
      final ResourceLocation var2 = var1.withPath(FileUtil::getFullResourcePath);
      return new GlslPreprocessor() {
         private final Set<ResourceLocation> importedLocations = new ObjectArraySet();

         @Override
         public String applyImport(boolean var1, String var2x) {
            ResourceLocation var3;
            try {
               if (var1) {
                  var3 = var2.withPath(var1x -> FileUtil.normalizeResourcePath(var1x + var2x));
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
                  String var5;
                  try (BufferedReader var4 = ((Resource)var0.get(var3)).openAsReader()) {
                     var5 = IOUtils.toString(var4);
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

   private static void loadProgram(ResourceLocation var0, Resource var1, Builder<ResourceLocation, ShaderProgramConfig> var2) {
      ResourceLocation var3 = PROGRAM_ID_CONVERTER.fileToId(var0);

      try (BufferedReader var4 = var1.openAsReader()) {
         JsonElement var5 = JsonParser.parseReader(var4);
         ShaderProgramConfig var6 = (ShaderProgramConfig)ShaderProgramConfig.CODEC.parse(JsonOps.INSTANCE, var5).getOrThrow(JsonSyntaxException::new);
         var2.put(var3, var6);
      } catch (JsonParseException | IOException var9) {
         LOGGER.error("Failed to parse shader config at {}", var0, var9);
      }
   }

   private static void loadPostChain(ResourceLocation var0, Resource var1, Builder<ResourceLocation, PostChainConfig> var2) {
      ResourceLocation var3 = POST_CHAIN_ID_CONVERTER.fileToId(var0);

      try (BufferedReader var4 = var1.openAsReader()) {
         JsonElement var5 = JsonParser.parseReader(var4);
         var2.put(var3, (PostChainConfig)PostChainConfig.CODEC.parse(JsonOps.INSTANCE, var5).getOrThrow(JsonSyntaxException::new));
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

   protected void apply(ShaderManager.Configs var1, ResourceManager var2, ProfilerFiller var3) {
      this.clearCompilationCache();
      this.configs = var1;
      HashMap var4 = new HashMap();

      for (ShaderProgram var6 : CoreShaders.getProgramsToPreload()) {
         try {
            this.compiledPrograms.put(var6, Optional.of(this.compileProgram(var6)));
         } catch (ShaderManager.CompilationException var8) {
            var4.put(var6, var8);
         }
      }

      if (!var4.isEmpty()) {
         throw new RuntimeException(
            "Failed to load required shader programs:\n"
               + var4.entrySet()
                  .stream()
                  .map(var0 -> " - " + var0.getKey() + ": " + ((ShaderManager.CompilationException)var0.getValue()).getMessage())
                  .collect(Collectors.joining("\n"))
         );
      }
   }

   @Override
   public String getName() {
      return "Shader Loader";
   }

   public void preloadForStartup(ResourceProvider var1, ShaderProgram... var2) throws IOException, ShaderManager.CompilationException {
      for (ShaderProgram var6 : var2) {
         Resource var7 = var1.getResourceOrThrow(PROGRAM_ID_CONVERTER.idToFile(var6.configId()));

         try (BufferedReader var8 = var7.openAsReader()) {
            JsonElement var9 = JsonParser.parseReader(var8);
            ShaderProgramConfig var10 = (ShaderProgramConfig)ShaderProgramConfig.CODEC.parse(JsonOps.INSTANCE, var9).getOrThrow(JsonSyntaxException::new);
            ShaderDefines var11 = var10.defines().withOverrides(var6.defines());
            CompiledShader var12 = this.preloadShader(var1, var10.vertex(), CompiledShader.Type.VERTEX, var11);
            CompiledShader var13 = this.preloadShader(var1, var10.fragment(), CompiledShader.Type.FRAGMENT, var11);
            CompiledShaderProgram var14 = this.linkProgram(var6, var10, var12, var13);
            this.compiledPrograms.put(var6, Optional.of(var14));
         }
      }
   }

   private CompiledShader preloadShader(ResourceProvider var1, ResourceLocation var2, CompiledShader.Type var3, ShaderDefines var4) throws IOException, ShaderManager.CompilationException {
      ResourceLocation var5 = var3.idConverter().idToFile(var2);

      CompiledShader var10;
      try (BufferedReader var6 = var1.getResourceOrThrow(var5).openAsReader()) {
         String var7 = IOUtils.toString(var6);
         String var8 = GlslPreprocessor.injectDefines(var7, var4);
         CompiledShader var9 = CompiledShader.compile(var2, var3, var8);
         this.compiledShaders.put(new ShaderManager.ShaderCompilationKey(var2, var3, var4), var9);
         var10 = var9;
      }

      return var10;
   }

   @Nullable
   public CompiledShaderProgram getProgram(ShaderProgram var1) {
      Optional var2 = this.compiledPrograms.get(var1);
      if (var2 != null) {
         return (CompiledShaderProgram)var2.orElse(null);
      } else {
         try {
            CompiledShaderProgram var3 = this.compileProgram(var1);
            this.compiledPrograms.put(var1, Optional.of(var3));
            return var3;
         } catch (ShaderManager.CompilationException var4) {
            LOGGER.error("Failed to load shader program: {}", var1, var4);
            this.compiledPrograms.put(var1, Optional.empty());
            return null;
         }
      }
   }

   private CompiledShaderProgram compileProgram(ShaderProgram var1) throws ShaderManager.CompilationException {
      ShaderProgramConfig var2 = this.configs.programs.get(var1.configId());
      if (var2 == null) {
         throw new ShaderManager.CompilationException("Could not find program with id: " + var1.configId());
      } else {
         ShaderDefines var3 = var2.defines().withOverrides(var1.defines());
         CompiledShader var4 = this.getOrCompileShader(var2.vertex(), CompiledShader.Type.VERTEX, var3);
         CompiledShader var5 = this.getOrCompileShader(var2.fragment(), CompiledShader.Type.FRAGMENT, var3);
         return this.linkProgram(var1, var2, var4, var5);
      }
   }

   private CompiledShaderProgram linkProgram(ShaderProgram var1, ShaderProgramConfig var2, CompiledShader var3, CompiledShader var4) throws ShaderManager.CompilationException {
      CompiledShaderProgram var5 = CompiledShaderProgram.link(var3, var4, var1.vertexFormat());
      var5.setupUniforms(var2.uniforms(), var2.samplers());
      return var5;
   }

   private CompiledShader getOrCompileShader(ResourceLocation var1, CompiledShader.Type var2, ShaderDefines var3) throws ShaderManager.CompilationException {
      ShaderManager.ShaderCompilationKey var4 = new ShaderManager.ShaderCompilationKey(var1, var2, var3);
      CompiledShader var5 = this.compiledShaders.get(var4);
      if (var5 == null) {
         var5 = this.compileShader(var4);
         this.compiledShaders.put(var4, var5);
      }

      return var5;
   }

   private CompiledShader compileShader(ShaderManager.ShaderCompilationKey var1) throws ShaderManager.CompilationException {
      String var2 = this.configs.shaderSources.get(new ShaderManager.ShaderSourceKey(var1.id, var1.type));
      if (var2 == null) {
         throw new ShaderManager.CompilationException("Could not find shader: " + var1);
      } else {
         String var3 = GlslPreprocessor.injectDefines(var2, var1.defines);
         return CompiledShader.compile(var1.id, var1.type, var3);
      }
   }

   @Nullable
   public PostChain getPostChain(ResourceLocation var1, Set<ResourceLocation> var2) {
      Optional var3 = this.postChains.get(var1);
      if (var3 != null) {
         return (PostChain)var3.orElse(null);
      } else {
         try {
            PostChain var4 = this.loadPostChain(var1, var2);
            this.postChains.put(var1, Optional.of(var4));
            return var4;
         } catch (ShaderManager.CompilationException var5) {
            LOGGER.error("Failed to load post chain: {}", var1, var5);
            this.postChains.put(var1, Optional.empty());
            return null;
         }
      }
   }

   private PostChain loadPostChain(ResourceLocation var1, Set<ResourceLocation> var2) throws ShaderManager.CompilationException {
      PostChainConfig var3 = this.configs.postChains.get(var1);
      if (var3 == null) {
         throw new ShaderManager.CompilationException("Could not find post chain with id: " + var1);
      } else {
         return PostChain.load(var3, this.textureManager, this, var2);
      }
   }

   private void clearCompilationCache() {
      RenderSystem.assertOnRenderThread();
      this.compiledPrograms.values().forEach(var0 -> var0.ifPresent(CompiledShaderProgram::close));
      this.compiledShaders.values().forEach(CompiledShader::close);
      this.compiledPrograms.clear();
      this.compiledShaders.clear();
      this.postChains.clear();
   }

   @Override
   public void close() {
      this.clearCompilationCache();
   }

   public static class CompilationException extends Exception {
      public CompilationException(String var1) {
         super(var1);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
