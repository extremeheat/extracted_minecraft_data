package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class TextureAtlas extends AbstractTexture implements Dumpable, TickableTexture {
   private static final Logger LOGGER = LogUtils.getLogger();
   /** @deprecated */
   @Deprecated
   public static final Identifier LOCATION_BLOCKS = Identifier.withDefaultNamespace("textures/atlas/blocks.png");
   /** @deprecated */
   @Deprecated
   public static final Identifier LOCATION_ITEMS = Identifier.withDefaultNamespace("textures/atlas/items.png");
   /** @deprecated */
   @Deprecated
   public static final Identifier LOCATION_PARTICLES = Identifier.withDefaultNamespace("textures/atlas/particles.png");
   private List<TextureAtlasSprite> sprites = List.of();
   private List<SpriteContents.AnimationState> animatedTexturesStates = List.of();
   private Map<Identifier, TextureAtlasSprite> texturesByName = Map.of();
   private @Nullable TextureAtlasSprite missingSprite;
   private final Identifier location;
   private final int maxSupportedTextureSize;
   private int width;
   private int height;
   private int maxMipLevel;
   private int mipLevelCount;
   private GpuTextureView[] mipViews = new GpuTextureView[0];
   private @Nullable GpuBuffer spriteUbos;

   public TextureAtlas(Identifier var1) {
      super();
      this.location = var1;
      this.maxSupportedTextureSize = RenderSystem.getDevice().getMaxTextureSize();
   }

   private void createTexture(int var1, int var2, int var3) {
      LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{var1, var2, var3, this.location});
      GpuDevice var4 = RenderSystem.getDevice();
      this.close();
      Identifier var10002 = this.location;
      Objects.requireNonNull(var10002);
      this.texture = var4.createTexture(var10002::toString, 15, TextureFormat.RGBA8, var1, var2, 1, var3 + 1);
      this.textureView = var4.createTextureView(this.texture);
      this.width = var1;
      this.height = var2;
      this.maxMipLevel = var3;
      this.mipLevelCount = var3 + 1;
      this.mipViews = new GpuTextureView[this.mipLevelCount];

      for(int var5 = 0; var5 <= this.maxMipLevel; ++var5) {
         this.mipViews[var5] = var4.createTextureView(this.texture, var5, 1);
      }

   }

   public void upload(SpriteLoader.Preparations var1) {
      this.createTexture(var1.width(), var1.height(), var1.mipLevel());
      this.clearTextureData();
      this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
      this.texturesByName = Map.copyOf(var1.regions());
      this.missingSprite = (TextureAtlasSprite)this.texturesByName.get(MissingTextureAtlasSprite.getLocation());
      if (this.missingSprite == null) {
         String var10002 = String.valueOf(this.location);
         throw new IllegalStateException("Atlas '" + var10002 + "' (" + this.texturesByName.size() + " sprites) has no missing texture sprite");
      } else {
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();
         int var4 = (int)var1.regions().values().stream().filter(TextureAtlasSprite::isAnimated).count();
         int var5 = Mth.roundToward(SpriteContents.UBO_SIZE, RenderSystem.getDevice().getUniformOffsetAlignment());
         int var6 = var5 * this.mipLevelCount;
         ByteBuffer var7 = MemoryUtil.memAlloc(var4 * var6);
         int var8 = 0;

         for(TextureAtlasSprite var10 : var1.regions().values()) {
            if (var10.isAnimated()) {
               var10.uploadSpriteUbo(var7, var8 * var6, this.maxMipLevel, this.width, this.height, var5);
               ++var8;
            }
         }

         GpuBuffer var15 = var8 > 0 ? RenderSystem.getDevice().createBuffer(() -> String.valueOf(this.location) + " sprite UBOs", 128, var7) : null;
         var8 = 0;

         for(TextureAtlasSprite var11 : var1.regions().values()) {
            var2.add(var11);
            if (var11.isAnimated() && var15 != null) {
               SpriteContents.AnimationState var12 = var11.createAnimationState(var15.slice(var8 * var6, var6), var5);
               ++var8;
               if (var12 != null) {
                  var3.add(var12);
               }
            }
         }

         this.spriteUbos = var15;
         this.sprites = var2;
         this.animatedTexturesStates = List.copyOf(var3);
         this.uploadInitialContents();
         if (SharedConstants.DEBUG_DUMP_TEXTURE_ATLAS) {
            Path var17 = TextureUtil.getDebugTexturePath();

            try {
               Files.createDirectories(var17);
               this.dumpContents(this.location, var17);
            } catch (IOException var13) {
               LOGGER.warn("Failed to dump atlas contents to {}", var17);
            }
         }

      }
   }

   private void uploadInitialContents() {
      GpuDevice var1 = RenderSystem.getDevice();
      int var2 = Mth.roundToward(SpriteContents.UBO_SIZE, RenderSystem.getDevice().getUniformOffsetAlignment());
      int var3 = var2 * this.mipLevelCount;
      GpuSampler var4 = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST, true);
      List var5 = this.sprites.stream().filter((var0) -> !var0.isAnimated()).toList();
      ArrayList var6 = new ArrayList();
      ByteBuffer var7 = MemoryUtil.memAlloc(var5.size() * var3);

      for(int var8 = 0; var8 < var5.size(); ++var8) {
         TextureAtlasSprite var9 = (TextureAtlasSprite)var5.get(var8);
         var9.uploadSpriteUbo(var7, var8 * var3, this.maxMipLevel, this.width, this.height, var2);
         GpuTexture var10 = var1.createTexture((Supplier)(() -> var9.contents().name().toString()), 5, TextureFormat.RGBA8, var9.contents().width(), var9.contents().height(), 1, this.mipLevelCount);
         GpuTextureView[] var11 = new GpuTextureView[this.mipLevelCount];

         for(int var12 = 0; var12 <= this.maxMipLevel; ++var12) {
            var9.uploadFirstFrame(var10, var12);
            var11[var12] = var1.createTextureView(var10);
         }

         var6.add(var11);
      }

      try (GpuBuffer var18 = var1.createBuffer(() -> "SpriteAnimationInfo", 128, var7)) {
         for(int var20 = 0; var20 < this.mipLevelCount; ++var20) {
            try (RenderPass var22 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Animate " + String.valueOf(this.location), this.mipViews[var20], OptionalInt.empty())) {
               var22.setPipeline(RenderPipelines.ANIMATE_SPRITE_BLIT);

               for(int var24 = 0; var24 < var5.size(); ++var24) {
                  var22.bindTexture("Sprite", ((GpuTextureView[])var6.get(var24))[var20], var4);
                  var22.setUniform("SpriteAnimationInfo", var18.slice(var24 * var3 + var20 * var2, SpriteContents.UBO_SIZE));
                  var22.draw(0, 6);
               }
            }
         }
      }

      for(GpuTextureView[] var21 : var6) {
         for(GpuTextureView var13 : var21) {
            var13.close();
            var13.texture().close();
         }
      }

      MemoryUtil.memFree(var7);
      this.uploadAnimationFrames();
   }

   public void dumpContents(Identifier var1, Path var2) throws IOException {
      String var3 = var1.toDebugFileName();
      TextureUtil.writeAsPNG(var2, var3, this.getTexture(), this.maxMipLevel, (var0) -> var0);
      dumpSpriteNames(var2, var3, this.texturesByName);
   }

   private static void dumpSpriteNames(Path var0, String var1, Map<Identifier, TextureAtlasSprite> var2) {
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
         for(SpriteContents.AnimationState var2 : this.animatedTexturesStates) {
            var2.tick();
         }

         this.uploadAnimationFrames();
      }
   }

   private void uploadAnimationFrames() {
      if (this.animatedTexturesStates.stream().anyMatch(SpriteContents.AnimationState::needsToDraw)) {
         for(int var1 = 0; var1 <= this.maxMipLevel; ++var1) {
            try (RenderPass var2 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Animate " + String.valueOf(this.location), this.mipViews[var1], OptionalInt.empty())) {
               for(SpriteContents.AnimationState var4 : this.animatedTexturesStates) {
                  if (var4.needsToDraw()) {
                     var4.drawToAtlas(var2, var4.getDrawUbo(var1));
                  }
               }
            }
         }
      }

   }

   public void tick() {
      this.cycleAnimationFrames();
   }

   public TextureAtlasSprite getSprite(Identifier var1) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.texturesByName.getOrDefault(var1, this.missingSprite);
      if (var2 == null) {
         throw new IllegalStateException("Tried to lookup sprite, but atlas is not initialized");
      } else {
         return var2;
      }
   }

   public TextureAtlasSprite missingSprite() {
      return (TextureAtlasSprite)Objects.requireNonNull(this.missingSprite, "Atlas not initialized");
   }

   public void clearTextureData() {
      this.sprites.forEach(TextureAtlasSprite::close);
      this.sprites = List.of();
      this.animatedTexturesStates = List.of();
      this.texturesByName = Map.of();
      this.missingSprite = null;
   }

   public void close() {
      super.close();

      for(GpuTextureView var4 : this.mipViews) {
         var4.close();
      }

      for(SpriteContents.AnimationState var6 : this.animatedTexturesStates) {
         var6.close();
      }

      if (this.spriteUbos != null) {
         this.spriteUbos.close();
         this.spriteUbos = null;
      }

   }

   public Identifier location() {
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
