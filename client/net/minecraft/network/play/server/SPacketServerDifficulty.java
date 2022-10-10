package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;

public class SPacketServerDifficulty implements Packet<INetHandlerPlayClient> {
   private EnumDifficulty field_179833_a;
   private boolean field_179832_b;

   public SPacketServerDifficulty() {
      super();
   }

   public SPacketServerDifficulty(EnumDifficulty var1, boolean var2) {
      super();
      this.field_179833_a = var1;
      this.field_179832_b = var2;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175101_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179833_a = EnumDifficulty.func_151523_a(var1.readUnsignedByte());
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_179833_a.func_151525_a());
   }

   public boolean func_179830_a() {
      return this.field_179832_b;
   }

   public EnumDifficulty func_179831_b() {
      return this.field_179833_a;
   }
}
