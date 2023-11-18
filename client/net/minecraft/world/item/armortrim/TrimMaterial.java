package net.minecraft.world.item.armortrim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

public record TrimMaterial(String c, Holder<Item> d, float e, Map<ArmorMaterials, String> f, Component g) {
   private final String assetName;
   private final Holder<Item> ingredient;
   private final float itemModelIndex;
   private final Map<ArmorMaterials, String> overrideArmorMaterials;
   private final Component description;
   public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.RESOURCE_PATH_CODEC.fieldOf("asset_name").forGetter(TrimMaterial::assetName),
               RegistryFixedCodec.create(Registries.ITEM).fieldOf("ingredient").forGetter(TrimMaterial::ingredient),
               Codec.FLOAT.fieldOf("item_model_index").forGetter(TrimMaterial::itemModelIndex),
               Codec.unboundedMap(ArmorMaterials.CODEC, Codec.STRING)
                  .optionalFieldOf("override_armor_materials", Map.of())
                  .forGetter(TrimMaterial::overrideArmorMaterials),
               ExtraCodecs.COMPONENT.fieldOf("description").forGetter(TrimMaterial::description)
            )
            .apply(var0, TrimMaterial::new)
   );
   public static final Codec<Holder<TrimMaterial>> CODEC = RegistryFileCodec.create(Registries.TRIM_MATERIAL, DIRECT_CODEC);

   public TrimMaterial(String var1, Holder<Item> var2, float var3, Map<ArmorMaterials, String> var4, Component var5) {
      super();
      this.assetName = var1;
      this.ingredient = var2;
      this.itemModelIndex = var3;
      this.overrideArmorMaterials = var4;
      this.description = var5;
   }

   public static TrimMaterial create(String var0, Item var1, float var2, Component var3, Map<ArmorMaterials, String> var4) {
      return new TrimMaterial(var0, BuiltInRegistries.ITEM.wrapAsHolder(var1), var2, var4, var3);
   }
}
