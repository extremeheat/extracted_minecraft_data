package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundSignUpdatePacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSignUpdatePacket> STREAM_CODEC = Packet.codec(ServerboundSignUpdatePacket::write, ServerboundSignUpdatePacket::new);
   private static final int MAX_STRING_LENGTH = 384;
   private final BlockPos pos;
   private final String[] lines;
   private final boolean isFrontText;

   public ServerboundSignUpdatePacket(BlockPos var1, boolean var2, String var3, String var4, String var5, String var6) {
      super();
      this.pos = var1;
      this.isFrontText = var2;
      this.lines = new String[]{var3, var4, var5, var6};
   }

   private ServerboundSignUpdatePacket(FriendlyByteBuf var1) {
      super();
      this.pos = var1.readBlockPos();
      this.isFrontText = var1.readBoolean();
      this.lines = new String[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         this.lines[var2] = var1.readUtf(384);
      }

   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeBoolean(this.isFrontText);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.writeUtf(this.lines[var2]);
      }

   }

   public PacketType<ServerboundSignUpdatePacket> type() {
      return GamePacketTypes.SERVERBOUND_SIGN_UPDATE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSignUpdate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public boolean isFrontText() {
      return this.isFrontText;
   }

   public String[] getLines() {
      return this.lines;
   }
}
