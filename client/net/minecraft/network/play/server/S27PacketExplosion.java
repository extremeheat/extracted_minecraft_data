package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class S27PacketExplosion implements Packet<INetHandlerPlayClient> {
   private double field_149158_a;
   private double field_149156_b;
   private double field_149157_c;
   private float field_149154_d;
   private List<BlockPos> field_149155_e;
   private float field_149152_f;
   private float field_149153_g;
   private float field_149159_h;

   public S27PacketExplosion() {
      super();
   }

   public S27PacketExplosion(double var1, double var3, double var5, float var7, List<BlockPos> var8, Vec3 var9) {
      super();
      this.field_149158_a = var1;
      this.field_149156_b = var3;
      this.field_149157_c = var5;
      this.field_149154_d = var7;
      this.field_149155_e = Lists.newArrayList(var8);
      if (var9 != null) {
         this.field_149152_f = (float)var9.field_72450_a;
         this.field_149153_g = (float)var9.field_72448_b;
         this.field_149159_h = (float)var9.field_72449_c;
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149158_a = (double)var1.readFloat();
      this.field_149156_b = (double)var1.readFloat();
      this.field_149157_c = (double)var1.readFloat();
      this.field_149154_d = var1.readFloat();
      int var2 = var1.readInt();
      this.field_149155_e = Lists.newArrayListWithCapacity(var2);
      int var3 = (int)this.field_149158_a;
      int var4 = (int)this.field_149156_b;
      int var5 = (int)this.field_149157_c;

      for(int var6 = 0; var6 < var2; ++var6) {
         int var7 = var1.readByte() + var3;
         int var8 = var1.readByte() + var4;
         int var9 = var1.readByte() + var5;
         this.field_149155_e.add(new BlockPos(var7, var8, var9));
      }

      this.field_149152_f = var1.readFloat();
      this.field_149153_g = var1.readFloat();
      this.field_149159_h = var1.readFloat();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeFloat((float)this.field_149158_a);
      var1.writeFloat((float)this.field_149156_b);
      var1.writeFloat((float)this.field_149157_c);
      var1.writeFloat(this.field_149154_d);
      var1.writeInt(this.field_149155_e.size());
      int var2 = (int)this.field_149158_a;
      int var3 = (int)this.field_149156_b;
      int var4 = (int)this.field_149157_c;
      Iterator var5 = this.field_149155_e.iterator();

      while(var5.hasNext()) {
         BlockPos var6 = (BlockPos)var5.next();
         int var7 = var6.func_177958_n() - var2;
         int var8 = var6.func_177956_o() - var3;
         int var9 = var6.func_177952_p() - var4;
         var1.writeByte(var7);
         var1.writeByte(var8);
         var1.writeByte(var9);
      }

      var1.writeFloat(this.field_149152_f);
      var1.writeFloat(this.field_149153_g);
      var1.writeFloat(this.field_149159_h);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147283_a(this);
   }

   public float func_149149_c() {
      return this.field_149152_f;
   }

   public float func_149144_d() {
      return this.field_149153_g;
   }

   public float func_149147_e() {
      return this.field_149159_h;
   }

   public double func_149148_f() {
      return this.field_149158_a;
   }

   public double func_149143_g() {
      return this.field_149156_b;
   }

   public double func_149145_h() {
      return this.field_149157_c;
   }

   public float func_149146_i() {
      return this.field_149154_d;
   }

   public List<BlockPos> func_149150_j() {
      return this.field_149155_e;
   }
}
