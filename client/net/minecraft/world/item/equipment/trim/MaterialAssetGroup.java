package net.minecraft.world.item.equipment.trim;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public record MaterialAssetGroup(AssetInfo base, Map<ResourceKey<EquipmentAsset>, AssetInfo> overrides) {
   public static final String SEPARATOR = "_";
   public static final MapCodec<MaterialAssetGroup> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(MaterialAssetGroup.AssetInfo.CODEC.fieldOf("asset_name").forGetter(MaterialAssetGroup::base), Codec.unboundedMap(ResourceKey.codec(EquipmentAssets.ROOT_ID), MaterialAssetGroup.AssetInfo.CODEC).optionalFieldOf("override_armor_assets", Map.of()).forGetter(MaterialAssetGroup::overrides)).apply(i, MaterialAssetGroup::new));
   public static final StreamCodec<ByteBuf, MaterialAssetGroup> STREAM_CODEC;
   public static final MaterialAssetGroup QUARTZ;
   public static final MaterialAssetGroup IRON;
   public static final MaterialAssetGroup NETHERITE;
   public static final MaterialAssetGroup REDSTONE;
   public static final MaterialAssetGroup COPPER;
   public static final MaterialAssetGroup GOLD;
   public static final MaterialAssetGroup EMERALD;
   public static final MaterialAssetGroup DIAMOND;
   public static final MaterialAssetGroup LAPIS;
   public static final MaterialAssetGroup AMETHYST;
   public static final MaterialAssetGroup RESIN;

   public MaterialAssetGroup {
      super();
   }

   public static MaterialAssetGroup create(final String base) {
      return new MaterialAssetGroup(new AssetInfo(base), Map.of());
   }

   public static MaterialAssetGroup create(final String base, final Map<ResourceKey<EquipmentAsset>, String> overrides) {
      return new MaterialAssetGroup(new AssetInfo(base), Map.copyOf(Maps.transformValues(overrides, AssetInfo::new)));
   }

   public AssetInfo assetId(final ResourceKey<EquipmentAsset> equipmentAssetId) {
      return (AssetInfo)this.overrides.getOrDefault(equipmentAssetId, this.base);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(MaterialAssetGroup.AssetInfo.STREAM_CODEC, MaterialAssetGroup::base, ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceKey.streamCodec(EquipmentAssets.ROOT_ID), MaterialAssetGroup.AssetInfo.STREAM_CODEC), MaterialAssetGroup::overrides, MaterialAssetGroup::new);
      QUARTZ = create("quartz");
      IRON = create("iron", Map.of(EquipmentAssets.IRON, "iron_darker"));
      NETHERITE = create("netherite", Map.of(EquipmentAssets.NETHERITE, "netherite_darker"));
      REDSTONE = create("redstone");
      COPPER = create("copper", Map.of(EquipmentAssets.COPPER, "copper_darker"));
      GOLD = create("gold", Map.of(EquipmentAssets.GOLD, "gold_darker"));
      EMERALD = create("emerald");
      DIAMOND = create("diamond", Map.of(EquipmentAssets.DIAMOND, "diamond_darker"));
      LAPIS = create("lapis");
      AMETHYST = create("amethyst");
      RESIN = create("resin");
   }

   public static record AssetInfo(String suffix) {
      public static final Codec<AssetInfo> CODEC;
      public static final StreamCodec<ByteBuf, AssetInfo> STREAM_CODEC;

      public AssetInfo {
         super();
         if (!Identifier.isValidPath(suffix)) {
            throw new IllegalArgumentException("Invalid string to use as a resource path element: " + suffix);
         }
      }

      static {
         CODEC = ExtraCodecs.RESOURCE_PATH_CODEC.xmap(AssetInfo::new, AssetInfo::suffix);
         STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(AssetInfo::new, AssetInfo::suffix);
      }
   }
}
