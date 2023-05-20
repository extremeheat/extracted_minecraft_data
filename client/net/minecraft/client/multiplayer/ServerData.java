package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import org.slf4j.Logger;

public class ServerData {
   private static final Logger LOGGER = LogUtils.getLogger();
   public String name;
   public String ip;
   public Component status;
   public Component motd;
   @Nullable
   public ServerStatus.Players players;
   public long ping;
   public int protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
   public Component version = Component.literal(SharedConstants.getCurrentVersion().getName());
   public boolean pinged;
   public List<Component> playerList = Collections.emptyList();
   private ServerData.ServerPackStatus packStatus = ServerData.ServerPackStatus.PROMPT;
   @Nullable
   private byte[] iconBytes;
   private boolean lan;
   private boolean enforcesSecureChat;

   public ServerData(String var1, String var2, boolean var3) {
      super();
      this.name = var1;
      this.ip = var2;
      this.lan = var3;
   }

   public CompoundTag write() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("name", this.name);
      var1.putString("ip", this.ip);
      if (this.iconBytes != null) {
         var1.putString("icon", Base64.getEncoder().encodeToString(this.iconBytes));
      }

      if (this.packStatus == ServerData.ServerPackStatus.ENABLED) {
         var1.putBoolean("acceptTextures", true);
      } else if (this.packStatus == ServerData.ServerPackStatus.DISABLED) {
         var1.putBoolean("acceptTextures", false);
      }

      return var1;
   }

   public ServerData.ServerPackStatus getResourcePackStatus() {
      return this.packStatus;
   }

   public void setResourcePackStatus(ServerData.ServerPackStatus var1) {
      this.packStatus = var1;
   }

   public static ServerData read(CompoundTag var0) {
      ServerData var1 = new ServerData(var0.getString("name"), var0.getString("ip"), false);
      if (var0.contains("icon", 8)) {
         try {
            var1.setIconBytes(Base64.getDecoder().decode(var0.getString("icon")));
         } catch (IllegalArgumentException var3) {
            LOGGER.warn("Malformed base64 server icon", var3);
         }
      }

      if (var0.contains("acceptTextures", 1)) {
         if (var0.getBoolean("acceptTextures")) {
            var1.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
         } else {
            var1.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
         }
      } else {
         var1.setResourcePackStatus(ServerData.ServerPackStatus.PROMPT);
      }

      return var1;
   }

   @Nullable
   public byte[] getIconBytes() {
      return this.iconBytes;
   }

   public void setIconBytes(@Nullable byte[] var1) {
      this.iconBytes = var1;
   }

   public boolean isLan() {
      return this.lan;
   }

   public void setEnforcesSecureChat(boolean var1) {
      this.enforcesSecureChat = var1;
   }

   public boolean enforcesSecureChat() {
      return this.enforcesSecureChat;
   }

   public void copyNameIconFrom(ServerData var1) {
      this.ip = var1.ip;
      this.name = var1.name;
      this.iconBytes = var1.iconBytes;
   }

   public void copyFrom(ServerData var1) {
      this.copyNameIconFrom(var1);
      this.setResourcePackStatus(var1.getResourcePackStatus());
      this.lan = var1.lan;
      this.enforcesSecureChat = var1.enforcesSecureChat;
   }

   public static enum ServerPackStatus {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final Component name;

      private ServerPackStatus(String var3) {
         this.name = Component.translatable("addServer.resourcePack." + var3);
      }

      public Component getName() {
         return this.name;
      }
   }
}
