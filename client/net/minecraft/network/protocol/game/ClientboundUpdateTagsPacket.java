package net.minecraft.network.protocol.game;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagCollection;

public class ClientboundUpdateTagsPacket implements Packet<ClientGamePacketListener> {
   private final Map<ResourceKey<? extends Registry<?>>, TagCollection.NetworkPayload> tags;

   public ClientboundUpdateTagsPacket(Map<ResourceKey<? extends Registry<?>>, TagCollection.NetworkPayload> var1) {
      super();
      this.tags = var1;
   }

   public ClientboundUpdateTagsPacket(FriendlyByteBuf var1) {
      super();
      this.tags = var1.readMap((var0) -> {
         return ResourceKey.createRegistryKey(var0.readResourceLocation());
      }, TagCollection.NetworkPayload::read);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeMap(this.tags, (var0, var1x) -> {
         var0.writeResourceLocation(var1x.location());
      }, (var0, var1x) -> {
         var1x.write(var0);
      });
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateTags(this);
   }

   public Map<ResourceKey<? extends Registry<?>>, TagCollection.NetworkPayload> getTags() {
      return this.tags;
   }
}
