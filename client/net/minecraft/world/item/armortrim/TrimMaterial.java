package net.minecraft.world.item.armortrim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

public record TrimMaterial(
   String assetName, Holder<Item> ingredient, float itemModelIndex, Map<Holder<ArmorMaterial>, String> overrideArmorMaterials, Component description
) {
   public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.RESOURCE_PATH_CODEC.fieldOf("asset_name").forGetter(TrimMaterial::assetName),
               RegistryFixedCodec.create(Registries.ITEM).fieldOf("ingredient").forGetter(TrimMaterial::ingredient),
               Codec.FLOAT.fieldOf("item_model_index").forGetter(TrimMaterial::itemModelIndex),
               Codec.unboundedMap(ArmorMaterial.CODEC, Codec.STRING)
                  .optionalFieldOf("override_armor_materials", Map.of())
                  .forGetter(TrimMaterial::overrideArmorMaterials),
               ComponentSerialization.CODEC.fieldOf("description").forGetter(TrimMaterial::description)
            )
            .apply(var0, TrimMaterial::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, TrimMaterial> DIRECT_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      TrimMaterial::assetName,
      ByteBufCodecs.holderRegistry(Registries.ITEM),
      TrimMaterial::ingredient,
      ByteBufCodecs.FLOAT,
      TrimMaterial::itemModelIndex,
      ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.holderRegistry(Registries.ARMOR_MATERIAL), ByteBufCodecs.STRING_UTF8),
      TrimMaterial::overrideArmorMaterials,
      ComponentSerialization.STREAM_CODEC,
      TrimMaterial::description,
      TrimMaterial::new
   );
   public static final Codec<Holder<TrimMaterial>> CODEC = RegistryFileCodec.create(Registries.TRIM_MATERIAL, DIRECT_CODEC);
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TrimMaterial>> STREAM_CODEC = ByteBufCodecs.holder(
      Registries.TRIM_MATERIAL, DIRECT_STREAM_CODEC
   );

   public TrimMaterial(
      String assetName, Holder<Item> ingredient, float itemModelIndex, Map<Holder<ArmorMaterial>, String> overrideArmorMaterials, Component description
   ) {
      super();
      this.assetName = assetName;
      this.ingredient = ingredient;
      this.itemModelIndex = itemModelIndex;
      this.overrideArmorMaterials = overrideArmorMaterials;
      this.description = description;
   }

   public static TrimMaterial create(String var0, Item var1, float var2, Component var3, Map<Holder<ArmorMaterial>, String> var4) {
      return new TrimMaterial(var0, BuiltInRegistries.ITEM.wrapAsHolder(var1), var2, var4, var3);
   }
}
