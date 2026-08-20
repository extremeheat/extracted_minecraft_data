package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;

public record MobVisibility(HolderSet<EntityType<?>> targetingEntityTypes, float visibility) {
   public static final float MIN_VISIBILITY = 0.0F;
   public static final float MAX_VISIBILITY = 10.0F;
   public static final Codec<MobVisibility> CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.ENTITY_TYPE).fieldOf("targeting_entity_types").forGetter(MobVisibility::targetingEntityTypes), ExtraCodecs.floatRange(0.0F, 10.0F).fieldOf("visibility").forGetter(MobVisibility::visibility)).apply(i, MobVisibility::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, MobVisibility> STREAM_CODEC;

   public MobVisibility {
      super();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.ENTITY_TYPE), MobVisibility::targetingEntityTypes, ByteBufCodecs.FLOAT, MobVisibility::visibility, MobVisibility::new);
   }
}
