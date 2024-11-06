package net.minecraft.client.renderer.special;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;

public class SpecialModelRenderers {
   private static final ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper();
   public static final Codec<SpecialModelRenderer.Unbaked> CODEC;
   private static final Map<Block, SpecialModelRenderer.Unbaked> STATIC_BLOCK_MAPPING;
   private static final ChestSpecialRenderer.Unbaked GIFT_CHEST;

   public SpecialModelRenderers() {
      super();
   }

   public static void bootstrap() {
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("bed"), BedSpecialRenderer.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("banner"), BannerSpecialRenderer.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("conduit"), ConduitSpecialRenderer.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("chest"), ChestSpecialRenderer.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("head"), SkullSpecialRenderer.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("shulker_box"), ShulkerBoxSpecialRenderer.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("shield"), ShieldSpecialRenderer.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("trident"), TridentSpecialRenderer.Unbaked.MAP_CODEC);
      ID_MAPPER.put(ResourceLocation.withDefaultNamespace("decorated_pot"), DecoratedPotSpecialRenderer.Unbaked.MAP_CODEC);
   }

   public static Map<Block, SpecialModelRenderer<?>> createBlockRenderers(EntityModelSet var0) {
      HashMap var1 = new HashMap(STATIC_BLOCK_MAPPING);
      if (ChestRenderer.xmasTextures()) {
         var1.put(Blocks.CHEST, GIFT_CHEST);
         var1.put(Blocks.TRAPPED_CHEST, GIFT_CHEST);
      }

      ImmutableMap.Builder var2 = ImmutableMap.builder();
      var1.forEach((var2x, var3) -> {
         SpecialModelRenderer var4 = var3.bake(var0);
         if (var4 != null) {
            var2.put(var2x, var4);
         }

      });
      return var2.build();
   }

   static {
      CODEC = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(SpecialModelRenderer.Unbaked::type, (var0) -> {
         return var0;
      });
      STATIC_BLOCK_MAPPING = ImmutableMap.builder().put(Blocks.SKELETON_SKULL, new SkullSpecialRenderer.Unbaked(SkullBlock.Types.SKELETON)).put(Blocks.ZOMBIE_HEAD, new SkullSpecialRenderer.Unbaked(SkullBlock.Types.ZOMBIE)).put(Blocks.CREEPER_HEAD, new SkullSpecialRenderer.Unbaked(SkullBlock.Types.CREEPER)).put(Blocks.DRAGON_HEAD, new SkullSpecialRenderer.Unbaked(SkullBlock.Types.DRAGON)).put(Blocks.PIGLIN_HEAD, new SkullSpecialRenderer.Unbaked(SkullBlock.Types.PIGLIN)).put(Blocks.PLAYER_HEAD, new SkullSpecialRenderer.Unbaked(SkullBlock.Types.PLAYER)).put(Blocks.WITHER_SKELETON_SKULL, new SkullSpecialRenderer.Unbaked(SkullBlock.Types.SKELETON)).put(Blocks.WHITE_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.WHITE)).put(Blocks.ORANGE_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.ORANGE)).put(Blocks.MAGENTA_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.MAGENTA)).put(Blocks.LIGHT_BLUE_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.LIGHT_BLUE)).put(Blocks.YELLOW_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.YELLOW)).put(Blocks.LIME_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.LIME)).put(Blocks.PINK_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.PINK)).put(Blocks.GRAY_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.GRAY)).put(Blocks.LIGHT_GRAY_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.LIGHT_GRAY)).put(Blocks.CYAN_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.CYAN)).put(Blocks.PURPLE_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.PURPLE)).put(Blocks.BLUE_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.BLUE)).put(Blocks.BROWN_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.BROWN)).put(Blocks.GREEN_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.GREEN)).put(Blocks.RED_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.RED)).put(Blocks.BLACK_BANNER, new BannerSpecialRenderer.Unbaked(DyeColor.BLACK)).put(Blocks.WHITE_BED, new BedSpecialRenderer.Unbaked(DyeColor.WHITE)).put(Blocks.ORANGE_BED, new BedSpecialRenderer.Unbaked(DyeColor.ORANGE)).put(Blocks.MAGENTA_BED, new BedSpecialRenderer.Unbaked(DyeColor.MAGENTA)).put(Blocks.LIGHT_BLUE_BED, new BedSpecialRenderer.Unbaked(DyeColor.LIGHT_BLUE)).put(Blocks.YELLOW_BED, new BedSpecialRenderer.Unbaked(DyeColor.YELLOW)).put(Blocks.LIME_BED, new BedSpecialRenderer.Unbaked(DyeColor.LIME)).put(Blocks.PINK_BED, new BedSpecialRenderer.Unbaked(DyeColor.PINK)).put(Blocks.GRAY_BED, new BedSpecialRenderer.Unbaked(DyeColor.GRAY)).put(Blocks.LIGHT_GRAY_BED, new BedSpecialRenderer.Unbaked(DyeColor.LIGHT_GRAY)).put(Blocks.CYAN_BED, new BedSpecialRenderer.Unbaked(DyeColor.CYAN)).put(Blocks.PURPLE_BED, new BedSpecialRenderer.Unbaked(DyeColor.PURPLE)).put(Blocks.BLUE_BED, new BedSpecialRenderer.Unbaked(DyeColor.BLUE)).put(Blocks.BROWN_BED, new BedSpecialRenderer.Unbaked(DyeColor.BROWN)).put(Blocks.GREEN_BED, new BedSpecialRenderer.Unbaked(DyeColor.GREEN)).put(Blocks.RED_BED, new BedSpecialRenderer.Unbaked(DyeColor.RED)).put(Blocks.BLACK_BED, new BedSpecialRenderer.Unbaked(DyeColor.BLACK)).put(Blocks.SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked()).put(Blocks.WHITE_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.WHITE)).put(Blocks.ORANGE_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.ORANGE)).put(Blocks.MAGENTA_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.MAGENTA)).put(Blocks.LIGHT_BLUE_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.LIGHT_BLUE)).put(Blocks.YELLOW_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.YELLOW)).put(Blocks.LIME_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.LIME)).put(Blocks.PINK_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.PINK)).put(Blocks.GRAY_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.GRAY)).put(Blocks.LIGHT_GRAY_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.LIGHT_GRAY)).put(Blocks.CYAN_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.CYAN)).put(Blocks.PURPLE_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.PURPLE)).put(Blocks.BLUE_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.BLUE)).put(Blocks.BROWN_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.BROWN)).put(Blocks.GREEN_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.GREEN)).put(Blocks.RED_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.RED)).put(Blocks.BLACK_SHULKER_BOX, new ShulkerBoxSpecialRenderer.Unbaked(DyeColor.BLACK)).put(Blocks.CONDUIT, new ConduitSpecialRenderer.Unbaked()).put(Blocks.CHEST, new ChestSpecialRenderer.Unbaked(ChestSpecialRenderer.NORMAL_CHEST_TEXTURE)).put(Blocks.TRAPPED_CHEST, new ChestSpecialRenderer.Unbaked(ChestSpecialRenderer.TRAPPED_CHEST_TEXTURE)).put(Blocks.ENDER_CHEST, new ChestSpecialRenderer.Unbaked(ChestSpecialRenderer.ENDER_CHEST_TEXTURE)).put(Blocks.DECORATED_POT, new DecoratedPotSpecialRenderer.Unbaked()).build();
      GIFT_CHEST = new ChestSpecialRenderer.Unbaked(ChestSpecialRenderer.GIFT_CHEST_TEXTURE);
   }
}
