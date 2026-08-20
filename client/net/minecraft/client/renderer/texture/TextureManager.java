package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.gui.screens.AddRealmPopupScreen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class TextureManager implements PreparableReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<Identifier, AbstractTexture> byPath = new HashMap();
   private final Set<TickableTexture> tickableTextures = new HashSet();
   private final ResourceManager resourceManager;

   public TextureManager(final ResourceManager resourceManager) {
      super();
      this.resourceManager = resourceManager;
      NativeImage checkerboard = MissingTextureAtlasSprite.generateMissingImage();
      this.register(MissingTextureAtlasSprite.getLocation(), new DynamicTexture(() -> "(intentionally-)Missing Texture", checkerboard));
   }

   public void registerAndLoad(final Identifier textureId, final ReloadableTexture texture) {
      try {
         texture.apply(this.loadContentsSafe(textureId, texture));
      } catch (Throwable t) {
         CrashReport report = CrashReport.forThrowable(t, "Uploading texture");
         CrashReportCategory category = report.addCategory("Uploaded texture");
         category.setDetail("Resource location", texture.resourceId());
         category.setDetail("Texture id", textureId);
         throw new ReportedException(report);
      }

      this.register(textureId, texture);
   }

   private TextureContents loadContentsSafe(final Identifier textureId, final ReloadableTexture texture) {
      try {
         return loadContents(this.resourceManager, texture);
      } catch (Exception e) {
         LOGGER.error("Failed to load texture {} into slot {}", new Object[]{texture.resourceId(), textureId, e});
         return TextureContents.createMissing();
      }
   }

   public void registerForNextReload(final Identifier location) {
      this.register(location, new SimpleTexture(location));
   }

   public void register(final Identifier location, final AbstractTexture texture) {
      AbstractTexture prev = (AbstractTexture)this.byPath.put(location, texture);
      if (prev != texture) {
         if (prev != null) {
            this.safeClose(location, prev);
         }

         if (texture instanceof TickableTexture) {
            TickableTexture tickableTexture = (TickableTexture)texture;
            this.tickableTextures.add(tickableTexture);
         }
      }

   }

   private void safeClose(final Identifier id, final AbstractTexture texture) {
      this.tickableTextures.remove(texture);

      try {
         texture.close();
      } catch (Exception e) {
         LOGGER.warn("Failed to close texture {}", id, e);
      }

   }

   public AbstractTexture getTexture(final Identifier location) {
      AbstractTexture textureObject = (AbstractTexture)this.byPath.get(location);
      if (textureObject != null) {
         return textureObject;
      } else {
         SimpleTexture texture = new SimpleTexture(location);
         this.registerAndLoad(location, texture);
         return texture;
      }
   }

   public void tick() {
      for(TickableTexture tickableTexture : this.tickableTextures) {
         tickableTexture.tick();
      }

   }

   public void release(final Identifier location) {
      AbstractTexture texture = (AbstractTexture)this.byPath.remove(location);
      if (texture != null) {
         this.safeClose(location, texture);
      }

   }

   public void close() {
      this.byPath.forEach(this::safeClose);
      this.byPath.clear();
      this.tickableTextures.clear();
   }

   public CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      List<PendingReload> reloads = new ArrayList();
      this.byPath.forEach((var3, texture) -> {
         if (texture instanceof ReloadableTexture reloadableTexture) {
            reloads.add(scheduleLoad(manager, reloadableTexture, taskExecutor));
         }

      });
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])reloads.stream().map(PendingReload::newContents).toArray((x$0) -> new CompletableFuture[x$0]));
      Objects.requireNonNull(preparationBarrier);
      return var10000.thenCompose(preparationBarrier::wait).thenAcceptAsync((unused) -> {
         AddRealmPopupScreen.updateCarouselImages(this.resourceManager);

         for(PendingReload reload : reloads) {
            reload.texture.apply((TextureContents)reload.newContents.join());
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
         if (texture instanceof Dumpable dumpable) {
            try {
               dumpable.dumpContents(location, targetDir);
            } catch (Exception e) {
               LOGGER.error("Failed to dump texture {}", location, e);
            }
         }

      });
   }

   private static TextureContents loadContents(final ResourceManager manager, final ReloadableTexture texture) throws IOException {
      try {
         return texture.loadContents(manager);
      } catch (FileNotFoundException var3) {
         return TextureContents.createMissing();
      }
   }

   private static PendingReload scheduleLoad(final ResourceManager manager, final ReloadableTexture texture, final Executor executor) {
      return new PendingReload(texture, CompletableFuture.supplyAsync(() -> {
         try {
            return loadContents(manager, texture);
         } catch (IOException e) {
            throw new UncheckedIOException(e);
         }
      }, executor));
   }

   private static record PendingReload(ReloadableTexture texture, CompletableFuture<TextureContents> newContents) {
      private PendingReload {
         super();
      }
   }
}
