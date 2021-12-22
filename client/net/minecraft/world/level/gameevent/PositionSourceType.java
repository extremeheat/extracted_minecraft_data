package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface PositionSourceType<T extends PositionSource> {
   PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
   PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

   T read(FriendlyByteBuf var1);

   void write(FriendlyByteBuf var1, T var2);

   Codec<T> codec();

   static <S extends PositionSourceType<T>, T extends PositionSource> S register(String var0, S var1) {
      return (PositionSourceType)Registry.register(Registry.POSITION_SOURCE_TYPE, (String)var0, var1);
   }

   static PositionSource fromNetwork(FriendlyByteBuf var0) {
      ResourceLocation var1 = var0.readResourceLocation();
      return ((PositionSourceType)Registry.POSITION_SOURCE_TYPE.getOptional(var1).orElseThrow(() -> {
         return new IllegalArgumentException("Unknown position source type " + var1);
      })).read(var0);
   }

   static <T extends PositionSource> void toNetwork(T var0, FriendlyByteBuf var1) {
      var1.writeResourceLocation(Registry.POSITION_SOURCE_TYPE.getKey(var0.getType()));
      var0.getType().write(var1, var0);
   }
}
