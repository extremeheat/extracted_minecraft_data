package net.minecraft.network.protocol.game;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;

public class ClientboundCommandSuggestionsPacket implements Packet<ClientGamePacketListener> {
   private final int id;
   private final Suggestions suggestions;

   public ClientboundCommandSuggestionsPacket(int var1, Suggestions var2) {
      super();
      this.id = var1;
      this.suggestions = var2;
   }

   public ClientboundCommandSuggestionsPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      int var2 = var1.readVarInt();
      int var3 = var1.readVarInt();
      StringRange var4 = StringRange.between(var2, var2 + var3);
      List var5 = var1.readList(var1x -> {
         String var2x = var1x.readUtf();
         Component var3x = var1x.readNullable(FriendlyByteBuf::readComponentTrusted);
         return new Suggestion(var4, var2x, var3x);
      });
      this.suggestions = new Suggestions(var4, var5);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeVarInt(this.suggestions.getRange().getStart());
      var1.writeVarInt(this.suggestions.getRange().getLength());
      var1.writeCollection(this.suggestions.getList(), (var0, var1x) -> {
         var0.writeUtf(var1x.getText());
         var0.writeNullable(var1x.getTooltip(), (var0x, var1xx) -> var0x.writeComponent(ComponentUtils.fromMessage(var1xx)));
      });
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCommandSuggestions(this);
   }

   public int getId() {
      return this.id;
   }

   public Suggestions getSuggestions() {
      return this.suggestions;
   }
}
