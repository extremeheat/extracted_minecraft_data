package net.minecraft.world.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;

public record TrimMaterial(MaterialAssetGroup assets, Component description) {
   public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(MaterialAssetGroup.MAP_CODEC.forGetter(TrimMaterial::assets), ComponentSerialization.CODEC.fieldOf("description").forGetter(TrimMaterial::description)).apply(var0, TrimMaterial::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, TrimMaterial> DIRECT_STREAM_CODEC;
   public static final Codec<Holder<TrimMaterial>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TrimMaterial>> STREAM_CODEC;

   public TrimMaterial(MaterialAssetGroup var1, Component var2) {
      super();
      this.assets = var1;
      this.description = var2;
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(MaterialAssetGroup.STREAM_CODEC, TrimMaterial::assets, ComponentSerialization.STREAM_CODEC, TrimMaterial::description, TrimMaterial::new);
      CODEC = RegistryFileCodec.<Holder<TrimMaterial>>create(Registries.TRIM_MATERIAL, DIRECT_CODEC);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.TRIM_MATERIAL, DIRECT_STREAM_CODEC);
   }
}
