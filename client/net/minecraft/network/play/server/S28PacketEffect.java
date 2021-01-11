package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S28PacketEffect implements Packet<INetHandlerPlayClient> {
   private int field_149251_a;
   private BlockPos field_179747_b;
   private int field_149249_b;
   private boolean field_149246_f;

   public S28PacketEffect() {
      super();
   }

   public S28PacketEffect(int var1, BlockPos var2, int var3, boolean var4) {
      super();
      this.field_149251_a = var1;
      this.field_179747_b = var2;
      this.field_149249_b = var3;
      this.field_149246_f = var4;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149251_a = var1.readInt();
      this.field_179747_b = var1.func_179259_c();
      this.field_149249_b = var1.readInt();
      this.field_149246_f = var1.readBoolean();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeInt(this.field_149251_a);
      var1.func_179255_a(this.field_179747_b);
      var1.writeInt(this.field_149249_b);
      var1.writeBoolean(this.field_149246_f);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147277_a(this);
   }

   public boolean func_149244_c() {
      return this.field_149246_f;
   }

   public int func_149242_d() {
      return this.field_149251_a;
   }

   public int func_149241_e() {
      return this.field_149249_b;
   }

   public BlockPos func_179746_d() {
      return this.field_179747_b;
   }
}
