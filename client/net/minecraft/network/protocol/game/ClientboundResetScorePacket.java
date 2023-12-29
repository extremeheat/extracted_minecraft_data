package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundResetScorePacket(String a, @Nullable String b) implements Packet<ClientGamePacketListener> {
   private final String owner;
   @Nullable
   private final String objectiveName;

   public ClientboundResetScorePacket(FriendlyByteBuf var1) {
      this(var1.readUtf(), var1.readNullable(FriendlyByteBuf::readUtf));
   }

   public ClientboundResetScorePacket(String var1, @Nullable String var2) {
      super();
      this.owner = var1;
      this.objectiveName = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.owner);
      var1.writeNullable(this.objectiveName, FriendlyByteBuf::writeUtf);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleResetScore(this);
   }
}
