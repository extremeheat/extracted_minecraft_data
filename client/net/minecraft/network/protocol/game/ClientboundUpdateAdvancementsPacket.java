package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateAdvancementsPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateAdvancementsPacket> STREAM_CODEC = Packet.codec(ClientboundUpdateAdvancementsPacket::write, ClientboundUpdateAdvancementsPacket::new);
   private final boolean reset;
   private final List<AdvancementHolder> added;
   private final Set<ResourceLocation> removed;
   private final Map<ResourceLocation, AdvancementProgress> progress;

   public ClientboundUpdateAdvancementsPacket(boolean var1, Collection<AdvancementHolder> var2, Set<ResourceLocation> var3, Map<ResourceLocation, AdvancementProgress> var4) {
      super();
      this.reset = var1;
      this.added = List.copyOf(var2);
      this.removed = Set.copyOf(var3);
      this.progress = Map.copyOf(var4);
   }

   private ClientboundUpdateAdvancementsPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.reset = var1.readBoolean();
      this.added = (List)AdvancementHolder.LIST_STREAM_CODEC.decode(var1);
      this.removed = (Set)var1.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readResourceLocation);
      this.progress = var1.readMap(FriendlyByteBuf::readResourceLocation, AdvancementProgress::fromNetwork);
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeBoolean(this.reset);
      AdvancementHolder.LIST_STREAM_CODEC.encode(var1, this.added);
      var1.writeCollection(this.removed, FriendlyByteBuf::writeResourceLocation);
      var1.writeMap(this.progress, FriendlyByteBuf::writeResourceLocation, (var0, var1x) -> {
         var1x.serializeToNetwork(var0);
      });
   }

   public PacketType<ClientboundUpdateAdvancementsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_ADVANCEMENTS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateAdvancementsPacket(this);
   }

   public List<AdvancementHolder> getAdded() {
      return this.added;
   }

   public Set<ResourceLocation> getRemoved() {
      return this.removed;
   }

   public Map<ResourceLocation, AdvancementProgress> getProgress() {
      return this.progress;
   }

   public boolean shouldReset() {
      return this.reset;
   }
}
