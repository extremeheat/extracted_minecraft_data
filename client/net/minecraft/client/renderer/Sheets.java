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
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
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
   private static final RenderType CUTOUT_ITEM_SHEET;
   private static final RenderType TRANSLUCENT_ITEM_SHEET;
   public static final SpriteMapper ITEMS_MAPPER;
   public static final SpriteMapper BLOCKS_MAPPER;
   public static final SpriteMapper BLOCK_ENTITIES_MAPPER;
   public static final SpriteMapper BANNER_MAPPER;
   public static final SpriteMapper SHIELD_MAPPER;
   public static final SpriteMapper CHEST_MAPPER;
   public static final SpriteMapper DECORATED_POT_MAPPER;
   public static final SpriteMapper BED_MAPPER;
   public static final SpriteMapper SHULKER_MAPPER;
   public static final SpriteMapper SIGN_MAPPER;
   public static final SpriteMapper HANGING_SIGN_MAPPER;
   public static final SpriteId DEFAULT_SHULKER_TEXTURE_LOCATION;
   public static final List<SpriteId> SHULKER_TEXTURE_LOCATION;
   public static final Map<WoodType, SpriteId> SIGN_SPRITES;
   public static final Map<WoodType, SpriteId> HANGING_SIGN_SPRITES;
   public static final SpriteId BANNER_BASE;
   public static final SpriteId SHIELD_BASE;
   public static final SpriteId SHIELD_BASE_NO_PATTERN;
   public static final SpriteId BANNER_PATTERN_BASE;
   public static final SpriteId SHIELD_PATTERN_BASE;
   private static final Map<Identifier, SpriteId> BANNER_SPRITES;
   private static final Map<Identifier, SpriteId> SHIELD_SPRITES;
   public static final Map<ResourceKey<DecoratedPotPattern>, SpriteId> DECORATED_POT_SPRITES;
   public static final SpriteId DECORATED_POT_BASE;
   public static final SpriteId DECORATED_POT_SIDE;
   private static final SpriteId[] BED_TEXTURES;
   public static final SpriteId ENDER_CHEST_LOCATION;
   public static final MultiblockChestResources<SpriteId> CHEST_REGULAR;
   public static final MultiblockChestResources<SpriteId> CHEST_TRAPPED;
   public static final MultiblockChestResources<SpriteId> CHEST_CHRISTMAS;
   public static final MultiblockChestResources<SpriteId> CHEST_COPPER_UNAFFECTED;
   public static final MultiblockChestResources<SpriteId> CHEST_COPPER_EXPOSED;
   public static final MultiblockChestResources<SpriteId> CHEST_COPPER_WEATHERED;
   public static final MultiblockChestResources<SpriteId> CHEST_COPPER_OXIDIZED;

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

   public static RenderType cutoutItemSheet() {
      return CUTOUT_ITEM_SHEET;
   }

   public static RenderType translucentItemSheet() {
      return TRANSLUCENT_ITEM_SHEET;
   }

   public static RenderType translucentBlockItemSheet() {
      return TRANSLUCENT_BLOCK_ITEM_SHEET;
   }

   public static SpriteId getBedSprite(final DyeColor color) {
      return BED_TEXTURES[color.getId()];
   }

   public static Identifier colorToResourceSprite(final DyeColor color) {
      return Identifier.withDefaultNamespace(color.getName());
   }

   public static SpriteId createBedSprite(final DyeColor color) {
      return BED_MAPPER.apply(colorToResourceSprite(color));
   }

   public static SpriteId getShulkerBoxSprite(final DyeColor color) {
      return (SpriteId)SHULKER_TEXTURE_LOCATION.get(color.getId());
   }

   public static Identifier colorToShulkerSprite(final DyeColor color) {
      return Identifier.withDefaultNamespace("shulker_" + color.getName());
   }

   public static SpriteId createShulkerSprite(final DyeColor color) {
      return SHULKER_MAPPER.apply(colorToShulkerSprite(color));
   }

   private static SpriteId createSignSprite(final WoodType type) {
      return SIGN_MAPPER.defaultNamespaceApply(type.name());
   }

   private static SpriteId createHangingSignSprite(final WoodType type) {
      return HANGING_SIGN_MAPPER.defaultNamespaceApply(type.name());
   }

   public static SpriteId getSignSprite(final WoodType type) {
      return (SpriteId)SIGN_SPRITES.get(type);
   }

   public static SpriteId getHangingSignSprite(final WoodType type) {
      return (SpriteId)HANGING_SIGN_SPRITES.get(type);
   }

   public static SpriteId getBannerSprite(final Holder<BannerPattern> pattern) {
      Map var10000 = BANNER_SPRITES;
      Identifier var10001 = ((BannerPattern)pattern.value()).assetId();
      SpriteMapper var10002 = BANNER_MAPPER;
      Objects.requireNonNull(var10002);
      return (SpriteId)var10000.computeIfAbsent(var10001, var10002::apply);
   }

   public static SpriteId getShieldSprite(final Holder<BannerPattern> pattern) {
      Map var10000 = SHIELD_SPRITES;
      Identifier var10001 = ((BannerPattern)pattern.value()).assetId();
      SpriteMapper var10002 = SHIELD_MAPPER;
      Objects.requireNonNull(var10002);
      return (SpriteId)var10000.computeIfAbsent(var10001, var10002::apply);
   }

   public static @Nullable SpriteId getDecoratedPotSprite(final @Nullable ResourceKey<DecoratedPotPattern> pattern) {
      return pattern == null ? null : (SpriteId)DECORATED_POT_SPRITES.get(pattern);
   }

   public static SpriteId chooseSprite(final ChestRenderState.ChestMaterialType materialType, final ChestType type) {
      SpriteId var10000;
      switch (materialType) {
         case ENDER_CHEST -> var10000 = ENDER_CHEST_LOCATION;
         case REGULAR -> var10000 = CHEST_REGULAR.select(type);
         case CHRISTMAS -> var10000 = CHEST_CHRISTMAS.select(type);
         case TRAPPED -> var10000 = CHEST_TRAPPED.select(type);
         case COPPER_UNAFFECTED -> var10000 = CHEST_COPPER_UNAFFECTED.select(type);
         case COPPER_EXPOSED -> var10000 = CHEST_COPPER_EXPOSED.select(type);
         case COPPER_WEATHERED -> var10000 = CHEST_COPPER_WEATHERED.select(type);
         case COPPER_OXIDIZED -> var10000 = CHEST_COPPER_OXIDIZED.select(type);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   static {
      ARMOR_TRIMS_SHEET_TYPE = RenderTypes.armorCutoutNoCull(ARMOR_TRIMS_SHEET);
      ARMOR_TRIMS_DECAL_SHEET_TYPE = RenderTypes.createArmorDecalCutoutNoCull(ARMOR_TRIMS_SHEET);
      CUTOUT_BLOCK_SHEET = RenderTypes.entityCutoutCull(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_BLOCK_SHEET = RenderTypes.entityTranslucentCullItemTarget(TextureAtlas.LOCATION_BLOCKS);
      CUTOUT_BLOCK_ITEM_SHEET = RenderTypes.itemCutout(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_BLOCK_ITEM_SHEET = RenderTypes.itemTranslucent(TextureAtlas.LOCATION_BLOCKS);
      CUTOUT_ITEM_SHEET = RenderTypes.itemCutout(TextureAtlas.LOCATION_ITEMS);
      TRANSLUCENT_ITEM_SHEET = RenderTypes.itemTranslucent(TextureAtlas.LOCATION_ITEMS);
      ITEMS_MAPPER = new SpriteMapper(TextureAtlas.LOCATION_ITEMS, "item");
      BLOCKS_MAPPER = new SpriteMapper(TextureAtlas.LOCATION_BLOCKS, "block");
      BLOCK_ENTITIES_MAPPER = new SpriteMapper(TextureAtlas.LOCATION_BLOCKS, "entity");
      BANNER_MAPPER = new SpriteMapper(BANNER_SHEET, "entity/banner");
      SHIELD_MAPPER = new SpriteMapper(SHIELD_SHEET, "entity/shield");
      CHEST_MAPPER = new SpriteMapper(CHEST_SHEET, "entity/chest");
      DECORATED_POT_MAPPER = new SpriteMapper(DECORATED_POT_SHEET, "entity/decorated_pot");
      BED_MAPPER = new SpriteMapper(BED_SHEET, "entity/bed");
      SHULKER_MAPPER = new SpriteMapper(SHULKER_SHEET, "entity/shulker");
      SIGN_MAPPER = new SpriteMapper(SIGN_SHEET, "entity/signs");
      HANGING_SIGN_MAPPER = new SpriteMapper(SIGN_SHEET, "entity/signs/hanging");
      DEFAULT_SHULKER_TEXTURE_LOCATION = SHULKER_MAPPER.defaultNamespaceApply("shulker");
      SHULKER_TEXTURE_LOCATION = (List)Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(Sheets::createShulkerSprite).collect(ImmutableList.toImmutableList());
      SIGN_SPRITES = (Map)WoodType.values().collect(Collectors.toMap(Function.identity(), Sheets::createSignSprite));
      HANGING_SIGN_SPRITES = (Map)WoodType.values().collect(Collectors.toMap(Function.identity(), Sheets::createHangingSignSprite));
      BANNER_BASE = BANNER_MAPPER.defaultNamespaceApply("banner_base");
      SHIELD_BASE = SHIELD_MAPPER.defaultNamespaceApply("shield_base");
      SHIELD_BASE_NO_PATTERN = SHIELD_MAPPER.defaultNamespaceApply("shield_base_nopattern");
      BANNER_PATTERN_BASE = BANNER_MAPPER.defaultNamespaceApply("base");
      SHIELD_PATTERN_BASE = SHIELD_MAPPER.defaultNamespaceApply("base");
      BANNER_SPRITES = new HashMap();
      SHIELD_SPRITES = new HashMap();
      DECORATED_POT_SPRITES = (Map)BuiltInRegistries.DECORATED_POT_PATTERN.listElements().collect(Collectors.toMap(Holder.Reference::key, (holder) -> DECORATED_POT_MAPPER.apply(((DecoratedPotPattern)holder.value()).assetId())));
      DECORATED_POT_BASE = DECORATED_POT_MAPPER.defaultNamespaceApply("decorated_pot_base");
      DECORATED_POT_SIDE = DECORATED_POT_MAPPER.defaultNamespaceApply("decorated_pot_side");
      BED_TEXTURES = (SpriteId[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(Sheets::createBedSprite).toArray((x$0) -> new SpriteId[x$0]);
      ENDER_CHEST_LOCATION = CHEST_MAPPER.defaultNamespaceApply("ender");
      MultiblockChestResources var10000 = ChestSpecialRenderer.REGULAR;
      SpriteMapper var10001 = CHEST_MAPPER;
      Objects.requireNonNull(var10001);
      CHEST_REGULAR = var10000.<SpriteId>map(var10001::apply);
      var10000 = ChestSpecialRenderer.TRAPPED;
      var10001 = CHEST_MAPPER;
      Objects.requireNonNull(var10001);
      CHEST_TRAPPED = var10000.<SpriteId>map(var10001::apply);
      var10000 = ChestSpecialRenderer.CHRISTMAS;
      var10001 = CHEST_MAPPER;
      Objects.requireNonNull(var10001);
      CHEST_CHRISTMAS = var10000.<SpriteId>map(var10001::apply);
      var10000 = ChestSpecialRenderer.COPPER_UNAFFECTED;
      var10001 = CHEST_MAPPER;
      Objects.requireNonNull(var10001);
      CHEST_COPPER_UNAFFECTED = var10000.<SpriteId>map(var10001::apply);
      var10000 = ChestSpecialRenderer.COPPER_EXPOSED;
      var10001 = CHEST_MAPPER;
      Objects.requireNonNull(var10001);
      CHEST_COPPER_EXPOSED = var10000.<SpriteId>map(var10001::apply);
      var10000 = ChestSpecialRenderer.COPPER_WEATHERED;
      var10001 = CHEST_MAPPER;
      Objects.requireNonNull(var10001);
      CHEST_COPPER_WEATHERED = var10000.<SpriteId>map(var10001::apply);
      var10000 = ChestSpecialRenderer.COPPER_OXIDIZED;
      var10001 = CHEST_MAPPER;
      Objects.requireNonNull(var10001);
      CHEST_COPPER_OXIDIZED = var10000.<SpriteId>map(var10001::apply);
   }
}
