package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityAttach implements Packet<INetHandlerPlayClient> {
   private int field_149406_b;
   private int field_149407_c;

   public SPacketEntityAttach() {
      super();
   }

   public SPacketEntityAttach(Entity var1, @Nullable Entity var2) {
      super();
      this.field_149406_b = var1.func_145782_y();
      this.field_149407_c = var2 != null ? var2.func_145782_y() : -1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149406_b = var1.readInt();
      this.field_149407_c = var1.readInt();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeInt(this.field_149406_b);
      var1.writeInt(this.field_149407_c);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147243_a(this);
   }

   public int func_149403_d() {
      return this.field_149406_b;
   }

   public int func_149402_e() {
      return this.field_149407_c;
   }
}
