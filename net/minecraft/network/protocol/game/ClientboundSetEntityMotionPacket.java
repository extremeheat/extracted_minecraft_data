package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundSetEntityMotionPacket implements Packet {
   private int id;
   private int xa;
   private int ya;
   private int za;

   public ClientboundSetEntityMotionPacket() {
   }

   public ClientboundSetEntityMotionPacket(Entity var1) {
      this(var1.getId(), var1.getDeltaMovement());
   }

   public ClientboundSetEntityMotionPacket(int var1, Vec3 var2) {
      this.id = var1;
      double var3 = 3.9D;
      double var5 = Mth.clamp(var2.x, -3.9D, 3.9D);
      double var7 = Mth.clamp(var2.y, -3.9D, 3.9D);
      double var9 = Mth.clamp(var2.z, -3.9D, 3.9D);
      this.xa = (int)(var5 * 8000.0D);
      this.ya = (int)(var7 * 8000.0D);
      this.za = (int)(var9 * 8000.0D);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      this.xa = var1.readShort();
      this.ya = var1.readShort();
      this.za = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
      var1.writeShort(this.xa);
      var1.writeShort(this.ya);
      var1.writeShort(this.za);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEntityMotion(this);
   }

   public int getId() {
      return this.id;
   }

   public int getXa() {
      return this.xa;
   }

   public int getYa() {
      return this.ya;
   }

   public int getZa() {
      return this.za;
   }
}
