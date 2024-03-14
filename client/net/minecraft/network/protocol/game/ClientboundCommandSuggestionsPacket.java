package net.minecraft.network.protocol.game;

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

public record ClientboundCommandSuggestionsPacket(int b, int c, int d, List<ClientboundCommandSuggestionsPacket.Entry> e)
   implements Packet<ClientGamePacketListener> {
   private final int id;
   private final int start;
   private final int length;
   private final List<ClientboundCommandSuggestionsPacket.Entry> suggestions;
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCommandSuggestionsPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      ClientboundCommandSuggestionsPacket::id,
      ByteBufCodecs.VAR_INT,
      ClientboundCommandSuggestionsPacket::start,
      ByteBufCodecs.VAR_INT,
      ClientboundCommandSuggestionsPacket::length,
      ClientboundCommandSuggestionsPacket.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()),
      ClientboundCommandSuggestionsPacket::suggestions,
      ClientboundCommandSuggestionsPacket::new
   );

   public ClientboundCommandSuggestionsPacket(int var1, Suggestions var2) {
      this(
         var1,
         var2.getRange().getStart(),
         var2.getRange().getLength(),
         var2.getList()
            .stream()
            .map(var0 -> new ClientboundCommandSuggestionsPacket.Entry(var0.getText(), Optional.ofNullable(var0.getTooltip()).map(ComponentUtils::fromMessage)))
            .toList()
      );
   }

   public ClientboundCommandSuggestionsPacket(int var1, int var2, int var3, List<ClientboundCommandSuggestionsPacket.Entry> var4) {
      super();
      this.id = var1;
      this.start = var2;
      this.length = var3;
      this.suggestions = var4;
   }

   @Override
   public PacketType<ClientboundCommandSuggestionsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_COMMAND_SUGGESTIONS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCommandSuggestions(this);
   }

   public Suggestions toSuggestions() {
      StringRange var1 = StringRange.between(this.start, this.start + this.length);
      return new Suggestions(var1, this.suggestions.stream().map(var1x -> new Suggestion(var1, var1x.text(), var1x.tooltip().orElse(null))).toList());
   }

   public static record Entry(String b, Optional<Component> c) {
      private final String text;
      private final Optional<Component> tooltip;
      public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCommandSuggestionsPacket.Entry> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.STRING_UTF8,
         ClientboundCommandSuggestionsPacket.Entry::text,
         ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC,
         ClientboundCommandSuggestionsPacket.Entry::tooltip,
         ClientboundCommandSuggestionsPacket.Entry::new
      );

      public Entry(String var1, Optional<Component> var2) {
         super();
         this.text = var1;
         this.tooltip = var2;
      }
   }
}
