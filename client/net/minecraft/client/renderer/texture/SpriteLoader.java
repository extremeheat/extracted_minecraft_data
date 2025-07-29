package net.minecraft.client.renderer.texture;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import org.slf4j.Logger;

public class SpriteLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
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

   private Preparations stitch(List<SpriteContents> var1, int var2, Executor var3) {
      try (Zone var4 = Profiler.get().zone((Supplier)(() -> "stitch " + String.valueOf(this.location)))) {
         int var5 = this.maxSupportedTextureSize;
         Stitcher var6 = new Stitcher(var5, var5, var2);
         int var7 = 2147483647;
         int var8 = 1 << var2;

         for(SpriteContents var10 : var1) {
            var7 = Math.min(var7, Math.min(var10.width(), var10.height()));
            int var11 = Math.min(Integer.lowestOneBit(var10.width()), Integer.lowestOneBit(var10.height()));
            if (var11 < var8) {
               LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{var10.name(), var10.width(), var10.height(), Mth.log2(var8), Mth.log2(var11)});
               var8 = var11;
            }

            var6.registerSprite(var10);
         }

         int var21 = Math.min(var7, var8);
         int var22 = Mth.log2(var21);
         int var23;
         if (var22 < var2) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.location, var2, var22, var21});
            var23 = var22;
         } else {
            var23 = var2;
         }

         try {
            var6.stitch();
         } catch (StitcherException var19) {
            CrashReport var13 = CrashReport.forThrowable(var19, "Stitching");
            CrashReportCategory var14 = var13.addCategory("Stitcher");
            var14.setDetail("Sprites", var19.getAllSprites().stream().map((var0) -> String.format(Locale.ROOT, "%s[%dx%d]", var0.name(), var0.width(), var0.height())).collect(Collectors.joining(",")));
            var14.setDetail("Max Texture Size", var5);
            throw new ReportedException(var13);
         }

         int var12 = Math.max(var6.getWidth(), this.minWidth);
         int var24 = Math.max(var6.getHeight(), this.minHeight);
         Map var25 = this.getStitchedSprites(var6, var12, var24);
         TextureAtlasSprite var15 = (TextureAtlasSprite)var25.get(MissingTextureAtlasSprite.getLocation());
         CompletableFuture var16;
         if (var23 > 0) {
            var16 = CompletableFuture.runAsync(() -> var25.values().forEach((var1) -> var1.contents().increaseMipLevel(var23)), var3);
         } else {
            var16 = CompletableFuture.completedFuture((Object)null);
         }

         return new Preparations(var12, var24, var23, var15, var25, var16);
      }
   }

   private static CompletableFuture<List<SpriteContents>> runSpriteSuppliers(SpriteResourceLoader var0, List<Function<SpriteResourceLoader, SpriteContents>> var1, Executor var2) {
      List var3 = var1.stream().map((var2x) -> CompletableFuture.supplyAsync(() -> (SpriteContents)var2x.apply(var0), var2)).toList();
      return Util.sequence(var3).thenApply((var0x) -> var0x.stream().filter(Objects::nonNull).toList());
   }

   public CompletableFuture<Preparations> loadAndStitch(ResourceManager var1, ResourceLocation var2, int var3, Executor var4, Set<MetadataSectionType<?>> var5) {
      SpriteResourceLoader var6 = SpriteResourceLoader.create(var5);
      return CompletableFuture.supplyAsync(() -> SpriteSourceList.load(var1, var2).list(var1), var4).thenCompose((var2x) -> runSpriteSuppliers(var6, var2x, var4)).thenApply((var3x) -> this.stitch(var3x, var3, var4));
   }

   private Map<ResourceLocation, TextureAtlasSprite> getStitchedSprites(Stitcher<SpriteContents> var1, int var2, int var3) {
      HashMap var4 = new HashMap();
      var1.gatherSprites((var4x, var5, var6) -> var4.put(var4x.name(), new TextureAtlasSprite(this.location, var4x, var2, var3, var5, var6)));
      return var4;
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

      @Nullable
      public TextureAtlasSprite getSprite(ResourceLocation var1) {
         return (TextureAtlasSprite)this.regions.get(var1);
      }
   }
}
