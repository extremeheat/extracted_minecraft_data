package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ClientboundSetPassengersPacket implements Packet<ClientGamePacketListener> {
   private int vehicle;
   private int[] passengers;

   public ClientboundSetPassengersPacket() {
      super();
   }

   public ClientboundSetPassengersPacket(Entity var1) {
      super();
      this.vehicle = var1.getId();
      List var2 = var1.getPassengers();
      this.passengers = new int[var2.size()];

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.passengers[var3] = ((Entity)var2.get(var3)).getId();
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.vehicle = var1.readVarInt();
      this.passengers = var1.readVarIntArray();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.vehicle);
      var1.writeVarIntArray(this.passengers);
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
