package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class TextureAtlas extends AbstractTexture implements Dumpable, Tickable {
   private static final Logger LOGGER = LogUtils.getLogger();
   /** @deprecated */
   @Deprecated
   public static final ResourceLocation LOCATION_BLOCKS = ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png");
   /** @deprecated */
   @Deprecated
   public static final ResourceLocation LOCATION_PARTICLES = ResourceLocation.withDefaultNamespace("textures/atlas/particles.png");
   private List<SpriteContents> sprites = List.of();
   private List<TextureAtlasSprite.Ticker> animatedTextures = List.of();
   private Map<ResourceLocation, TextureAtlasSprite> texturesByName = Map.of();
   @Nullable
   private TextureAtlasSprite missingSprite;
   private final ResourceLocation location;
   private final int maxSupportedTextureSize;
   private int width;
   private int height;
   private int mipLevel;

   public TextureAtlas(ResourceLocation var1) {
      super();
      this.location = var1;
      this.maxSupportedTextureSize = RenderSystem.getDevice().getMaxTextureSize();
   }

   public void upload(SpriteLoader.Preparations var1) {
      LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{var1.width(), var1.height(), var1.mipLevel(), this.location});
      GpuDevice var2 = RenderSystem.getDevice();
      ResourceLocation var10002 = this.location;
      Objects.requireNonNull(var10002);
      this.texture = var2.createTexture(var10002::toString, 7, TextureFormat.RGBA8, var1.width(), var1.height(), 1, var1.mipLevel() + 1);
      this.textureView = var2.createTextureView(this.texture);
      this.width = var1.width();
      this.height = var1.height();
      this.mipLevel = var1.mipLevel();
      this.clearTextureData();
      this.setFilter(false, this.mipLevel > 1);
      this.texturesByName = Map.copyOf(var1.regions());
      this.missingSprite = (TextureAtlasSprite)this.texturesByName.get(MissingTextureAtlasSprite.getLocation());
      if (this.missingSprite == null) {
         String var11 = String.valueOf(this.location);
         throw new IllegalStateException("Atlas '" + var11 + "' (" + this.texturesByName.size() + " sprites) has no missing texture sprite");
      } else {
         ArrayList var3 = new ArrayList();
         ArrayList var4 = new ArrayList();

         for(TextureAtlasSprite var6 : var1.regions().values()) {
            var3.add(var6.contents());

            try {
               var6.uploadFirstFrame(this.texture);
            } catch (Throwable var10) {
               CrashReport var8 = CrashReport.forThrowable(var10, "Stitching texture atlas");
               CrashReportCategory var9 = var8.addCategory("Texture being stitched together");
               var9.setDetail("Atlas path", this.location);
               var9.setDetail("Sprite", var6);
               throw new ReportedException(var8);
            }

            TextureAtlasSprite.Ticker var7 = var6.createTicker();
            if (var7 != null) {
               var4.add(var7);
            }
         }

         this.sprites = List.copyOf(var3);
         this.animatedTextures = List.copyOf(var4);
      }
   }

   public void dumpContents(ResourceLocation var1, Path var2) throws IOException {
      String var3 = var1.toDebugFileName();
      TextureUtil.writeAsPNG(var2, var3, this.getTexture(), this.mipLevel, (var0) -> var0);
      dumpSpriteNames(var2, var3, this.texturesByName);
   }

   private static void dumpSpriteNames(Path var0, String var1, Map<ResourceLocation, TextureAtlasSprite> var2) {
      Path var3 = var0.resolve(var1 + ".txt");

      try {
         BufferedWriter var4 = Files.newBufferedWriter(var3);

         try {
            for(Map.Entry var6 : var2.entrySet().stream().sorted(Entry.comparingByKey()).toList()) {
               TextureAtlasSprite var7 = (TextureAtlasSprite)var6.getValue();
               ((Writer)var4).write(String.format(Locale.ROOT, "%s\tx=%d\ty=%d\tw=%d\th=%d%n", var6.getKey(), var7.getX(), var7.getY(), var7.contents().width(), var7.contents().height()));
            }
         } catch (Throwable var9) {
            if (var4 != null) {
               try {
                  ((Writer)var4).close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var4 != null) {
            ((Writer)var4).close();
         }
      } catch (IOException var10) {
         LOGGER.warn("Failed to write file {}", var3, var10);
      }

   }

   public void cycleAnimationFrames() {
      if (this.texture != null) {
         for(TextureAtlasSprite.Ticker var2 : this.animatedTextures) {
            var2.tickAndUpload(this.texture);
         }

      }
   }

   public void tick() {
      this.cycleAnimationFrames();
   }

   public TextureAtlasSprite getSprite(ResourceLocation var1) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.texturesByName.getOrDefault(var1, this.missingSprite);
      if (var2 == null) {
         throw new IllegalStateException("Tried to lookup sprite, but atlas is not initialized");
      } else {
         return var2;
      }
   }

   public void clearTextureData() {
      this.sprites.forEach(SpriteContents::close);
      this.animatedTextures.forEach(TextureAtlasSprite.Ticker::close);
      this.sprites = List.of();
      this.animatedTextures = List.of();
      this.texturesByName = Map.of();
      this.missingSprite = null;
   }

   public ResourceLocation location() {
      return this.location;
   }

   public int maxSupportedTextureSize() {
      return this.maxSupportedTextureSize;
   }

   int getWidth() {
      return this.width;
   }

   int getHeight() {
      return this.height;
   }
}
