package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.IdMapProvider;

public class RegistryFriendlyByteBuf extends FriendlyByteBuf {
   private final HolderGetter.Provider registries;
   private final IdMapProvider ids;

   public RegistryFriendlyByteBuf(final ByteBuf source, final RegistryAccess registryAccess) {
      this(source, registryAccess, registryAccess);
   }

   public RegistryFriendlyByteBuf(final ByteBuf source, final HolderGetter.Provider registries, final IdMapProvider ids) {
      super(source);
      this.registries = registries;
      this.ids = ids;
   }

   public HolderGetter.Provider registries() {
      return this.registries;
   }

   public IdMapProvider ids() {
      return this.ids;
   }

   public static Function<ByteBuf, RegistryFriendlyByteBuf> decorator(final RegistryAccess registryAccess) {
      return (buf) -> new RegistryFriendlyByteBuf(buf, registryAccess);
   }
}
