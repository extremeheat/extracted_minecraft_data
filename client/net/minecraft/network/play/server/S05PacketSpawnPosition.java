package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S05PacketSpawnPosition implements Packet<INetHandlerPlayClient> {
   private BlockPos field_179801_a;

   public S05PacketSpawnPosition() {
      super();
   }

   public S05PacketSpawnPosition(BlockPos var1) {
      super();
      this.field_179801_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179801_a = var1.func_179259_c();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179801_a);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147271_a(this);
   }

   public BlockPos func_179800_a() {
      return this.field_179801_a;
   }
}
