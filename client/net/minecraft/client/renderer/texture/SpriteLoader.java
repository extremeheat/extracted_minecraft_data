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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceList;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SpriteLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Identifier location;
   private final int maxSupportedTextureSize;
   private final int minWidth;
   private final int minHeight;

   public SpriteLoader(Identifier var1, int var2, int var3, int var4) {
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
         int var6 = 2147483647;
         int var7 = 1 << var2;

         for(SpriteContents var9 : var1) {
            var6 = Math.min(var6, Math.min(var9.width(), var9.height()));
            int var10 = Math.min(Integer.lowestOneBit(var9.width()), Integer.lowestOneBit(var9.height()));
            if (var10 < var7) {
               LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{var9.name(), var9.width(), var9.height(), Mth.log2(var7), Mth.log2(var10)});
               var7 = var10;
            }
         }

         int var21 = Math.min(var6, var7);
         int var22 = Mth.log2(var21);
         int var23;
         if (var22 < var2) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.location, var2, var22, var21});
            var23 = var22;
         } else {
            var23 = var2;
         }

         Stitcher var11 = new Stitcher(var5, var5, var23, var23 == 0 ? 0 : (Integer)Minecraft.getInstance().options.maxAnisotropyBit().get());

         for(SpriteContents var13 : var1) {
            var11.registerSprite(var13);
         }

         try {
            var11.stitch();
         } catch (StitcherException var19) {
            CrashReport var25 = CrashReport.forThrowable(var19, "Stitching");
            CrashReportCategory var14 = var25.addCategory("Stitcher");
            var14.setDetail("Sprites", var19.getAllSprites().stream().map((var0) -> String.format(Locale.ROOT, "%s[%dx%d]", var0.name(), var0.width(), var0.height())).collect(Collectors.joining(",")));
            var14.setDetail("Max Texture Size", var5);
            throw new ReportedException(var25);
         }

         int var24 = Math.max(var11.getWidth(), this.minWidth);
         int var26 = Math.max(var11.getHeight(), this.minHeight);
         Map var27 = this.getStitchedSprites(var11, var24, var26);
         TextureAtlasSprite var15 = (TextureAtlasSprite)var27.get(MissingTextureAtlasSprite.getLocation());
         CompletableFuture var16;
         if (var23 > 0) {
            var16 = CompletableFuture.runAsync(() -> var27.values().forEach((var1) -> var1.contents().increaseMipLevel(var23)), var3);
         } else {
            var16 = CompletableFuture.completedFuture((Object)null);
         }

         return new Preparations(var24, var26, var23, var15, var27, var16);
      }
   }

   private static CompletableFuture<List<SpriteContents>> runSpriteSuppliers(SpriteResourceLoader var0, List<SpriteSource.Loader> var1, Executor var2) {
      List var3 = var1.stream().map((var2x) -> CompletableFuture.supplyAsync(() -> var2x.get(var0), var2)).toList();
      return Util.sequence(var3).thenApply((var0x) -> var0x.stream().filter(Objects::nonNull).toList());
   }

   public CompletableFuture<Preparations> loadAndStitch(ResourceManager var1, Identifier var2, int var3, Executor var4, Set<MetadataSectionType<?>> var5) {
      SpriteResourceLoader var6 = SpriteResourceLoader.create(var5);
      return CompletableFuture.supplyAsync(() -> SpriteSourceList.load(var1, var2).list(var1), var4).thenCompose((var2x) -> runSpriteSuppliers(var6, var2x, var4)).thenApply((var3x) -> this.stitch(var3x, var3, var4));
   }

   private Map<Identifier, TextureAtlasSprite> getStitchedSprites(Stitcher<SpriteContents> var1, int var2, int var3) {
      HashMap var4 = new HashMap();
      var1.gatherSprites((var4x, var5, var6, var7) -> var4.put(var4x.name(), new TextureAtlasSprite(this.location, var4x, var2, var3, var5, var6, var7)));
      return var4;
   }

   public static record Preparations(int width, int height, int mipLevel, TextureAtlasSprite missing, Map<Identifier, TextureAtlasSprite> regions, CompletableFuture<Void> readyForUpload) {
      public Preparations(int var1, int var2, int var3, TextureAtlasSprite var4, Map<Identifier, TextureAtlasSprite> var5, CompletableFuture<Void> var6) {
         super();
         this.width = var1;
         this.height = var2;
         this.mipLevel = var3;
         this.missing = var4;
         this.regions = var5;
         this.readyForUpload = var6;
      }

      public @Nullable TextureAtlasSprite getSprite(Identifier var1) {
         return (TextureAtlasSprite)this.regions.get(var1);
      }
   }
}
