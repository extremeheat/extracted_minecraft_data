package net.minecraft.network.protocol.game;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundEditBookPacket(int slot, List<String> pages, Optional<String> title) implements Packet<ServerGamePacketListener> {
   public static final int MAX_BYTES_PER_CHAR = 4;
   private static final int TITLE_MAX_CHARS = 128;
   private static final int PAGE_MAX_CHARS = 8192;
   private static final int MAX_PAGES_COUNT = 200;
   public static final StreamCodec<FriendlyByteBuf, ServerboundEditBookPacket> STREAM_CODEC;

   public ServerboundEditBookPacket(int var1, List<String> var2, Optional<String> var3) {
      super();
      var2 = List.copyOf(var2);
      this.slot = var1;
      this.pages = var2;
      this.title = var3;
   }

   public PacketType<ServerboundEditBookPacket> type() {
      return GamePacketTypes.SERVERBOUND_EDIT_BOOK;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleEditBook(this);
   }

   public int slot() {
      return this.slot;
   }

   public List<String> pages() {
      return this.pages;
   }

   public Optional<String> title() {
      return this.title;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ServerboundEditBookPacket::slot, ByteBufCodecs.stringUtf8(8192).apply(ByteBufCodecs.list(200)), ServerboundEditBookPacket::pages, ByteBufCodecs.stringUtf8(128).apply(ByteBufCodecs::optional), ServerboundEditBookPacket::title, ServerboundEditBookPacket::new);
   }
}
