package net.minecraft.network.protocol.game;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> a) implements Packet<ClientGamePacketListener> {
   private final Set<ResourceLocation> features;

   public ClientboundUpdateEnabledFeaturesPacket(FriendlyByteBuf var1) {
      this(var1.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
   }

   public ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> var1) {
      super();
      this.features = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.features, FriendlyByteBuf::writeResourceLocation);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleEnabledFeatures(this);
   }
}
