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
   // $FF: renamed from: id int
   private final int field_320;
   private final Suggestions suggestions;

   public ClientboundCommandSuggestionsPacket(int var1, Suggestions var2) {
      super();
      this.field_320 = var1;
      this.suggestions = var2;
   }

   public ClientboundCommandSuggestionsPacket(FriendlyByteBuf var1) {
      super();
      this.field_320 = var1.readVarInt();
      int var2 = var1.readVarInt();
      int var3 = var1.readVarInt();
      StringRange var4 = StringRange.between(var2, var2 + var3);
      List var5 = var1.readList((var1x) -> {
         String var2 = var1x.readUtf();
         Component var3 = var1x.readBoolean() ? var1x.readComponent() : null;
         return new Suggestion(var4, var2, var3);
      });
      this.suggestions = new Suggestions(var4, var5);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_320);
      var1.writeVarInt(this.suggestions.getRange().getStart());
      var1.writeVarInt(this.suggestions.getRange().getLength());
      var1.writeCollection(this.suggestions.getList(), (var0, var1x) -> {
         var0.writeUtf(var1x.getText());
         var0.writeBoolean(var1x.getTooltip() != null);
         if (var1x.getTooltip() != null) {
            var0.writeComponent(ComponentUtils.fromMessage(var1x.getTooltip()));
         }

      });
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCommandSuggestions(this);
   }

   public int getId() {
      return this.field_320;
   }

   public Suggestions getSuggestions() {
      return this.suggestions;
   }
}
