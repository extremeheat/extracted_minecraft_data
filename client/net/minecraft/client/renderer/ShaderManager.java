package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
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
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class ShaderManager extends SimplePreparableReloadListener<Configs> implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final int MAX_LOG_LENGTH = 32768;
   public static final String SHADER_PATH = "shaders";
   private static final String SHADER_INCLUDE_PATH = "shaders/include/";
   private static final FileToIdConverter POST_CHAIN_ID_CONVERTER = FileToIdConverter.json("post_effect");
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
      Map var4 = var1.listResources("shaders", ShaderManager::isShader);

      for(Map.Entry var6 : var4.entrySet()) {
         ResourceLocation var7 = (ResourceLocation)var6.getKey();
         ShaderType var8 = ShaderType.byLocation(var7);
         if (var8 != null) {
            loadShader(var7, (Resource)var6.getValue(), var8, var4, var3);
         }
      }

      ImmutableMap.Builder var9 = ImmutableMap.builder();

      for(Map.Entry var11 : POST_CHAIN_ID_CONVERTER.listMatchingResources(var1).entrySet()) {
         loadPostChain((ResourceLocation)var11.getKey(), (Resource)var11.getValue(), var9);
      }

      return new Configs(var3.build(), var9.build());
   }

   private static void loadShader(ResourceLocation var0, Resource var1, ShaderType var2, Map<ResourceLocation, Resource> var3, ImmutableMap.Builder<ShaderSourceKey, String> var4) {
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
                  var3 = var2.withPath((UnaryOperator)((var1x) -> FileUtil.normalizeResourcePath(var1x + var2x)));
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

   private static boolean isShader(ResourceLocation var0) {
      return ShaderType.byLocation(var0) != null || var0.getPath().endsWith(".glsl");
   }

   protected void apply(Configs var1, ResourceManager var2, ProfilerFiller var3) {
      CompilationCache var4 = new CompilationCache(var1);
      HashSet var5 = new HashSet(RenderPipelines.getStaticPipelines());
      ArrayList var6 = new ArrayList();
      GpuDevice var7 = RenderSystem.getDevice();
      var7.clearPipelineCache();

      for(RenderPipeline var9 : var5) {
         Objects.requireNonNull(var4);
         CompiledRenderPipeline var10 = var7.precompilePipeline(var9, var4::getShaderSource);
         if (!var10.isValid()) {
            var6.add(var9.getLocation());
         }
      }

      if (!var6.isEmpty()) {
         var7.clearPipelineCache();
         Stream var10002 = var6.stream().map((var0) -> " - " + String.valueOf(var0));
         throw new RuntimeException("Failed to load required shader programs:\n" + (String)var10002.collect(Collectors.joining("\n")));
      } else {
         this.compilationCache.close();
         this.compilationCache = var4;
      }
   }

   public String getName() {
      return "Shader Loader";
   }

   private void tryTriggerRecovery(Exception var1) {
      if (!this.compilationCache.triggeredRecovery) {
         this.recoveryHandler.accept(var1);
         this.compilationCache.triggeredRecovery = true;
      }
   }

   @Nullable
   public PostChain getPostChain(ResourceLocation var1, Set<ResourceLocation> var2) {
      try {
         return this.compilationCache.getOrLoadPostChain(var1, var2);
      } catch (CompilationException var4) {
         LOGGER.error("Failed to load post chain: {}", var1, var4);
         this.compilationCache.postChains.put(var1, Optional.empty());
         this.tryTriggerRecovery(var4);
         return null;
      }
   }

   public void close() {
      this.compilationCache.close();
   }

   public String getShader(ResourceLocation var1, ShaderType var2) {
      return this.compilationCache.getShaderSource(var1, var2);
   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }

   public static record Configs(Map<ShaderSourceKey, String> shaderSources, Map<ResourceLocation, PostChainConfig> postChains) {
      final Map<ShaderSourceKey, String> shaderSources;
      final Map<ResourceLocation, PostChainConfig> postChains;
      public static final Configs EMPTY = new Configs(Map.of(), Map.of());

      public Configs(Map<ShaderSourceKey, String> var1, Map<ResourceLocation, PostChainConfig> var2) {
         super();
         this.shaderSources = var1;
         this.postChains = var2;
      }
   }

   class CompilationCache implements AutoCloseable {
      private final Configs configs;
      final Map<ResourceLocation, Optional<PostChain>> postChains = new HashMap();
      boolean triggeredRecovery;

      CompilationCache(final Configs var2) {
         super();
         this.configs = var2;
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
            return PostChain.load(var3, ShaderManager.this.textureManager, var2, var1);
         }
      }

      public void close() {
         this.postChains.clear();
      }

      public String getShaderSource(ResourceLocation var1, ShaderType var2) {
         return (String)this.configs.shaderSources.get(new ShaderSourceKey(var1, var2));
      }
   }

   static record ShaderSourceKey(ResourceLocation id, ShaderType type) {
      ShaderSourceKey(ResourceLocation var1, ShaderType var2) {
         super();
         this.id = var1;
         this.type = var2;
      }

      public String toString() {
         String var10000 = String.valueOf(this.id);
         return var10000 + " (" + String.valueOf(this.type) + ")";
      }
   }

   public static class CompilationException extends Exception {
      public CompilationException(String var1) {
         super(var1);
      }
   }
}
