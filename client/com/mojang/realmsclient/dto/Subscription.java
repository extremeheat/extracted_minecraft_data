package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public record Subscription(long startDate, int daysLeft, SubscriptionType type) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public Subscription(long var1, int var3, SubscriptionType var4) {
      super();
      this.startDate = var1;
      this.daysLeft = var3;
      this.type = var4;
   }

   public static Subscription parse(String var0) {
      try {
         JsonObject var1 = LenientJsonParser.parse(var0).getAsJsonObject();
         return new Subscription(JsonUtils.getLongOr("startDate", var1, 0L), JsonUtils.getIntOr("daysLeft", var1, 0), typeFrom(JsonUtils.getStringOr("subscriptionType", var1, (String)null)));
      } catch (Exception var2) {
         LOGGER.error("Could not parse Subscription", var2);
         return new Subscription(0L, 0, Subscription.SubscriptionType.NORMAL);
      }
   }

   private static SubscriptionType typeFrom(@Nullable String var0) {
      try {
         if (var0 != null) {
            return Subscription.SubscriptionType.valueOf(var0);
         }
      } catch (Exception var2) {
      }

      return Subscription.SubscriptionType.NORMAL;
   }

   public static enum SubscriptionType {
      NORMAL,
      RECURRING;

      private SubscriptionType() {
      }

      // $FF: synthetic method
      private static SubscriptionType[] $values() {
         return new SubscriptionType[]{NORMAL, RECURRING};
      }
   }
}
