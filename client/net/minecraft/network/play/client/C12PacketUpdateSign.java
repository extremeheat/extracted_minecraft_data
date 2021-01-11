package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

public class C12PacketUpdateSign implements Packet<INetHandlerPlayServer> {
   private BlockPos field_179723_a;
   private IChatComponent[] field_149590_d;

   public C12PacketUpdateSign() {
      super();
   }

   public C12PacketUpdateSign(BlockPos var1, IChatComponent[] var2) {
      super();
      this.field_179723_a = var1;
      this.field_149590_d = new IChatComponent[]{var2[0], var2[1], var2[2], var2[3]};
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179723_a = var1.func_179259_c();
      this.field_149590_d = new IChatComponent[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         String var3 = var1.func_150789_c(384);
         IChatComponent var4 = IChatComponent.Serializer.func_150699_a(var3);
         this.field_149590_d[var2] = var4;
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179723_a);

      for(int var2 = 0; var2 < 4; ++var2) {
         IChatComponent var3 = this.field_149590_d[var2];
         String var4 = IChatComponent.Serializer.func_150696_a(var3);
         var1.func_180714_a(var4);
      }

   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147343_a(this);
   }

   public BlockPos func_179722_a() {
      return this.field_179723_a;
   }

   public IChatComponent[] func_180768_b() {
      return this.field_149590_d;
   }
}
