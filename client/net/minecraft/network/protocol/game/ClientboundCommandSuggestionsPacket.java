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

   public ClientboundCommandSuggestionsPacket(int var1, Suggestions var2) {
      this(var1, var2.getRange().getStart(), var2.getRange().getLength(), var2.getList().stream().map((var0) -> {
         return new Entry(var0.getText(), Optional.ofNullable(var0.getTooltip()).map(ComponentUtils::fromMessage));
      }).toList());
   }

   public ClientboundCommandSuggestionsPacket(int id, int start, int length, List<Entry> suggestions) {
      super();
      this.id = id;
      this.start = start;
      this.length = length;
      this.suggestions = suggestions;
   }

   public PacketType<ClientboundCommandSuggestionsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_COMMAND_SUGGESTIONS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCommandSuggestions(this);
   }

   public Suggestions toSuggestions() {
      StringRange var1 = StringRange.between(this.start, this.start + this.length);
      return new Suggestions(var1, this.suggestions.stream().map((var1x) -> {
         return new Suggestion(var1, var1x.text(), (Message)var1x.tooltip().orElse((Object)null));
      }).toList());
   }

   public int id() {
      return this.id;
   }

   public int start() {
      return this.start;
   }

   public int length() {
      return this.length;
   }

   public List<Entry> suggestions() {
      return this.suggestions;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundCommandSuggestionsPacket::id, ByteBufCodecs.VAR_INT, ClientboundCommandSuggestionsPacket::start, ByteBufCodecs.VAR_INT, ClientboundCommandSuggestionsPacket::length, ClientboundCommandSuggestionsPacket.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundCommandSuggestionsPacket::suggestions, ClientboundCommandSuggestionsPacket::new);
   }

   public static record Entry(String text, Optional<Component> tooltip) {
      public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

      public Entry(String text, Optional<Component> tooltip) {
         super();
         this.text = text;
         this.tooltip = tooltip;
      }

      public String text() {
         return this.text;
      }

      public Optional<Component> tooltip() {
         return this.tooltip;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Entry::text, ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC, Entry::tooltip, Entry::new);
      }
   }
}
