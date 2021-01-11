package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0CPacketSpawnPlayer implements Packet<INetHandlerPlayClient> {
   private int field_148957_a;
   private UUID field_179820_b;
   private int field_148956_c;
   private int field_148953_d;
   private int field_148954_e;
   private byte field_148951_f;
   private byte field_148952_g;
   private int field_148959_h;
   private DataWatcher field_148960_i;
   private List<DataWatcher.WatchableObject> field_148958_j;

   public S0CPacketSpawnPlayer() {
      super();
   }

   public S0CPacketSpawnPlayer(EntityPlayer var1) {
      super();
      this.field_148957_a = var1.func_145782_y();
      this.field_179820_b = var1.func_146103_bH().getId();
      this.field_148956_c = MathHelper.func_76128_c(var1.field_70165_t * 32.0D);
      this.field_148953_d = MathHelper.func_76128_c(var1.field_70163_u * 32.0D);
      this.field_148954_e = MathHelper.func_76128_c(var1.field_70161_v * 32.0D);
      this.field_148951_f = (byte)((int)(var1.field_70177_z * 256.0F / 360.0F));
      this.field_148952_g = (byte)((int)(var1.field_70125_A * 256.0F / 360.0F));
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      this.field_148959_h = var2 == null ? 0 : Item.func_150891_b(var2.func_77973_b());
      this.field_148960_i = var1.func_70096_w();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148957_a = var1.func_150792_a();
      this.field_179820_b = var1.func_179253_g();
      this.field_148956_c = var1.readInt();
      this.field_148953_d = var1.readInt();
      this.field_148954_e = var1.readInt();
      this.field_148951_f = var1.readByte();
      this.field_148952_g = var1.readByte();
      this.field_148959_h = var1.readShort();
      this.field_148958_j = DataWatcher.func_151508_b(var1);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_148957_a);
      var1.func_179252_a(this.field_179820_b);
      var1.writeInt(this.field_148956_c);
      var1.writeInt(this.field_148953_d);
      var1.writeInt(this.field_148954_e);
      var1.writeByte(this.field_148951_f);
      var1.writeByte(this.field_148952_g);
      var1.writeShort(this.field_148959_h);
      this.field_148960_i.func_151509_a(var1);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147237_a(this);
   }

   public List<DataWatcher.WatchableObject> func_148944_c() {
      if (this.field_148958_j == null) {
         this.field_148958_j = this.field_148960_i.func_75685_c();
      }

      return this.field_148958_j;
   }

   public int func_148943_d() {
      return this.field_148957_a;
   }

   public UUID func_179819_c() {
      return this.field_179820_b;
   }

   public int func_148942_f() {
      return this.field_148956_c;
   }

   public int func_148949_g() {
      return this.field_148953_d;
   }

   public int func_148946_h() {
      return this.field_148954_e;
   }

   public byte func_148941_i() {
      return this.field_148951_f;
   }

   public byte func_148945_j() {
      return this.field_148952_g;
   }

   public int func_148947_k() {
      return this.field_148959_h;
   }
}
