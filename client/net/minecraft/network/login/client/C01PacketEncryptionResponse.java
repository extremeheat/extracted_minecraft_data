package net.minecraft.network.login.client;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.util.CryptManager;

public class C01PacketEncryptionResponse extends Packet {
   private byte[] field_149302_a = new byte[0];
   private byte[] field_149301_b = new byte[0];

   public C01PacketEncryptionResponse() {
      super();
   }

   public C01PacketEncryptionResponse(SecretKey var1, PublicKey var2, byte[] var3) {
      super();
      this.field_149302_a = CryptManager.func_75894_a(var2, var1.getEncoded());
      this.field_149301_b = CryptManager.func_75894_a(var2, var3);
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149302_a = func_148834_a(var1);
      this.field_149301_b = func_148834_a(var1);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      func_148838_a(var1, this.field_149302_a);
      func_148838_a(var1, this.field_149301_b);
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
