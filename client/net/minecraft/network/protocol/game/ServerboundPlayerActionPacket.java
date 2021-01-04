package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPlayerActionPacket implements Packet<ServerGamePacketListener> {
   private BlockPos pos;
   private Direction direction;
   private ServerboundPlayerActionPacket.Action action;

   public ServerboundPlayerActionPacket() {
      super();
   }

   public ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action var1, BlockPos var2, Direction var3) {
      super();
      this.action = var1;
      this.pos = var2.immutable();
      this.direction = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.action = (ServerboundPlayerActionPacket.Action)var1.readEnum(ServerboundPlayerActionPacket.Action.class);
      this.pos = var1.readBlockPos();
      this.direction = Direction.from3DDataValue(var1.readUnsignedByte());
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.action);
      var1.writeBlockPos(this.pos);
      var1.writeByte(this.direction.get3DDataValue());
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlayerAction(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public ServerboundPlayerActionPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      START_DESTROY_BLOCK,
      ABORT_DESTROY_BLOCK,
      STOP_DESTROY_BLOCK,
      DROP_ALL_ITEMS,
      DROP_ITEM,
      RELEASE_USE_ITEM,
      SWAP_HELD_ITEMS;

      private Action() {
      }
   }
}
