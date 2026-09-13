package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;

public class S01PacketJoinGame extends Packet {
   private int field_149206_a;
   private boolean field_149204_b;
   private WorldSettings$GameType field_149205_c;
   private int field_149202_d;
   private EnumDifficulty field_149203_e;
   private int field_149200_f;
   private WorldType field_149201_g;

   public S01PacketJoinGame() {
      super();
   }

   public S01PacketJoinGame(int var1, WorldSettings$GameType var2, boolean var3, int var4, EnumDifficulty var5, int var6, WorldType var7) {
      super();
      this.field_149206_a = var1;
      this.field_149202_d = var4;
      this.field_149203_e = var5;
      this.field_149205_c = var2;
      this.field_149200_f = var6;
      this.field_149204_b = var3;
      this.field_149201_g = var7;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149206_a = var1.readInt();
      int var2 = var1.readUnsignedByte();
      this.field_149204_b = (var2 & 8) == 8;
      var2 &= -9;
      this.field_149205_c = WorldSettings$GameType.func_77146_a(var2);
      this.field_149202_d = var1.readByte();
      this.field_149203_e = EnumDifficulty.func_151523_a(var1.readUnsignedByte());
      this.field_149200_f = var1.readUnsignedByte();
      this.field_149201_g = WorldType.func_77130_a(var1.func_150789_c(16));
      if (this.field_149201_g == null) {
         this.field_149201_g = WorldType.field_77137_b;
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149206_a);
      int var2 = this.field_149205_c.func_77148_a();
      if (this.field_149204_b) {
         var2 |= 8;
      }

      var1.writeByte(var2);
      var1.writeByte(this.field_149202_d);
      var1.writeByte(this.field_149203_e.func_151525_a());
      var1.writeByte(this.field_149200_f);
      var1.func_150785_a(this.field_149201_g.func_77127_a());
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147282_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format(
         "eid=%d, gameType=%d, hardcore=%b, dimension=%d, difficulty=%s, maxplayers=%d",
         this.field_149206_a,
         this.field_149205_c.func_77148_a(),
         this.field_149204_b,
         this.field_149202_d,
         this.field_149203_e,
         this.field_149200_f
      );
   }

   public int func_149197_c() {
      return this.field_149206_a;
   }

   public boolean func_149195_d() {
      return this.field_149204_b;
   }

   public WorldSettings$GameType func_149198_e() {
      return this.field_149205_c;
   }

   public int func_149194_f() {
      return this.field_149202_d;
   }

   public EnumDifficulty func_149192_g() {
      return this.field_149203_e;
   }

   public int func_149193_h() {
      return this.field_149200_f;
   }

   public WorldType func_149196_i() {
      return this.field_149201_g;
   }
}
