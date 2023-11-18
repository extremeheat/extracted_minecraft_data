package net.minecraft.network.protocol.configuration;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.RegistryOps;

public record ClientboundRegistryDataPacket(RegistryAccess.Frozen a) implements Packet<ClientConfigurationPacketListener> {
   private final RegistryAccess.Frozen registryHolder;
   private static final RegistryOps<Tag> BUILTIN_CONTEXT_OPS = RegistryOps.create(
      NbtOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
   );

   public ClientboundRegistryDataPacket(FriendlyByteBuf var1) {
      this(var1.<RegistryAccess>readWithCodecTrusted(BUILTIN_CONTEXT_OPS, RegistrySynchronization.NETWORK_CODEC).freeze());
   }

   public ClientboundRegistryDataPacket(RegistryAccess.Frozen var1) {
      super();
      this.registryHolder = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeWithCodec(BUILTIN_CONTEXT_OPS, RegistrySynchronization.NETWORK_CODEC, this.registryHolder);
   }

   public void handle(ClientConfigurationPacketListener var1) {
      var1.handleRegistryData(this);
   }
}
