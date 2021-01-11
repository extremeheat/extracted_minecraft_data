package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class S10PacketSpawnPainting implements Packet<INetHandlerPlayClient> {
   private int field_148973_a;
   private BlockPos field_179838_b;
   private EnumFacing field_179839_c;
   private String field_148968_f;

   public S10PacketSpawnPainting() {
      super();
   }

   public S10PacketSpawnPainting(EntityPainting var1) {
      super();
      this.field_148973_a = var1.func_145782_y();
      this.field_179838_b = var1.func_174857_n();
      this.field_179839_c = var1.field_174860_b;
      this.field_148968_f = var1.field_70522_e.field_75702_A;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148973_a = var1.func_150792_a();
      this.field_148968_f = var1.func_150789_c(EntityPainting.EnumArt.field_180001_A);
      this.field_179838_b = var1.func_179259_c();
      this.field_179839_c = EnumFacing.func_176731_b(var1.readUnsignedByte());
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_148973_a);
      var1.func_180714_a(this.field_148968_f);
      var1.func_179255_a(this.field_179838_b);
      var1.writeByte(this.field_179839_c.func_176736_b());
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147288_a(this);
   }

   public int func_148965_c() {
      return this.field_148973_a;
   }

   public BlockPos func_179837_b() {
      return this.field_179838_b;
   }

   public EnumFacing func_179836_c() {
      return this.field_179839_c;
   }

   public String func_148961_h() {
      return this.field_148968_f;
   }
}
