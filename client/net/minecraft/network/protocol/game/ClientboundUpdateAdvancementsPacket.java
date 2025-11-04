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
import net.minecraft.resources.Identifier;

public class ClientboundUpdateAdvancementsPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateAdvancementsPacket> STREAM_CODEC = Packet.<RegistryFriendlyByteBuf, ClientboundUpdateAdvancementsPacket>codec(ClientboundUpdateAdvancementsPacket::write, ClientboundUpdateAdvancementsPacket::new);
   private final boolean reset;
   private final List<AdvancementHolder> added;
   private final Set<Identifier> removed;
   private final Map<Identifier, AdvancementProgress> progress;
   private final boolean showAdvancements;

   public ClientboundUpdateAdvancementsPacket(boolean var1, Collection<AdvancementHolder> var2, Set<Identifier> var3, Map<Identifier, AdvancementProgress> var4, boolean var5) {
      super();
      this.reset = var1;
      this.added = List.copyOf(var2);
      this.removed = Set.copyOf(var3);
      this.progress = Map.copyOf(var4);
      this.showAdvancements = var5;
   }

   private ClientboundUpdateAdvancementsPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.reset = var1.readBoolean();
      this.added = (List)AdvancementHolder.LIST_STREAM_CODEC.decode(var1);
      this.removed = (Set)var1.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readIdentifier);
      this.progress = var1.readMap(FriendlyByteBuf::readIdentifier, AdvancementProgress::fromNetwork);
      this.showAdvancements = var1.readBoolean();
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeBoolean(this.reset);
      AdvancementHolder.LIST_STREAM_CODEC.encode(var1, this.added);
      var1.writeCollection(this.removed, FriendlyByteBuf::writeIdentifier);
      var1.writeMap(this.progress, FriendlyByteBuf::writeIdentifier, (var0, var1x) -> var1x.serializeToNetwork(var0));
      var1.writeBoolean(this.showAdvancements);
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

   public Set<Identifier> getRemoved() {
      return this.removed;
   }

   public Map<Identifier, AdvancementProgress> getProgress() {
      return this.progress;
   }

   public boolean shouldReset() {
      return this.reset;
   }

   public boolean shouldShowAdvancements() {
      return this.showAdvancements;
   }
}
