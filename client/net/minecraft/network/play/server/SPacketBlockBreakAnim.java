package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;

public class SPacketBlockBreakAnim implements Packet<INetHandlerPlayClient> {
   private int field_148852_a;
   private BlockPos field_179822_b;
   private int field_148849_e;

   public SPacketBlockBreakAnim() {
      super();
   }

   public SPacketBlockBreakAnim(int var1, BlockPos var2, int var3) {
      super();
      this.field_148852_a = var1;
      this.field_179822_b = var2;
      this.field_148849_e = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148852_a = var1.func_150792_a();
      this.field_179822_b = var1.func_179259_c();
      this.field_148849_e = var1.readUnsignedByte();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_148852_a);
      var1.func_179255_a(this.field_179822_b);
      var1.writeByte(this.field_148849_e);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147294_a(this);
   }

   public int func_148845_c() {
      return this.field_148852_a;
   }

   public BlockPos func_179821_b() {
      return this.field_179822_b;
   }

   public int func_148846_g() {
      return this.field_148849_e;
   }
}
