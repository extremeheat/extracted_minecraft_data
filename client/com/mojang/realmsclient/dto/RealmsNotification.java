package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsNotification {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String NOTIFICATION_UUID = "notificationUuid";
   private static final String DISMISSABLE = "dismissable";
   private static final String SEEN = "seen";
   private static final String TYPE = "type";
   private static final String VISIT_URL = "visitUrl";
   private static final String INFO_POPUP = "infoPopup";
   static final Component BUTTON_TEXT_FALLBACK = Component.translatable("mco.notification.visitUrl.buttonText.default");
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

         return (RealmsNotification)(switch(var4) {
            case "visitUrl" -> RealmsNotification.VisitUrl.parse(var5, var0);
            case "infoPopup" -> RealmsNotification.InfoPopup.parse(var5, var0);
            default -> var5;
         });
      }
   }

   public static class InfoPopup extends RealmsNotification {
      private static final String TITLE = "title";
      private static final String MESSAGE = "message";
      private static final String IMAGE = "image";
      private static final String URL_BUTTON = "urlButton";
      private final RealmsText title;
      private final RealmsText message;
      private final ResourceLocation image;
      @Nullable
      private final RealmsNotification.UrlButton urlButton;

      private InfoPopup(RealmsNotification var1, RealmsText var2, RealmsText var3, ResourceLocation var4, @Nullable RealmsNotification.UrlButton var5) {
         super(var1.uuid, var1.dismissable, var1.seen, var1.type);
         this.title = var2;
         this.message = var3;
         this.image = var4;
         this.urlButton = var5;
      }

      public static RealmsNotification.InfoPopup parse(RealmsNotification var0, JsonObject var1) {
         RealmsText var2 = JsonUtils.getRequired("title", var1, RealmsText::parse);
         RealmsText var3 = JsonUtils.getRequired("message", var1, RealmsText::parse);
         ResourceLocation var4 = new ResourceLocation(JsonUtils.getRequiredString("image", var1));
         RealmsNotification.UrlButton var5 = JsonUtils.getOptional("urlButton", var1, RealmsNotification.UrlButton::parse);
         return new RealmsNotification.InfoPopup(var0, var2, var3, var4, var5);
      }

      @Nullable
      public PopupScreen buildScreen(Screen var1, Consumer<UUID> var2) {
         Component var3 = this.title.createComponent();
         if (var3 == null) {
            RealmsNotification.LOGGER.warn("Realms info popup had title with no available translation: {}", this.title);
            return null;
         } else {
            PopupScreen.Builder var4 = new PopupScreen.Builder(var1, var3)
               .setImage(this.image)
               .setMessage(this.message.createComponent(CommonComponents.EMPTY));
            if (this.urlButton != null) {
               var4.addButton(this.urlButton.urlText.createComponent(RealmsNotification.BUTTON_TEXT_FALLBACK), var3x -> {
                  Minecraft var4xx = Minecraft.getInstance();
                  var4xx.setScreen(new ConfirmLinkScreen(var4xx -> {
                     if (var4xx) {
                        Util.getPlatform().openUri(this.urlButton.url);
                        var4x.setScreen(var1);
                     } else {
                        var4x.setScreen(var3x);
                     }
                  }, this.urlButton.url, true));
                  var2.accept(this.uuid());
               });
            }

            var4.addButton(CommonComponents.GUI_OK, var2x -> {
               var2x.onClose();
               var2.accept(this.uuid());
            });
            var4.onClose(() -> var2.accept(this.uuid()));
            return var4.build();
         }
      }
   }

   static record UrlButton(String a, RealmsText b) {
      final String url;
      final RealmsText urlText;
      private static final String URL = "url";
      private static final String URL_TEXT = "urlText";

      private UrlButton(String var1, RealmsText var2) {
         super();
         this.url = var1;
         this.urlText = var2;
      }

      public static RealmsNotification.UrlButton parse(JsonObject var0) {
         String var1 = JsonUtils.getRequiredString("url", var0);
         RealmsText var2 = JsonUtils.getRequired("urlText", var0, RealmsText::parse);
         return new RealmsNotification.UrlButton(var1, var2);
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
         Component var2 = this.buttonText.createComponent(RealmsNotification.BUTTON_TEXT_FALLBACK);
         return Button.builder(var2, ConfirmLinkScreen.confirmLink(var1, this.url)).build();
      }
   }
}
