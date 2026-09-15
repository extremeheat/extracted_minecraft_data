package net.minecraft.client.resources.palette;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.client.renderer.texture.DynamicAtlasTree;
import net.minecraft.client.renderer.texture.DynamicAtlasTreeSlot;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.client.resources.metadata.texture.PaletteMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PalettedTextureManager implements PreparableReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final TextureManager textureManager;
   private BiFunction<Identifier, Identifier, PaletteMapping> paletteMappings = (var0, var1) -> PaletteMapping.NONE;
   private final LoadingCache<Identifier, Optional<BaseTexture>> baseTextureCache;
   private final List<AtlasTexture> atlasTextures = new ArrayList();
   private final List<OverflowTexture> overflowTextures = new ArrayList();
   private final Map<SlotKey, Handle> handles = new HashMap();
   private final Handle missingHandle;

   public PalettedTextureManager(final ResourceManager resourceManager, final TextureManager textureManager) {
      super();
      this.textureManager = textureManager;
      this.baseTextureCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5L)).maximumSize(64L).removalListener((notification) -> ((Optional)notification.getValue()).ifPresent(BaseTexture::close)).build(new CacheLoader<Identifier, Optional<BaseTexture>>() {
         {
            Objects.requireNonNull(PalettedTextureManager.this);
         }

         public Optional<BaseTexture> load(final Identifier id) {
            return Optional.ofNullable(PalettedTextureManager.loadBaseTexture(id, resourceManager));
         }
      });
      this.missingHandle = new Handle() {
         {
            Objects.requireNonNull(PalettedTextureManager.this);
         }

         public Identifier textureLocation() {
            return MissingTextureAtlasSprite.getLocation();
         }

         public float getU(final float offset) {
            return offset;
         }

         public float getV(final float offset) {
            return offset;
         }
      };
   }

   private static @Nullable BaseTexture loadBaseTexture(final Identifier id, final ResourceManager resourceManager) {
      Identifier location = id.withPath((UnaryOperator)((path) -> "textures/" + path + ".png"));

      try {
         Resource resource = resourceManager.getResourceOrThrow(location);
         InputStream input = resource.open();

         NativeImage image;
         try {
            image = NativeImage.read(input);
         } catch (Throwable var9) {
            if (input != null) {
               try {
                  input.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (input != null) {
            input.close();
         }

         return new BaseTexture(image, (PaletteMetadataSection)resource.metadata().getSection(PaletteMetadataSection.TYPE).orElse((Object)null));
      } catch (IOException e) {
         LOGGER.error("Failed to load paletted base texture at {}", location, e);
         return null;
      }
   }

   public Handle getOrPrepare(final Identifier baseTexture, final Identifier paletteId) {
      SlotKey key = new SlotKey(baseTexture, paletteId);
      Handle existingHandle = (Handle)this.handles.get(key);
      if (existingHandle != null) {
         return existingHandle;
      } else {
         Handle handle = this.prepareSlot(baseTexture, paletteId);
         this.handles.put(key, handle);
         return handle;
      }
   }

   private Handle prepareSlot(final Identifier baseTextureId, final Identifier paletteId) {
      Optional<BaseTexture> maybeBaseTexture = (Optional)this.baseTextureCache.getUnchecked(baseTextureId);
      if (maybeBaseTexture.isEmpty()) {
         return this.missingHandle;
      } else {
         BaseTexture baseTexture = (BaseTexture)maybeBaseTexture.get();
         PaletteMetadataSection paletteMetadata = baseTexture.paletteMetadata();
         PaletteMapping paletteMapping = paletteMetadata != null ? (PaletteMapping)this.paletteMappings.apply(paletteMetadata.basePalette(), paletteId) : PaletteMapping.NONE;

         try (NativeImage newTexture = baseTexture.image().mappedCopy(paletteMapping)) {
            Slot slot = this.allocateSlot(newTexture.getWidth(), newTexture.getHeight());
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(slot.texture.getTexture(), newTexture, 0, 0, slot.x, slot.y);
            return slot;
         }
      }
   }

   private Slot allocateSlot(final int width, final int height) {
      if (width <= 512 && height <= 512) {
         for(AtlasTexture atlas : this.atlasTextures) {
            DynamicAtlasTreeSlot atlasSlot = atlas.tryAllocateSlot(width, height);
            if (atlasSlot != null) {
               return new Slot(atlas, atlas.location, atlasSlot.x(), atlasSlot.y(), atlasSlot.width(), atlasSlot.height());
            }
         }

         AtlasTexture atlas = new AtlasTexture(Identifier.withDefaultNamespace("paletted/atlas_" + this.atlasTextures.size()));
         this.textureManager.register(atlas.location, atlas);
         this.atlasTextures.add(atlas);
         DynamicAtlasTreeSlot atlasSlot = atlas.tryAllocateSlot(width, height);
         if (atlasSlot == null) {
            throw new IllegalStateException("Could not allocate slot in fresh atlas for sprite with size " + width + "x" + height);
         } else {
            return new Slot(atlas, atlas.location, atlasSlot.x(), atlasSlot.y(), atlasSlot.width(), atlasSlot.height());
         }
      } else {
         OverflowTexture texture = new OverflowTexture(Identifier.withDefaultNamespace("paletted/overflow_" + this.overflowTextures.size()), width, height);
         this.textureManager.register(texture.location, texture);
         this.overflowTextures.add(texture);
         return new Slot(texture, texture.location, 0, 0, width, height);
      }
   }

   public void close() {
      for(AtlasTexture atlas : this.atlasTextures) {
         this.textureManager.release(atlas.location);
      }

      for(OverflowTexture texture : this.overflowTextures) {
         this.textureManager.release(texture.location);
      }

      this.atlasTextures.clear();
      this.overflowTextures.clear();
      this.handles.clear();
      this.paletteMappings = (var0, var1) -> PaletteMapping.NONE;
      this.baseTextureCache.invalidateAll();
   }

   public CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      CompletableFuture var10000 = Palette.listAndLoad(currentReload.resourceManager(), taskExecutor);
      Objects.requireNonNull(preparationBarrier);
      return var10000.thenCompose(preparationBarrier::wait).thenAcceptAsync((palettes) -> {
         this.close();
         PaletteMappingCache paletteMappings = new PaletteMappingCache(palettes);
         Objects.requireNonNull(paletteMappings);
         this.paletteMappings = paletteMappings::get;
      }, reloadExecutor);
   }

   private static record BaseTexture(NativeImage image, @Nullable PaletteMetadataSection paletteMetadata) implements AutoCloseable {
      private BaseTexture {
         super();
      }

      public void close() {
         this.image.close();
      }
   }

   private static record SlotKey(Identifier baseTexture, Identifier paletteId) {
      private SlotKey {
         super();
      }
   }

   private static record Slot(AbstractTexture texture, Identifier textureLocation, int x, int y, int width, int height) implements Handle {
      private Slot {
         super();
      }

      public float getU(final float offset) {
         return ((float)this.x + offset * (float)this.width) / (float)this.texture.getTexture().getWidth(0);
      }

      public float getV(final float offset) {
         return ((float)this.y + offset * (float)this.height) / (float)this.texture.getTexture().getHeight(0);
      }
   }

   private static class AtlasTexture extends AbstractTexture implements Dumpable {
      private static final int SIZE = 512;
      private final Identifier location;
      private final DynamicAtlasTree tree = new DynamicAtlasTree(0, 0, 512, 512);

      private AtlasTexture(final Identifier location) {
         super();
         GpuDevice device = RenderSystem.getDevice();
         this.texture = device.createTexture((Supplier)(() -> "Paletted Atlas"), 7, GpuFormat.RGBA8_UNORM, 512, 512, 1, 1);
         this.textureView = device.createTextureView(this.texture);
         this.location = location;
         this.sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
      }

      public @Nullable DynamicAtlasTreeSlot tryAllocateSlot(final int width, final int height) {
         return this.tree.insert(width, height, 0);
      }

      public void dumpContents(final Identifier selfId, final Path dir) {
         if (this.texture != null) {
            String outputId = selfId.toDebugFileName();
            TextureUtil.writeAsPNG(dir, outputId, this.texture, 0, (argb) -> ARGB.alpha(argb) == 0 ? -16777216 : argb);
         }

      }
   }

   private static class OverflowTexture extends DynamicTexture {
      private final Identifier location;

      public OverflowTexture(final Identifier location, final int width, final int height) {
         Objects.requireNonNull(location);
         super(location::toString, width, height, false);
         this.location = location;
      }
   }

   public interface Handle extends UvMapping {
      Identifier textureLocation();
   }
}
