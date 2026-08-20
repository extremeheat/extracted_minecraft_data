package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.PipelineCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ShaderManager extends SimplePreparableReloadListener<Configs> implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MAX_LOG_LENGTH = 32768;
   public static final String SHADER_PATH = "shaders";
   public static final String SHADER_INCLUDE_PATH = "shaders/include/";
   private static final FileToIdConverter POST_CHAIN_ID_CONVERTER = FileToIdConverter.json("post_effect");
   private final TextureManager textureManager;
   private final Consumer<Exception> recoveryHandler;
   private CompilationCache compilationCache;
   private final Projection postChainProjection;
   private final ProjectionMatrixBuffer postChainProjectionMatrixBuffer;

   public ShaderManager(final TextureManager textureManager, final Consumer<Exception> recoveryHandler) {
      super();
      this.compilationCache = new CompilationCache(ShaderManager.Configs.EMPTY);
      this.postChainProjection = new Projection();
      this.postChainProjectionMatrixBuffer = new ProjectionMatrixBuffer("post");
      this.textureManager = textureManager;
      this.recoveryHandler = recoveryHandler;
      this.postChainProjection.setupOrtho(0.1F, 1000.0F, 1.0F, 1.0F, false);
   }

   protected Configs prepare(final ResourceManager manager, final ProfilerFiller profiler) {
      ImmutableMap.Builder<ShaderSourceKey, String> shaderSources = ImmutableMap.builder();
      Map<Identifier, Resource> files = manager.listResources("shaders", ShaderManager::isShader);

      for(Map.Entry<Identifier, Resource> entry : files.entrySet()) {
         Identifier location = (Identifier)entry.getKey();
         ShaderType shaderType = ShaderType.byLocation(location);
         loadShader(location, (Resource)entry.getValue(), shaderType, files, shaderSources);
      }

      ImmutableMap.Builder<Identifier, PostChainConfig> postChains = ImmutableMap.builder();

      for(Map.Entry<Identifier, Resource> entry : POST_CHAIN_ID_CONVERTER.listMatchingResources(manager).entrySet()) {
         loadPostChain((Identifier)entry.getKey(), (Resource)entry.getValue(), postChains);
      }

      return new Configs(shaderSources.build(), postChains.build());
   }

   private static void loadShader(final Identifier location, final Resource resource, final @Nullable ShaderType type, final Map<Identifier, Resource> files, final ImmutableMap.Builder<ShaderSourceKey, String> output) {
      Identifier id = type == null ? location : type.idConverter().fileToId(location);

      try {
         Reader reader = resource.openAsReader();

         try {
            String source = IOUtils.toString(reader);
            output.put(new ShaderSourceKey(id, type), source);
         } catch (Throwable var10) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (reader != null) {
            reader.close();
         }
      } catch (IOException e) {
         LOGGER.error("Failed to load shader source at {}", location, e);
      }

   }

   private static void loadPostChain(final Identifier location, final Resource resource, final ImmutableMap.Builder<Identifier, PostChainConfig> output) {
      Identifier id = POST_CHAIN_ID_CONVERTER.fileToId(location);

      try {
         Reader reader = resource.openAsReader();

         try {
            JsonElement json = StrictJsonParser.parse(reader);
            output.put(id, (PostChainConfig)PostChainConfig.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonSyntaxException::new));
         } catch (Throwable var8) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (reader != null) {
            reader.close();
         }
      } catch (JsonParseException | IOException e) {
         LOGGER.error("Failed to parse post chain at {}", location, e);
      }

   }

   private static boolean isShader(final Identifier location) {
      return ShaderType.byLocation(location) != null || location.getPath().endsWith(".glsl");
   }

   protected void apply(final Configs preparations, final ResourceManager manager, final ProfilerFiller profiler) {
      CompilationCache newCompilationCache = new CompilationCache(preparations);
      Set<RenderPipeline> pipelinesToPreload = new HashSet(RenderPipelines.requiredPipelines());
      List<Identifier> failedLoads = new ArrayList();
      GpuDevice device = RenderSystem.getDevice();
      Objects.requireNonNull(newCompilationCache);
      PipelineCache pipelineCache = new PipelineCache(device, newCompilationCache::getShaderSource);
      pipelineCache.clear();

      for(RenderPipeline pipeline : pipelinesToPreload) {
         CompiledRenderPipeline compiled = pipelineCache.get(pipeline);
         if (compiled == null) {
            failedLoads.add(pipeline.getLocation());
         }
      }

      if (!failedLoads.isEmpty()) {
         pipelineCache.close();
         Stream var10002 = failedLoads.stream().map((entry) -> " - " + String.valueOf(entry));
         throw new RuntimeException("Failed to load required shader programs:\n" + (String)var10002.collect(Collectors.joining("\n")));
      } else {
         for(RenderPipeline pipeline : RenderPipelines.optionalPipelines()) {
            CompiledRenderPipeline compiled = pipelineCache.get(pipeline);
            if (compiled == null) {
               failedLoads.add(pipeline.getLocation());
            }
         }

         if (!failedLoads.isEmpty()) {
            LOGGER.warn("Failed to load optional shader programs:\n{}", failedLoads.stream().map((entry) -> " - " + String.valueOf(entry)).collect(Collectors.joining("\n")));
         }

         this.compilationCache.close();
         this.compilationCache = newCompilationCache;
         PipelineCache oldPipelineCache = RenderSystem.setCurrentPipelineCache(pipelineCache);
         if (oldPipelineCache != null) {
            oldPipelineCache.close();
         }

      }
   }

   public String getName() {
      return "Shader Loader";
   }

   private void tryTriggerRecovery(final Exception exception) {
      if (!this.compilationCache.triggeredRecovery) {
         this.recoveryHandler.accept(exception);
         this.compilationCache.triggeredRecovery = true;
      }
   }

   public boolean isPostEffectValid(final Identifier id, final Set<Identifier> allowedTargets) {
      PostChainConfig postChainConfig = (PostChainConfig)this.compilationCache.configs.postChains.get(id);
      if (postChainConfig == null) {
         LOGGER.warn("Requested post effect does not exist: {}", id);
         return false;
      } else {
         Set<Identifier> invalidExternalTargets = Sets.difference(PostChain.getReferencedExternalTargets(postChainConfig), allowedTargets);
         if (!invalidExternalTargets.isEmpty()) {
            LOGGER.warn("Requested post chain {} can not be used as a post effect because it uses targets inaccessible to post effects: {}", id, invalidExternalTargets);
            return false;
         } else {
            return true;
         }
      }
   }

   public @Nullable PostChain getPostChain(final Identifier id, final Set<Identifier> allowedTargets) {
      try {
         return this.compilationCache.getOrLoadPostChain(id, allowedTargets);
      } catch (CompilationException e) {
         LOGGER.error("Failed to load post chain: {}", id, e);
         this.compilationCache.postChains.put(id, Optional.empty());
         this.tryTriggerRecovery(e);
         return null;
      }
   }

   public void close() {
      this.compilationCache.close();
      this.postChainProjectionMatrixBuffer.close();
   }

   public Stream<Identifier> getAvailablePostEffects() {
      return this.compilationCache.getKnownPostEffects();
   }

   public static record Configs(Map<ShaderSourceKey, String> shaderSources, Map<Identifier, PostChainConfig> postChains) {
      public static final Configs EMPTY = new Configs(Map.of(), Map.of());

      public Configs {
         super();
      }
   }

   private class CompilationCache implements AutoCloseable {
      private final Configs configs;
      private final Map<Identifier, Optional<PostChain>> postChains;
      private boolean triggeredRecovery;

      private CompilationCache(final Configs configs) {
         Objects.requireNonNull(ShaderManager.this);
         super();
         this.postChains = new HashMap();
         this.configs = configs;
      }

      public @Nullable PostChain getOrLoadPostChain(final Identifier id, final Set<Identifier> allowedTargets) throws CompilationException {
         Optional<PostChain> cached = (Optional)this.postChains.get(id);
         if (cached != null) {
            return (PostChain)cached.orElse((Object)null);
         } else {
            PostChain postChain = this.loadPostChain(id, allowedTargets);
            this.postChains.put(id, Optional.ofNullable(postChain));
            return postChain;
         }
      }

      private @Nullable PostChain loadPostChain(final Identifier id, final Set<Identifier> allowedTargets) throws CompilationException {
         PostChainConfig config = (PostChainConfig)this.configs.postChains.get(id);
         if (config == null) {
            if (!id.equals(GameRenderer.END_OF_FRAME_POST_EFFECT)) {
               ShaderManager.LOGGER.warn("Attempted to load a non-existent post effect {}", id);
            }

            return null;
         } else {
            return PostChain.load(config, ShaderManager.this.textureManager, allowedTargets, id, ShaderManager.this.postChainProjection, ShaderManager.this.postChainProjectionMatrixBuffer);
         }
      }

      public void close() {
         this.postChains.values().forEach((chain) -> chain.ifPresent(PostChain::close));
         this.postChains.clear();
      }

      public @Nullable String getShaderSource(final Identifier id, final @Nullable ShaderType type) {
         return (String)this.configs.shaderSources.get(new ShaderSourceKey(id, type));
      }

      public Stream<Identifier> getKnownPostEffects() {
         return this.configs.postChains.keySet().stream();
      }
   }

   private static record ShaderSourceKey(Identifier id, @Nullable ShaderType type) {
      private ShaderSourceKey {
         super();
      }

      public String toString() {
         String var10000 = String.valueOf(this.id);
         return var10000 + " (" + String.valueOf(this.type) + ")";
      }
   }

   public static class CompilationException extends Exception {
      public CompilationException(final String message) {
         super(message);
      }
   }
}
