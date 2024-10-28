package net.minecraft.server.network.config;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundSelectKnownPacks;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.tags.TagNetworkSerialization;

public class SynchronizeRegistriesTask implements ConfigurationTask {
   public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type("synchronize_registries");
   private final List<KnownPack> requestedPacks;
   private final LayeredRegistryAccess<RegistryLayer> registries;

   public SynchronizeRegistriesTask(List<KnownPack> var1, LayeredRegistryAccess<RegistryLayer> var2) {
      super();
      this.requestedPacks = var1;
      this.registries = var2;
   }

   public void start(Consumer<Packet<?>> var1) {
      var1.accept(new ClientboundSelectKnownPacks(this.requestedPacks));
   }

   private void sendRegistries(Consumer<Packet<?>> var1, Set<KnownPack> var2) {
      RegistryOps var3 = this.registries.compositeAccess().createSerializationContext(NbtOps.INSTANCE);
      RegistrySynchronization.packRegistries(var3, this.registries.getAccessFrom(RegistryLayer.WORLDGEN), var2, (var1x, var2x) -> {
         var1.accept(new ClientboundRegistryDataPacket(var1x, var2x));
      });
      var1.accept(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(this.registries)));
   }

   public void handleResponse(List<KnownPack> var1, Consumer<Packet<?>> var2) {
      if (var1.equals(this.requestedPacks)) {
         this.sendRegistries(var2, Set.copyOf(this.requestedPacks));
      } else {
         this.sendRegistries(var2, Set.of());
      }

   }

   public ConfigurationTask.Type type() {
      return TYPE;
   }
}
