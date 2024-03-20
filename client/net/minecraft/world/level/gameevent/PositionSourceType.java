package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface PositionSourceType<T extends PositionSource> {
   PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
   PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

   Codec<T> codec();

   StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

   static <S extends PositionSourceType<T>, T extends PositionSource> S register(String var0, S var1) {
      return Registry.register(BuiltInRegistries.POSITION_SOURCE_TYPE, var0, (S)var1);
   }
}