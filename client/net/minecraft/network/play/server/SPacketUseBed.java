package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SPacketUseBed implements Packet<INetHandlerPlayClient> {
   private int field_149097_a;
   private BlockPos field_179799_b;

   public SPacketUseBed() {
      super();
   }

   public SPacketUseBed(EntityPlayer var1, BlockPos var2) {
      super();
      this.field_149097_a = var1.func_145782_y();
      this.field_179799_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149097_a = var1.func_150792_a();
      this.field_179799_b = var1.func_179259_c();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149097_a);
      var1.func_179255_a(this.field_179799_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147278_a(this);
   }

   public EntityPlayer func_149091_a(World var1) {
      return (EntityPlayer)var1.func_73045_a(this.field_149097_a);
   }

   public BlockPos func_179798_a() {
      return this.field_179799_b;
   }
}
