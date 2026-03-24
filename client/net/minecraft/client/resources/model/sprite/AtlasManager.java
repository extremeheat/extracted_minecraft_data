package net.minecraft.client.resources.model.sprite;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class AtlasManager implements AutoCloseable, PreparableReloadListener, SpriteGetter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final List<AtlasConfig> KNOWN_ATLASES;
   public static final PreparableReloadListener.StateKey<PendingStitchResults> PENDING_STITCH;
   private final Map<Identifier, AtlasEntry> atlasByTexture = new HashMap();
   private final Map<Identifier, AtlasEntry> atlasById = new HashMap();
   private Map<SpriteId, TextureAtlasSprite> spriteLookup = Map.of();
   private int maxMipmapLevels;

   public AtlasManager(final TextureManager textureManager, final int maxMipmapLevels) {
      super();

      for(AtlasConfig info : KNOWN_ATLASES) {
         TextureAtlas atlasTexture = new TextureAtlas(info.textureId);
         textureManager.register(info.textureId, atlasTexture);
         AtlasEntry atlasEntry = new AtlasEntry(atlasTexture, info);
         this.atlasByTexture.put(info.textureId, atlasEntry);
         this.atlasById.put(info.definitionLocation, atlasEntry);
      }

      this.maxMipmapLevels = maxMipmapLevels;
   }

   public TextureAtlas getAtlasOrThrow(final Identifier atlasId) {
      AtlasEntry atlasEntry = (AtlasEntry)this.atlasById.get(atlasId);
      if (atlasEntry == null) {
         throw new IllegalArgumentException("Invalid atlas id: " + String.valueOf(atlasId));
      } else {
         return atlasEntry.atlas();
      }
   }

   public void forEach(final BiConsumer<Identifier, TextureAtlas> output) {
      this.atlasById.forEach((atlasId, entry) -> output.accept(atlasId, entry.atlas));
   }

   public void updateMaxMipLevel(final int maxMipmapLevels) {
      this.maxMipmapLevels = maxMipmapLevels;
   }

   public void close() {
      this.spriteLookup = Map.of();
      this.atlasById.values().forEach(AtlasEntry::close);
      this.atlasById.clear();
      this.atlasByTexture.clear();
   }

   public TextureAtlasSprite get(final SpriteId sprite) {
      TextureAtlasSprite result = (TextureAtlasSprite)this.spriteLookup.get(sprite);
      if (result != null) {
         return result;
      } else {
         Identifier atlasTextureId = sprite.atlasLocation();
         AtlasEntry atlasEntry = (AtlasEntry)this.atlasByTexture.get(atlasTextureId);
         if (atlasEntry == null) {
            throw new IllegalArgumentException("Invalid atlas texture id: " + String.valueOf(atlasTextureId));
         } else {
            return atlasEntry.atlas().missingSprite();
         }
      }
   }

   public void prepareSharedState(final PreparableReloadListener.SharedState currentReload) {
      int atlasCount = this.atlasById.size();
      List<PendingStitch> pendingStitches = new ArrayList(atlasCount);
      Map<Identifier, CompletableFuture<SpriteLoader.Preparations>> pendingStitchById = new HashMap(atlasCount);
      List<CompletableFuture<?>> readyForUploads = new ArrayList(atlasCount);
      this.atlasById.forEach((atlasId, atlasEntry) -> {
         CompletableFuture<SpriteLoader.Preparations> stitchingDone = new CompletableFuture();
         pendingStitchById.put(atlasId, stitchingDone);
         pendingStitches.add(new PendingStitch(atlasEntry, stitchingDone));
         readyForUploads.add(stitchingDone.thenCompose(SpriteLoader.Preparations::readyForUpload));
      });
      CompletableFuture<?> allReadyForUploads = CompletableFuture.allOf((CompletableFuture[])readyForUploads.toArray((x$0) -> new CompletableFuture[x$0]));
      currentReload.set(PENDING_STITCH, new PendingStitchResults(pendingStitches, pendingStitchById, allReadyForUploads));
   }

   public CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      PendingStitchResults pendingStitches = (PendingStitchResults)currentReload.get(PENDING_STITCH);
      ResourceManager resourceManager = currentReload.resourceManager();
      pendingStitches.pendingStitches.forEach((pending) -> pending.entry.scheduleLoad(resourceManager, taskExecutor, this.maxMipmapLevels).whenComplete((value, throwable) -> {
            if (value != null) {
               pending.preparations.complete(value);
            } else {
               pending.preparations.completeExceptionally(throwable);
            }

         }));
      CompletableFuture var10000 = pendingStitches.allReadyToUpload;
      Objects.requireNonNull(preparationBarrier);
      return var10000.thenCompose(preparationBarrier::wait).thenAcceptAsync((unused) -> this.updateSpriteMaps(pendingStitches), reloadExecutor);
   }

   private void updateSpriteMaps(final PendingStitchResults pendingStitches) {
      this.spriteLookup = pendingStitches.joinAndUpload();
      Map<Identifier, TextureAtlasSprite> globalSpriteLookup = new HashMap();
      this.spriteLookup.forEach((id, sprite) -> {
         if (!id.texture().equals(MissingTextureAtlasSprite.getLocation())) {
            TextureAtlasSprite previous = (TextureAtlasSprite)globalSpriteLookup.putIfAbsent(id.texture(), sprite);
            if (previous != null) {
               LOGGER.warn("Duplicate sprite {} from atlas {}, already defined in atlas {}. This will be rejected in a future version", new Object[]{id.texture(), id.atlasLocation(), previous.atlasLocation()});
            }
         }

      });
   }

   static {
      KNOWN_ATLASES = List.of(new AtlasConfig(Sheets.ARMOR_TRIMS_SHEET, AtlasIds.ARMOR_TRIMS, false), new AtlasConfig(Sheets.BANNER_SHEET, AtlasIds.BANNER_PATTERNS, false), new AtlasConfig(Sheets.BED_SHEET, AtlasIds.BEDS, false), new AtlasConfig(TextureAtlas.LOCATION_BLOCKS, AtlasIds.BLOCKS, true), new AtlasConfig(TextureAtlas.LOCATION_ITEMS, AtlasIds.ITEMS, false), new AtlasConfig(Sheets.CHEST_SHEET, AtlasIds.CHESTS, false), new AtlasConfig(Sheets.DECORATED_POT_SHEET, AtlasIds.DECORATED_POT, false), new AtlasConfig(Sheets.GUI_SHEET, AtlasIds.GUI, false, Set.of(GuiMetadataSection.TYPE)), new AtlasConfig(Sheets.MAP_DECORATIONS_SHEET, AtlasIds.MAP_DECORATIONS, false), new AtlasConfig(Sheets.PAINTINGS_SHEET, AtlasIds.PAINTINGS, false), new AtlasConfig(TextureAtlas.LOCATION_PARTICLES, AtlasIds.PARTICLES, false), new AtlasConfig(Sheets.SHIELD_SHEET, AtlasIds.SHIELD_PATTERNS, false), new AtlasConfig(Sheets.SHULKER_SHEET, AtlasIds.SHULKER_BOXES, false), new AtlasConfig(Sheets.SIGN_SHEET, AtlasIds.SIGNS, false), new AtlasConfig(Sheets.CELESTIAL_SHEET, AtlasIds.CELESTIALS, false));
      PENDING_STITCH = new PreparableReloadListener.StateKey<PendingStitchResults>();
   }

   private static record PendingStitch(AtlasEntry entry, CompletableFuture<SpriteLoader.Preparations> preparations) {
      private PendingStitch {
         super();
      }

      public void joinAndUpload(final Map<SpriteId, TextureAtlasSprite> result) {
         SpriteLoader.Preparations preparations = (SpriteLoader.Preparations)this.preparations.join();
         this.entry.atlas.upload(preparations);
         preparations.regions().forEach((spriteId, spriteContents) -> result.put(new SpriteId(this.entry.config.textureId, spriteId), spriteContents));
      }
   }

   private static record AtlasEntry(TextureAtlas atlas, AtlasConfig config) implements AutoCloseable {
      private AtlasEntry {
         super();
      }

      public void close() {
         this.atlas.clearTextureData();
      }

      private CompletableFuture<SpriteLoader.Preparations> scheduleLoad(final ResourceManager resourceManager, final Executor executor, final int maxMipmapLevels) {
         return SpriteLoader.create(this.atlas).loadAndStitch(resourceManager, this.config.definitionLocation, this.config.createMipmaps ? maxMipmapLevels : 0, executor, this.config.additionalMetadata);
      }
   }

   public static record AtlasConfig(Identifier textureId, Identifier definitionLocation, boolean createMipmaps, Set<MetadataSectionType<?>> additionalMetadata) {
      public AtlasConfig(final Identifier textureId, final Identifier definitionLocation, final boolean createMipmaps) {
         this(textureId, definitionLocation, createMipmaps, Set.of());
      }

      public AtlasConfig {
         super();
      }
   }

   public static class PendingStitchResults {
      private final List<PendingStitch> pendingStitches;
      private final Map<Identifier, CompletableFuture<SpriteLoader.Preparations>> stitchFuturesById;
      private final CompletableFuture<?> allReadyToUpload;

      private PendingStitchResults(final List<PendingStitch> pendingStitches, final Map<Identifier, CompletableFuture<SpriteLoader.Preparations>> stitchFuturesById, final CompletableFuture<?> allReadyToUpload) {
         super();
         this.pendingStitches = pendingStitches;
         this.stitchFuturesById = stitchFuturesById;
         this.allReadyToUpload = allReadyToUpload;
      }

      public Map<SpriteId, TextureAtlasSprite> joinAndUpload() {
         Map<SpriteId, TextureAtlasSprite> result = new HashMap();
         this.pendingStitches.forEach((pendingStitch) -> pendingStitch.joinAndUpload(result));
         return result;
      }

      public CompletableFuture<SpriteLoader.Preparations> get(final Identifier atlasId) {
         return (CompletableFuture)Objects.requireNonNull((CompletableFuture)this.stitchFuturesById.get(atlasId));
      }
   }
}
