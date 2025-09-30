package net.minecraft.client;

import com.mojang.util.UndashedUuid;
import java.util.Optional;
import java.util.UUID;

public class User {
   private final String name;
   private final UUID uuid;
   private final String accessToken;
   private final Optional<String> xuid;
   private final Optional<String> clientId;

   public User(String var1, UUID var2, String var3, Optional<String> var4, Optional<String> var5) {
      super();
      this.name = var1;
      this.uuid = var2;
      this.accessToken = var3;
      this.xuid = var4;
      this.clientId = var5;
   }

   public String getSessionId() {
      String var10000 = this.accessToken;
      return "token:" + var10000 + ":" + UndashedUuid.toString(this.uuid);
   }

   public UUID getProfileId() {
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
}
