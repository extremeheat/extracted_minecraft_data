package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.properties.ChestType;

public class Sheets {
   public static final Identifier SHULKER_SHEET = Identifier.withDefaultNamespace("textures/atlas/shulker_boxes.png");
   public static final Identifier BANNER_SHEET = Identifier.withDefaultNamespace("textures/atlas/banner_patterns.png");
   public static final Identifier SHIELD_SHEET = Identifier.withDefaultNamespace("textures/atlas/shield_patterns.png");
   public static final Identifier CHEST_SHEET = Identifier.withDefaultNamespace("textures/atlas/chest.png");
   public static final Identifier DECORATED_POT_SHEET = Identifier.withDefaultNamespace("textures/atlas/decorated_pot.png");
   public static final Identifier GUI_SHEET = Identifier.withDefaultNamespace("textures/atlas/gui.png");
   public static final Identifier MAP_DECORATIONS_SHEET = Identifier.withDefaultNamespace("textures/atlas/map_decorations.png");
   public static final Identifier PAINTINGS_SHEET = Identifier.withDefaultNamespace("textures/atlas/paintings.png");
   public static final Identifier CELESTIAL_SHEET = Identifier.withDefaultNamespace("textures/atlas/celestials.png");
   private static final RenderType CUTOUT_BLOCK_ITEM_SHEET;
   private static final RenderType CUTOUT_BLOCK_ITEM_GLINT_SHEET;
   private static final RenderType CUTOUT_BLOCK_ITEM_GLINT_SPECIAL_SHEET;
   private static final RenderType TRANSLUCENT_BLOCK_ITEM_SHEET;
   private static final RenderType TRANSLUCENT_BLOCK_ITEM_GLINT_SHEET;
   private static final RenderType TRANSLUCENT_BLOCK_ITEM_GLINT_SPECIAL_SHEET;
   private static final RenderType CUTOUT_ITEM_SHEET;
   private static final RenderType CUTOUT_ITEM_GLINT_SHEET;
   private static final RenderType CUTOUT_ITEM_GLINT_SPECIAL_SHEET;
   private static final RenderType TRANSLUCENT_ITEM_SHEET;
   private static final RenderType TRANSLUCENT_ITEM_GLINT_SHEET;
   private static final RenderType TRANSLUCENT_ITEM_GLINT_SPECIAL_SHEET;
   public static final SpriteMapper ITEMS_MAPPER;
   public static final SpriteMapper BLOCKS_MAPPER;
   public static final SpriteMapper BLOCK_ENTITIES_MAPPER;
   public static final SpriteMapper BANNER_MAPPER;
   public static final SpriteMapper SHIELD_MAPPER;
   public static final SpriteMapper CHEST_MAPPER;
   public static final SpriteMapper DECORATED_POT_MAPPER;
   public static final SpriteMapper SHULKER_MAPPER;
   public static final SpriteId DEFAULT_SHULKER_TEXTURE_LOCATION;
   public static final List<SpriteId> SHULKER_TEXTURE_LOCATION;
   public static final SpriteId BANNER_BASE;
   public static final SpriteId SHIELD_BASE;
   public static final SpriteId SHIELD_BASE_NO_PATTERN;
   public static final SpriteId BANNER_PATTERN_BASE;
   public static final SpriteId SHIELD_PATTERN_BASE;
   private static final Map<Identifier, SpriteId> BANNER_SPRITES;
   private static final Map<Identifier, SpriteId> SHIELD_SPRITES;
   public static final SpriteId DECORATED_POT_BASE;
   public static final SpriteId DECORATED_POT_SIDE;
   public static final SpriteId ENDER_CHEST_LOCATION;
   public static final MultiblockChestResources<SpriteId> CHEST_REGULAR;
   public static final MultiblockChestResources<SpriteId> CHEST_TRAPPED;
   public static final MultiblockChestResources<SpriteId> CHEST_CHRISTMAS;
   public static final WeatheringCopperCollection.ByState<MultiblockChestResources<SpriteId>> CHEST_COPPER;

   public Sheets() {
      super();
   }

   public static RenderType cutoutBlockItemSheet() {
      return CUTOUT_BLOCK_ITEM_SHEET;
   }

   public static RenderType cutoutBlockItemGlintSheet() {
      return CUTOUT_BLOCK_ITEM_GLINT_SHEET;
   }

   public static RenderType cutoutBlockItemGlintSpecialSheet() {
      return CUTOUT_BLOCK_ITEM_GLINT_SPECIAL_SHEET;
   }

   public static RenderType cutoutItemSheet() {
      return CUTOUT_ITEM_SHEET;
   }

   public static RenderType cutoutItemGlintSheet() {
      return CUTOUT_ITEM_GLINT_SHEET;
   }

   public static RenderType cutoutItemGlintSpecialSheet() {
      return CUTOUT_ITEM_GLINT_SPECIAL_SHEET;
   }

   public static RenderType translucentItemSheet() {
      return TRANSLUCENT_ITEM_SHEET;
   }

   public static RenderType translucentItemGlintSheet() {
      return TRANSLUCENT_ITEM_GLINT_SHEET;
   }

   public static RenderType translucentItemGlintSpecialSheet() {
      return TRANSLUCENT_ITEM_GLINT_SPECIAL_SHEET;
   }

   public static RenderType translucentBlockItemSheet() {
      return TRANSLUCENT_BLOCK_ITEM_SHEET;
   }

   public static RenderType translucentBlockItemGlintSheet() {
      return TRANSLUCENT_BLOCK_ITEM_GLINT_SHEET;
   }

   public static RenderType translucentBlockItemGlintSpecialSheet() {
      return TRANSLUCENT_BLOCK_ITEM_GLINT_SPECIAL_SHEET;
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

   public static SpriteId chooseSprite(final ChestRenderState.ChestMaterialType materialType, final ChestType type) {
      SpriteId var10000;
      switch (materialType) {
         case ENDER_CHEST -> var10000 = ENDER_CHEST_LOCATION;
         case REGULAR -> var10000 = CHEST_REGULAR.select(type);
         case CHRISTMAS -> var10000 = CHEST_CHRISTMAS.select(type);
         case TRAPPED -> var10000 = CHEST_TRAPPED.select(type);
         case COPPER_UNAFFECTED -> var10000 = (SpriteId)((MultiblockChestResources)CHEST_COPPER.unaffected()).select(type);
         case COPPER_EXPOSED -> var10000 = (SpriteId)((MultiblockChestResources)CHEST_COPPER.exposed()).select(type);
         case COPPER_WEATHERED -> var10000 = (SpriteId)((MultiblockChestResources)CHEST_COPPER.weathered()).select(type);
         case COPPER_OXIDIZED -> var10000 = (SpriteId)((MultiblockChestResources)CHEST_COPPER.oxidized()).select(type);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   static {
      CUTOUT_BLOCK_ITEM_SHEET = RenderTypes.itemCutout(TextureAtlas.LOCATION_BLOCKS);
      CUTOUT_BLOCK_ITEM_GLINT_SHEET = RenderTypes.itemCutoutGlint(TextureAtlas.LOCATION_BLOCKS);
      CUTOUT_BLOCK_ITEM_GLINT_SPECIAL_SHEET = RenderTypes.itemCutoutGlintSpecial(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_BLOCK_ITEM_SHEET = RenderTypes.itemTranslucent(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_BLOCK_ITEM_GLINT_SHEET = RenderTypes.itemTranslucentGlint(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_BLOCK_ITEM_GLINT_SPECIAL_SHEET = RenderTypes.itemTranslucentGlintSpecial(TextureAtlas.LOCATION_BLOCKS);
      CUTOUT_ITEM_SHEET = RenderTypes.itemCutout(TextureAtlas.LOCATION_ITEMS);
      CUTOUT_ITEM_GLINT_SHEET = RenderTypes.itemCutoutGlint(TextureAtlas.LOCATION_ITEMS);
      CUTOUT_ITEM_GLINT_SPECIAL_SHEET = RenderTypes.itemCutoutGlintSpecial(TextureAtlas.LOCATION_ITEMS);
      TRANSLUCENT_ITEM_SHEET = RenderTypes.itemTranslucent(TextureAtlas.LOCATION_ITEMS);
      TRANSLUCENT_ITEM_GLINT_SHEET = RenderTypes.itemTranslucentGlint(TextureAtlas.LOCATION_ITEMS);
      TRANSLUCENT_ITEM_GLINT_SPECIAL_SHEET = RenderTypes.itemTranslucentGlintSpecial(TextureAtlas.LOCATION_ITEMS);
      ITEMS_MAPPER = new SpriteMapper(TextureAtlas.LOCATION_ITEMS, "item");
      BLOCKS_MAPPER = new SpriteMapper(TextureAtlas.LOCATION_BLOCKS, "block");
      BLOCK_ENTITIES_MAPPER = new SpriteMapper(TextureAtlas.LOCATION_BLOCKS, "entity");
      BANNER_MAPPER = new SpriteMapper(BANNER_SHEET, "entity/banner");
      SHIELD_MAPPER = new SpriteMapper(SHIELD_SHEET, "entity/shield");
      CHEST_MAPPER = new SpriteMapper(CHEST_SHEET, "entity/chest");
      DECORATED_POT_MAPPER = new SpriteMapper(DECORATED_POT_SHEET, "entity/decorated_pot");
      SHULKER_MAPPER = new SpriteMapper(SHULKER_SHEET, "entity/shulker");
      DEFAULT_SHULKER_TEXTURE_LOCATION = SHULKER_MAPPER.defaultNamespaceApply("shulker");
      SHULKER_TEXTURE_LOCATION = (List)Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(Sheets::createShulkerSprite).collect(ImmutableList.toImmutableList());
      BANNER_BASE = BANNER_MAPPER.defaultNamespaceApply("banner_base");
      SHIELD_BASE = SHIELD_MAPPER.defaultNamespaceApply("shield_base");
      SHIELD_BASE_NO_PATTERN = SHIELD_MAPPER.defaultNamespaceApply("shield_base_nopattern");
      BANNER_PATTERN_BASE = BANNER_MAPPER.defaultNamespaceApply("base");
      SHIELD_PATTERN_BASE = SHIELD_MAPPER.defaultNamespaceApply("base");
      BANNER_SPRITES = new HashMap();
      SHIELD_SPRITES = new HashMap();
      DECORATED_POT_BASE = DECORATED_POT_MAPPER.defaultNamespaceApply("decorated_pot_base");
      DECORATED_POT_SIDE = DECORATED_POT_MAPPER.defaultNamespaceApply("decorated_pot_side");
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
      CHEST_COPPER = ChestSpecialRenderer.COPPER.<MultiblockChestResources<SpriteId>>map((r) -> {
         SpriteMapper var10001 = CHEST_MAPPER;
         Objects.requireNonNull(var10001);
         return r.map(var10001::apply);
      });
   }
}
