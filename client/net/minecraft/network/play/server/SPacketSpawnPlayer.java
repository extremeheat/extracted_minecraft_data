package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSpawnPlayer implements Packet<INetHandlerPlayClient> {
   private int field_148957_a;
   private UUID field_179820_b;
   private double field_148956_c;
   private double field_148953_d;
   private double field_148954_e;
   private byte field_148951_f;
   private byte field_148952_g;
   private EntityDataManager field_148960_i;
   private List<EntityDataManager.DataEntry<?>> field_148958_j;

   public SPacketSpawnPlayer() {
      super();
   }

   public SPacketSpawnPlayer(EntityPlayer var1) {
      super();
      this.field_148957_a = var1.func_145782_y();
      this.field_179820_b = var1.func_146103_bH().getId();
      this.field_148956_c = var1.field_70165_t;
      this.field_148953_d = var1.field_70163_u;
      this.field_148954_e = var1.field_70161_v;
      this.field_148951_f = (byte)((int)(var1.field_70177_z * 256.0F / 360.0F));
      this.field_148952_g = (byte)((int)(var1.field_70125_A * 256.0F / 360.0F));
      this.field_148960_i = var1.func_184212_Q();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148957_a = var1.func_150792_a();
      this.field_179820_b = var1.func_179253_g();
      this.field_148956_c = var1.readDouble();
      this.field_148953_d = var1.readDouble();
      this.field_148954_e = var1.readDouble();
      this.field_148951_f = var1.readByte();
      this.field_148952_g = var1.readByte();
      this.field_148958_j = EntityDataManager.func_187215_b(var1);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_148957_a);
      var1.func_179252_a(this.field_179820_b);
      var1.writeDouble(this.field_148956_c);
      var1.writeDouble(this.field_148953_d);
      var1.writeDouble(this.field_148954_e);
      var1.writeByte(this.field_148951_f);
      var1.writeByte(this.field_148952_g);
      this.field_148960_i.func_187216_a(var1);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147237_a(this);
   }

   @Nullable
   public List<EntityDataManager.DataEntry<?>> func_148944_c() {
      return this.field_148958_j;
   }

   public int func_148943_d() {
      return this.field_148957_a;
   }

   public UUID func_179819_c() {
      return this.field_179820_b;
   }

   public double func_186898_d() {
      return this.field_148956_c;
   }

   public double func_186897_e() {
      return this.field_148953_d;
   }

   public double func_186899_f() {
      return this.field_148954_e;
   }

   public byte func_148941_i() {
      return this.field_148951_f;
   }

   public byte func_148945_j() {
      return this.field_148952_g;
   }
}
