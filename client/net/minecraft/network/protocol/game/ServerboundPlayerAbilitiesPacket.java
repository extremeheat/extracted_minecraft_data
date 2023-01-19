package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Abilities;

public class ServerboundPlayerAbilitiesPacket implements Packet<ServerGamePacketListener> {
   private static final int FLAG_FLYING = 2;
   private final boolean isFlying;

   public ServerboundPlayerAbilitiesPacket(Abilities var1) {
      super();
      this.isFlying = var1.flying;
   }

   public ServerboundPlayerAbilitiesPacket(FriendlyByteBuf var1) {
      super();
      byte var2 = var1.readByte();
      this.isFlying = (var2 & 2) != 0;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      byte var2 = 0;
      if (this.isFlying) {
         var2 = (byte)(var2 | 2);
      }

      var1.writeByte(var2);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlayerAbilities(this);
   }

   public boolean isFlying() {
      return this.isFlying;
   }
}
