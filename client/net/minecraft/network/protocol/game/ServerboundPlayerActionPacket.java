package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPlayerActionPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPlayerActionPacket> STREAM_CODEC = Packet.codec(
      ServerboundPlayerActionPacket::write, ServerboundPlayerActionPacket::new
   );
   private final BlockPos pos;
   private final Direction direction;
   private final ServerboundPlayerActionPacket.Action action;
   private final int sequence;

   public ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action var1, BlockPos var2, Direction var3, int var4) {
      super();
      this.action = var1;
      this.pos = var2.immutable();
      this.direction = var3;
      this.sequence = var4;
   }

   public ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action var1, BlockPos var2, Direction var3) {
      this(var1, var2, var3, 0);
   }

   private ServerboundPlayerActionPacket(FriendlyByteBuf var1) {
      super();
      this.action = var1.readEnum(ServerboundPlayerActionPacket.Action.class);
      this.pos = var1.readBlockPos();
      this.direction = Direction.from3DDataValue(var1.readUnsignedByte());
      this.sequence = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
      var1.writeBlockPos(this.pos);
      var1.writeByte(this.direction.get3DDataValue());
      var1.writeVarInt(this.sequence);
   }

   @Override
   public PacketType<ServerboundPlayerActionPacket> type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_ACTION;
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

   public int getSequence() {
      return this.sequence;
   }

   public static enum Action {
      START_DESTROY_BLOCK,
      ABORT_DESTROY_BLOCK,
      STOP_DESTROY_BLOCK,
      DROP_ALL_ITEMS,
      DROP_ITEM,
      RELEASE_USE_ITEM,
      SWAP_ITEM_WITH_OFFHAND;

      private Action() {
      }
   }
}
