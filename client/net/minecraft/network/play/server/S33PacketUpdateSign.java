package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class S33PacketUpdateSign implements Packet<INetHandlerPlayClient> {
   private World field_179706_a;
   private BlockPos field_179705_b;
   private IChatComponent[] field_149349_d;

   public S33PacketUpdateSign() {
      super();
   }

   public S33PacketUpdateSign(World var1, BlockPos var2, IChatComponent[] var3) {
      super();
      this.field_179706_a = var1;
      this.field_179705_b = var2;
      this.field_149349_d = new IChatComponent[]{var3[0], var3[1], var3[2], var3[3]};
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179705_b = var1.func_179259_c();
      this.field_149349_d = new IChatComponent[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         this.field_149349_d[var2] = var1.func_179258_d();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179705_b);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.func_179256_a(this.field_149349_d[var2]);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147248_a(this);
   }

   public BlockPos func_179704_a() {
      return this.field_179705_b;
   }

   public IChatComponent[] func_180753_b() {
      return this.field_149349_d;
   }
}
