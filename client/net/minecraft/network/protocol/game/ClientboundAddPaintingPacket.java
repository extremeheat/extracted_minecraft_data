package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;

public class ClientboundAddPaintingPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_439;
   private final UUID uuid;
   private final BlockPos pos;
   private final Direction direction;
   private final int motive;

   public ClientboundAddPaintingPacket(Painting var1) {
      super();
      this.field_439 = var1.getId();
      this.uuid = var1.getUUID();
      this.pos = var1.getPos();
      this.direction = var1.getDirection();
      this.motive = Registry.MOTIVE.getId(var1.motive);
   }

   public ClientboundAddPaintingPacket(FriendlyByteBuf var1) {
      super();
      this.field_439 = var1.readVarInt();
      this.uuid = var1.readUUID();
      this.motive = var1.readVarInt();
      this.pos = var1.readBlockPos();
      this.direction = Direction.from2DDataValue(var1.readUnsignedByte());
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_439);
      var1.writeUUID(this.uuid);
      var1.writeVarInt(this.motive);
      var1.writeBlockPos(this.pos);
      var1.writeByte(this.direction.get2DDataValue());
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddPainting(this);
   }

   public int getId() {
      return this.field_439;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public Motive getMotive() {
      return (Motive)Registry.MOTIVE.byId(this.motive);
   }
}
