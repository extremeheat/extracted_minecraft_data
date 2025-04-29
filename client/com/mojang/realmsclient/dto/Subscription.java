package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class Subscription extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public long startDate;
   public int daysLeft;
   public SubscriptionType type;

   public Subscription() {
      super();
      this.type = Subscription.SubscriptionType.NORMAL;
   }

   public static Subscription parse(String var0) {
      Subscription var1 = new Subscription();

      try {
         JsonObject var2 = LenientJsonParser.parse(var0).getAsJsonObject();
         var1.startDate = JsonUtils.getLongOr("startDate", var2, 0L);
         var1.daysLeft = JsonUtils.getIntOr("daysLeft", var2, 0);
         var1.type = typeFrom(JsonUtils.getStringOr("subscriptionType", var2, Subscription.SubscriptionType.NORMAL.name()));
      } catch (Exception var3) {
         LOGGER.error("Could not parse Subscription: {}", var3.getMessage());
      }

      return var1;
   }

   private static SubscriptionType typeFrom(String var0) {
      try {
         return Subscription.SubscriptionType.valueOf(var0);
      } catch (Exception var2) {
         return Subscription.SubscriptionType.NORMAL;
      }
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
