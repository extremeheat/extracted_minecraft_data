package net.minecraft.world.item;

import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class ServerItemCooldowns extends ItemCooldowns {
   private final ServerPlayer player;

   public ServerItemCooldowns(ServerPlayer var1) {
      super();
      this.player = var1;
   }

   protected void onCooldownStarted(Identifier var1, int var2) {
      super.onCooldownStarted(var1, var2);
      this.player.connection.send(new ClientboundCooldownPacket(var1, var2));
   }

   protected void onCooldownEnded(Identifier var1) {
      super.onCooldownEnded(var1);
      this.player.connection.send(new ClientboundCooldownPacket(var1, 0));
   }
}
