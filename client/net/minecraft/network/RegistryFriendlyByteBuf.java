package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.core.RegistryAccess;

public class RegistryFriendlyByteBuf extends FriendlyByteBuf {
   private final RegistryAccess registryAccess;

   public RegistryFriendlyByteBuf(ByteBuf var1, RegistryAccess var2) {
      super(var1);
      this.registryAccess = var2;
   }

   public RegistryAccess registryAccess() {
      return this.registryAccess;
   }

   public static Function<ByteBuf, RegistryFriendlyByteBuf> decorator(RegistryAccess var0) {
      return (var1) -> {
         return new RegistryFriendlyByteBuf(var1, var0);
      };
   }
}
