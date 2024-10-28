package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSelectAdvancementsTabPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSelectAdvancementsTabPacket> STREAM_CODEC = Packet.codec(ClientboundSelectAdvancementsTabPacket::write, ClientboundSelectAdvancementsTabPacket::new);
   @Nullable
   private final ResourceLocation tab;

   public ClientboundSelectAdvancementsTabPacket(@Nullable ResourceLocation var1) {
      super();
      this.tab = var1;
   }

   private ClientboundSelectAdvancementsTabPacket(FriendlyByteBuf var1) {
      super();
      this.tab = (ResourceLocation)var1.readNullable(FriendlyByteBuf::readResourceLocation);
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeNullable(this.tab, FriendlyByteBuf::writeResourceLocation);
   }

   public PacketType<ClientboundSelectAdvancementsTabPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SELECT_ADVANCEMENTS_TAB;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSelectAdvancementsTab(this);
   }

   @Nullable
   public ResourceLocation getTab() {
      return this.tab;
   }
}
