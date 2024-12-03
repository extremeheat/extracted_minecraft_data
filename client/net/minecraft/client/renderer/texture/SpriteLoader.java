package net.minecraft.client.renderer.texture;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceList;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class SpriteLoader {
   public static final Set<MetadataSectionType<?>> DEFAULT_METADATA_SECTIONS;
   private static final Logger LOGGER;
   private final ResourceLocation location;
   private final int maxSupportedTextureSize;
   private final int minWidth;
   private final int minHeight;

   public SpriteLoader(ResourceLocation var1, int var2, int var3, int var4) {
      super();
      this.location = var1;
      this.maxSupportedTextureSize = var2;
      this.minWidth = var3;
      this.minHeight = var4;
   }

   public static SpriteLoader create(TextureAtlas var0) {
      return new SpriteLoader(var0.location(), var0.maxSupportedTextureSize(), var0.getWidth(), var0.getHeight());
   }

   public Preparations stitch(List<SpriteContents> var1, int var2, Executor var3) {
      int var4 = this.maxSupportedTextureSize;
      Stitcher var5 = new Stitcher(var4, var4, var2);
      int var6 = 2147483647;
      int var7 = 1 << var2;

      for(SpriteContents var9 : var1) {
         var6 = Math.min(var6, Math.min(var9.width(), var9.height()));
         int var10 = Math.min(Integer.lowestOneBit(var9.width()), Integer.lowestOneBit(var9.height()));
         if (var10 < var7) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{var9.name(), var9.width(), var9.height(), Mth.log2(var7), Mth.log2(var10)});
            var7 = var10;
         }

         var5.registerSprite(var9);
      }

      int var17 = Math.min(var6, var7);
      int var18 = Mth.log2(var17);
      int var19;
      if (var18 < var2) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.location, var2, var18, var17});
         var19 = var18;
      } else {
         var19 = var2;
      }

      try {
         var5.stitch();
      } catch (StitcherException var16) {
         CrashReport var12 = CrashReport.forThrowable(var16, "Stitching");
         CrashReportCategory var13 = var12.addCategory("Stitcher");
         var13.setDetail("Sprites", var16.getAllSprites().stream().map((var0) -> String.format(Locale.ROOT, "%s[%dx%d]", var0.name(), var0.width(), var0.height())).collect(Collectors.joining(",")));
         var13.setDetail("Max Texture Size", var4);
         throw new ReportedException(var12);
      }

      int var11 = Math.max(var5.getWidth(), this.minWidth);
      int var20 = Math.max(var5.getHeight(), this.minHeight);
      Map var21 = this.getStitchedSprites(var5, var11, var20);
      TextureAtlasSprite var14 = (TextureAtlasSprite)var21.get(MissingTextureAtlasSprite.getLocation());
      CompletableFuture var15;
      if (var19 > 0) {
         var15 = CompletableFuture.runAsync(() -> var21.values().forEach((var1) -> var1.contents().increaseMipLevel(var19)), var3);
      } else {
         var15 = CompletableFuture.completedFuture((Object)null);
      }

      return new Preparations(var11, var20, var19, var14, var21, var15);
   }

   public static CompletableFuture<List<SpriteContents>> runSpriteSuppliers(SpriteResourceLoader var0, List<Function<SpriteResourceLoader, SpriteContents>> var1, Executor var2) {
      List var3 = var1.stream().map((var2x) -> CompletableFuture.supplyAsync(() -> (SpriteContents)var2x.apply(var0), var2)).toList();
      return Util.sequence(var3).thenApply((var0x) -> var0x.stream().filter(Objects::nonNull).toList());
   }

   public CompletableFuture<Preparations> loadAndStitch(ResourceManager var1, ResourceLocation var2, int var3, Executor var4) {
      return this.loadAndStitch(var1, var2, var3, var4, DEFAULT_METADATA_SECTIONS);
   }

   public CompletableFuture<Preparations> loadAndStitch(ResourceManager var1, ResourceLocation var2, int var3, Executor var4, Collection<MetadataSectionType<?>> var5) {
      SpriteResourceLoader var6 = SpriteResourceLoader.create(var5);
      return CompletableFuture.supplyAsync(() -> SpriteSourceList.load(var1, var2).list(var1), var4).thenCompose((var2x) -> runSpriteSuppliers(var6, var2x, var4)).thenApply((var3x) -> this.stitch(var3x, var3, var4));
   }

   private Map<ResourceLocation, TextureAtlasSprite> getStitchedSprites(Stitcher<SpriteContents> var1, int var2, int var3) {
      HashMap var4 = new HashMap();
      var1.gatherSprites((var4x, var5, var6) -> var4.put(var4x.name(), new TextureAtlasSprite(this.location, var4x, var2, var3, var5, var6)));
      return var4;
   }

   static {
      DEFAULT_METADATA_SECTIONS = Set.of(AnimationMetadataSection.TYPE);
      LOGGER = LogUtils.getLogger();
   }

   public static record Preparations(int width, int height, int mipLevel, TextureAtlasSprite missing, Map<ResourceLocation, TextureAtlasSprite> regions, CompletableFuture<Void> readyForUpload) {
      public Preparations(int var1, int var2, int var3, TextureAtlasSprite var4, Map<ResourceLocation, TextureAtlasSprite> var5, CompletableFuture<Void> var6) {
         super();
         this.width = var1;
         this.height = var2;
         this.mipLevel = var3;
         this.missing = var4;
         this.regions = var5;
         this.readyForUpload = var6;
      }

      public CompletableFuture<Preparations> waitForUpload() {
         return this.readyForUpload.thenApply((var1) -> this);
      }
   }
}
