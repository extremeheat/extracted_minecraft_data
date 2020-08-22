package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClientboundExplodePacket implements Packet {
   private double x;
   private double y;
   private double z;
   private float power;
   private List toBlow;
   private float knockbackX;
   private float knockbackY;
   private float knockbackZ;

   public ClientboundExplodePacket() {
   }

   public ClientboundExplodePacket(double var1, double var3, double var5, float var7, List var8, Vec3 var9) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.power = var7;
      this.toBlow = Lists.newArrayList(var8);
      if (var9 != null) {
         this.knockbackX = (float)var9.x;
         this.knockbackY = (float)var9.y;
         this.knockbackZ = (float)var9.z;
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.x = (double)var1.readFloat();
      this.y = (double)var1.readFloat();
      this.z = (double)var1.readFloat();
      this.power = var1.readFloat();
      int var2 = var1.readInt();
      this.toBlow = Lists.newArrayListWithCapacity(var2);
      int var3 = Mth.floor(this.x);
      int var4 = Mth.floor(this.y);
      int var5 = Mth.floor(this.z);

      for(int var6 = 0; var6 < var2; ++var6) {
         int var7 = var1.readByte() + var3;
         int var8 = var1.readByte() + var4;
         int var9 = var1.readByte() + var5;
         this.toBlow.add(new BlockPos(var7, var8, var9));
      }

      this.knockbackX = var1.readFloat();
      this.knockbackY = var1.readFloat();
      this.knockbackZ = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeFloat((float)this.x);
      var1.writeFloat((float)this.y);
      var1.writeFloat((float)this.z);
      var1.writeFloat(this.power);
      var1.writeInt(this.toBlow.size());
      int var2 = Mth.floor(this.x);
      int var3 = Mth.floor(this.y);
      int var4 = Mth.floor(this.z);
      Iterator var5 = this.toBlow.iterator();

      while(var5.hasNext()) {
         BlockPos var6 = (BlockPos)var5.next();
         int var7 = var6.getX() - var2;
         int var8 = var6.getY() - var3;
         int var9 = var6.getZ() - var4;
         var1.writeByte(var7);
         var1.writeByte(var8);
         var1.writeByte(var9);
      }

      var1.writeFloat(this.knockbackX);
      var1.writeFloat(this.knockbackY);
      var1.writeFloat(this.knockbackZ);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleExplosion(this);
   }

   public float getKnockbackX() {
      return this.knockbackX;
   }

   public float getKnockbackY() {
      return this.knockbackY;
   }

   public float getKnockbackZ() {
      return this.knockbackZ;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getPower() {
      return this.power;
   }

   public List getToBlow() {
      return this.toBlow;
   }
}
