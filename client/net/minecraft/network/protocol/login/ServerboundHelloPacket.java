package net.minecraft.network.protocol.login;

import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record ServerboundHelloPacket(String a, Optional<ProfilePublicKey.Data> b) implements Packet<ServerLoginPacketListener> {
   private final String name;
   private final Optional<ProfilePublicKey.Data> publicKey;

   public ServerboundHelloPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(16), var1.readOptional(ProfilePublicKey.Data::new));
   }

   public ServerboundHelloPacket(String var1, Optional<ProfilePublicKey.Data> var2) {
      super();
      this.name = var1;
      this.publicKey = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.name, 16);
      var1.writeOptional(this.publicKey, (var1x, var2) -> var2.write(var1));
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleHello(this);
   }
}
