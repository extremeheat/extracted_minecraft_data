package net.minecraft.client.multiplayer;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ServerData {
   public String name;
   public String ip;
   public Component status;
   public Component motd;
   public long ping;
   public int protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
   public Component version = new TextComponent(SharedConstants.getCurrentVersion().getName());
   public boolean pinged;
   public List<Component> playerList = Collections.emptyList();
   private ServerData.ServerPackStatus packStatus;
   @Nullable
   private String iconB64;
   private boolean lan;

   public ServerData(String var1, String var2, boolean var3) {
      super();
      this.packStatus = ServerData.ServerPackStatus.PROMPT;
      this.name = var1;
      this.ip = var2;
      this.lan = var3;
   }

   public CompoundTag write() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("name", this.name);
      var1.putString("ip", this.ip);
      if (this.iconB64 != null) {
         var1.putString("icon", this.iconB64);
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
         var1.setIconB64(var0.getString("icon"));
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
   public String getIconB64() {
      return this.iconB64;
   }

   public void setIconB64(@Nullable String var1) {
      this.iconB64 = var1;
   }

   public boolean isLan() {
      return this.lan;
   }

   public void copyFrom(ServerData var1) {
      this.ip = var1.ip;
      this.name = var1.name;
      this.setResourcePackStatus(var1.getResourcePackStatus());
      this.iconB64 = var1.iconB64;
      this.lan = var1.lan;
   }

   public static enum ServerPackStatus {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final Component name;

      private ServerPackStatus(String var3) {
         this.name = new TranslatableComponent("addServer.resourcePack." + var3);
      }

      public Component getName() {
         return this.name;
      }
   }
}
