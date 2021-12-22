package net.minecraft.client.renderer.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureManager implements PreparableReloadListener, Tickable, AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
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
            this.tickableTextures.remove(var3);
            this.safeClose(var1, var3);
         }

         if (var2 instanceof Tickable) {
            this.tickableTextures.add((Tickable)var2);
         }
      }

   }

   private void safeClose(ResourceLocation var1, AbstractTexture var2) {
      if (var2 != MissingTextureAtlasSprite.getTexture()) {
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
      AbstractTexture var2 = this.getTexture(var1, MissingTextureAtlasSprite.getTexture());
      if (var2 != MissingTextureAtlasSprite.getTexture()) {
         TextureUtil.releaseTextureId(var2.getId());
      }

   }

   public void close() {
      this.byPath.forEach(this::safeClose);
      this.byPath.clear();
      this.tickableTextures.clear();
      this.prefixRegister.clear();
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      CompletableFuture var10000 = CompletableFuture.allOf(TitleScreen.preloadResources(this, var5), this.preload(AbstractWidget.WIDGETS_LOCATION, var5));
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var3x) -> {
         MissingTextureAtlasSprite.getTexture();
         RealmsMainScreen.updateTeaserImages(this.resourceManager);
         Iterator var4 = this.byPath.entrySet().iterator();

         while(true) {
            while(var4.hasNext()) {
               Entry var5 = (Entry)var4.next();
               ResourceLocation var6x = (ResourceLocation)var5.getKey();
               AbstractTexture var7 = (AbstractTexture)var5.getValue();
               if (var7 == MissingTextureAtlasSprite.getTexture() && !var6x.equals(MissingTextureAtlasSprite.getLocation())) {
                  var4.remove();
               } else {
                  var7.reset(this, var2, var6x, var6);
               }
            }

            return;
         }
      }, (var0) -> {
         Objects.requireNonNull(var0);
         RenderSystem.recordRenderCall(var0::run);
      });
   }
}
