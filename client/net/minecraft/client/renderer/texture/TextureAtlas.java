package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureAtlas extends AbstractTexture implements TickableTextureObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation LOCATION_BLOCKS = new ResourceLocation("textures/atlas/blocks.png");
   public static final ResourceLocation LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
   public static final ResourceLocation LOCATION_PAINTINGS = new ResourceLocation("textures/atlas/paintings.png");
   public static final ResourceLocation LOCATION_MOB_EFFECTS = new ResourceLocation("textures/atlas/mob_effects.png");
   private final List<TextureAtlasSprite> animatedTextures = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> texturesByName = Maps.newHashMap();
   private final String path;
   private final int maxSupportedTextureSize;
   private int maxMipLevel;
   private final TextureAtlasSprite missingTextureSprite = MissingTextureAtlasSprite.newInstance();

   public TextureAtlas(String var1) {
      super();
      this.path = var1;
      this.maxSupportedTextureSize = Minecraft.maxSupportedTextureSize();
   }

   public void load(ResourceManager var1) throws IOException {
   }

   public void reload(TextureAtlas.Preparations var1) {
      this.sprites.clear();
      this.sprites.addAll(var1.sprites);
      LOGGER.info("Created: {}x{} {}-atlas", var1.width, var1.height, this.path);
      TextureUtil.prepareImage(this.getId(), this.maxMipLevel, var1.width, var1.height);
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
            var6.setDetail("Atlas path", (Object)this.path);
            var6.setDetail("Sprite", (Object)var3);
            throw new ReportedException(var5);
         }

         if (var3.isAnimation()) {
            this.animatedTextures.add(var3);
         }
      }

   }

   public TextureAtlas.Preparations prepareToStitch(ResourceManager var1, Iterable<ResourceLocation> var2, ProfilerFiller var3) {
      HashSet var4 = Sets.newHashSet();
      var3.push("preparing");
      var2.forEach((var1x) -> {
         if (var1x == null) {
            throw new IllegalArgumentException("Location cannot be null!");
         } else {
            var4.add(var1x);
         }
      });
      int var5 = this.maxSupportedTextureSize;
      Stitcher var6 = new Stitcher(var5, var5, this.maxMipLevel);
      int var7 = 2147483647;
      int var8 = 1 << this.maxMipLevel;
      var3.popPush("extracting_frames");

      TextureAtlasSprite var10;
      for(Iterator var9 = this.getBasicSpriteInfos(var1, var4).iterator(); var9.hasNext(); var6.registerSprite(var10)) {
         var10 = (TextureAtlasSprite)var9.next();
         var7 = Math.min(var7, Math.min(var10.getWidth(), var10.getHeight()));
         int var11 = Math.min(Integer.lowestOneBit(var10.getWidth()), Integer.lowestOneBit(var10.getHeight()));
         if (var11 < var8) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", var10.getName(), var10.getWidth(), var10.getHeight(), Mth.log2(var8), Mth.log2(var11));
            var8 = var11;
         }
      }

      int var17 = Math.min(var7, var8);
      int var15 = Mth.log2(var17);
      if (var15 < this.maxMipLevel) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.path, this.maxMipLevel, var15, var17);
         this.maxMipLevel = var15;
      }

      var3.popPush("mipmapping");
      this.missingTextureSprite.applyMipmapping(this.maxMipLevel);
      var3.popPush("register");
      var6.registerSprite(this.missingTextureSprite);
      var3.popPush("stitching");

      try {
         var6.stitch();
      } catch (StitcherException var14) {
         CrashReport var12 = CrashReport.forThrowable(var14, "Stitching");
         CrashReportCategory var13 = var12.addCategory("Stitcher");
         var13.setDetail("Sprites", var14.getAllSprites().stream().map((var0) -> {
            return String.format("%s[%dx%d]", var0.getName(), var0.getWidth(), var0.getHeight());
         }).collect(Collectors.joining(",")));
         var13.setDetail("Max Texture Size", (Object)var5);
         throw new ReportedException(var12);
      }

      var3.popPush("loading");
      List var16 = this.getLoadedSprites(var1, var6);
      var3.pop();
      return new TextureAtlas.Preparations(var4, var6.getWidth(), var6.getHeight(), var16);
   }

   private Collection<TextureAtlasSprite> getBasicSpriteInfos(ResourceManager var1, Set<ResourceLocation> var2) {
      ArrayList var3 = new ArrayList();
      ConcurrentLinkedQueue var4 = new ConcurrentLinkedQueue();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         ResourceLocation var6 = (ResourceLocation)var5.next();
         if (!this.missingTextureSprite.getName().equals(var6)) {
            var3.add(CompletableFuture.runAsync(() -> {
               ResourceLocation var4x = this.getResourceLocation(var6);

               TextureAtlasSprite var5;
               try {
                  Resource var6x = var1.getResource(var4x);
                  Throwable var7 = null;

                  try {
                     PngInfo var8 = new PngInfo(var6x.toString(), var6x.getInputStream());
                     AnimationMetadataSection var9 = (AnimationMetadataSection)var6x.getMetadata(AnimationMetadataSection.SERIALIZER);
                     var5 = new TextureAtlasSprite(var6, var8, var9);
                  } catch (Throwable var19) {
                     var7 = var19;
                     throw var19;
                  } finally {
                     if (var6x != null) {
                        if (var7 != null) {
                           try {
                              var6x.close();
                           } catch (Throwable var18) {
                              var7.addSuppressed(var18);
                           }
                        } else {
                           var6x.close();
                        }
                     }

                  }
               } catch (RuntimeException var21) {
                  LOGGER.error("Unable to parse metadata from {} : {}", var4x, var21);
                  return;
               } catch (IOException var22) {
                  LOGGER.error("Using missing texture, unable to load {} : {}", var4x, var22);
                  return;
               }

               var4.add(var5);
            }, Util.backgroundExecutor()));
         }
      }

      CompletableFuture.allOf((CompletableFuture[])var3.toArray(new CompletableFuture[0])).join();
      return var4;
   }

   private List<TextureAtlasSprite> getLoadedSprites(ResourceManager var1, Stitcher var2) {
      ConcurrentLinkedQueue var3 = new ConcurrentLinkedQueue();
      ArrayList var4 = new ArrayList();
      Iterator var5 = var2.gatherSprites().iterator();

      while(var5.hasNext()) {
         TextureAtlasSprite var6 = (TextureAtlasSprite)var5.next();
         if (var6 == this.missingTextureSprite) {
            var3.add(var6);
         } else {
            var4.add(CompletableFuture.runAsync(() -> {
               if (this.load(var1, var6)) {
                  var3.add(var6);
               }

            }, Util.backgroundExecutor()));
         }
      }

      CompletableFuture.allOf((CompletableFuture[])var4.toArray(new CompletableFuture[0])).join();
      return new ArrayList(var3);
   }

   private boolean load(ResourceManager var1, TextureAtlasSprite var2) {
      ResourceLocation var3 = this.getResourceLocation(var2.getName());
      Resource var4 = null;

      label62: {
         boolean var6;
         try {
            var4 = var1.getResource(var3);
            var2.loadData(var4, this.maxMipLevel + 1);
            break label62;
         } catch (RuntimeException var13) {
            LOGGER.error("Unable to parse metadata from {}", var3, var13);
            var6 = false;
            return var6;
         } catch (IOException var14) {
            LOGGER.error("Using missing texture, unable to load {}", var3, var14);
            var6 = false;
         } finally {
            IOUtils.closeQuietly(var4);
         }

         return var6;
      }

      try {
         var2.applyMipmapping(this.maxMipLevel);
         return true;
      } catch (Throwable var12) {
         CrashReport var16 = CrashReport.forThrowable(var12, "Applying mipmap");
         CrashReportCategory var7 = var16.addCategory("Sprite being mipmapped");
         var7.setDetail("Sprite name", () -> {
            return var2.getName().toString();
         });
         var7.setDetail("Sprite size", () -> {
            return var2.getWidth() + " x " + var2.getHeight();
         });
         var7.setDetail("Sprite frames", () -> {
            return var2.getFrameCount() + " frames";
         });
         var7.setDetail("Mipmap levels", (Object)this.maxMipLevel);
         throw new ReportedException(var16);
      }
   }

   private ResourceLocation getResourceLocation(ResourceLocation var1) {
      return new ResourceLocation(var1.getNamespace(), String.format("%s/%s%s", this.path, var1.getPath(), ".png"));
   }

   public TextureAtlasSprite getTexture(String var1) {
      return this.getSprite(new ResourceLocation(var1));
   }

   public void cycleAnimationFrames() {
      this.bind();
      Iterator var1 = this.animatedTextures.iterator();

      while(var1.hasNext()) {
         TextureAtlasSprite var2 = (TextureAtlasSprite)var1.next();
         var2.cycleFrames();
      }

   }

   public void tick() {
      this.cycleAnimationFrames();
   }

   public void setMaxMipLevel(int var1) {
      this.maxMipLevel = var1;
   }

   public TextureAtlasSprite getSprite(ResourceLocation var1) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.texturesByName.get(var1);
      return var2 == null ? this.missingTextureSprite : var2;
   }

   public void clearTextureData() {
      Iterator var1 = this.texturesByName.values().iterator();

      while(var1.hasNext()) {
         TextureAtlasSprite var2 = (TextureAtlasSprite)var1.next();
         var2.wipeFrameData();
      }

      this.texturesByName.clear();
      this.animatedTextures.clear();
   }

   public static class Preparations {
      final Set<ResourceLocation> sprites;
      final int width;
      final int height;
      final List<TextureAtlasSprite> regions;

      public Preparations(Set<ResourceLocation> var1, int var2, int var3, List<TextureAtlasSprite> var4) {
         super();
         this.sprites = var1;
         this.width = var2;
         this.height = var3;
         this.regions = var4;
      }
   }
}
