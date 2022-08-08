package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundEditBookPacket implements Packet<ServerGamePacketListener> {
   public static final int MAX_BYTES_PER_CHAR = 4;
   private static final int TITLE_MAX_CHARS = 128;
   private static final int PAGE_MAX_CHARS = 8192;
   private static final int MAX_PAGES_COUNT = 200;
   private final int slot;
   private final List<String> pages;
   private final Optional<String> title;

   public ServerboundEditBookPacket(int var1, List<String> var2, Optional<String> var3) {
      super();
      this.slot = var1;
      this.pages = ImmutableList.copyOf(var2);
      this.title = var3;
   }

   public ServerboundEditBookPacket(FriendlyByteBuf var1) {
      super();
      this.slot = var1.readVarInt();
      this.pages = (List)var1.readCollection(FriendlyByteBuf.limitValue(Lists::newArrayListWithCapacity, 200), (var0) -> {
         return var0.readUtf(8192);
      });
      this.title = var1.readOptional((var0) -> {
         return var0.readUtf(128);
      });
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.slot);
      var1.writeCollection(this.pages, (var0, var1x) -> {
         var0.writeUtf(var1x, 8192);
      });
      var1.writeOptional(this.title, (var0, var1x) -> {
         var0.writeUtf(var1x, 128);
      });
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleEditBook(this);
   }

   public List<String> getPages() {
      return this.pages;
   }

   public Optional<String> getTitle() {
      return this.title;
   }

   public int getSlot() {
      return this.slot;
   }
}
