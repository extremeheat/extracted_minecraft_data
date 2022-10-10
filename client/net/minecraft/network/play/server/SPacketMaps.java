package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class SPacketMaps implements Packet<INetHandlerPlayClient> {
   private int field_149191_a;
   private byte field_179739_b;
   private boolean field_186950_c;
   private MapDecoration[] field_179740_c;
   private int field_179737_d;
   private int field_179738_e;
   private int field_179735_f;
   private int field_179736_g;
   private byte[] field_179741_h;

   public SPacketMaps() {
      super();
   }

   public SPacketMaps(int var1, byte var2, boolean var3, Collection<MapDecoration> var4, byte[] var5, int var6, int var7, int var8, int var9) {
      super();
      this.field_149191_a = var1;
      this.field_179739_b = var2;
      this.field_186950_c = var3;
      this.field_179740_c = (MapDecoration[])var4.toArray(new MapDecoration[var4.size()]);
      this.field_179737_d = var6;
      this.field_179738_e = var7;
      this.field_179735_f = var8;
      this.field_179736_g = var9;
      this.field_179741_h = new byte[var8 * var9];

      for(int var10 = 0; var10 < var8; ++var10) {
         for(int var11 = 0; var11 < var9; ++var11) {
            this.field_179741_h[var10 + var11 * var8] = var5[var6 + var10 + (var7 + var11) * 128];
         }
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149191_a = var1.func_150792_a();
      this.field_179739_b = var1.readByte();
      this.field_186950_c = var1.readBoolean();
      this.field_179740_c = new MapDecoration[var1.func_150792_a()];

      for(int var2 = 0; var2 < this.field_179740_c.length; ++var2) {
         MapDecoration.Type var3 = (MapDecoration.Type)var1.func_179257_a(MapDecoration.Type.class);
         this.field_179740_c[var2] = new MapDecoration(var3, var1.readByte(), var1.readByte(), (byte)(var1.readByte() & 15), var1.readBoolean() ? var1.func_179258_d() : null);
      }

      this.field_179735_f = var1.readUnsignedByte();
      if (this.field_179735_f > 0) {
         this.field_179736_g = var1.readUnsignedByte();
         this.field_179737_d = var1.readUnsignedByte();
         this.field_179738_e = var1.readUnsignedByte();
         this.field_179741_h = var1.func_179251_a();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149191_a);
      var1.writeByte(this.field_179739_b);
      var1.writeBoolean(this.field_186950_c);
      var1.func_150787_b(this.field_179740_c.length);
      MapDecoration[] var2 = this.field_179740_c;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MapDecoration var5 = var2[var4];
         var1.func_179249_a(var5.func_191179_b());
         var1.writeByte(var5.func_176112_b());
         var1.writeByte(var5.func_176113_c());
         var1.writeByte(var5.func_176111_d() & 15);
         if (var5.func_204309_g() != null) {
            var1.writeBoolean(true);
            var1.func_179256_a(var5.func_204309_g());
         } else {
            var1.writeBoolean(false);
         }
      }

      var1.writeByte(this.field_179735_f);
      if (this.field_179735_f > 0) {
         var1.writeByte(this.field_179736_g);
         var1.writeByte(this.field_179737_d);
         var1.writeByte(this.field_179738_e);
         var1.func_179250_a(this.field_179741_h);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147264_a(this);
   }

   public int func_149188_c() {
      return this.field_149191_a;
   }

   public void func_179734_a(MapData var1) {
      var1.field_76197_d = this.field_179739_b;
      var1.field_186210_e = this.field_186950_c;
      var1.field_76203_h.clear();

      int var2;
      for(var2 = 0; var2 < this.field_179740_c.length; ++var2) {
         MapDecoration var3 = this.field_179740_c[var2];
         var1.field_76203_h.put("icon-" + var2, var3);
      }

      for(var2 = 0; var2 < this.field_179735_f; ++var2) {
         for(int var4 = 0; var4 < this.field_179736_g; ++var4) {
            var1.field_76198_e[this.field_179737_d + var2 + (this.field_179738_e + var4) * 128] = this.field_179741_h[var2 + var4 * this.field_179735_f];
         }
      }

   }
}
