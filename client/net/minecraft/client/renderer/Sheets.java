package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jspecify.annotations.Nullable;

public class Sheets {
   public static final Identifier SHULKER_SHEET = Identifier.withDefaultNamespace("textures/atlas/shulker_boxes.png");
   public static final Identifier BED_SHEET = Identifier.withDefaultNamespace("textures/atlas/beds.png");
   public static final Identifier BANNER_SHEET = Identifier.withDefaultNamespace("textures/atlas/banner_patterns.png");
   public static final Identifier SHIELD_SHEET = Identifier.withDefaultNamespace("textures/atlas/shield_patterns.png");
   public static final Identifier SIGN_SHEET = Identifier.withDefaultNamespace("textures/atlas/signs.png");
   public static final Identifier CHEST_SHEET = Identifier.withDefaultNamespace("textures/atlas/chest.png");
   public static final Identifier ARMOR_TRIMS_SHEET = Identifier.withDefaultNamespace("textures/atlas/armor_trims.png");
   public static final Identifier DECORATED_POT_SHEET = Identifier.withDefaultNamespace("textures/atlas/decorated_pot.png");
   public static final Identifier GUI_SHEET = Identifier.withDefaultNamespace("textures/atlas/gui.png");
   public static final Identifier MAP_DECORATIONS_SHEET = Identifier.withDefaultNamespace("textures/atlas/map_decorations.png");
   public static final Identifier PAINTINGS_SHEET = Identifier.withDefaultNamespace("textures/atlas/paintings.png");
   public static final Identifier CELESTIAL_SHEET = Identifier.withDefaultNamespace("textures/atlas/celestials.png");
   private static final RenderType ARMOR_TRIMS_SHEET_TYPE;
   private static final RenderType ARMOR_TRIMS_DECAL_SHEET_TYPE;
   private static final RenderType CUTOUT_BLOCK_SHEET;
   private static final RenderType TRANSLUCENT_BLOCK_SHEET;
   private static final RenderType CUTOUT_BLOCK_ITEM_SHEET;
   private static final RenderType TRANSLUCENT_BLOCK_ITEM_SHEET;
   private static final RenderType TRANSLUCENT_ITEM_SHEET;
   public static final MaterialMapper ITEMS_MAPPER;
   public static final MaterialMapper BLOCKS_MAPPER;
   public static final MaterialMapper BLOCK_ENTITIES_MAPPER;
   public static final MaterialMapper BANNER_MAPPER;
   public static final MaterialMapper SHIELD_MAPPER;
   public static final MaterialMapper CHEST_MAPPER;
   public static final MaterialMapper DECORATED_POT_MAPPER;
   public static final MaterialMapper BED_MAPPER;
   public static final MaterialMapper SHULKER_MAPPER;
   public static final MaterialMapper SIGN_MAPPER;
   public static final MaterialMapper HANGING_SIGN_MAPPER;
   public static final Material DEFAULT_SHULKER_TEXTURE_LOCATION;
   public static final List<Material> SHULKER_TEXTURE_LOCATION;
   public static final Map<WoodType, Material> SIGN_MATERIALS;
   public static final Map<WoodType, Material> HANGING_SIGN_MATERIALS;
   public static final Material BANNER_BASE;
   public static final Material SHIELD_BASE;
   private static final Map<Identifier, Material> BANNER_MATERIALS;
   private static final Map<Identifier, Material> SHIELD_MATERIALS;
   public static final Map<ResourceKey<DecoratedPotPattern>, Material> DECORATED_POT_MATERIALS;
   public static final Material DECORATED_POT_BASE;
   public static final Material DECORATED_POT_SIDE;
   private static final Material[] BED_TEXTURES;
   public static final Material CHEST_TRAP_LOCATION;
   public static final Material CHEST_TRAP_LOCATION_LEFT;
   public static final Material CHEST_TRAP_LOCATION_RIGHT;
   public static final Material CHEST_XMAS_LOCATION;
   public static final Material CHEST_XMAS_LOCATION_LEFT;
   public static final Material CHEST_XMAS_LOCATION_RIGHT;
   public static final Material CHEST_LOCATION;
   public static final Material CHEST_LOCATION_LEFT;
   public static final Material CHEST_LOCATION_RIGHT;
   public static final Material ENDER_CHEST_LOCATION;
   public static final Material COPPER_CHEST_LOCATION;
   public static final Material COPPER_CHEST_LOCATION_LEFT;
   public static final Material COPPER_CHEST_LOCATION_RIGHT;
   public static final Material EXPOSED_COPPER_CHEST_LOCATION;
   public static final Material EXPOSED_COPPER_CHEST_LOCATION_LEFT;
   public static final Material EXPOSED_COPPER_CHEST_LOCATION_RIGHT;
   public static final Material WEATHERED_COPPER_CHEST_LOCATION;
   public static final Material WEATHERED_COPPER_CHEST_LOCATION_LEFT;
   public static final Material WEATHERED_COPPER_CHEST_LOCATION_RIGHT;
   public static final Material OXIDIZED_COPPER_CHEST_LOCATION;
   public static final Material OXIDIZED_COPPER_CHEST_LOCATION_LEFT;
   public static final Material OXIDIZED_COPPER_CHEST_LOCATION_RIGHT;

   public Sheets() {
      super();
   }

   public static RenderType armorTrimsSheet(final boolean decal) {
      return decal ? ARMOR_TRIMS_DECAL_SHEET_TYPE : ARMOR_TRIMS_SHEET_TYPE;
   }

   public static RenderType cutoutBlockSheet() {
      return CUTOUT_BLOCK_SHEET;
   }

   public static RenderType translucentBlockSheet() {
      return TRANSLUCENT_BLOCK_SHEET;
   }

   public static RenderType cutoutBlockItemSheet() {
      return CUTOUT_BLOCK_ITEM_SHEET;
   }

   public static RenderType translucentItemSheet() {
      return TRANSLUCENT_ITEM_SHEET;
   }

   public static RenderType translucentBlockItemSheet() {
      return TRANSLUCENT_BLOCK_ITEM_SHEET;
   }

   public static Material getBedMaterial(final DyeColor color) {
      return BED_TEXTURES[color.getId()];
   }

   public static Identifier colorToResourceMaterial(final DyeColor color) {
      return Identifier.withDefaultNamespace(color.getName());
   }

   public static Material createBedMaterial(final DyeColor color) {
      return BED_MAPPER.apply(colorToResourceMaterial(color));
   }

   public static Material getShulkerBoxMaterial(final DyeColor color) {
      return (Material)SHULKER_TEXTURE_LOCATION.get(color.getId());
   }

   public static Identifier colorToShulkerMaterial(final DyeColor color) {
      return Identifier.withDefaultNamespace("shulker_" + color.getName());
   }

   public static Material createShulkerMaterial(final DyeColor color) {
      return SHULKER_MAPPER.apply(colorToShulkerMaterial(color));
   }

   private static Material createSignMaterial(final WoodType type) {
      return SIGN_MAPPER.defaultNamespaceApply(type.name());
   }

   private static Material createHangingSignMaterial(final WoodType type) {
      return HANGING_SIGN_MAPPER.defaultNamespaceApply(type.name());
   }

   public static Material getSignMaterial(final WoodType type) {
      return (Material)SIGN_MATERIALS.get(type);
   }

   public static Material getHangingSignMaterial(final WoodType type) {
      return (Material)HANGING_SIGN_MATERIALS.get(type);
   }

   public static Material getBannerMaterial(final Holder<BannerPattern> pattern) {
      Map var10000 = BANNER_MATERIALS;
      Identifier var10001 = ((BannerPattern)pattern.value()).assetId();
      MaterialMapper var10002 = BANNER_MAPPER;
      Objects.requireNonNull(var10002);
      return (Material)var10000.computeIfAbsent(var10001, var10002::apply);
   }

   public static Material getShieldMaterial(final Holder<BannerPattern> pattern) {
      Map var10000 = SHIELD_MATERIALS;
      Identifier var10001 = ((BannerPattern)pattern.value()).assetId();
      MaterialMapper var10002 = SHIELD_MAPPER;
      Objects.requireNonNull(var10002);
      return (Material)var10000.computeIfAbsent(var10001, var10002::apply);
   }

   public static @Nullable Material getDecoratedPotMaterial(final @Nullable ResourceKey<DecoratedPotPattern> pattern) {
      return pattern == null ? null : (Material)DECORATED_POT_MATERIALS.get(pattern);
   }

   public static Material chooseMaterial(final ChestRenderState.ChestMaterialType materialType, final ChestType type) {
      Material var10000;
      switch (materialType) {
         case ENDER_CHEST -> var10000 = ENDER_CHEST_LOCATION;
         case CHRISTMAS -> var10000 = chooseMaterial(type, CHEST_XMAS_LOCATION, CHEST_XMAS_LOCATION_LEFT, CHEST_XMAS_LOCATION_RIGHT);
         case TRAPPED -> var10000 = chooseMaterial(type, CHEST_TRAP_LOCATION, CHEST_TRAP_LOCATION_LEFT, CHEST_TRAP_LOCATION_RIGHT);
         case COPPER_UNAFFECTED -> var10000 = chooseMaterial(type, COPPER_CHEST_LOCATION, COPPER_CHEST_LOCATION_LEFT, COPPER_CHEST_LOCATION_RIGHT);
         case COPPER_EXPOSED -> var10000 = chooseMaterial(type, EXPOSED_COPPER_CHEST_LOCATION, EXPOSED_COPPER_CHEST_LOCATION_LEFT, EXPOSED_COPPER_CHEST_LOCATION_RIGHT);
         case COPPER_WEATHERED -> var10000 = chooseMaterial(type, WEATHERED_COPPER_CHEST_LOCATION, WEATHERED_COPPER_CHEST_LOCATION_LEFT, WEATHERED_COPPER_CHEST_LOCATION_RIGHT);
         case COPPER_OXIDIZED -> var10000 = chooseMaterial(type, OXIDIZED_COPPER_CHEST_LOCATION, OXIDIZED_COPPER_CHEST_LOCATION_LEFT, OXIDIZED_COPPER_CHEST_LOCATION_RIGHT);
         case REGULAR -> var10000 = chooseMaterial(type, CHEST_LOCATION, CHEST_LOCATION_LEFT, CHEST_LOCATION_RIGHT);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static Material chooseMaterial(final ChestType type, final Material single, final Material left, final Material right) {
      switch (type) {
         case LEFT:
            return left;
         case RIGHT:
            return right;
         case SINGLE:
         default:
            return single;
      }
   }

   static {
      ARMOR_TRIMS_SHEET_TYPE = RenderTypes.armorCutoutNoCull(ARMOR_TRIMS_SHEET);
      ARMOR_TRIMS_DECAL_SHEET_TYPE = RenderTypes.createArmorDecalCutoutNoCull(ARMOR_TRIMS_SHEET);
      CUTOUT_BLOCK_SHEET = RenderTypes.entityCutoutCull(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_BLOCK_SHEET = RenderTypes.entityTranslucentCullItemTarget(TextureAtlas.LOCATION_BLOCKS);
      CUTOUT_BLOCK_ITEM_SHEET = RenderTypes.itemCutout(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_BLOCK_ITEM_SHEET = RenderTypes.itemTranslucent(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_ITEM_SHEET = RenderTypes.itemTranslucent(TextureAtlas.LOCATION_ITEMS);
      ITEMS_MAPPER = new MaterialMapper(TextureAtlas.LOCATION_ITEMS, "item");
      BLOCKS_MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "block");
      BLOCK_ENTITIES_MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "entity");
      BANNER_MAPPER = new MaterialMapper(BANNER_SHEET, "entity/banner");
      SHIELD_MAPPER = new MaterialMapper(SHIELD_SHEET, "entity/shield");
      CHEST_MAPPER = new MaterialMapper(CHEST_SHEET, "entity/chest");
      DECORATED_POT_MAPPER = new MaterialMapper(DECORATED_POT_SHEET, "entity/decorated_pot");
      BED_MAPPER = new MaterialMapper(BED_SHEET, "entity/bed");
      SHULKER_MAPPER = new MaterialMapper(SHULKER_SHEET, "entity/shulker");
      SIGN_MAPPER = new MaterialMapper(SIGN_SHEET, "entity/signs");
      HANGING_SIGN_MAPPER = new MaterialMapper(SIGN_SHEET, "entity/signs/hanging");
      DEFAULT_SHULKER_TEXTURE_LOCATION = SHULKER_MAPPER.defaultNamespaceApply("shulker");
      SHULKER_TEXTURE_LOCATION = (List)Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(Sheets::createShulkerMaterial).collect(ImmutableList.toImmutableList());
      SIGN_MATERIALS = (Map)WoodType.values().collect(Collectors.toMap(Function.identity(), Sheets::createSignMaterial));
      HANGING_SIGN_MATERIALS = (Map)WoodType.values().collect(Collectors.toMap(Function.identity(), Sheets::createHangingSignMaterial));
      BANNER_BASE = BANNER_MAPPER.defaultNamespaceApply("base");
      SHIELD_BASE = SHIELD_MAPPER.defaultNamespaceApply("base");
      BANNER_MATERIALS = new HashMap();
      SHIELD_MATERIALS = new HashMap();
      DECORATED_POT_MATERIALS = (Map)BuiltInRegistries.DECORATED_POT_PATTERN.listElements().collect(Collectors.toMap(Holder.Reference::key, (holder) -> DECORATED_POT_MAPPER.apply(((DecoratedPotPattern)holder.value()).assetId())));
      DECORATED_POT_BASE = DECORATED_POT_MAPPER.defaultNamespaceApply("decorated_pot_base");
      DECORATED_POT_SIDE = DECORATED_POT_MAPPER.defaultNamespaceApply("decorated_pot_side");
      BED_TEXTURES = (Material[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(Sheets::createBedMaterial).toArray((x$0) -> new Material[x$0]);
      CHEST_TRAP_LOCATION = CHEST_MAPPER.defaultNamespaceApply("trapped");
      CHEST_TRAP_LOCATION_LEFT = CHEST_MAPPER.defaultNamespaceApply("trapped_left");
      CHEST_TRAP_LOCATION_RIGHT = CHEST_MAPPER.defaultNamespaceApply("trapped_right");
      CHEST_XMAS_LOCATION = CHEST_MAPPER.defaultNamespaceApply("christmas");
      CHEST_XMAS_LOCATION_LEFT = CHEST_MAPPER.defaultNamespaceApply("christmas_left");
      CHEST_XMAS_LOCATION_RIGHT = CHEST_MAPPER.defaultNamespaceApply("christmas_right");
      CHEST_LOCATION = CHEST_MAPPER.defaultNamespaceApply("normal");
      CHEST_LOCATION_LEFT = CHEST_MAPPER.defaultNamespaceApply("normal_left");
      CHEST_LOCATION_RIGHT = CHEST_MAPPER.defaultNamespaceApply("normal_right");
      ENDER_CHEST_LOCATION = CHEST_MAPPER.defaultNamespaceApply("ender");
      COPPER_CHEST_LOCATION = CHEST_MAPPER.defaultNamespaceApply("copper");
      COPPER_CHEST_LOCATION_LEFT = CHEST_MAPPER.defaultNamespaceApply("copper_left");
      COPPER_CHEST_LOCATION_RIGHT = CHEST_MAPPER.defaultNamespaceApply("copper_right");
      EXPOSED_COPPER_CHEST_LOCATION = CHEST_MAPPER.defaultNamespaceApply("copper_exposed");
      EXPOSED_COPPER_CHEST_LOCATION_LEFT = CHEST_MAPPER.defaultNamespaceApply("copper_exposed_left");
      EXPOSED_COPPER_CHEST_LOCATION_RIGHT = CHEST_MAPPER.defaultNamespaceApply("copper_exposed_right");
      WEATHERED_COPPER_CHEST_LOCATION = CHEST_MAPPER.defaultNamespaceApply("copper_weathered");
      WEATHERED_COPPER_CHEST_LOCATION_LEFT = CHEST_MAPPER.defaultNamespaceApply("copper_weathered_left");
      WEATHERED_COPPER_CHEST_LOCATION_RIGHT = CHEST_MAPPER.defaultNamespaceApply("copper_weathered_right");
      OXIDIZED_COPPER_CHEST_LOCATION = CHEST_MAPPER.defaultNamespaceApply("copper_oxidized");
      OXIDIZED_COPPER_CHEST_LOCATION_LEFT = CHEST_MAPPER.defaultNamespaceApply("copper_oxidized_left");
      OXIDIZED_COPPER_CHEST_LOCATION_RIGHT = CHEST_MAPPER.defaultNamespaceApply("copper_oxidized_right");
   }
}
