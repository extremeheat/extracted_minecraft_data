package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;

public class ClientboundCommandSuggestionsPacket implements Packet {
   private int id;
   private Suggestions suggestions;

   public ClientboundCommandSuggestionsPacket() {
   }

   public ClientboundCommandSuggestionsPacket(int var1, Suggestions var2) {
      this.id = var1;
      this.suggestions = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      int var2 = var1.readVarInt();
      int var3 = var1.readVarInt();
      StringRange var4 = StringRange.between(var2, var2 + var3);
      int var5 = var1.readVarInt();
      ArrayList var6 = Lists.newArrayListWithCapacity(var5);

      for(int var7 = 0; var7 < var5; ++var7) {
         String var8 = var1.readUtf(32767);
         Component var9 = var1.readBoolean() ? var1.readComponent() : null;
         var6.add(new Suggestion(var4, var8, var9));
      }

      this.suggestions = new Suggestions(var4, var6);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
      var1.writeVarInt(this.suggestions.getRange().getStart());
      var1.writeVarInt(this.suggestions.getRange().getLength());
      var1.writeVarInt(this.suggestions.getList().size());
      Iterator var2 = this.suggestions.getList().iterator();

      while(var2.hasNext()) {
         Suggestion var3 = (Suggestion)var2.next();
         var1.writeUtf(var3.getText());
         var1.writeBoolean(var3.getTooltip() != null);
         if (var3.getTooltip() != null) {
            var1.writeComponent(ComponentUtils.fromMessage(var3.getTooltip()));
         }
      }

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
