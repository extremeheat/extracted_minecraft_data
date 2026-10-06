package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class TextureManager implements PreparableReloadListener, UncheckedAutoCloseable {
   private static final TextureProvider2d DEFAULT_PROVIDER = new TextureProvider2d();
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<Identifier, TextureResources> byPath = new HashMap();
   private final Map<Identifier, TextureProvider<? extends UncheckedAutoCloseable>> providers = new HashMap();
   private final ResourceManager resourceManager;

   public TextureManager(final ResourceManager resourceManager) {
      super();
      this.resourceManager = resourceManager;

      try (NativeImage checkerboard = MissingTextureAtlasSprite.generateMissingImage()) {
         this.register(MissingTextureAtlasSprite.getLocation(), TextureResources.from2dImage(() -> "(intentionally-)Missing Texture", checkerboard));
      }

   }

   public <T extends UncheckedAutoCloseable> TextureHandle registerAndLoad(final Identifier textureId, final TextureProvider<T> textureProvider) {
      try (T content = this.loadContentsSafe(textureId, textureProvider)) {
         TextureResources texture = textureProvider.createTexture(content, textureId);
         this.register(textureId, texture);
         return texture;
      } catch (Throwable t) {
         CrashReport report = CrashReport.forThrowable(t, "Uploading texture");
         CrashReportCategory category = report.addCategory("Uploaded texture");
         category.setDetail("Texture id", textureId);
         throw new ReportedException(report);
      }
   }

   private <T extends UncheckedAutoCloseable> T loadContentsSafe(final Identifier textureId, final TextureProvider<T> provider) {
      try {
         return (T)loadContents(this.resourceManager, provider, textureId);
      } catch (Exception e) {
         LOGGER.error("Failed to load texture {}", textureId, e);
         return provider.missingState();
      }
   }

   public void registerForNextReload(final Identifier location) {
      this.registerProvider(location, DEFAULT_PROVIDER);
   }

   public void register(final Identifier location, final TextureResources texture) {
      TextureResources prev = (TextureResources)this.byPath.put(location, texture);
      if (prev != texture && prev != null) {
         this.safeClose(location, prev);
      }

   }

   public <T extends UncheckedAutoCloseable> void registerProvider(final Identifier location, final TextureProvider<T> provider) {
      this.providers.put(location, provider);
   }

   private void safeClose(final Identifier id, final TextureResources texture) {
      try {
         texture.close();
      } catch (Exception e) {
         LOGGER.warn("Failed to close texture {}", id, e);
      }

   }

   public TextureHandle getTexture(final Identifier location) {
      TextureHandle textureObject = (TextureHandle)this.byPath.get(location);
      if (textureObject != null) {
         return textureObject;
      } else {
         TextureProvider<?> provider = (TextureProvider)this.providers.getOrDefault(location, DEFAULT_PROVIDER);
         return this.registerAndLoad(location, provider);
      }
   }

   public void release(final Identifier location) {
      TextureResources texture = (TextureResources)this.byPath.remove(location);
      if (texture != null) {
         this.safeClose(location, texture);
      }

   }

   public void close() {
      this.byPath.forEach(this::safeClose);
      this.byPath.clear();
   }

   public CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      List<PendingReload<?>> reloads = new ArrayList();
      this.providers.forEach((identifier, provider) -> reloads.add(scheduleLoad(manager, provider, taskExecutor, identifier)));
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])reloads.stream().map(PendingReload::getState).toArray((x$0) -> new CompletableFuture[x$0]));
      Objects.requireNonNull(preparationBarrier);
      return var10000.thenCompose(preparationBarrier::wait).thenAcceptAsync((var2) -> {
         for(PendingReload<?> reload : reloads) {
            this.register(reload.identifier, reload.finish());
         }

      }, reloadExecutor);
   }

   public void dumpAllSheets(final Path targetDir) {
      try {
         Files.createDirectories(targetDir);
      } catch (IOException e) {
         LOGGER.error("Failed to create directory {}", targetDir, e);
         return;
      }

      this.byPath.forEach((location, texture) -> {
         try {
            texture.dumpContents(location, targetDir);
         } catch (Exception e) {
            LOGGER.error("Failed to dump texture {}", location, e);
         }

      });
   }

   private static <T extends UncheckedAutoCloseable> T loadContents(final ResourceManager manager, final TextureProvider<T> provider, final Identifier identifier) throws IOException {
      try {
         return provider.prepareState(manager, identifier);
      } catch (FileNotFoundException var4) {
         return provider.missingState();
      }
   }

   private static <T extends UncheckedAutoCloseable> PendingReload<T> scheduleLoad(final ResourceManager manager, final TextureProvider<T> provider, final Executor executor, final Identifier identifier) {
      return new PendingReload<T>(identifier, CompletableFuture.supplyAsync(() -> {
         try {
            return loadContents(manager, provider, identifier);
         } catch (IOException e) {
            throw new UncheckedIOException(e);
         }
      }, executor), provider);
   }

   private static record PendingReload<T extends UncheckedAutoCloseable>(Identifier identifier, CompletableFuture<T> getState, TextureProvider<T> provider) {
      private PendingReload {
         super();
      }

      private TextureResources finish() {
         try (T state = (T)(this.getState.join())) {
            return this.provider.createTexture(state, this.identifier);
         }
      }
   }
}
