package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;

public class ClientboundAddVibrationSignalPacket implements Packet<ClientGamePacketListener> {
   private final VibrationPath vibrationPath;

   public ClientboundAddVibrationSignalPacket(VibrationPath var1) {
      super();
      this.vibrationPath = var1;
   }

   public ClientboundAddVibrationSignalPacket(FriendlyByteBuf var1) {
      super();
      this.vibrationPath = VibrationPath.read(var1);
   }

   public void write(FriendlyByteBuf var1) {
      VibrationPath.write(var1, this.vibrationPath);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddVibrationSignal(this);
   }

   public VibrationPath getVibrationPath() {
      return this.vibrationPath;
   }
}
