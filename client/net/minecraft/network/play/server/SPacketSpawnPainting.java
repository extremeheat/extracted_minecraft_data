package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;

public class SPacketSpawnPainting implements Packet<INetHandlerPlayClient> {
   private int field_148973_a;
   private UUID field_186896_b;
   private BlockPos field_179838_b;
   private EnumFacing field_179839_c;
   private int field_148968_f;

   public SPacketSpawnPainting() {
      super();
   }

   public SPacketSpawnPainting(EntityPainting var1) {
      super();
      this.field_148973_a = var1.func_145782_y();
      this.field_186896_b = var1.func_110124_au();
      this.field_179838_b = var1.func_174857_n();
      this.field_179839_c = var1.field_174860_b;
      this.field_148968_f = IRegistry.field_212620_i.func_148757_b(var1.field_70522_e);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148973_a = var1.func_150792_a();
      this.field_186896_b = var1.func_179253_g();
      this.field_148968_f = var1.func_150792_a();
      this.field_179838_b = var1.func_179259_c();
      this.field_179839_c = EnumFacing.func_176731_b(var1.readUnsignedByte());
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_148973_a);
      var1.func_179252_a(this.field_186896_b);
      var1.func_150787_b(this.field_148968_f);
      var1.func_179255_a(this.field_179838_b);
      var1.writeByte(this.field_179839_c.func_176736_b());
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147288_a(this);
   }

   public int func_148965_c() {
      return this.field_148973_a;
   }

   public UUID func_186895_b() {
      return this.field_186896_b;
   }

   public BlockPos func_179837_b() {
      return this.field_179838_b;
   }

   public EnumFacing func_179836_c() {
      return this.field_179839_c;
   }

   public PaintingType func_201063_e() {
      return (PaintingType)IRegistry.field_212620_i.func_148754_a(this.field_148968_f);
   }
}
