package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S36PacketSignEditorOpen implements Packet<INetHandlerPlayClient> {
   private BlockPos field_179778_a;

   public S36PacketSignEditorOpen() {
      super();
   }

   public S36PacketSignEditorOpen(BlockPos var1) {
      super();
      this.field_179778_a = var1;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147268_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179778_a = var1.func_179259_c();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179778_a);
   }

   public BlockPos func_179777_a() {
      return this.field_179778_a;
   }
}
