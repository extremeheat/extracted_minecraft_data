package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2BPacketChangeGameState implements Packet<INetHandlerPlayClient> {
   public static final String[] field_149142_a = new String[]{"tile.bed.notValid"};
   private int field_149140_b;
   private float field_149141_c;

   public S2BPacketChangeGameState() {
      super();
   }

   public S2BPacketChangeGameState(int var1, float var2) {
      super();
      this.field_149140_b = var1;
      this.field_149141_c = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149140_b = var1.readUnsignedByte();
      this.field_149141_c = var1.readFloat();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_149140_b);
      var1.writeFloat(this.field_149141_c);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147252_a(this);
   }

   public int func_149138_c() {
      return this.field_149140_b;
   }

   public float func_149137_d() {
      return this.field_149141_c;
   }
}
