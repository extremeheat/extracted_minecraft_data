package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class RealmsNotification {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String NOTIFICATION_UUID = "notificationUuid";
   private static final String DISMISSABLE = "dismissable";
   private static final String SEEN = "seen";
   private static final String TYPE = "type";
   private static final String VISIT_URL = "visitUrl";
   final UUID uuid;
   final boolean dismissable;
   final boolean seen;
   final String type;

   RealmsNotification(UUID var1, boolean var2, boolean var3, String var4) {
      super();
      this.uuid = var1;
      this.dismissable = var2;
      this.seen = var3;
      this.type = var4;
   }

   public boolean seen() {
      return this.seen;
   }

   public boolean dismissable() {
      return this.dismissable;
   }

   public UUID uuid() {
      return this.uuid;
   }

   public static List<RealmsNotification> parseList(String var0) {
      ArrayList var1 = new ArrayList();

      try {
         for(JsonElement var4 : JsonParser.parseString(var0).getAsJsonObject().get("notifications").getAsJsonArray()) {
            var1.add(parse(var4.getAsJsonObject()));
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse list of RealmsNotifications", var5);
      }

      return var1;
   }

   private static RealmsNotification parse(JsonObject var0) {
      UUID var1 = JsonUtils.getUuidOr("notificationUuid", var0, null);
      if (var1 == null) {
         throw new IllegalStateException("Missing required property notificationUuid");
      } else {
         boolean var2 = JsonUtils.getBooleanOr("dismissable", var0, true);
         boolean var3 = JsonUtils.getBooleanOr("seen", var0, false);
         String var4 = JsonUtils.getRequiredString("type", var0);
         RealmsNotification var5 = new RealmsNotification(var1, var2, var3, var4);
         return (RealmsNotification)("visitUrl".equals(var4) ? RealmsNotification.VisitUrl.parse(var5, var0) : var5);
      }
   }

   public static class VisitUrl extends RealmsNotification {
      private static final String URL = "url";
      private static final String BUTTON_TEXT = "buttonText";
      private static final String MESSAGE = "message";
      private final String url;
      private final RealmsText buttonText;
      private final RealmsText message;

      private VisitUrl(RealmsNotification var1, String var2, RealmsText var3, RealmsText var4) {
         super(var1.uuid, var1.dismissable, var1.seen, var1.type);
         this.url = var2;
         this.buttonText = var3;
         this.message = var4;
      }

      public static RealmsNotification.VisitUrl parse(RealmsNotification var0, JsonObject var1) {
         String var2 = JsonUtils.getRequiredString("url", var1);
         RealmsText var3 = JsonUtils.getRequired("buttonText", var1, RealmsText::parse);
         RealmsText var4 = JsonUtils.getRequired("message", var1, RealmsText::parse);
         return new RealmsNotification.VisitUrl(var0, var2, var3, var4);
      }

      public Component getMessage() {
         return this.message.createComponent(Component.translatable("mco.notification.visitUrl.message.default"));
      }

      public Button buildOpenLinkButton(Screen var1) {
         Component var2 = this.buttonText.createComponent(Component.translatable("mco.notification.visitUrl.buttonText.default"));
         return Button.builder(var2, ConfirmLinkScreen.confirmLink(this.url, var1, true)).build();
      }
   }
}
