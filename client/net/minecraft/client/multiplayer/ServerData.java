package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.util.PngInfo;
import org.slf4j.Logger;

public class ServerData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_ICON_SIZE = 1024;
   public String name;
   public String ip;
   public Component status;
   public Component motd;
   @Nullable
   public ServerStatus.Players players;
   public long ping;
   public int protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
   public Component version = Component.literal(SharedConstants.getCurrentVersion().getName());
   public List<Component> playerList = Collections.emptyList();
   private ServerPackStatus packStatus;
   @Nullable
   private byte[] iconBytes;
   private Type type;
   private State state;

   public ServerData(String var1, String var2, Type var3) {
      super();
      this.packStatus = ServerData.ServerPackStatus.PROMPT;
      this.state = ServerData.State.INITIAL;
      this.name = var1;
      this.ip = var2;
      this.type = var3;
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

   public ServerPackStatus getResourcePackStatus() {
      return this.packStatus;
   }

   public void setResourcePackStatus(ServerPackStatus var1) {
      this.packStatus = var1;
   }

   public static ServerData read(CompoundTag var0) {
      ServerData var1 = new ServerData(var0.getString("name"), var0.getString("ip"), ServerData.Type.OTHER);
      if (var0.contains("icon", 8)) {
         try {
            byte[] var2 = Base64.getDecoder().decode(var0.getString("icon"));
            var1.setIconBytes(validateIcon(var2));
         } catch (IllegalArgumentException var3) {
            LOGGER.warn("Malformed base64 server icon", var3);
         }
      }

      if (var0.contains("acceptTextures", 99)) {
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
      return this.type == ServerData.Type.LAN;
   }

   public boolean isRealm() {
      return this.type == ServerData.Type.REALM;
   }

   public Type type() {
      return this.type;
   }

   public void copyNameIconFrom(ServerData var1) {
      this.ip = var1.ip;
      this.name = var1.name;
      this.iconBytes = var1.iconBytes;
   }

   public void copyFrom(ServerData var1) {
      this.copyNameIconFrom(var1);
      this.setResourcePackStatus(var1.getResourcePackStatus());
      this.type = var1.type;
   }

   public State state() {
      return this.state;
   }

   public void setState(State var1) {
      this.state = var1;
   }

   @Nullable
   public static byte[] validateIcon(@Nullable byte[] var0) {
      if (var0 != null) {
         try {
            PngInfo var1 = PngInfo.fromBytes(var0);
            if (var1.width() <= 1024 && var1.height() <= 1024) {
               return var0;
            }
         } catch (IOException var2) {
            LOGGER.warn("Failed to decode server icon", var2);
         }
      }

      return null;
   }

   public static enum ServerPackStatus {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final Component name;

      private ServerPackStatus(final String var3) {
         this.name = Component.translatable("addServer.resourcePack." + var3);
      }

      public Component getName() {
         return this.name;
      }

      // $FF: synthetic method
      private static ServerPackStatus[] $values() {
         return new ServerPackStatus[]{ENABLED, DISABLED, PROMPT};
      }
   }

   public static enum State {
      INITIAL,
      PINGING,
      UNREACHABLE,
      INCOMPATIBLE,
      SUCCESSFUL;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{INITIAL, PINGING, UNREACHABLE, INCOMPATIBLE, SUCCESSFUL};
      }
   }

   public static enum Type {
      LAN,
      REALM,
      OTHER;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{LAN, REALM, OTHER};
      }
   }
}
