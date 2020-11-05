package net.minecraft.network.protocol.login;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ServerboundKeyPacket implements Packet<ServerLoginPacketListener> {
   private byte[] keybytes = new byte[0];
   private byte[] nonce = new byte[0];

   public ServerboundKeyPacket() {
      super();
   }

   public ServerboundKeyPacket(SecretKey var1, PublicKey var2, byte[] var3) throws CryptException {
      super();
      this.keybytes = Crypt.encryptUsingKey(var2, var1.getEncoded());
      this.nonce = Crypt.encryptUsingKey(var2, var3);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.keybytes = var1.readByteArray();
      this.nonce = var1.readByteArray();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByteArray(this.keybytes);
      var1.writeByteArray(this.nonce);
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleKey(this);
   }

   public SecretKey getSecretKey(PrivateKey var1) throws CryptException {
      return Crypt.decryptByteToSecretKey(var1, this.keybytes);
   }

   public byte[] getNonce(PrivateKey var1) throws CryptException {
      return Crypt.decryptUsingKey(var1, this.nonce);
   }
}
