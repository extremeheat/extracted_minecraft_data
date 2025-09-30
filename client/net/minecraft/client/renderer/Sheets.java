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
import javax.annotation.Nullable;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class Sheets {
   public static final ResourceLocation SHULKER_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/shulker_boxes.png");
   public static final ResourceLocation BED_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/beds.png");
   public static final ResourceLocation BANNER_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/banner_patterns.png");
   public static final ResourceLocation SHIELD_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/shield_patterns.png");
   public static final ResourceLocation SIGN_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/signs.png");
   public static final ResourceLocation CHEST_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/chest.png");
   public static final ResourceLocation ARMOR_TRIMS_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/armor_trims.png");
   public static final ResourceLocation DECORATED_POT_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/decorated_pot.png");
   public static final ResourceLocation GUI_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/gui.png");
   public static final ResourceLocation MAP_DECORATIONS_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/map_decorations.png");
   public static final ResourceLocation PAINTINGS_SHEET = ResourceLocation.withDefaultNamespace("textures/atlas/paintings.png");
   private static final RenderType SHULKER_BOX_SHEET_TYPE;
   private static final RenderType BED_SHEET_TYPE;
   private static final RenderType BANNER_SHEET_TYPE;
   private static final RenderType SHIELD_SHEET_TYPE;
   private static final RenderType SIGN_SHEET_TYPE;
   private static final RenderType CHEST_SHEET_TYPE;
   private static final RenderType ARMOR_TRIMS_SHEET_TYPE;
   private static final RenderType ARMOR_TRIMS_DECAL_SHEET_TYPE;
   private static final RenderType SOLID_BLOCK_SHEET;
   private static final RenderType CUTOUT_BLOCK_SHEET;
   private static final RenderType TRANSLUCENT_ITEM_CULL_BLOCK_SHEET;
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
   private static final Map<ResourceLocation, Material> BANNER_MATERIALS;
   private static final Map<ResourceLocation, Material> SHIELD_MATERIALS;
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

   public static RenderType bannerSheet() {
      return BANNER_SHEET_TYPE;
   }

   public static RenderType shieldSheet() {
      return SHIELD_SHEET_TYPE;
   }

   public static RenderType bedSheet() {
      return BED_SHEET_TYPE;
   }

   public static RenderType shulkerBoxSheet() {
      return SHULKER_BOX_SHEET_TYPE;
   }

   public static RenderType signSheet() {
      return SIGN_SHEET_TYPE;
   }

   public static RenderType hangingSignSheet() {
      return SIGN_SHEET_TYPE;
   }

   public static RenderType chestSheet() {
      return CHEST_SHEET_TYPE;
   }

   public static RenderType armorTrimsSheet(boolean var0) {
      return var0 ? ARMOR_TRIMS_DECAL_SHEET_TYPE : ARMOR_TRIMS_SHEET_TYPE;
   }

   public static RenderType solidBlockSheet() {
      return SOLID_BLOCK_SHEET;
   }

   public static RenderType cutoutBlockSheet() {
      return CUTOUT_BLOCK_SHEET;
   }

   public static RenderType translucentItemSheet() {
      return TRANSLUCENT_ITEM_CULL_BLOCK_SHEET;
   }

   public static Material getBedMaterial(DyeColor var0) {
      return BED_TEXTURES[var0.getId()];
   }

   public static ResourceLocation colorToResourceMaterial(DyeColor var0) {
      return ResourceLocation.withDefaultNamespace(var0.getName());
   }

   public static Material createBedMaterial(DyeColor var0) {
      return BED_MAPPER.apply(colorToResourceMaterial(var0));
   }

   public static Material getShulkerBoxMaterial(DyeColor var0) {
      return (Material)SHULKER_TEXTURE_LOCATION.get(var0.getId());
   }

   public static ResourceLocation colorToShulkerMaterial(DyeColor var0) {
      return ResourceLocation.withDefaultNamespace("shulker_" + var0.getName());
   }

   public static Material createShulkerMaterial(DyeColor var0) {
      return SHULKER_MAPPER.apply(colorToShulkerMaterial(var0));
   }

   private static Material createSignMaterial(WoodType var0) {
      return SIGN_MAPPER.defaultNamespaceApply(var0.name());
   }

   private static Material createHangingSignMaterial(WoodType var0) {
      return HANGING_SIGN_MAPPER.defaultNamespaceApply(var0.name());
   }

   public static Material getSignMaterial(WoodType var0) {
      return (Material)SIGN_MATERIALS.get(var0);
   }

   public static Material getHangingSignMaterial(WoodType var0) {
      return (Material)HANGING_SIGN_MATERIALS.get(var0);
   }

   public static Material getBannerMaterial(Holder<BannerPattern> var0) {
      Map var10000 = BANNER_MATERIALS;
      ResourceLocation var10001 = ((BannerPattern)var0.value()).assetId();
      MaterialMapper var10002 = BANNER_MAPPER;
      Objects.requireNonNull(var10002);
      return (Material)var10000.computeIfAbsent(var10001, var10002::apply);
   }

   public static Material getShieldMaterial(Holder<BannerPattern> var0) {
      Map var10000 = SHIELD_MATERIALS;
      ResourceLocation var10001 = ((BannerPattern)var0.value()).assetId();
      MaterialMapper var10002 = SHIELD_MAPPER;
      Objects.requireNonNull(var10002);
      return (Material)var10000.computeIfAbsent(var10001, var10002::apply);
   }

   @Nullable
   public static Material getDecoratedPotMaterial(@Nullable ResourceKey<DecoratedPotPattern> var0) {
      return var0 == null ? null : (Material)DECORATED_POT_MATERIALS.get(var0);
   }

   public static Material chooseMaterial(ChestRenderState.ChestMaterialType var0, ChestType var1) {
      Material var10000;
      switch (var0) {
         case ENDER_CHEST -> var10000 = ENDER_CHEST_LOCATION;
         case CHRISTMAS -> var10000 = chooseMaterial(var1, CHEST_XMAS_LOCATION, CHEST_XMAS_LOCATION_LEFT, CHEST_XMAS_LOCATION_RIGHT);
         case TRAPPED -> var10000 = chooseMaterial(var1, CHEST_TRAP_LOCATION, CHEST_TRAP_LOCATION_LEFT, CHEST_TRAP_LOCATION_RIGHT);
         case COPPER_UNAFFECTED -> var10000 = chooseMaterial(var1, COPPER_CHEST_LOCATION, COPPER_CHEST_LOCATION_LEFT, COPPER_CHEST_LOCATION_RIGHT);
         case COPPER_EXPOSED -> var10000 = chooseMaterial(var1, EXPOSED_COPPER_CHEST_LOCATION, EXPOSED_COPPER_CHEST_LOCATION_LEFT, EXPOSED_COPPER_CHEST_LOCATION_RIGHT);
         case COPPER_WEATHERED -> var10000 = chooseMaterial(var1, WEATHERED_COPPER_CHEST_LOCATION, WEATHERED_COPPER_CHEST_LOCATION_LEFT, WEATHERED_COPPER_CHEST_LOCATION_RIGHT);
         case COPPER_OXIDIZED -> var10000 = chooseMaterial(var1, OXIDIZED_COPPER_CHEST_LOCATION, OXIDIZED_COPPER_CHEST_LOCATION_LEFT, OXIDIZED_COPPER_CHEST_LOCATION_RIGHT);
         case REGULAR -> var10000 = chooseMaterial(var1, CHEST_LOCATION, CHEST_LOCATION_LEFT, CHEST_LOCATION_RIGHT);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static Material chooseMaterial(ChestType var0, Material var1, Material var2, Material var3) {
      switch (var0) {
         case LEFT:
            return var2;
         case RIGHT:
            return var3;
         case SINGLE:
         default:
            return var1;
      }
   }

   static {
      SHULKER_BOX_SHEET_TYPE = RenderType.entityCutoutNoCull(SHULKER_SHEET);
      BED_SHEET_TYPE = RenderType.entitySolid(BED_SHEET);
      BANNER_SHEET_TYPE = RenderType.entityNoOutline(BANNER_SHEET);
      SHIELD_SHEET_TYPE = RenderType.entityNoOutline(SHIELD_SHEET);
      SIGN_SHEET_TYPE = RenderType.entityCutoutNoCull(SIGN_SHEET);
      CHEST_SHEET_TYPE = RenderType.entityCutout(CHEST_SHEET);
      ARMOR_TRIMS_SHEET_TYPE = RenderType.armorCutoutNoCull(ARMOR_TRIMS_SHEET);
      ARMOR_TRIMS_DECAL_SHEET_TYPE = RenderType.createArmorDecalCutoutNoCull(ARMOR_TRIMS_SHEET);
      SOLID_BLOCK_SHEET = RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS);
      CUTOUT_BLOCK_SHEET = RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_ITEM_CULL_BLOCK_SHEET = RenderType.itemEntityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
      ITEMS_MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "item");
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
      DECORATED_POT_MATERIALS = (Map)BuiltInRegistries.DECORATED_POT_PATTERN.listElements().collect(Collectors.toMap(Holder.Reference::key, (var0) -> DECORATED_POT_MAPPER.apply(((DecoratedPotPattern)var0.value()).assetId())));
      DECORATED_POT_BASE = DECORATED_POT_MAPPER.defaultNamespaceApply("decorated_pot_base");
      DECORATED_POT_SIDE = DECORATED_POT_MAPPER.defaultNamespaceApply("decorated_pot_side");
      BED_TEXTURES = (Material[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(Sheets::createBedMaterial).toArray((var0) -> new Material[var0]);
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
