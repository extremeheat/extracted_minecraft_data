package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class ServerData {
   private static final Logger LOGGER = LogUtils.getLogger();
   public String name;
   public String ip;
   public Component status;
   public Component motd;
   public long ping;
   public int protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
   public Component version = Component.literal(SharedConstants.getCurrentVersion().getName());
   public boolean pinged;
   public List<Component> playerList = Collections.emptyList();
   private ServerPackStatus packStatus;
   @Nullable
   private String iconB64;
   private boolean lan;
   @Nullable
   private ChatPreview chatPreview;
   private boolean chatPreviewEnabled;

   public ServerData(String var1, String var2, boolean var3) {
      super();
      this.packStatus = ServerData.ServerPackStatus.PROMPT;
      this.chatPreviewEnabled = true;
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

      if (this.chatPreview != null) {
         ServerData.ChatPreview.CODEC.encodeStart(NbtOps.INSTANCE, this.chatPreview).result().ifPresent((var1x) -> {
            var1.put("chatPreview", var1x);
         });
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

      if (var0.contains("chatPreview", 10)) {
         DataResult var10000 = ServerData.ChatPreview.CODEC.parse(NbtOps.INSTANCE, var0.getCompound("chatPreview"));
         Logger var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
            var1.chatPreview = var1x;
         });
      }

      return var1;
   }

   @Nullable
   public String getIconB64() {
      return this.iconB64;
   }

   public static String parseFavicon(String var0) throws ParseException {
      if (var0.startsWith("data:image/png;base64,")) {
         return var0.substring("data:image/png;base64,".length());
      } else {
         throw new ParseException("Unknown format", 0);
      }
   }

   public void setIconB64(@Nullable String var1) {
      this.iconB64 = var1;
   }

   public boolean isLan() {
      return this.lan;
   }

   public void setPreviewsChat(boolean var1) {
      if (var1 && this.chatPreview == null) {
         this.chatPreview = new ChatPreview(false, false);
      } else if (!var1 && this.chatPreview != null) {
         this.chatPreview = null;
      }

   }

   @Nullable
   public ChatPreview getChatPreview() {
      return this.chatPreview;
   }

   public void setChatPreviewEnabled(boolean var1) {
      this.chatPreviewEnabled = var1;
   }

   public boolean previewsChat() {
      return this.chatPreviewEnabled && this.chatPreview != null;
   }

   public void copyNameIconFrom(ServerData var1) {
      this.ip = var1.ip;
      this.name = var1.name;
      this.iconB64 = var1.iconB64;
   }

   public void copyFrom(ServerData var1) {
      this.copyNameIconFrom(var1);
      this.setResourcePackStatus(var1.getResourcePackStatus());
      this.lan = var1.lan;
      this.chatPreview = (ChatPreview)Util.mapNullable(var1.chatPreview, ChatPreview::copy);
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

      // $FF: synthetic method
      private static ServerPackStatus[] $values() {
         return new ServerPackStatus[]{ENABLED, DISABLED, PROMPT};
      }
   }

   public static class ChatPreview {
      public static final Codec<ChatPreview> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.BOOL.optionalFieldOf("acknowledged", false).forGetter((var0x) -> {
            return var0x.acknowledged;
         }), Codec.BOOL.optionalFieldOf("toastShown", false).forGetter((var0x) -> {
            return var0x.toastShown;
         })).apply(var0, ChatPreview::new);
      });
      private boolean acknowledged;
      private boolean toastShown;

      ChatPreview(boolean var1, boolean var2) {
         super();
         this.acknowledged = var1;
         this.toastShown = var2;
      }

      public void acknowledge() {
         this.acknowledged = true;
      }

      public boolean showToast() {
         if (!this.toastShown) {
            this.toastShown = true;
            return true;
         } else {
            return false;
         }
      }

      public boolean isAcknowledged() {
         return this.acknowledged;
      }

      private ChatPreview copy() {
         return new ChatPreview(this.acknowledged, this.toastShown);
      }
   }
}
