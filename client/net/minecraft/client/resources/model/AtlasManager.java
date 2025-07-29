package net.minecraft.client.resources.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

public class AtlasManager implements PreparableReloadListener, MaterialSet, AutoCloseable {
   private static final List<AtlasConfig> KNOWN_ATLASES;
   public static final PreparableReloadListener.StateKey<PendingStitchResults> PENDING_STITCH;
   private final Map<ResourceLocation, AtlasEntry> atlases;
   private Map<Material, TextureAtlasSprite> materialLookup = Map.of();
   private int maxMipmapLevels;

   public AtlasManager(TextureManager var1, int var2) {
      super();
      this.atlases = (Map)KNOWN_ATLASES.stream().collect(Collectors.toMap(AtlasConfig::textureId, (var1x) -> {
         TextureAtlas var2 = new TextureAtlas(var1x.textureId);
         var1.register(var1x.textureId, var2);
         return new AtlasEntry(var2, var1x);
      }));
      this.maxMipmapLevels = var2;
   }

   private AtlasEntry getAtlasEntry(ResourceLocation var1) {
      AtlasEntry var2 = (AtlasEntry)this.atlases.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Invalid atlas id: " + String.valueOf(var1));
      } else {
         return var2;
      }
   }

   public TextureAtlas getAtlas(ResourceLocation var1) {
      return this.getAtlasEntry(var1).atlas();
   }

   public void updateMaxMipLevel(int var1) {
      this.maxMipmapLevels = var1;
   }

   public void close() {
      this.materialLookup = Map.of();
      this.atlases.values().forEach(AtlasEntry::close);
      this.atlases.clear();
   }

   public TextureAtlasSprite get(Material var1) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.materialLookup.get(var1);
      return var2 != null ? var2 : this.getAtlasEntry(var1.atlasLocation()).atlas().missingSprite();
   }

   public void prepareSharedState(PreparableReloadListener.SharedState var1) {
      int var2 = this.atlases.size();
      ArrayList var3 = new ArrayList(var2);
      HashMap var4 = new HashMap(var2);
      ArrayList var5 = new ArrayList(var2);
      this.atlases.forEach((var3x, var4x) -> {
         CompletableFuture var5x = new CompletableFuture();
         var4.put(var3x, var5x);
         var3.add(new PendingStitch(var4x, var5x));
         var5.add(var5x.thenCompose(SpriteLoader.Preparations::readyForUpload));
      });
      CompletableFuture var6 = CompletableFuture.allOf((CompletableFuture[])var5.toArray((var0) -> new CompletableFuture[var0]));
      var1.set(PENDING_STITCH, new PendingStitchResults(var3, var4, var6));
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.SharedState var1, Executor var2, PreparableReloadListener.PreparationBarrier var3, Executor var4) {
      PendingStitchResults var5 = (PendingStitchResults)var1.get(PENDING_STITCH);
      ResourceManager var6 = var1.resourceManager();
      var5.pendingStitches.forEach((var3x) -> var3x.entry.scheduleLoad(var6, var2, this.maxMipmapLevels).whenComplete((var1, var2x) -> {
            if (var1 != null) {
               var3x.preparations.complete(var1);
            } else {
               var3x.preparations.completeExceptionally(var2x);
            }

         }));
      CompletableFuture var10000 = var5.allReadyToUpload;
      Objects.requireNonNull(var3);
      return var10000.thenCompose(var3::wait).thenAcceptAsync((var2x) -> this.materialLookup = var5.joinAndUpload(), var4);
   }

   static {
      KNOWN_ATLASES = List.of(new AtlasConfig(Sheets.ARMOR_TRIMS_SHEET, AtlasIds.ARMOR_TRIMS, false), new AtlasConfig(Sheets.BANNER_SHEET, AtlasIds.BANNER_PATTERNS, false), new AtlasConfig(Sheets.BED_SHEET, AtlasIds.BEDS, false), new AtlasConfig(TextureAtlas.LOCATION_BLOCKS, AtlasIds.BLOCKS, true), new AtlasConfig(Sheets.CHEST_SHEET, AtlasIds.CHESTS, false), new AtlasConfig(Sheets.DECORATED_POT_SHEET, AtlasIds.DECORATED_POT, false), new AtlasConfig(Sheets.GUI_SHEET, AtlasIds.GUI, false, Set.of(GuiMetadataSection.TYPE)), new AtlasConfig(Sheets.MAP_DECORATIONS_SHEET, AtlasIds.MAP_DECORATIONS, false), new AtlasConfig(Sheets.PAINTINGS_SHEET, AtlasIds.PAINTINGS, false), new AtlasConfig(TextureAtlas.LOCATION_PARTICLES, AtlasIds.PARTICLES, false), new AtlasConfig(Sheets.SHIELD_SHEET, AtlasIds.SHIELD_PATTERNS, false), new AtlasConfig(Sheets.SHULKER_SHEET, AtlasIds.SHULKER_BOXES, false), new AtlasConfig(Sheets.SIGN_SHEET, AtlasIds.SIGNS, false));
      PENDING_STITCH = new PreparableReloadListener.StateKey<PendingStitchResults>();
   }

   static record PendingStitch(AtlasEntry entry, CompletableFuture<SpriteLoader.Preparations> preparations) {
      final AtlasEntry entry;
      final CompletableFuture<SpriteLoader.Preparations> preparations;

      PendingStitch(AtlasEntry var1, CompletableFuture<SpriteLoader.Preparations> var2) {
         super();
         this.entry = var1;
         this.preparations = var2;
      }

      public void joinAndUpload(Map<Material, TextureAtlasSprite> var1) {
         SpriteLoader.Preparations var2 = (SpriteLoader.Preparations)this.preparations.join();
         this.entry.atlas.upload(var2);
         var2.regions().forEach((var2x, var3) -> var1.put(new Material(this.entry.config.textureId, var2x), var3));
      }
   }

   static record AtlasEntry(TextureAtlas atlas, AtlasConfig config) implements AutoCloseable {
      final TextureAtlas atlas;
      final AtlasConfig config;

      AtlasEntry(TextureAtlas var1, AtlasConfig var2) {
         super();
         this.atlas = var1;
         this.config = var2;
      }

      public void close() {
         this.atlas.clearTextureData();
      }

      CompletableFuture<SpriteLoader.Preparations> scheduleLoad(ResourceManager var1, Executor var2, int var3) {
         return SpriteLoader.create(this.atlas).loadAndStitch(var1, this.config.definitionLocation, this.config.createMipmaps ? var3 : 0, var2, this.config.additionalMetadata);
      }
   }

   public static record AtlasConfig(ResourceLocation textureId, ResourceLocation definitionLocation, boolean createMipmaps, Set<MetadataSectionType<?>> additionalMetadata) {
      final ResourceLocation textureId;
      final ResourceLocation definitionLocation;
      final boolean createMipmaps;
      final Set<MetadataSectionType<?>> additionalMetadata;

      public AtlasConfig(ResourceLocation var1, ResourceLocation var2, boolean var3) {
         this(var1, var2, var3, Set.of());
      }

      public AtlasConfig(ResourceLocation var1, ResourceLocation var2, boolean var3, Set<MetadataSectionType<?>> var4) {
         super();
         this.textureId = var1;
         this.definitionLocation = var2;
         this.createMipmaps = var3;
         this.additionalMetadata = var4;
      }
   }

   public static class PendingStitchResults {
      final List<PendingStitch> pendingStitches;
      private final Map<ResourceLocation, CompletableFuture<SpriteLoader.Preparations>> stitchFuturesById;
      final CompletableFuture<?> allReadyToUpload;

      PendingStitchResults(List<PendingStitch> var1, Map<ResourceLocation, CompletableFuture<SpriteLoader.Preparations>> var2, CompletableFuture<?> var3) {
         super();
         this.pendingStitches = var1;
         this.stitchFuturesById = var2;
         this.allReadyToUpload = var3;
      }

      public Map<Material, TextureAtlasSprite> joinAndUpload() {
         HashMap var1 = new HashMap();
         this.pendingStitches.forEach((var1x) -> var1x.joinAndUpload(var1));
         return var1;
      }

      public CompletableFuture<SpriteLoader.Preparations> get(ResourceLocation var1) {
         return (CompletableFuture)Objects.requireNonNull((CompletableFuture)this.stitchFuturesById.get(var1));
      }
   }
}
