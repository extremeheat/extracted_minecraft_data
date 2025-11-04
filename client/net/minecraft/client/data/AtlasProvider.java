package net.minecraft.client.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;

public class AtlasProvider implements DataProvider {
   private static final Identifier TRIM_PALETTE_KEY = Identifier.withDefaultNamespace("trims/color_palettes/trim_palette");
   private static final Map<String, Identifier> TRIM_PALETTE_VALUES = (Map)extractAllMaterialAssets().collect(Collectors.toMap(MaterialAssetGroup.AssetInfo::suffix, (var0) -> Identifier.withDefaultNamespace("trims/color_palettes/" + var0.suffix())));
   private static final List<ResourceKey<TrimPattern>> VANILLA_PATTERNS;
   private static final List<EquipmentClientInfo.LayerType> HUMANOID_LAYERS;
   private final PackOutput.PathProvider pathProvider;

   public AtlasProvider(PackOutput var1) {
      super();
      this.pathProvider = var1.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
   }

   private static List<Identifier> patternTextures() {
      ArrayList var0 = new ArrayList(VANILLA_PATTERNS.size() * HUMANOID_LAYERS.size());

      for(ResourceKey var2 : VANILLA_PATTERNS) {
         Identifier var3 = TrimPatterns.defaultAssetId(var2);

         for(EquipmentClientInfo.LayerType var5 : HUMANOID_LAYERS) {
            var0.add(var3.withPath((UnaryOperator)((var1) -> {
               String var10000 = var5.trimAssetPrefix();
               return var10000 + "/" + var1;
            })));
         }
      }

      return var0;
   }

   private static SpriteSource forMaterial(Material var0) {
      return new SingleFile(var0.texture());
   }

   private static SpriteSource forMapper(MaterialMapper var0) {
      return new DirectoryLister(var0.prefix(), var0.prefix() + "/");
   }

   private static List<SpriteSource> simpleMapper(MaterialMapper var0) {
      return List.of(forMapper(var0));
   }

   private static List<SpriteSource> noPrefixMapper(String var0) {
      return List.of(new DirectoryLister(var0, ""));
   }

   private static Stream<MaterialAssetGroup.AssetInfo> extractAllMaterialAssets() {
      return ItemModelGenerators.TRIM_MATERIAL_MODELS.stream().map(ItemModelGenerators.TrimMaterialData::assets).flatMap((var0) -> Stream.concat(Stream.of(var0.base()), var0.overrides().values().stream())).sorted(Comparator.comparing(MaterialAssetGroup.AssetInfo::suffix));
   }

   private static List<SpriteSource> armorTrims() {
      return List.of(new PalettedPermutations(patternTextures(), TRIM_PALETTE_KEY, TRIM_PALETTE_VALUES));
   }

   private static List<SpriteSource> blocksList() {
      return List.of(forMapper(Sheets.BLOCKS_MAPPER), forMapper(ConduitRenderer.MAPPER), forMaterial(BellRenderer.BELL_RESOURCE_LOCATION), forMaterial(Sheets.DECORATED_POT_SIDE), forMaterial(EnchantTableRenderer.BOOK_LOCATION));
   }

   private static List<SpriteSource> itemsList() {
      return List.of(forMapper(Sheets.ITEMS_MAPPER), new PalettedPermutations(List.of(ItemModelGenerators.TRIM_PREFIX_HELMET, ItemModelGenerators.TRIM_PREFIX_CHESTPLATE, ItemModelGenerators.TRIM_PREFIX_LEGGINGS, ItemModelGenerators.TRIM_PREFIX_BOOTS), TRIM_PALETTE_KEY, TRIM_PALETTE_VALUES));
   }

   private static List<SpriteSource> bannerPatterns() {
      return List.of(forMaterial(ModelBakery.BANNER_BASE), forMapper(Sheets.BANNER_MAPPER));
   }

   private static List<SpriteSource> shieldPatterns() {
      return List.of(forMaterial(ModelBakery.SHIELD_BASE), forMaterial(ModelBakery.NO_PATTERN_SHIELD), forMapper(Sheets.SHIELD_MAPPER));
   }

   private static List<SpriteSource> guiSprites() {
      return List.of(new DirectoryLister("gui/sprites", ""), new DirectoryLister("mob_effect", "mob_effect/"));
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      return CompletableFuture.allOf(this.storeAtlas(var1, AtlasIds.ARMOR_TRIMS, armorTrims()), this.storeAtlas(var1, AtlasIds.BANNER_PATTERNS, bannerPatterns()), this.storeAtlas(var1, AtlasIds.BEDS, simpleMapper(Sheets.BED_MAPPER)), this.storeAtlas(var1, AtlasIds.BLOCKS, blocksList()), this.storeAtlas(var1, AtlasIds.ITEMS, itemsList()), this.storeAtlas(var1, AtlasIds.CHESTS, simpleMapper(Sheets.CHEST_MAPPER)), this.storeAtlas(var1, AtlasIds.DECORATED_POT, simpleMapper(Sheets.DECORATED_POT_MAPPER)), this.storeAtlas(var1, AtlasIds.GUI, guiSprites()), this.storeAtlas(var1, AtlasIds.MAP_DECORATIONS, noPrefixMapper("map/decorations")), this.storeAtlas(var1, AtlasIds.PAINTINGS, noPrefixMapper("painting")), this.storeAtlas(var1, AtlasIds.PARTICLES, noPrefixMapper("particle")), this.storeAtlas(var1, AtlasIds.SHIELD_PATTERNS, shieldPatterns()), this.storeAtlas(var1, AtlasIds.SHULKER_BOXES, simpleMapper(Sheets.SHULKER_MAPPER)), this.storeAtlas(var1, AtlasIds.SIGNS, simpleMapper(Sheets.SIGN_MAPPER)), this.storeAtlas(var1, AtlasIds.CELESTIALS, noPrefixMapper("environment/celestial")));
   }

   private CompletableFuture<?> storeAtlas(CachedOutput var1, Identifier var2, List<SpriteSource> var3) {
      return DataProvider.saveStable(var1, SpriteSources.FILE_CODEC, var3, this.pathProvider.json(var2));
   }

   public String getName() {
      return "Atlas Definitions";
   }

   static {
      VANILLA_PATTERNS = List.of(TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD, TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB, TrimPatterns.SPIRE, TrimPatterns.WAYFINDER, TrimPatterns.SHAPER, TrimPatterns.SILENCE, TrimPatterns.RAISER, TrimPatterns.HOST, TrimPatterns.FLOW, TrimPatterns.BOLT);
      HUMANOID_LAYERS = List.of(EquipmentClientInfo.LayerType.HUMANOID, EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS);
   }
}
