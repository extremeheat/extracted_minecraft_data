package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import org.slf4j.Logger;

public class TextureAtlas extends AbstractTexture implements Tickable {
   private static final Logger LOGGER = LogUtils.getLogger();
   /** @deprecated */
   @Deprecated
   public static final ResourceLocation LOCATION_BLOCKS;
   /** @deprecated */
   @Deprecated
   public static final ResourceLocation LOCATION_PARTICLES;
   private static final String FILE_EXTENSION = ".png";
   private final List<Tickable> animatedTextures = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> texturesByName = Maps.newHashMap();
   private final ResourceLocation location;
   private final int maxSupportedTextureSize;

   public TextureAtlas(ResourceLocation var1) {
      super();
      this.location = var1;
      this.maxSupportedTextureSize = RenderSystem.maxSupportedTextureSize();
   }

   public void load(ResourceManager var1) {
   }

   public void reload(Preparations var1) {
      this.sprites.clear();
      this.sprites.addAll(var1.sprites);
      LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{var1.width, var1.height, var1.mipLevel, this.location});
      TextureUtil.prepareImage(this.getId(), var1.mipLevel, var1.width, var1.height);
      this.clearTextureData();
      Iterator var2 = var1.regions.iterator();

      while(var2.hasNext()) {
         TextureAtlasSprite var3 = (TextureAtlasSprite)var2.next();
         this.texturesByName.put(var3.getName(), var3);

         try {
            var3.uploadFirstFrame();
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.forThrowable(var7, "Stitching texture atlas");
            CrashReportCategory var6 = var5.addCategory("Texture being stitched together");
            var6.setDetail("Atlas path", (Object)this.location);
            var6.setDetail("Sprite", (Object)var3);
            throw new ReportedException(var5);
         }

         Tickable var4 = var3.getAnimationTicker();
         if (var4 != null) {
            this.animatedTextures.add(var4);
         }
      }

   }

   public Preparations prepareToStitch(ResourceManager var1, Stream<ResourceLocation> var2, ProfilerFiller var3, int var4) {
      var3.push("preparing");
      Set var5 = (Set)var2.peek((var0) -> {
         if (var0 == null) {
            throw new IllegalArgumentException("Location cannot be null!");
         }
      }).collect(Collectors.toSet());
      int var6 = this.maxSupportedTextureSize;
      Stitcher var7 = new Stitcher(var6, var6, var4);
      int var8 = 2147483647;
      int var9 = 1 << var4;
      var3.popPush("extracting_frames");

      TextureAtlasSprite.Info var11;
      int var12;
      for(Iterator var10 = this.getBasicSpriteInfos(var1, var5).iterator(); var10.hasNext(); var7.registerSprite(var11)) {
         var11 = (TextureAtlasSprite.Info)var10.next();
         var8 = Math.min(var8, Math.min(var11.width(), var11.height()));
         var12 = Math.min(Integer.lowestOneBit(var11.width()), Integer.lowestOneBit(var11.height()));
         if (var12 < var9) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{var11.name(), var11.width(), var11.height(), Mth.log2(var9), Mth.log2(var12)});
            var9 = var12;
         }
      }

      int var17 = Math.min(var8, var9);
      int var18 = Mth.log2(var17);
      if (var18 < var4) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.location, var4, var18, var17});
         var12 = var18;
      } else {
         var12 = var4;
      }

      var3.popPush("register");
      var7.registerSprite(MissingTextureAtlasSprite.info());
      var3.popPush("stitching");

      try {
         var7.stitch();
      } catch (StitcherException var16) {
         CrashReport var14 = CrashReport.forThrowable(var16, "Stitching");
         CrashReportCategory var15 = var14.addCategory("Stitcher");
         var15.setDetail("Sprites", var16.getAllSprites().stream().map((var0) -> {
            return String.format("%s[%dx%d]", var0.name(), var0.width(), var0.height());
         }).collect(Collectors.joining(",")));
         var15.setDetail("Max Texture Size", (Object)var6);
         throw new ReportedException(var14);
      }

      var3.popPush("loading");
      List var13 = this.getLoadedSprites(var1, var7, var12);
      var3.pop();
      return new Preparations(var5, var7.getWidth(), var7.getHeight(), var12, var13);
   }

   private Collection<TextureAtlasSprite.Info> getBasicSpriteInfos(ResourceManager var1, Set<ResourceLocation> var2) {
      ArrayList var3 = Lists.newArrayList();
      ConcurrentLinkedQueue var4 = new ConcurrentLinkedQueue();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         ResourceLocation var6 = (ResourceLocation)var5.next();
         if (!MissingTextureAtlasSprite.getLocation().equals(var6)) {
            var3.add(CompletableFuture.runAsync(() -> {
               ResourceLocation var4x = this.getResourceLocation(var6);
               Optional var6x = var1.getResource(var4x);
               if (var6x.isEmpty()) {
                  LOGGER.error("Using missing texture, file {} not found", var4x);
               } else {
                  Resource var7 = (Resource)var6x.get();

                  PngInfo var8;
                  try {
                     InputStream var9 = var7.open();

                     try {
                        Objects.requireNonNull(var4x);
                        var8 = new PngInfo(var4x::toString, var9);
                     } catch (Throwable var14) {
                        if (var9 != null) {
                           try {
                              var9.close();
                           } catch (Throwable var12) {
                              var14.addSuppressed(var12);
                           }
                        }

                        throw var14;
                     }

                     if (var9 != null) {
                        var9.close();
                     }
                  } catch (IOException var15) {
                     LOGGER.error("Using missing texture, unable to load {} : {}", var4x, var15);
                     return;
                  }

                  AnimationMetadataSection var16;
                  try {
                     var16 = (AnimationMetadataSection)var7.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
                  } catch (Exception var13) {
                     LOGGER.error("Unable to parse metadata from {} : {}", var4x, var13);
                     return;
                  }

                  Pair var10 = var16.getFrameSize(var8.width, var8.height);
                  TextureAtlasSprite.Info var5 = new TextureAtlasSprite.Info(var6, (Integer)var10.getFirst(), (Integer)var10.getSecond(), var16);
                  var4.add(var5);
               }
            }, Util.backgroundExecutor()));
         }
      }

      CompletableFuture.allOf((CompletableFuture[])var3.toArray(new CompletableFuture[0])).join();
      return var4;
   }

   private List<TextureAtlasSprite> getLoadedSprites(ResourceManager var1, Stitcher var2, int var3) {
      ConcurrentLinkedQueue var4 = new ConcurrentLinkedQueue();
      ArrayList var5 = Lists.newArrayList();
      var2.gatherSprites((var5x, var6, var7, var8, var9) -> {
         if (var5x == MissingTextureAtlasSprite.info()) {
            MissingTextureAtlasSprite var10 = MissingTextureAtlasSprite.newInstance(this, var3, var6, var7, var8, var9);
            var4.add(var10);
         } else {
            var5.add(CompletableFuture.runAsync(() -> {
               TextureAtlasSprite var9x = this.load(var1, var5x, var6, var7, var3, var8, var9);
               if (var9x != null) {
                  var4.add(var9x);
               }

            }, Util.backgroundExecutor()));
         }

      });
      CompletableFuture.allOf((CompletableFuture[])var5.toArray(new CompletableFuture[0])).join();
      return Lists.newArrayList(var4);
   }

   @Nullable
   private TextureAtlasSprite load(ResourceManager var1, TextureAtlasSprite.Info var2, int var3, int var4, int var5, int var6, int var7) {
      ResourceLocation var8 = this.getResourceLocation(var2.name());

      try {
         InputStream var9 = var1.open(var8);

         TextureAtlasSprite var11;
         try {
            NativeImage var10 = NativeImage.read(var9);
            var11 = new TextureAtlasSprite(this, var2, var5, var3, var4, var6, var7, var10);
         } catch (Throwable var13) {
            if (var9 != null) {
               try {
                  var9.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (var9 != null) {
            var9.close();
         }

         return var11;
      } catch (RuntimeException var14) {
         LOGGER.error("Unable to parse metadata from {}", var8, var14);
         return null;
      } catch (IOException var15) {
         LOGGER.error("Using missing texture, unable to load {}", var8, var15);
         return null;
      }
   }

   private ResourceLocation getResourceLocation(ResourceLocation var1) {
      return new ResourceLocation(var1.getNamespace(), String.format("textures/%s%s", var1.getPath(), ".png"));
   }

   public void cycleAnimationFrames() {
      this.bind();
      Iterator var1 = this.animatedTextures.iterator();

      while(var1.hasNext()) {
         Tickable var2 = (Tickable)var1.next();
         var2.tick();
      }

   }

   public void tick() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::cycleAnimationFrames);
      } else {
         this.cycleAnimationFrames();
      }

   }

   public TextureAtlasSprite getSprite(ResourceLocation var1) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.texturesByName.get(var1);
      return var2 == null ? (TextureAtlasSprite)this.texturesByName.get(MissingTextureAtlasSprite.getLocation()) : var2;
   }

   public void clearTextureData() {
      Iterator var1 = this.texturesByName.values().iterator();

      while(var1.hasNext()) {
         TextureAtlasSprite var2 = (TextureAtlasSprite)var1.next();
         var2.close();
      }

      this.texturesByName.clear();
      this.animatedTextures.clear();
   }

   public ResourceLocation location() {
      return this.location;
   }

   public void updateFilter(Preparations var1) {
      this.setFilter(false, var1.mipLevel > 0);
   }

   static {
      LOCATION_BLOCKS = InventoryMenu.BLOCK_ATLAS;
      LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
   }

   public static class Preparations {
      final Set<ResourceLocation> sprites;
      final int width;
      final int height;
      final int mipLevel;
      final List<TextureAtlasSprite> regions;

      public Preparations(Set<ResourceLocation> var1, int var2, int var3, int var4, List<TextureAtlasSprite> var5) {
         super();
         this.sprites = var1;
         this.width = var2;
         this.height = var3;
         this.mipLevel = var4;
         this.regions = var5;
      }
   }
}
