package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class S23PacketBlockChange implements Packet<INetHandlerPlayClient> {
   private BlockPos field_179828_a;
   private IBlockState field_148883_d;

   public S23PacketBlockChange() {
      super();
   }

   public S23PacketBlockChange(World var1, BlockPos var2) {
      super();
      this.field_179828_a = var2;
      this.field_148883_d = var1.func_180495_p(var2);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179828_a = var1.func_179259_c();
      this.field_148883_d = (IBlockState)Block.field_176229_d.func_148745_a(var1.func_150792_a());
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179828_a);
      var1.func_150787_b(Block.field_176229_d.func_148747_b(this.field_148883_d));
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147234_a(this);
   }

   public IBlockState func_180728_a() {
      return this.field_148883_d;
   }

   public BlockPos func_179827_b() {
      return this.field_179828_a;
   }
}
