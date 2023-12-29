package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public interface PositionSourceType<T extends PositionSource> {
   PositionSourceType<BlockPositionSource> BLOCK = register("block", new BlockPositionSource.Type());
   PositionSourceType<EntityPositionSource> ENTITY = register("entity", new EntityPositionSource.Type());

   T read(FriendlyByteBuf var1);

   void write(FriendlyByteBuf var1, T var2);

   Codec<T> codec();

   static <S extends PositionSourceType<T>, T extends PositionSource> S register(String var0, S var1) {
      return Registry.register(BuiltInRegistries.POSITION_SOURCE_TYPE, var0, (S)var1);
   }

   static PositionSource fromNetwork(FriendlyByteBuf var0) {
      PositionSourceType var1 = var0.readById(BuiltInRegistries.POSITION_SOURCE_TYPE);
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown position source type");
      } else {
         return var1.read(var0);
      }
   }

   static <T extends PositionSource> void toNetwork(T var0, FriendlyByteBuf var1) {
      var1.writeId(BuiltInRegistries.POSITION_SOURCE_TYPE, var0.getType());
      var0.getType().write(var1, var0);
   }
}
