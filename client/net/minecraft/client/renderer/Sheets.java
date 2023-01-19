package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class Sheets {
   public static final ResourceLocation SHULKER_SHEET = new ResourceLocation("textures/atlas/shulker_boxes.png");
   public static final ResourceLocation BED_SHEET = new ResourceLocation("textures/atlas/beds.png");
   public static final ResourceLocation BANNER_SHEET = new ResourceLocation("textures/atlas/banner_patterns.png");
   public static final ResourceLocation SHIELD_SHEET = new ResourceLocation("textures/atlas/shield_patterns.png");
   public static final ResourceLocation SIGN_SHEET = new ResourceLocation("textures/atlas/signs.png");
   public static final ResourceLocation CHEST_SHEET = new ResourceLocation("textures/atlas/chest.png");
   private static final RenderType SHULKER_BOX_SHEET_TYPE = RenderType.entityCutoutNoCull(SHULKER_SHEET);
   private static final RenderType BED_SHEET_TYPE = RenderType.entitySolid(BED_SHEET);
   private static final RenderType BANNER_SHEET_TYPE = RenderType.entityNoOutline(BANNER_SHEET);
   private static final RenderType SHIELD_SHEET_TYPE = RenderType.entityNoOutline(SHIELD_SHEET);
   private static final RenderType SIGN_SHEET_TYPE = RenderType.entityCutoutNoCull(SIGN_SHEET);
   private static final RenderType CHEST_SHEET_TYPE = RenderType.entityCutout(CHEST_SHEET);
   private static final RenderType SOLID_BLOCK_SHEET = RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS);
   private static final RenderType CUTOUT_BLOCK_SHEET = RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS);
   private static final RenderType TRANSLUCENT_ITEM_CULL_BLOCK_SHEET = RenderType.itemEntityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
   private static final RenderType TRANSLUCENT_CULL_BLOCK_SHEET = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
   public static final Material DEFAULT_SHULKER_TEXTURE_LOCATION = new Material(SHULKER_SHEET, new ResourceLocation("entity/shulker/shulker"));
   public static final List<Material> SHULKER_TEXTURE_LOCATION = Stream.of(
         "white",
         "orange",
         "magenta",
         "light_blue",
         "yellow",
         "lime",
         "pink",
         "gray",
         "light_gray",
         "cyan",
         "purple",
         "blue",
         "brown",
         "green",
         "red",
         "black"
      )
      .map(var0 -> new Material(SHULKER_SHEET, new ResourceLocation("entity/shulker/shulker_" + var0)))
      .collect(ImmutableList.toImmutableList());
   public static final Map<WoodType, Material> SIGN_MATERIALS = WoodType.values().collect(Collectors.toMap(Function.identity(), Sheets::createSignMaterial));
   public static final Map<ResourceKey<BannerPattern>, Material> BANNER_MATERIALS = Registry.BANNER_PATTERN
      .registryKeySet()
      .stream()
      .collect(Collectors.toMap(Function.identity(), Sheets::createBannerMaterial));
   public static final Map<ResourceKey<BannerPattern>, Material> SHIELD_MATERIALS = Registry.BANNER_PATTERN
      .registryKeySet()
      .stream()
      .collect(Collectors.toMap(Function.identity(), Sheets::createShieldMaterial));
   public static final Material[] BED_TEXTURES = Arrays.stream(DyeColor.values())
      .sorted(Comparator.comparingInt(DyeColor::getId))
      .map(var0 -> new Material(BED_SHEET, new ResourceLocation("entity/bed/" + var0.getName())))
      .toArray(var0 -> new Material[var0]);
   public static final Material CHEST_TRAP_LOCATION = chestMaterial("trapped");
   public static final Material CHEST_TRAP_LOCATION_LEFT = chestMaterial("trapped_left");
   public static final Material CHEST_TRAP_LOCATION_RIGHT = chestMaterial("trapped_right");
   public static final Material CHEST_XMAS_LOCATION = chestMaterial("christmas");
   public static final Material CHEST_XMAS_LOCATION_LEFT = chestMaterial("christmas_left");
   public static final Material CHEST_XMAS_LOCATION_RIGHT = chestMaterial("christmas_right");
   public static final Material CHEST_LOCATION = chestMaterial("normal");
   public static final Material CHEST_LOCATION_LEFT = chestMaterial("normal_left");
   public static final Material CHEST_LOCATION_RIGHT = chestMaterial("normal_right");
   public static final Material ENDER_CHEST_LOCATION = chestMaterial("ender");

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

   public static RenderType chestSheet() {
      return CHEST_SHEET_TYPE;
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

   public static RenderType translucentCullBlockSheet() {
      return TRANSLUCENT_CULL_BLOCK_SHEET;
   }

   public static void getAllMaterials(Consumer<Material> var0) {
      var0.accept(DEFAULT_SHULKER_TEXTURE_LOCATION);
      SHULKER_TEXTURE_LOCATION.forEach(var0);
      BANNER_MATERIALS.values().forEach(var0);
      SHIELD_MATERIALS.values().forEach(var0);
      SIGN_MATERIALS.values().forEach(var0);

      for(Material var4 : BED_TEXTURES) {
         var0.accept(var4);
      }

      var0.accept(CHEST_TRAP_LOCATION);
      var0.accept(CHEST_TRAP_LOCATION_LEFT);
      var0.accept(CHEST_TRAP_LOCATION_RIGHT);
      var0.accept(CHEST_XMAS_LOCATION);
      var0.accept(CHEST_XMAS_LOCATION_LEFT);
      var0.accept(CHEST_XMAS_LOCATION_RIGHT);
      var0.accept(CHEST_LOCATION);
      var0.accept(CHEST_LOCATION_LEFT);
      var0.accept(CHEST_LOCATION_RIGHT);
      var0.accept(ENDER_CHEST_LOCATION);
   }

   private static Material createSignMaterial(WoodType var0) {
      return new Material(SIGN_SHEET, new ResourceLocation("entity/signs/" + var0.name()));
   }

   public static Material getSignMaterial(WoodType var0) {
      return SIGN_MATERIALS.get(var0);
   }

   private static Material createBannerMaterial(ResourceKey<BannerPattern> var0) {
      return new Material(BANNER_SHEET, BannerPattern.location(var0, true));
   }

   public static Material getBannerMaterial(ResourceKey<BannerPattern> var0) {
      return BANNER_MATERIALS.get(var0);
   }

   private static Material createShieldMaterial(ResourceKey<BannerPattern> var0) {
      return new Material(SHIELD_SHEET, BannerPattern.location(var0, false));
   }

   public static Material getShieldMaterial(ResourceKey<BannerPattern> var0) {
      return SHIELD_MATERIALS.get(var0);
   }

   private static Material chestMaterial(String var0) {
      return new Material(CHEST_SHEET, new ResourceLocation("entity/chest/" + var0));
   }

   public static Material chooseMaterial(BlockEntity var0, ChestType var1, boolean var2) {
      if (var0 instanceof EnderChestBlockEntity) {
         return ENDER_CHEST_LOCATION;
      } else if (var2) {
         return chooseMaterial(var1, CHEST_XMAS_LOCATION, CHEST_XMAS_LOCATION_LEFT, CHEST_XMAS_LOCATION_RIGHT);
      } else {
         return var0 instanceof TrappedChestBlockEntity
            ? chooseMaterial(var1, CHEST_TRAP_LOCATION, CHEST_TRAP_LOCATION_LEFT, CHEST_TRAP_LOCATION_RIGHT)
            : chooseMaterial(var1, CHEST_LOCATION, CHEST_LOCATION_LEFT, CHEST_LOCATION_RIGHT);
      }
   }

   private static Material chooseMaterial(ChestType var0, Material var1, Material var2, Material var3) {
      switch(var0) {
         case LEFT:
            return var2;
         case RIGHT:
            return var3;
         case SINGLE:
         default:
            return var1;
      }
   }
}