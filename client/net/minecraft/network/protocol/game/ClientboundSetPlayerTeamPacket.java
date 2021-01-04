package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public class ClientboundSetPlayerTeamPacket implements Packet<ClientGamePacketListener> {
   private String name = "";
   private Component displayName = new TextComponent("");
   private Component playerPrefix = new TextComponent("");
   private Component playerSuffix = new TextComponent("");
   private String nametagVisibility;
   private String collisionRule;
   private ChatFormatting color;
   private final Collection<String> players;
   private int method;
   private int options;

   public ClientboundSetPlayerTeamPacket() {
      super();
      this.nametagVisibility = Team.Visibility.ALWAYS.name;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = ChatFormatting.RESET;
      this.players = Lists.newArrayList();
   }

   public ClientboundSetPlayerTeamPacket(PlayerTeam var1, int var2) {
      super();
      this.nametagVisibility = Team.Visibility.ALWAYS.name;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = ChatFormatting.RESET;
      this.players = Lists.newArrayList();
      this.name = var1.getName();
      this.method = var2;
      if (var2 == 0 || var2 == 2) {
         this.displayName = var1.getDisplayName();
         this.options = var1.packOptions();
         this.nametagVisibility = var1.getNameTagVisibility().name;
         this.collisionRule = var1.getCollisionRule().name;
         this.color = var1.getColor();
         this.playerPrefix = var1.getPlayerPrefix();
         this.playerSuffix = var1.getPlayerSuffix();
      }

      if (var2 == 0) {
         this.players.addAll(var1.getPlayers());
      }

   }

   public ClientboundSetPlayerTeamPacket(PlayerTeam var1, Collection<String> var2, int var3) {
      super();
      this.nametagVisibility = Team.Visibility.ALWAYS.name;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = ChatFormatting.RESET;
      this.players = Lists.newArrayList();
      if (var3 != 3 && var3 != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if (var2 != null && !var2.isEmpty()) {
         this.method = var3;
         this.name = var1.getName();
         this.players.addAll(var2);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.name = var1.readUtf(16);
      this.method = var1.readByte();
      if (this.method == 0 || this.method == 2) {
         this.displayName = var1.readComponent();
         this.options = var1.readByte();
         this.nametagVisibility = var1.readUtf(40);
         this.collisionRule = var1.readUtf(40);
         this.color = (ChatFormatting)var1.readEnum(ChatFormatting.class);
         this.playerPrefix = var1.readComponent();
         this.playerSuffix = var1.readComponent();
      }

      if (this.method == 0 || this.method == 3 || this.method == 4) {
         int var2 = var1.readVarInt();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.players.add(var1.readUtf(40));
         }
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUtf(this.name);
      var1.writeByte(this.method);
      if (this.method == 0 || this.method == 2) {
         var1.writeComponent(this.displayName);
         var1.writeByte(this.options);
         var1.writeUtf(this.nametagVisibility);
         var1.writeUtf(this.collisionRule);
         var1.writeEnum(this.color);
         var1.writeComponent(this.playerPrefix);
         var1.writeComponent(this.playerSuffix);
      }

      if (this.method == 0 || this.method == 3 || this.method == 4) {
         var1.writeVarInt(this.players.size());
         Iterator var2 = this.players.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var1.writeUtf(var3);
         }
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetPlayerTeamPacket(this);
   }

   public String getName() {
      return this.name;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public Collection<String> getPlayers() {
      return this.players;
   }

   public int getMethod() {
      return this.method;
   }

   public int getOptions() {
      return this.options;
   }

   public ChatFormatting getColor() {
      return this.color;
   }

   public String getNametagVisibility() {
      return this.nametagVisibility;
   }

   public String getCollisionRule() {
      return this.collisionRule;
   }

   public Component getPlayerPrefix() {
      return this.playerPrefix;
   }

   public Component getPlayerSuffix() {
      return this.playerSuffix;
   }
}
