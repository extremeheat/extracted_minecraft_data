package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ServerboundPlayerCommandPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPlayerCommandPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundPlayerCommandPacket>codec(ServerboundPlayerCommandPacket::write, ServerboundPlayerCommandPacket::new);
   private final int id;
   private final Action action;
   private final int data;

   public ServerboundPlayerCommandPacket(final Entity entity, final Action action) {
      this(entity, action, 0);
   }

   public ServerboundPlayerCommandPacket(final Entity entity, final Action action, final int data) {
      super();
      this.id = entity.getId();
      this.action = action;
      this.data = data;
   }

   private ServerboundPlayerCommandPacket(final FriendlyByteBuf input) {
      super();
      this.id = input.readVarInt();
      this.action = (Action)input.readEnum(Action.class);
      this.data = input.readVarInt();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.id);
      output.writeEnum(this.action);
      output.writeVarInt(this.data);
   }

   public PacketType<ServerboundPlayerCommandPacket> type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_COMMAND;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handlePlayerCommand(this);
   }

   public int getId() {
      return this.id;
   }

   public Action getAction() {
      return this.action;
   }

   public int getData() {
      return this.data;
   }

   public static enum Action {
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      START_RIDING_JUMP,
      STOP_RIDING_JUMP,
      OPEN_INVENTORY,
      START_FALL_FLYING;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, START_RIDING_JUMP, STOP_RIDING_JUMP, OPEN_INVENTORY, START_FALL_FLYING};
      }
   }
}
