package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class RealmsText {
   private static final String TRANSLATION_KEY = "translationKey";
   private static final String ARGS = "args";
   private final String translationKey;
   @Nullable
   private final String[] args;

   private RealmsText(String var1, @Nullable String[] var2) {
      super();
      this.translationKey = var1;
      this.args = var2;
   }

   public Component createComponent(Component var1) {
      return (Component)Objects.requireNonNullElse(this.createComponent(), var1);
   }

   @Nullable
   public Component createComponent() {
      if (!I18n.exists(this.translationKey)) {
         return null;
      } else {
         return this.args == null ? Component.translatable(this.translationKey) : Component.translatable(this.translationKey, (Object[])this.args);
      }
   }

   public static RealmsText parse(JsonObject var0) {
      String var1 = JsonUtils.getRequiredString("translationKey", var0);
      JsonElement var2 = var0.get("args");
      String[] var3;
      if (var2 != null && !var2.isJsonNull()) {
         JsonArray var4 = var2.getAsJsonArray();
         var3 = new String[var4.size()];

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            var3[var5] = var4.get(var5).getAsString();
         }
      } else {
         var3 = null;
      }

      return new RealmsText(var1, var3);
   }

   public String toString() {
      return this.translationKey;
   }
}
