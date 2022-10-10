package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;

public class SPacketJoinGame implements Packet<INetHandlerPlayClient> {
   private int field_149206_a;
   private boolean field_149204_b;
   private GameType field_149205_c;
   private DimensionType field_149202_d;
   private EnumDifficulty field_149203_e;
   private int field_149200_f;
   private WorldType field_149201_g;
   private boolean field_179745_h;

   public SPacketJoinGame() {
      super();
   }

   public SPacketJoinGame(int var1, GameType var2, boolean var3, DimensionType var4, EnumDifficulty var5, int var6, WorldType var7, boolean var8) {
      super();
      this.field_149206_a = var1;
      this.field_149202_d = var4;
      this.field_149203_e = var5;
      this.field_149205_c = var2;
      this.field_149200_f = var6;
      this.field_149204_b = var3;
      this.field_149201_g = var7;
      this.field_179745_h = var8;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149206_a = var1.readInt();
      short var2 = var1.readUnsignedByte();
      this.field_149204_b = (var2 & 8) == 8;
      int var3 = var2 & -9;
      this.field_149205_c = GameType.func_77146_a(var3);
      this.field_149202_d = DimensionType.func_186069_a(var1.readInt());
      this.field_149203_e = EnumDifficulty.func_151523_a(var1.readUnsignedByte());
      this.field_149200_f = var1.readUnsignedByte();
      this.field_149201_g = WorldType.func_77130_a(var1.func_150789_c(16));
      if (this.field_149201_g == null) {
         this.field_149201_g = WorldType.field_77137_b;
      }

      this.field_179745_h = var1.readBoolean();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeInt(this.field_149206_a);
      int var2 = this.field_149205_c.func_77148_a();
      if (this.field_149204_b) {
         var2 |= 8;
      }

      var1.writeByte(var2);
      var1.writeInt(this.field_149202_d.func_186068_a());
      var1.writeByte(this.field_149203_e.func_151525_a());
      var1.writeByte(this.field_149200_f);
      var1.func_180714_a(this.field_149201_g.func_211888_a());
      var1.writeBoolean(this.field_179745_h);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147282_a(this);
   }

   public int func_149197_c() {
      return this.field_149206_a;
   }

   public boolean func_149195_d() {
      return this.field_149204_b;
   }

   public GameType func_149198_e() {
      return this.field_149205_c;
   }

   public DimensionType func_212642_e() {
      return this.field_149202_d;
   }

   public EnumDifficulty func_149192_g() {
      return this.field_149203_e;
   }

   public WorldType func_149196_i() {
      return this.field_149201_g;
   }

   public boolean func_179744_h() {
      return this.field_179745_h;
   }
}
