package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.StringUtils;

public class C14PacketTabComplete implements Packet<INetHandlerPlayServer> {
   private String field_149420_a;
   private BlockPos field_179710_b;

   public C14PacketTabComplete() {
      super();
   }

   public C14PacketTabComplete(String var1) {
      this(var1, (BlockPos)null);
   }

   public C14PacketTabComplete(String var1, BlockPos var2) {
      super();
      this.field_149420_a = var1;
      this.field_179710_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149420_a = var1.func_150789_c(32767);
      boolean var2 = var1.readBoolean();
      if (var2) {
         this.field_179710_b = var1.func_179259_c();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(StringUtils.substring(this.field_149420_a, 0, 32767));
      boolean var2 = this.field_179710_b != null;
      var1.writeBoolean(var2);
      if (var2) {
         var1.func_179255_a(this.field_179710_b);
      }

   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147341_a(this);
   }

   public String func_149419_c() {
      return this.field_149420_a;
   }

   public BlockPos func_179709_b() {
      return this.field_179710_b;
   }
}
