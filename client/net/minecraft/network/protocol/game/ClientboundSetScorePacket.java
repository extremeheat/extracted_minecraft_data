package net.minecraft.network.protocol.game;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.ServerScoreboard;

public class ClientboundSetScorePacket implements Packet<ClientGamePacketListener> {
   private final String owner;
   @Nullable
   private final String objectiveName;
   private final int score;
   private final ServerScoreboard.Method method;

   public ClientboundSetScorePacket(ServerScoreboard.Method var1, @Nullable String var2, String var3, int var4) {
      super();
      if (var1 != ServerScoreboard.Method.REMOVE && var2 == null) {
         throw new IllegalArgumentException("Need an objective name");
      } else {
         this.owner = var3;
         this.objectiveName = var2;
         this.score = var4;
         this.method = var1;
      }
   }

   public ClientboundSetScorePacket(FriendlyByteBuf var1) {
      super();
      this.owner = var1.readUtf();
      this.method = (ServerScoreboard.Method)var1.readEnum(ServerScoreboard.Method.class);
      String var2 = var1.readUtf();
      this.objectiveName = Objects.equals(var2, "") ? null : var2;
      if (this.method != ServerScoreboard.Method.REMOVE) {
         this.score = var1.readVarInt();
      } else {
         this.score = 0;
      }

   }

   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.owner);
      var1.writeEnum(this.method);
      var1.writeUtf(this.objectiveName == null ? "" : this.objectiveName);
      if (this.method != ServerScoreboard.Method.REMOVE) {
         var1.writeVarInt(this.score);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetScore(this);
   }

   public String getOwner() {
      return this.owner;
   }

   @Nullable
   public String getObjectiveName() {
      return this.objectiveName;
   }

   public int getScore() {
      return this.score;
   }

   public ServerScoreboard.Method getMethod() {
      return this.method;
   }
}
