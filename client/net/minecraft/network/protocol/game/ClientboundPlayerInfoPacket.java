package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoPacket implements Packet<ClientGamePacketListener> {
   private ClientboundPlayerInfoPacket.Action action;
   private final List<ClientboundPlayerInfoPacket.PlayerUpdate> entries = Lists.newArrayList();

   public ClientboundPlayerInfoPacket() {
      super();
   }

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action var1, ServerPlayer... var2) {
      super();
      this.action = var1;
      ServerPlayer[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ServerPlayer var6 = var3[var5];
         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(var6.getGameProfile(), var6.latency, var6.gameMode.getGameModeForPlayer(), var6.getTabListDisplayName()));
      }

   }

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action var1, Iterable<ServerPlayer> var2) {
      super();
      this.action = var1;
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(var4.getGameProfile(), var4.latency, var4.gameMode.getGameModeForPlayer(), var4.getTabListDisplayName()));
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.action = (ClientboundPlayerInfoPacket.Action)var1.readEnum(ClientboundPlayerInfoPacket.Action.class);
      int var2 = var1.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         GameProfile var4 = null;
         int var5 = 0;
         GameType var6 = null;
         Component var7 = null;
         switch(this.action) {
         case ADD_PLAYER:
            var4 = new GameProfile(var1.readUUID(), var1.readUtf(16));
            int var8 = var1.readVarInt();
            int var9 = 0;

            for(; var9 < var8; ++var9) {
               String var10 = var1.readUtf(32767);
               String var11 = var1.readUtf(32767);
               if (var1.readBoolean()) {
                  var4.getProperties().put(var10, new Property(var10, var11, var1.readUtf(32767)));
               } else {
                  var4.getProperties().put(var10, new Property(var10, var11));
               }
            }

            var6 = GameType.byId(var1.readVarInt());
            var5 = var1.readVarInt();
            if (var1.readBoolean()) {
               var7 = var1.readComponent();
            }
            break;
         case UPDATE_GAME_MODE:
            var4 = new GameProfile(var1.readUUID(), (String)null);
            var6 = GameType.byId(var1.readVarInt());
            break;
         case UPDATE_LATENCY:
            var4 = new GameProfile(var1.readUUID(), (String)null);
            var5 = var1.readVarInt();
            break;
         case UPDATE_DISPLAY_NAME:
            var4 = new GameProfile(var1.readUUID(), (String)null);
            if (var1.readBoolean()) {
               var7 = var1.readComponent();
            }
            break;
         case REMOVE_PLAYER:
            var4 = new GameProfile(var1.readUUID(), (String)null);
         }

         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(var4, var5, var6, var7));
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.action);
      var1.writeVarInt(this.entries.size());
      Iterator var2 = this.entries.iterator();

      while(true) {
         while(var2.hasNext()) {
            ClientboundPlayerInfoPacket.PlayerUpdate var3 = (ClientboundPlayerInfoPacket.PlayerUpdate)var2.next();
            switch(this.action) {
            case ADD_PLAYER:
               var1.writeUUID(var3.getProfile().getId());
               var1.writeUtf(var3.getProfile().getName());
               var1.writeVarInt(var3.getProfile().getProperties().size());
               Iterator var4 = var3.getProfile().getProperties().values().iterator();

               while(var4.hasNext()) {
                  Property var5 = (Property)var4.next();
                  var1.writeUtf(var5.getName());
                  var1.writeUtf(var5.getValue());
                  if (var5.hasSignature()) {
                     var1.writeBoolean(true);
                     var1.writeUtf(var5.getSignature());
                  } else {
                     var1.writeBoolean(false);
                  }
               }

               var1.writeVarInt(var3.getGameMode().getId());
               var1.writeVarInt(var3.getLatency());
               if (var3.getDisplayName() == null) {
                  var1.writeBoolean(false);
               } else {
                  var1.writeBoolean(true);
                  var1.writeComponent(var3.getDisplayName());
               }
               break;
            case UPDATE_GAME_MODE:
               var1.writeUUID(var3.getProfile().getId());
               var1.writeVarInt(var3.getGameMode().getId());
               break;
            case UPDATE_LATENCY:
               var1.writeUUID(var3.getProfile().getId());
               var1.writeVarInt(var3.getLatency());
               break;
            case UPDATE_DISPLAY_NAME:
               var1.writeUUID(var3.getProfile().getId());
               if (var3.getDisplayName() == null) {
                  var1.writeBoolean(false);
               } else {
                  var1.writeBoolean(true);
                  var1.writeComponent(var3.getDisplayName());
               }
               break;
            case REMOVE_PLAYER:
               var1.writeUUID(var3.getProfile().getId());
            }
         }

         return;
      }
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerInfo(this);
   }

   public List<ClientboundPlayerInfoPacket.PlayerUpdate> getEntries() {
      return this.entries;
   }

   public ClientboundPlayerInfoPacket.Action getAction() {
      return this.action;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
   }

   public class PlayerUpdate {
      private final int latency;
      private final GameType gameMode;
      private final GameProfile profile;
      private final Component displayName;

      public PlayerUpdate(GameProfile var2, int var3, GameType var4, @Nullable Component var5) {
         super();
         this.profile = var2;
         this.latency = var3;
         this.gameMode = var4;
         this.displayName = var5;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getLatency() {
         return this.latency;
      }

      public GameType getGameMode() {
         return this.gameMode;
      }

      @Nullable
      public Component getDisplayName() {
         return this.displayName;
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : Component.Serializer.toJson(this.displayName)).toString();
      }
   }

   public static enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;

      private Action() {
      }
   }
}
