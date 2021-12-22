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
   // $FF: renamed from: x double
   private final double field_248;
   // $FF: renamed from: y double
   private final double field_249;
   // $FF: renamed from: z double
   private final double field_250;
   private final float power;
   private final List<BlockPos> toBlow;
   private final float knockbackX;
   private final float knockbackY;
   private final float knockbackZ;

   public ClientboundExplodePacket(double var1, double var3, double var5, float var7, List<BlockPos> var8, @Nullable Vec3 var9) {
      super();
      this.field_248 = var1;
      this.field_249 = var3;
      this.field_250 = var5;
      this.power = var7;
      this.toBlow = Lists.newArrayList(var8);
      if (var9 != null) {
         this.knockbackX = (float)var9.field_414;
         this.knockbackY = (float)var9.field_415;
         this.knockbackZ = (float)var9.field_416;
      } else {
         this.knockbackX = 0.0F;
         this.knockbackY = 0.0F;
         this.knockbackZ = 0.0F;
      }

   }

   public ClientboundExplodePacket(FriendlyByteBuf var1) {
      super();
      this.field_248 = (double)var1.readFloat();
      this.field_249 = (double)var1.readFloat();
      this.field_250 = (double)var1.readFloat();
      this.power = var1.readFloat();
      int var2 = Mth.floor(this.field_248);
      int var3 = Mth.floor(this.field_249);
      int var4 = Mth.floor(this.field_250);
      this.toBlow = var1.readList((var3x) -> {
         int var4x = var3x.readByte() + var2;
         int var5 = var3x.readByte() + var3;
         int var6 = var3x.readByte() + var4;
         return new BlockPos(var4x, var5, var6);
      });
      this.knockbackX = var1.readFloat();
      this.knockbackY = var1.readFloat();
      this.knockbackZ = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeFloat((float)this.field_248);
      var1.writeFloat((float)this.field_249);
      var1.writeFloat((float)this.field_250);
      var1.writeFloat(this.power);
      int var2 = Mth.floor(this.field_248);
      int var3 = Mth.floor(this.field_249);
      int var4 = Mth.floor(this.field_250);
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
      return this.field_248;
   }

   public double getY() {
      return this.field_249;
   }

   public double getZ() {
      return this.field_250;
   }

   public float getPower() {
      return this.power;
   }

   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }
}
