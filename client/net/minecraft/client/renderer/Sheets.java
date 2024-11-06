package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
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
      return createBedMaterial(colorToResourceMaterial(var0));
   }

   public static Material createBedMaterial(ResourceLocation var0) {
      return new Material(BED_SHEET, var0.withPrefix("entity/bed/"));
   }

   public static Material getShulkerBoxMaterial(DyeColor var0) {
      return (Material)SHULKER_TEXTURE_LOCATION.get(var0.getId());
   }

   public static ResourceLocation colorToShulkerMaterial(DyeColor var0) {
      return ResourceLocation.withDefaultNamespace("shulker_" + var0.getName());
   }

   public static Material createShulkerMaterial(DyeColor var0) {
      return createShulkerMaterial(colorToShulkerMaterial(var0));
   }

   public static Material createShulkerMaterial(ResourceLocation var0) {
      return new Material(SHULKER_SHEET, var0.withPrefix("entity/shulker/"));
   }

   private static Material createSignMaterial(WoodType var0) {
      return new Material(SIGN_SHEET, ResourceLocation.withDefaultNamespace("entity/signs/" + var0.name()));
   }

   private static Material createHangingSignMaterial(WoodType var0) {
      return new Material(SIGN_SHEET, ResourceLocation.withDefaultNamespace("entity/signs/hanging/" + var0.name()));
   }

   public static Material getSignMaterial(WoodType var0) {
      return (Material)SIGN_MATERIALS.get(var0);
   }

   public static Material getHangingSignMaterial(WoodType var0) {
      return (Material)HANGING_SIGN_MATERIALS.get(var0);
   }

   public static Material getBannerMaterial(Holder<BannerPattern> var0) {
      return (Material)BANNER_MATERIALS.computeIfAbsent(((BannerPattern)var0.value()).assetId(), (var0x) -> {
         ResourceLocation var1 = var0x.withPrefix("entity/banner/");
         return new Material(BANNER_SHEET, var1);
      });
   }

   public static Material getShieldMaterial(Holder<BannerPattern> var0) {
      return (Material)SHIELD_MATERIALS.computeIfAbsent(((BannerPattern)var0.value()).assetId(), (var0x) -> {
         ResourceLocation var1 = var0x.withPrefix("entity/shield/");
         return new Material(SHIELD_SHEET, var1);
      });
   }

   private static Material chestMaterial(String var0) {
      return new Material(CHEST_SHEET, ResourceLocation.withDefaultNamespace("entity/chest/" + var0));
   }

   public static Material chestMaterial(ResourceLocation var0) {
      return new Material(CHEST_SHEET, var0.withPrefix("entity/chest/"));
   }

   private static Material createDecoratedPotMaterial(ResourceLocation var0) {
      return new Material(DECORATED_POT_SHEET, var0.withPrefix("entity/decorated_pot/"));
   }

   @Nullable
   public static Material getDecoratedPotMaterial(@Nullable ResourceKey<DecoratedPotPattern> var0) {
      return var0 == null ? null : (Material)DECORATED_POT_MATERIALS.get(var0);
   }

   public static Material chooseMaterial(BlockEntity var0, ChestType var1, boolean var2) {
      if (var0 instanceof EnderChestBlockEntity) {
         return ENDER_CHEST_LOCATION;
      } else if (var2) {
         return chooseMaterial(var1, CHEST_XMAS_LOCATION, CHEST_XMAS_LOCATION_LEFT, CHEST_XMAS_LOCATION_RIGHT);
      } else {
         return var0 instanceof TrappedChestBlockEntity ? chooseMaterial(var1, CHEST_TRAP_LOCATION, CHEST_TRAP_LOCATION_LEFT, CHEST_TRAP_LOCATION_RIGHT) : chooseMaterial(var1, CHEST_LOCATION, CHEST_LOCATION_LEFT, CHEST_LOCATION_RIGHT);
      }
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
      DEFAULT_SHULKER_TEXTURE_LOCATION = createShulkerMaterial(ResourceLocation.withDefaultNamespace("shulker"));
      SHULKER_TEXTURE_LOCATION = (List)Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(Sheets::createShulkerMaterial).collect(ImmutableList.toImmutableList());
      SIGN_MATERIALS = (Map)WoodType.values().collect(Collectors.toMap(Function.identity(), Sheets::createSignMaterial));
      HANGING_SIGN_MATERIALS = (Map)WoodType.values().collect(Collectors.toMap(Function.identity(), Sheets::createHangingSignMaterial));
      BANNER_BASE = new Material(BANNER_SHEET, ResourceLocation.withDefaultNamespace("entity/banner/base"));
      SHIELD_BASE = new Material(SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield/base"));
      BANNER_MATERIALS = new HashMap();
      SHIELD_MATERIALS = new HashMap();
      DECORATED_POT_MATERIALS = (Map)BuiltInRegistries.DECORATED_POT_PATTERN.listElements().collect(Collectors.toMap(Holder.Reference::key, (var0) -> {
         return createDecoratedPotMaterial(((DecoratedPotPattern)var0.value()).assetId());
      }));
      DECORATED_POT_BASE = createDecoratedPotMaterial(ResourceLocation.withDefaultNamespace("decorated_pot_base"));
      DECORATED_POT_SIDE = createDecoratedPotMaterial(ResourceLocation.withDefaultNamespace("decorated_pot_side"));
      BED_TEXTURES = (Material[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(Sheets::createBedMaterial).toArray((var0) -> {
         return new Material[var0];
      });
      CHEST_TRAP_LOCATION = chestMaterial("trapped");
      CHEST_TRAP_LOCATION_LEFT = chestMaterial("trapped_left");
      CHEST_TRAP_LOCATION_RIGHT = chestMaterial("trapped_right");
      CHEST_XMAS_LOCATION = chestMaterial("christmas");
      CHEST_XMAS_LOCATION_LEFT = chestMaterial("christmas_left");
      CHEST_XMAS_LOCATION_RIGHT = chestMaterial("christmas_right");
      CHEST_LOCATION = chestMaterial("normal");
      CHEST_LOCATION_LEFT = chestMaterial("normal_left");
      CHEST_LOCATION_RIGHT = chestMaterial("normal_right");
      ENDER_CHEST_LOCATION = chestMaterial("ender");
   }
}
