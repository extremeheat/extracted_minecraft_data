package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundSetEntityMotionPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_463;
   // $FF: renamed from: xa int
   private final int field_464;
   // $FF: renamed from: ya int
   private final int field_465;
   // $FF: renamed from: za int
   private final int field_466;

   public ClientboundSetEntityMotionPacket(Entity var1) {
      this(var1.getId(), var1.getDeltaMovement());
   }

   public ClientboundSetEntityMotionPacket(int var1, Vec3 var2) {
      super();
      this.field_463 = var1;
      double var3 = 3.9D;
      double var5 = Mth.clamp(var2.field_414, -3.9D, 3.9D);
      double var7 = Mth.clamp(var2.field_415, -3.9D, 3.9D);
      double var9 = Mth.clamp(var2.field_416, -3.9D, 3.9D);
      this.field_464 = (int)(var5 * 8000.0D);
      this.field_465 = (int)(var7 * 8000.0D);
      this.field_466 = (int)(var9 * 8000.0D);
   }

   public ClientboundSetEntityMotionPacket(FriendlyByteBuf var1) {
      super();
      this.field_463 = var1.readVarInt();
      this.field_464 = var1.readShort();
      this.field_465 = var1.readShort();
      this.field_466 = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_463);
      var1.writeShort(this.field_464);
      var1.writeShort(this.field_465);
      var1.writeShort(this.field_466);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEntityMotion(this);
   }

   public int getId() {
      return this.field_463;
   }

   public int getXa() {
      return this.field_464;
   }

   public int getYa() {
      return this.field_465;
   }

   public int getZa() {
      return this.field_466;
   }
}
