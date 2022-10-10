package net.minecraft.network.login.client;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.util.CryptManager;

public class CPacketEncryptionResponse implements Packet<INetHandlerLoginServer> {
   private byte[] field_149302_a = new byte[0];
   private byte[] field_149301_b = new byte[0];

   public CPacketEncryptionResponse() {
      super();
   }

   public CPacketEncryptionResponse(SecretKey var1, PublicKey var2, byte[] var3) {
      super();
      this.field_149302_a = CryptManager.func_75894_a(var2, var1.getEncoded());
      this.field_149301_b = CryptManager.func_75894_a(var2, var3);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149302_a = var1.func_179251_a();
      this.field_149301_b = var1.func_179251_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179250_a(this.field_149302_a);
      var1.func_179250_a(this.field_149301_b);
   }

   public void func_148833_a(INetHandlerLoginServer var1) {
      var1.func_147315_a(this);
   }

   public SecretKey func_149300_a(PrivateKey var1) {
      return CryptManager.func_75887_a(var1, this.field_149302_a);
   }

   public byte[] func_149299_b(PrivateKey var1) {
      return var1 == null ? this.field_149301_b : CryptManager.func_75889_b(var1, this.field_149301_b);
   }
}
