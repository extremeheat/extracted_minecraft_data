package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class TextureManager implements PreparableReloadListener, Tickable, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ResourceLocation INTENTIONAL_MISSING_TEXTURE = ResourceLocation.withDefaultNamespace("");
   private final Map<ResourceLocation, AbstractTexture> byPath = new HashMap();
   private final Set<Tickable> tickableTextures = new HashSet();
   private final ResourceManager resourceManager;

   public TextureManager(ResourceManager var1) {
      super();
      this.resourceManager = var1;
      NativeImage var2 = MissingTextureAtlasSprite.generateMissingImage();
      this.register(MissingTextureAtlasSprite.getLocation(), new DynamicTexture(var2));
   }

   public void registerAndLoad(ResourceLocation var1, ReloadableTexture var2) {
      try {
         var2.apply(this.loadContentsSafe(var1, var2));
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, "Uploading texture");
         CrashReportCategory var5 = var4.addCategory("Uploaded texture");
         var5.setDetail("Resource location", var2.resourceId());
         var5.setDetail("Texture id", var1);
         throw new ReportedException(var4);
      }

      this.register(var1, var2);
   }

   private TextureContents loadContentsSafe(ResourceLocation var1, ReloadableTexture var2) {
      try {
         return loadContents(this.resourceManager, var1, var2);
      } catch (Exception var4) {
         LOGGER.error("Failed to load texture {} into slot {}", new Object[]{var2.resourceId(), var1, var4});
         return TextureContents.createMissing();
      }
   }

   public void registerForNextReload(ResourceLocation var1) {
      this.register(var1, new SimpleTexture(var1));
   }

   public void register(ResourceLocation var1, AbstractTexture var2) {
      AbstractTexture var3 = (AbstractTexture)this.byPath.put(var1, var2);
      if (var3 != var2) {
         if (var3 != null) {
            this.safeClose(var1, var3);
         }

         if (var2 instanceof Tickable) {
            Tickable var4 = (Tickable)var2;
            this.tickableTextures.add(var4);
         }
      }

   }

   private void safeClose(ResourceLocation var1, AbstractTexture var2) {
      this.tickableTextures.remove(var2);

      try {
         var2.close();
      } catch (Exception var4) {
         LOGGER.warn("Failed to close texture {}", var1, var4);
      }

      var2.releaseId();
   }

   public AbstractTexture getTexture(ResourceLocation var1) {
      AbstractTexture var2 = (AbstractTexture)this.byPath.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         SimpleTexture var3 = new SimpleTexture(var1);
         this.registerAndLoad(var1, var3);
         return var3;
      }
   }

   public void tick() {
      for(Tickable var2 : this.tickableTextures) {
         var2.tick();
      }

   }

   public void release(ResourceLocation var1) {
      AbstractTexture var2 = (AbstractTexture)this.byPath.remove(var1);
      if (var2 != null) {
         this.safeClose(var1, var2);
      }

   }

   public void close() {
      this.byPath.forEach(this::safeClose);
      this.byPath.clear();
      this.tickableTextures.clear();
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      ArrayList var5 = new ArrayList();
      this.byPath.forEach((var3x, var4x) -> {
         if (var4x instanceof ReloadableTexture var5x) {
            var5.add(scheduleLoad(var2, var3x, var5x, var3));
         }

      });
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])var5.stream().map(PendingReload::newContents).toArray((var0) -> new CompletableFuture[var0]));
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var2x) -> {
         AddRealmPopupScreen.updateCarouselImages(this.resourceManager);

         for(PendingReload var4 : var5) {
            var4.texture.apply((TextureContents)var4.newContents.join());
         }

      }, var4);
   }

   public void dumpAllSheets(Path var1) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> this._dumpAllSheets(var1));
      } else {
         this._dumpAllSheets(var1);
      }

   }

   private void _dumpAllSheets(Path var1) {
      try {
         Files.createDirectories(var1);
      } catch (IOException var3) {
         LOGGER.error("Failed to create directory {}", var1, var3);
         return;
      }

      this.byPath.forEach((var1x, var2) -> {
         if (var2 instanceof Dumpable var3) {
            try {
               var3.dumpContents(var1x, var1);
            } catch (IOException var5) {
               LOGGER.error("Failed to dump texture {}", var1x, var5);
            }
         }

      });
   }

   private static TextureContents loadContents(ResourceManager var0, ResourceLocation var1, ReloadableTexture var2) throws IOException {
      try {
         return var2.loadContents(var0);
      } catch (FileNotFoundException var4) {
         if (var1 != INTENTIONAL_MISSING_TEXTURE) {
            LOGGER.warn("Missing resource {} referenced from {}", var2.resourceId(), var1);
         }

         return TextureContents.createMissing();
      }
   }

   private static PendingReload scheduleLoad(ResourceManager var0, ResourceLocation var1, ReloadableTexture var2, Executor var3) {
      return new PendingReload(var2, CompletableFuture.supplyAsync(() -> {
         try {
            return loadContents(var0, var1, var2);
         } catch (IOException var4) {
            throw new UncheckedIOException(var4);
         }
      }, var3));
   }

   static record PendingReload(ReloadableTexture texture, CompletableFuture<TextureContents> newContents) {
      final ReloadableTexture texture;
      final CompletableFuture<TextureContents> newContents;

      PendingReload(ReloadableTexture var1, CompletableFuture<TextureContents> var2) {
         super();
         this.texture = var1;
         this.newContents = var2;
      }
   }
}
