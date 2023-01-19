package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClientboundExplodePacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float power;
   private final List<BlockPos> toBlow;
   private final float knockbackX;
   private final float knockbackY;
   private final float knockbackZ;

   public ClientboundExplodePacket(double var1, double var3, double var5, float var7, List<BlockPos> var8, @Nullable Vec3 var9) {
      super();
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.power = var7;
      this.toBlow = Lists.newArrayList(var8);
      if (var9 != null) {
         this.knockbackX = (float)var9.x;
         this.knockbackY = (float)var9.y;
         this.knockbackZ = (float)var9.z;
      } else {
         this.knockbackX = 0.0F;
         this.knockbackY = 0.0F;
         this.knockbackZ = 0.0F;
      }
   }

   public ClientboundExplodePacket(FriendlyByteBuf var1) {
      super();
      this.x = (double)var1.readFloat();
      this.y = (double)var1.readFloat();
      this.z = (double)var1.readFloat();
      this.power = var1.readFloat();
      int var2 = Mth.floor(this.x);
      int var3 = Mth.floor(this.y);
      int var4 = Mth.floor(this.z);
      this.toBlow = var1.readList(var3x -> {
         int var4x = var3x.readByte() + var2;
         int var5 = var3x.readByte() + var3;
         int var6 = var3x.readByte() + var4;
         return new BlockPos(var4x, var5, var6);
      });
      this.knockbackX = var1.readFloat();
      this.knockbackY = var1.readFloat();
      this.knockbackZ = var1.readFloat();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeFloat((float)this.x);
      var1.writeFloat((float)this.y);
      var1.writeFloat((float)this.z);
      var1.writeFloat(this.power);
      int var2 = Mth.floor(this.x);
      int var3 = Mth.floor(this.y);
      int var4 = Mth.floor(this.z);
      var1.writeCollection(this.toBlow, (var3x, var4x) -> {
         int var5 = var4x.getX() - var2;
         int var6 = var4x.getY() - var3;
         int var7 = var4x.getZ() - var4;
         var3x.writeByte(var5);
         var3x.writeByte(var6);
         var3x.writeByte(var7);
      });
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

   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }
}
