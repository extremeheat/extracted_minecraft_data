package net.minecraft.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

class TileEntityCommandBlock$1 extends CommandBlockLogic {
   TileEntityCommandBlock$1(TileEntityCommandBlock var1) {
      super();
      this.field_145767_a = var1;
   }

   @Override
   public ChunkCoordinates func_82114_b() {
      return new ChunkCoordinates(this.field_145767_a.field_145851_c, this.field_145767_a.field_145848_d, this.field_145767_a.field_145849_e);
   }

   @Override
   public World func_130014_f_() {
      return this.field_145767_a.func_145831_w();
   }

   @Override
   public void func_145752_a(String var1) {
      super.func_145752_a(var1);
      this.field_145767_a.func_70296_d();
   }

   @Override
   public void func_145756_e() {
      this.field_145767_a
         .func_145831_w()
         .func_147471_g(this.field_145767_a.field_145851_c, this.field_145767_a.field_145848_d, this.field_145767_a.field_145849_e);
   }

   @Override
   public int func_145751_f() {
      return 0;
   }

   @Override
   public void func_145757_a(ByteBuf var1) {
      var1.writeInt(this.field_145767_a.field_145851_c);
      var1.writeInt(this.field_145767_a.field_145848_d);
      var1.writeInt(this.field_145767_a.field_145849_e);
   }
}
