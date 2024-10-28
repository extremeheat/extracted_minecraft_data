package net.minecraft.client.renderer.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.gui.screens.AddRealmPopupScreen;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class TextureManager implements PreparableReloadListener, Tickable, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ResourceLocation INTENTIONAL_MISSING_TEXTURE = new ResourceLocation("");
   private final Map<ResourceLocation, AbstractTexture> byPath = Maps.newHashMap();
   private final Set<Tickable> tickableTextures = Sets.newHashSet();
   private final Map<String, Integer> prefixRegister = Maps.newHashMap();
   private final ResourceManager resourceManager;

   public TextureManager(ResourceManager var1) {
      super();
      this.resourceManager = var1;
   }

   public void bindForSetup(ResourceLocation var1) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._bind(var1);
         });
      } else {
         this._bind(var1);
      }

   }

   private void _bind(ResourceLocation var1) {
      Object var2 = (AbstractTexture)this.byPath.get(var1);
      if (var2 == null) {
         var2 = new SimpleTexture(var1);
         this.register((ResourceLocation)var1, (AbstractTexture)var2);
      }

      ((AbstractTexture)var2).bind();
   }

   public void register(ResourceLocation var1, AbstractTexture var2) {
      var2 = this.loadTexture(var1, var2);
      AbstractTexture var3 = (AbstractTexture)this.byPath.put(var1, var2);
      if (var3 != var2) {
         if (var3 != null && var3 != MissingTextureAtlasSprite.getTexture()) {
            this.safeClose(var1, var3);
         }

         if (var2 instanceof Tickable) {
            this.tickableTextures.add((Tickable)var2);
         }
      }

   }

   private void safeClose(ResourceLocation var1, AbstractTexture var2) {
      if (var2 != MissingTextureAtlasSprite.getTexture()) {
         this.tickableTextures.remove(var2);

         try {
            var2.close();
         } catch (Exception var4) {
            LOGGER.warn("Failed to close texture {}", var1, var4);
         }
      }

      var2.releaseId();
   }

   private AbstractTexture loadTexture(ResourceLocation var1, AbstractTexture var2) {
      try {
         var2.load(this.resourceManager);
         return var2;
      } catch (IOException var6) {
         if (var1 != INTENTIONAL_MISSING_TEXTURE) {
            LOGGER.warn("Failed to load texture: {}", var1, var6);
         }

         return MissingTextureAtlasSprite.getTexture();
      } catch (Throwable var7) {
         CrashReport var4 = CrashReport.forThrowable(var7, "Registering texture");
         CrashReportCategory var5 = var4.addCategory("Resource location being registered");
         var5.setDetail("Resource location", (Object)var1);
         var5.setDetail("Texture object class", () -> {
            return var2.getClass().getName();
         });
         throw new ReportedException(var4);
      }
   }

   public AbstractTexture getTexture(ResourceLocation var1) {
      Object var2 = (AbstractTexture)this.byPath.get(var1);
      if (var2 == null) {
         var2 = new SimpleTexture(var1);
         this.register((ResourceLocation)var1, (AbstractTexture)var2);
      }

      return (AbstractTexture)var2;
   }

   public AbstractTexture getTexture(ResourceLocation var1, AbstractTexture var2) {
      return (AbstractTexture)this.byPath.getOrDefault(var1, var2);
   }

   public ResourceLocation register(String var1, DynamicTexture var2) {
      Integer var3 = (Integer)this.prefixRegister.get(var1);
      if (var3 == null) {
         var3 = 1;
      } else {
         var3 = var3 + 1;
      }

      this.prefixRegister.put(var1, var3);
      ResourceLocation var4 = new ResourceLocation(String.format(Locale.ROOT, "dynamic/%s_%d", var1, var3));
      this.register((ResourceLocation)var4, (AbstractTexture)var2);
      return var4;
   }

   public CompletableFuture<Void> preload(ResourceLocation var1, Executor var2) {
      if (!this.byPath.containsKey(var1)) {
         PreloadedTexture var3 = new PreloadedTexture(this.resourceManager, var1, var2);
         this.byPath.put(var1, var3);
         return var3.getFuture().thenRunAsync(() -> {
            this.register((ResourceLocation)var1, (AbstractTexture)var3);
         }, TextureManager::execute);
      } else {
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   private static void execute(Runnable var0) {
      Minecraft.getInstance().execute(() -> {
         Objects.requireNonNull(var0);
         RenderSystem.recordRenderCall(var0::run);
      });
   }

   public void tick() {
      Iterator var1 = this.tickableTextures.iterator();

      while(var1.hasNext()) {
         Tickable var2 = (Tickable)var1.next();
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
      this.prefixRegister.clear();
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      CompletableFuture var7 = new CompletableFuture();
      CompletableFuture var10000 = TitleScreen.preloadResources(this, var5);
      Objects.requireNonNull(var1);
      var10000.thenCompose(var1::wait).thenAcceptAsync((var4x) -> {
         MissingTextureAtlasSprite.getTexture();
         AddRealmPopupScreen.updateCarouselImages(this.resourceManager);
         Iterator var5 = this.byPath.entrySet().iterator();

         while(true) {
            while(var5.hasNext()) {
               Map.Entry var6x = (Map.Entry)var5.next();
               ResourceLocation var7x = (ResourceLocation)var6x.getKey();
               AbstractTexture var8 = (AbstractTexture)var6x.getValue();
               if (var8 == MissingTextureAtlasSprite.getTexture() && !var7x.equals(MissingTextureAtlasSprite.getLocation())) {
                  var5.remove();
               } else {
                  var8.reset(this, var2, var7x, var6);
               }
            }

            Minecraft.getInstance().tell(() -> {
               var7.complete((Object)null);
            });
            return;
         }
      }, (var0) -> {
         Objects.requireNonNull(var0);
         RenderSystem.recordRenderCall(var0::run);
      });
      return var7;
   }

   public void dumpAllSheets(Path var1) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._dumpAllSheets(var1);
         });
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
}
