package net.minecraft.network.protocol.game;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundCommandSuggestionsPacket(int id, int start, int length, List<Entry> suggestions) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCommandSuggestionsPacket> STREAM_CODEC;

   public ClientboundCommandSuggestionsPacket(final int id, final Suggestions suggestions) {
      this(id, suggestions.getRange().getStart(), suggestions.getRange().getLength(), suggestions.getList().stream().map((suggestion) -> new Entry(suggestion.getText(), Optional.ofNullable(suggestion.getTooltip()).map(ComponentUtils::fromMessage))).toList());
   }

   public ClientboundCommandSuggestionsPacket {
      super();
   }

   public PacketType<ClientboundCommandSuggestionsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_COMMAND_SUGGESTIONS;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleCommandSuggestions(this);
   }

   public Suggestions toSuggestions() {
      StringRange range = StringRange.between(this.start, this.start + this.length);
      return new Suggestions(range, this.suggestions.stream().map((entry) -> new Suggestion(range, entry.text(), (Message)entry.tooltip().orElse((Object)null))).toList());
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundCommandSuggestionsPacket::id, ByteBufCodecs.VAR_INT, ClientboundCommandSuggestionsPacket::start, ByteBufCodecs.VAR_INT, ClientboundCommandSuggestionsPacket::length, ClientboundCommandSuggestionsPacket.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundCommandSuggestionsPacket::suggestions, ClientboundCommandSuggestionsPacket::new);
   }

   public static record Entry(String text, Optional<Component> tooltip) {
      public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

      public Entry {
         super();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Entry::text, ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC, Entry::tooltip, Entry::new);
      }
   }
}
