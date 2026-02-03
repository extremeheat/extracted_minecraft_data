package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public record DamageResistant(TagKey<DamageType> types) {
   public static final Codec<DamageResistant> CODEC = RecordCodecBuilder.create((i) -> i.group(TagKey.hashedCodec(Registries.DAMAGE_TYPE).fieldOf("types").forGetter(DamageResistant::types)).apply(i, DamageResistant::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, DamageResistant> STREAM_CODEC;

   public DamageResistant {
      super();
   }

   public boolean isResistantTo(final DamageSource source) {
      return source.is(this.types);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(TagKey.streamCodec(Registries.DAMAGE_TYPE), DamageResistant::types, DamageResistant::new);
   }
}
