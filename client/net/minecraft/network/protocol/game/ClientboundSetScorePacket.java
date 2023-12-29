package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.protocol.Packet;

public record ClientboundSetScorePacket(String a, String b, int c, @Nullable Component d, @Nullable NumberFormat e) implements Packet<ClientGamePacketListener> {
   private final String owner;
   private final String objectiveName;
   private final int score;
   @Nullable
   private final Component display;
   @Nullable
   private final NumberFormat numberFormat;

   public ClientboundSetScorePacket(FriendlyByteBuf var1) {
      this(
         var1.readUtf(),
         var1.readUtf(),
         var1.readVarInt(),
         var1.readNullable(FriendlyByteBuf::readComponentTrusted),
         var1.readNullable(NumberFormatTypes::readFromStream)
      );
   }

   public ClientboundSetScorePacket(String var1, String var2, int var3, @Nullable Component var4, @Nullable NumberFormat var5) {
      super();
      this.owner = var1;
      this.objectiveName = var2;
      this.score = var3;
      this.display = var4;
      this.numberFormat = var5;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.owner);
      var1.writeUtf(this.objectiveName);
      var1.writeVarInt(this.score);
      var1.writeNullable(this.display, FriendlyByteBuf::writeComponent);
      var1.writeNullable(this.numberFormat, NumberFormatTypes::writeToStream);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetScore(this);
   }
}
