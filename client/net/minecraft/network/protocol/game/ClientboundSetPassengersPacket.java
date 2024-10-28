package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ClientboundSetPassengersPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetPassengersPacket> STREAM_CODEC = Packet.codec(ClientboundSetPassengersPacket::write, ClientboundSetPassengersPacket::new);
   private final int vehicle;
   private final int[] passengers;

   public ClientboundSetPassengersPacket(Entity var1) {
      super();
      this.vehicle = var1.getId();
      List var2 = var1.getPassengers();
      this.passengers = new int[var2.size()];

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.passengers[var3] = ((Entity)var2.get(var3)).getId();
      }

   }

   private ClientboundSetPassengersPacket(FriendlyByteBuf var1) {
      super();
      this.vehicle = var1.readVarInt();
      this.passengers = var1.readVarIntArray();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.vehicle);
      var1.writeVarIntArray(this.passengers);
   }

   public PacketType<ClientboundSetPassengersPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_PASSENGERS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEntityPassengersPacket(this);
   }

   public int[] getPassengers() {
      return this.passengers;
   }

   public int getVehicle() {
      return this.vehicle;
   }
}
