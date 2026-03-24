package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class RealmsText {
   private static final String TRANSLATION_KEY = "translationKey";
   private static final String ARGS = "args";
   private final String translationKey;
   private final String @Nullable [] args;

   private RealmsText(final String translationKey, final String @Nullable [] args) {
      super();
      this.translationKey = translationKey;
      this.args = args;
   }

   public Component createComponent(final Component fallback) {
      return (Component)Objects.requireNonNullElse(this.createComponent(), fallback);
   }

   public @Nullable Component createComponent() {
      if (!I18n.exists(this.translationKey)) {
         return null;
      } else {
         return this.args == null ? Component.translatable(this.translationKey) : Component.translatable(this.translationKey, (Object[])this.args);
      }
   }

   public static RealmsText parse(final JsonObject jsonObject) {
      String translationKey = JsonUtils.getRequiredString("translationKey", jsonObject);
      JsonElement argsJsonElement = jsonObject.get("args");
      String[] args;
      if (argsJsonElement != null && !argsJsonElement.isJsonNull()) {
         JsonArray argsJsonArray = argsJsonElement.getAsJsonArray();
         args = new String[argsJsonArray.size()];

         for(int i = 0; i < argsJsonArray.size(); ++i) {
            args[i] = argsJsonArray.get(i).getAsString();
         }
      } else {
         args = null;
      }

      return new RealmsText(translationKey, args);
   }

   public String toString() {
      return this.translationKey;
   }
}
