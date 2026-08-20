package net.minecraft.world.clock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WorldClock() {
   public static final Codec<Holder<WorldClock>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WorldClock>> STREAM_CODEC;
   public static final Codec<WorldClock> DIRECT_CODEC;

   public WorldClock() {
      super();
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.WORLD_CLOCK);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.WORLD_CLOCK);
      DIRECT_CODEC = MapCodec.unitCodec(WorldClock::new);
   }
}
