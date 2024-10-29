package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public record ClientboundRecipeBookAddPacket(List<Entry> entries, boolean replace) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRecipeBookAddPacket> STREAM_CODEC;

   public ClientboundRecipeBookAddPacket(List<Entry> var1, boolean var2) {
      super();
      this.entries = var1;
      this.replace = var2;
   }

   public PacketType<ClientboundRecipeBookAddPacket> type() {
      return GamePacketTypes.CLIENTBOUND_RECIPE_BOOK_ADD;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRecipeBookAdd(this);
   }

   public List<Entry> entries() {
      return this.entries;
   }

   public boolean replace() {
      return this.replace;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ClientboundRecipeBookAddPacket.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundRecipeBookAddPacket::entries, ByteBufCodecs.BOOL, ClientboundRecipeBookAddPacket::replace, ClientboundRecipeBookAddPacket::new);
   }

   public static record Entry(RecipeDisplayEntry contents, byte flags) {
      public static final byte FLAG_NOTIFICATION = 1;
      public static final byte FLAG_HIGHLIGHT = 2;
      public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

      public Entry(RecipeDisplayEntry var1, boolean var2, boolean var3) {
         this(var1, (byte)((var2 ? 1 : 0) | (var3 ? 2 : 0)));
      }

      public Entry(RecipeDisplayEntry var1, byte var2) {
         super();
         this.contents = var1;
         this.flags = var2;
      }

      public boolean notification() {
         return (this.flags & 1) != 0;
      }

      public boolean highlight() {
         return (this.flags & 2) != 0;
      }

      public RecipeDisplayEntry contents() {
         return this.contents;
      }

      public byte flags() {
         return this.flags;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(RecipeDisplayEntry.STREAM_CODEC, Entry::contents, ByteBufCodecs.BYTE, Entry::flags, Entry::new);
      }
   }
}
