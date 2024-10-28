package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;

public class ServerboundSetCommandMinecartPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSetCommandMinecartPacket> STREAM_CODEC = Packet.codec(ServerboundSetCommandMinecartPacket::write, ServerboundSetCommandMinecartPacket::new);
   private final int entity;
   private final String command;
   private final boolean trackOutput;

   public ServerboundSetCommandMinecartPacket(int var1, String var2, boolean var3) {
      super();
      this.entity = var1;
      this.command = var2;
      this.trackOutput = var3;
   }

   private ServerboundSetCommandMinecartPacket(FriendlyByteBuf var1) {
      super();
      this.entity = var1.readVarInt();
      this.command = var1.readUtf();
      this.trackOutput = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.entity);
      var1.writeUtf(this.command);
      var1.writeBoolean(this.trackOutput);
   }

   public PacketType<ServerboundSetCommandMinecartPacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_COMMAND_MINECART;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCommandMinecart(this);
   }

   @Nullable
   public BaseCommandBlock getCommandBlock(Level var1) {
      Entity var2 = var1.getEntity(this.entity);
      return var2 instanceof MinecartCommandBlock ? ((MinecartCommandBlock)var2).getCommandBlock() : null;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }
}
