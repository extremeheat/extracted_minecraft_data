package net.minecraft.network.protocol.login;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import javax.crypto.SecretKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ServerboundKeyPacket implements Packet<ServerLoginPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundKeyPacket> STREAM_CODEC = Packet.codec(ServerboundKeyPacket::write, ServerboundKeyPacket::new);
   private final byte[] keybytes;
   private final byte[] encryptedChallenge;

   public ServerboundKeyPacket(SecretKey var1, PublicKey var2, byte[] var3) throws CryptException {
      super();
      this.keybytes = Crypt.encryptUsingKey(var2, var1.getEncoded());
      this.encryptedChallenge = Crypt.encryptUsingKey(var2, var3);
   }

   private ServerboundKeyPacket(FriendlyByteBuf var1) {
      super();
      this.keybytes = var1.readByteArray();
      this.encryptedChallenge = var1.readByteArray();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByteArray(this.keybytes);
      var1.writeByteArray(this.encryptedChallenge);
   }

   public PacketType<ServerboundKeyPacket> type() {
      return LoginPacketTypes.SERVERBOUND_KEY;
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleKey(this);
   }

   public SecretKey getSecretKey(PrivateKey var1) throws CryptException {
      return Crypt.decryptByteToSecretKey(var1, this.keybytes);
   }

   public boolean isChallengeValid(byte[] var1, PrivateKey var2) {
      try {
         return Arrays.equals(var1, Crypt.decryptUsingKey(var2, this.encryptedChallenge));
      } catch (CryptException var4) {
         return false;
      }
   }
}
