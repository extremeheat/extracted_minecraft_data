package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;

public class SPacketBlockAction implements Packet<INetHandlerPlayClient> {
   private BlockPos field_179826_a;
   private int field_148872_d;
   private int field_148873_e;
   private Block field_148871_f;

   public SPacketBlockAction() {
      super();
   }

   public SPacketBlockAction(BlockPos var1, Block var2, int var3, int var4) {
      super();
      this.field_179826_a = var1;
      this.field_148871_f = var2;
      this.field_148872_d = var3;
      this.field_148873_e = var4;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179826_a = var1.func_179259_c();
      this.field_148872_d = var1.readUnsignedByte();
      this.field_148873_e = var1.readUnsignedByte();
      this.field_148871_f = (Block)IRegistry.field_212618_g.func_148754_a(var1.func_150792_a());
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179826_a);
      var1.writeByte(this.field_148872_d);
      var1.writeByte(this.field_148873_e);
      var1.func_150787_b(IRegistry.field_212618_g.func_148757_b(this.field_148871_f));
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147261_a(this);
   }

   public BlockPos func_179825_a() {
      return this.field_179826_a;
   }

   public int func_148869_g() {
      return this.field_148872_d;
   }

   public int func_148864_h() {
      return this.field_148873_e;
   }

   public Block func_148868_c() {
      return this.field_148871_f;
   }
}
