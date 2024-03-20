package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import org.slf4j.Logger;

public class TextureAtlas extends AbstractTexture implements Dumpable, Tickable {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Deprecated
   public static final ResourceLocation LOCATION_BLOCKS = InventoryMenu.BLOCK_ATLAS;
   @Deprecated
   public static final ResourceLocation LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
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
      this.maxSupportedTextureSize = RenderSystem.maxSupportedTextureSize();
   }

   @Override
   public void load(ResourceManager var1) {
   }

   public void upload(SpriteLoader.Preparations var1) {
      LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{var1.width(), var1.height(), var1.mipLevel(), this.location});
      TextureUtil.prepareImage(this.getId(), var1.mipLevel(), var1.width(), var1.height());
      this.width = var1.width();
      this.height = var1.height();
      this.mipLevel = var1.mipLevel();
      this.clearTextureData();
      this.texturesByName = Map.copyOf(var1.regions());
      this.missingSprite = this.texturesByName.get(MissingTextureAtlasSprite.getLocation());
      if (this.missingSprite == null) {
         throw new IllegalStateException("Atlas '" + this.location + "' (" + this.texturesByName.size() + " sprites) has no missing texture sprite");
      } else {
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();

         for(TextureAtlasSprite var5 : var1.regions().values()) {
            var2.add(var5.contents());

            try {
               var5.uploadFirstFrame();
            } catch (Throwable var9) {
               CrashReport var7 = CrashReport.forThrowable(var9, "Stitching texture atlas");
               CrashReportCategory var8 = var7.addCategory("Texture being stitched together");
               var8.setDetail("Atlas path", this.location);
               var8.setDetail("Sprite", var5);
               throw new ReportedException(var7);
            }

            TextureAtlasSprite.Ticker var6 = var5.createTicker();
            if (var6 != null) {
               var3.add(var6);
            }
         }

         this.sprites = List.copyOf(var2);
         this.animatedTextures = List.copyOf(var3);
      }
   }

   @Override
   public void dumpContents(ResourceLocation var1, Path var2) throws IOException {
      String var3 = var1.toDebugFileName();
      TextureUtil.writeAsPNG(var2, var3, this.getId(), this.mipLevel, this.width, this.height);
      dumpSpriteNames(var2, var3, this.texturesByName);
   }

   private static void dumpSpriteNames(Path var0, String var1, Map<ResourceLocation, TextureAtlasSprite> var2) {
      Path var3 = var0.resolve(var1 + ".txt");

      try (BufferedWriter var4 = Files.newBufferedWriter(var3)) {
         for(Entry var6 : var2.entrySet().stream().sorted(Entry.comparingByKey()).toList()) {
            TextureAtlasSprite var7 = (TextureAtlasSprite)var6.getValue();
            var4.write(
               String.format(
                  Locale.ROOT, "%s\tx=%d\ty=%d\tw=%d\th=%d%n", var6.getKey(), var7.getX(), var7.getY(), var7.contents().width(), var7.contents().height()
               )
            );
         }
      } catch (IOException var10) {
         LOGGER.warn("Failed to write file {}", var3, var10);
      }
   }

   public void cycleAnimationFrames() {
      this.bind();

      for(TextureAtlasSprite.Ticker var2 : this.animatedTextures) {
         var2.tickAndUpload();
      }
   }

   @Override
   public void tick() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::cycleAnimationFrames);
      } else {
         this.cycleAnimationFrames();
      }
   }

   public TextureAtlasSprite getSprite(ResourceLocation var1) {
      TextureAtlasSprite var2 = this.texturesByName.getOrDefault(var1, this.missingSprite);
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

   public void updateFilter(SpriteLoader.Preparations var1) {
      this.setFilter(false, var1.mipLevel() > 0);
   }
}