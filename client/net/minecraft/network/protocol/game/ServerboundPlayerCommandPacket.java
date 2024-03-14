package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ServerboundPlayerCommandPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPlayerCommandPacket> STREAM_CODEC = Packet.codec(
      ServerboundPlayerCommandPacket::write, ServerboundPlayerCommandPacket::new
   );
   private final int id;
   private final ServerboundPlayerCommandPacket.Action action;
   private final int data;

   public ServerboundPlayerCommandPacket(Entity var1, ServerboundPlayerCommandPacket.Action var2) {
      this(var1, var2, 0);
   }

   public ServerboundPlayerCommandPacket(Entity var1, ServerboundPlayerCommandPacket.Action var2, int var3) {
      super();
      this.id = var1.getId();
      this.action = var2;
      this.data = var3;
   }

   private ServerboundPlayerCommandPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.action = var1.readEnum(ServerboundPlayerCommandPacket.Action.class);
      this.data = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeEnum(this.action);
      var1.writeVarInt(this.data);
   }

   @Override
   public PacketType<ServerboundPlayerCommandPacket> type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_COMMAND;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlayerCommand(this);
   }

   public int getId() {
      return this.id;
   }

   public ServerboundPlayerCommandPacket.Action getAction() {
      return this.action;
   }

   public int getData() {
      return this.data;
   }

   public static enum Action {
      PRESS_SHIFT_KEY,
      RELEASE_SHIFT_KEY,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      START_RIDING_JUMP,
      STOP_RIDING_JUMP,
      OPEN_INVENTORY,
      START_FALL_FLYING;

      private Action() {
      }
   }
}
