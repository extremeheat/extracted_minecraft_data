package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ClientboundSelectAdvancementsTabPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSelectAdvancementsTabPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundSelectAdvancementsTabPacket>codec(ClientboundSelectAdvancementsTabPacket::write, ClientboundSelectAdvancementsTabPacket::new);
   private final @Nullable Identifier tab;

   public ClientboundSelectAdvancementsTabPacket(@Nullable Identifier var1) {
      super();
      this.tab = var1;
   }

   private ClientboundSelectAdvancementsTabPacket(FriendlyByteBuf var1) {
      super();
      this.tab = (Identifier)var1.readNullable(FriendlyByteBuf::readIdentifier);
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeNullable(this.tab, FriendlyByteBuf::writeIdentifier);
   }

   public PacketType<ClientboundSelectAdvancementsTabPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SELECT_ADVANCEMENTS_TAB;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSelectAdvancementsTab(this);
   }

   public @Nullable Identifier getTab() {
      return this.tab;
   }
}
