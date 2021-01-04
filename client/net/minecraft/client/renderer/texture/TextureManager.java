package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

public class TextureManager implements Tickable, PreparableReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation INTENTIONAL_MISSING_TEXTURE = new ResourceLocation("");
   private final Map<ResourceLocation, TextureObject> byPath = Maps.newHashMap();
   private final List<Tickable> tickableTextures = Lists.newArrayList();
   private final Map<String, Integer> prefixRegister = Maps.newHashMap();
   private final ResourceManager resourceManager;

   public TextureManager(ResourceManager var1) {
      super();
      this.resourceManager = var1;
   }

   public void bind(ResourceLocation var1) {
      Object var2 = (TextureObject)this.byPath.get(var1);
      if (var2 == null) {
         var2 = new SimpleTexture(var1);
         this.register((ResourceLocation)var1, (TextureObject)var2);
      }

      ((TextureObject)var2).bind();
   }

   public boolean register(ResourceLocation var1, TickableTextureObject var2) {
      if (this.register((ResourceLocation)var1, (TextureObject)var2)) {
         this.tickableTextures.add(var2);
         return true;
      } else {
         return false;
      }
   }

   public boolean register(ResourceLocation var1, TextureObject var2) {
      boolean var3 = true;

      try {
         ((TextureObject)var2).load(this.resourceManager);
      } catch (IOException var8) {
         if (var1 != INTENTIONAL_MISSING_TEXTURE) {
            LOGGER.warn("Failed to load texture: {}", var1, var8);
         }

         var2 = MissingTextureAtlasSprite.getTexture();
         this.byPath.put(var1, var2);
         var3 = false;
      } catch (Throwable var9) {
         CrashReport var5 = CrashReport.forThrowable(var9, "Registering texture");
         CrashReportCategory var6 = var5.addCategory("Resource location being registered");
         var6.setDetail("Resource location", (Object)var1);
         var6.setDetail("Texture object class", () -> {
            return var2.getClass().getName();
         });
         throw new ReportedException(var5);
      }

      this.byPath.put(var1, var2);
      return var3;
   }

   public TextureObject getTexture(ResourceLocation var1) {
      return (TextureObject)this.byPath.get(var1);
   }

   public ResourceLocation register(String var1, DynamicTexture var2) {
      Integer var3 = (Integer)this.prefixRegister.get(var1);
      if (var3 == null) {
         var3 = 1;
      } else {
         var3 = var3 + 1;
      }

      this.prefixRegister.put(var1, var3);
      ResourceLocation var4 = new ResourceLocation(String.format("dynamic/%s_%d", var1, var3));
      this.register((ResourceLocation)var4, (TextureObject)var2);
      return var4;
   }

   public CompletableFuture<Void> preload(ResourceLocation var1, Executor var2) {
      if (!this.byPath.containsKey(var1)) {
         PreloadedTexture var3 = new PreloadedTexture(this.resourceManager, var1, var2);
         this.byPath.put(var1, var3);
         return var3.getFuture().thenRunAsync(() -> {
            this.register((ResourceLocation)var1, (TextureObject)var3);
         }, Minecraft.getInstance());
      } else {
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   public void tick() {
      Iterator var1 = this.tickableTextures.iterator();

      while(var1.hasNext()) {
         Tickable var2 = (Tickable)var1.next();
         var2.tick();
      }

   }

   public void release(ResourceLocation var1) {
      TextureObject var2 = this.getTexture(var1);
      if (var2 != null) {
         TextureUtil.releaseTextureId(var2.getId());
      }

   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      CompletableFuture var10000 = CompletableFuture.allOf(TitleScreen.preloadResources(this, var5), this.preload(AbstractWidget.WIDGETS_LOCATION, var5));
      var1.getClass();
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var3x) -> {
         MissingTextureAtlasSprite.getTexture();
         Iterator var4 = this.byPath.entrySet().iterator();

         while(true) {
            while(var4.hasNext()) {
               Entry var5 = (Entry)var4.next();
               ResourceLocation var6x = (ResourceLocation)var5.getKey();
               TextureObject var7 = (TextureObject)var5.getValue();
               if (var7 == MissingTextureAtlasSprite.getTexture() && !var6x.equals(MissingTextureAtlasSprite.getLocation())) {
                  var4.remove();
               } else {
                  var7.reset(this, var2, var6x, var6);
               }
            }

            return;
         }
      }, var6);
   }
}
