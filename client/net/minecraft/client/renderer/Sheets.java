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
   private static final RenderType SHULKER_BOX_SHEET_TYPE;
   private static final RenderType BED_SHEET_TYPE;
   private static final RenderType BANNER_SHEET_TYPE;
   private static final RenderType SHIELD_SHEET_TYPE;
   private static final RenderType SIGN_SHEET_TYPE;
   private static final RenderType CHEST_SHEET_TYPE;
   private static final RenderType SOLID_BLOCK_SHEET;
   private static final RenderType CUTOUT_BLOCK_SHEET;
   private static final RenderType TRANSLUCENT_ITEM_CULL_BLOCK_SHEET;
   private static final RenderType TRANSLUCENT_CULL_BLOCK_SHEET;
   public static final Material DEFAULT_SHULKER_TEXTURE_LOCATION;
   public static final List<Material> SHULKER_TEXTURE_LOCATION;
   public static final Map<WoodType, Material> SIGN_MATERIALS;
   public static final Map<BannerPattern, Material> BANNER_MATERIALS;
   public static final Map<BannerPattern, Material> SHIELD_MATERIALS;
   public static final Material[] BED_TEXTURES;
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
      Material[] var1 = BED_TEXTURES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Material var4 = var1[var3];
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
      return (Material)SIGN_MATERIALS.get(var0);
   }

   private static Material createBannerMaterial(BannerPattern var0) {
      return new Material(BANNER_SHEET, var0.location(true));
   }

   public static Material getBannerMaterial(BannerPattern var0) {
      return (Material)BANNER_MATERIALS.get(var0);
   }

   private static Material createShieldMaterial(BannerPattern var0) {
      return new Material(SHIELD_SHEET, var0.location(false));
   }

   public static Material getShieldMaterial(BannerPattern var0) {
      return (Material)SHIELD_MATERIALS.get(var0);
   }

   private static Material chestMaterial(String var0) {
      return new Material(CHEST_SHEET, new ResourceLocation("entity/chest/" + var0));
   }

   public static Material chooseMaterial(BlockEntity var0, ChestType var1, boolean var2) {
      if (var2) {
         return chooseMaterial(var1, CHEST_XMAS_LOCATION, CHEST_XMAS_LOCATION_LEFT, CHEST_XMAS_LOCATION_RIGHT);
      } else if (var0 instanceof TrappedChestBlockEntity) {
         return chooseMaterial(var1, CHEST_TRAP_LOCATION, CHEST_TRAP_LOCATION_LEFT, CHEST_TRAP_LOCATION_RIGHT);
      } else {
         return var0 instanceof EnderChestBlockEntity ? ENDER_CHEST_LOCATION : chooseMaterial(var1, CHEST_LOCATION, CHEST_LOCATION_LEFT, CHEST_LOCATION_RIGHT);
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

   static {
      SHULKER_BOX_SHEET_TYPE = RenderType.entityCutoutNoCull(SHULKER_SHEET);
      BED_SHEET_TYPE = RenderType.entitySolid(BED_SHEET);
      BANNER_SHEET_TYPE = RenderType.entityNoOutline(BANNER_SHEET);
      SHIELD_SHEET_TYPE = RenderType.entityNoOutline(SHIELD_SHEET);
      SIGN_SHEET_TYPE = RenderType.entityCutoutNoCull(SIGN_SHEET);
      CHEST_SHEET_TYPE = RenderType.entityCutout(CHEST_SHEET);
      SOLID_BLOCK_SHEET = RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS);
      CUTOUT_BLOCK_SHEET = RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_ITEM_CULL_BLOCK_SHEET = RenderType.itemEntityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
      TRANSLUCENT_CULL_BLOCK_SHEET = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
      DEFAULT_SHULKER_TEXTURE_LOCATION = new Material(SHULKER_SHEET, new ResourceLocation("entity/shulker/shulker"));
      SHULKER_TEXTURE_LOCATION = (List)Stream.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black").map((var0) -> {
         return new Material(SHULKER_SHEET, new ResourceLocation("entity/shulker/shulker_" + var0));
      }).collect(ImmutableList.toImmutableList());
      SIGN_MATERIALS = (Map)WoodType.values().collect(Collectors.toMap(Function.identity(), Sheets::createSignMaterial));
      BANNER_MATERIALS = (Map)Arrays.stream(BannerPattern.values()).collect(Collectors.toMap(Function.identity(), Sheets::createBannerMaterial));
      SHIELD_MATERIALS = (Map)Arrays.stream(BannerPattern.values()).collect(Collectors.toMap(Function.identity(), Sheets::createShieldMaterial));
      BED_TEXTURES = (Material[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((var0) -> {
         return new Material(BED_SHEET, new ResourceLocation("entity/bed/" + var0.getName()));
      }).toArray((var0) -> {
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
