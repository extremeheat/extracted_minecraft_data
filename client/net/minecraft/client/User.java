package net.minecraft.client;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class User {
   private final String name;
   private final String uuid;
   private final String accessToken;
   private final Optional<String> xuid;
   private final Optional<String> clientId;
   private final User.Type type;

   public User(String var1, String var2, String var3, Optional<String> var4, Optional<String> var5, User.Type var6) {
      super();
      this.name = var1;
      this.uuid = var2;
      this.accessToken = var3;
      this.xuid = var4;
      this.clientId = var5;
      this.type = var6;
   }

   public String getSessionId() {
      return "token:" + this.accessToken + ":" + this.uuid;
   }

   public String getUuid() {
      return this.uuid;
   }

   public String getName() {
      return this.name;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public Optional<String> getClientId() {
      return this.clientId;
   }

   public Optional<String> getXuid() {
      return this.xuid;
   }

   public GameProfile getGameProfile() {
      try {
         UUID var1 = UUIDTypeAdapter.fromString(this.getUuid());
         return new GameProfile(var1, this.getName());
      } catch (IllegalArgumentException var2) {
         return new GameProfile(null, this.getName());
      }
   }

   public User.Type getType() {
      return this.type;
   }

   public static enum Type {
      LEGACY("legacy"),
      MOJANG("mojang"),
      MSA("msa");

      private static final Map<String, User.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(var0 -> var0.name, Function.identity()));
      private final String name;

      private Type(String var3) {
         this.name = var3;
      }

      @Nullable
      public static User.Type byName(String var0) {
         return BY_NAME.get(var0.toLowerCase(Locale.ROOT));
      }

      public String getName() {
         return this.name;
      }
   }
}
