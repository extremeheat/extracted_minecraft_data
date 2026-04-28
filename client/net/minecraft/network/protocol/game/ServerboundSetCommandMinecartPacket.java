package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ServerboundSetCommandMinecartPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSetCommandMinecartPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundSetCommandMinecartPacket>codec(ServerboundSetCommandMinecartPacket::write, ServerboundSetCommandMinecartPacket::new);
   private final int entity;
   private final String command;
   private final boolean trackOutput;

   public ServerboundSetCommandMinecartPacket(final int entity, final String command, final boolean trackOutput) {
      super();
      this.entity = entity;
      this.command = command;
      this.trackOutput = trackOutput;
   }

   private ServerboundSetCommandMinecartPacket(final FriendlyByteBuf input) {
      super();
      this.entity = input.readVarInt();
      this.command = input.readUtf();
      this.trackOutput = input.readBoolean();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.entity);
      output.writeUtf(this.command);
      output.writeBoolean(this.trackOutput);
   }

   public PacketType<ServerboundSetCommandMinecartPacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_COMMAND_MINECART;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleSetCommandMinecart(this);
   }

   public @Nullable BaseCommandBlock getCommandBlock(final Level level) {
      Entity entity = level.getEntity(this.entity);
      if (entity instanceof MinecartCommandBlock minecartCommandBlock) {
         return minecartCommandBlock.getCommandBlock();
      } else {
         return null;
      }
   }

   public String getCommand() {
      return this.command;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }
}
