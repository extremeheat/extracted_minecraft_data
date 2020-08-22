package net.minecraft.network.protocol.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class ServerStatus {
   private Component description;
   private ServerStatus.Players players;
   private ServerStatus.Version version;
   private String favicon;

   public Component getDescription() {
      return this.description;
   }

   public void setDescription(Component var1) {
      this.description = var1;
   }

   public ServerStatus.Players getPlayers() {
      return this.players;
   }

   public void setPlayers(ServerStatus.Players var1) {
      this.players = var1;
   }

   public ServerStatus.Version getVersion() {
      return this.version;
   }

   public void setVersion(ServerStatus.Version var1) {
      this.version = var1;
   }

   public void setFavicon(String var1) {
      this.favicon = var1;
   }

   public String getFavicon() {
      return this.favicon;
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public ServerStatus deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(var1, "status");
         ServerStatus var5 = new ServerStatus();
         if (var4.has("description")) {
            var5.setDescription((Component)var3.deserialize(var4.get("description"), Component.class));
         }

         if (var4.has("players")) {
            var5.setPlayers((ServerStatus.Players)var3.deserialize(var4.get("players"), ServerStatus.Players.class));
         }

         if (var4.has("version")) {
            var5.setVersion((ServerStatus.Version)var3.deserialize(var4.get("version"), ServerStatus.Version.class));
         }

         if (var4.has("favicon")) {
            var5.setFavicon(GsonHelper.getAsString(var4, "favicon"));
         }

         return var5;
      }

      public JsonElement serialize(ServerStatus var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         if (var1.getDescription() != null) {
            var4.add("description", var3.serialize(var1.getDescription()));
         }

         if (var1.getPlayers() != null) {
            var4.add("players", var3.serialize(var1.getPlayers()));
         }

         if (var1.getVersion() != null) {
            var4.add("version", var3.serialize(var1.getVersion()));
         }

         if (var1.getFavicon() != null) {
            var4.addProperty("favicon", var1.getFavicon());
         }

         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((ServerStatus)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static class Version {
      private final String name;
      private final int protocol;

      public Version(String var1, int var2) {
         this.name = var1;
         this.protocol = var2;
      }

      public String getName() {
         return this.name;
      }

      public int getProtocol() {
         return this.protocol;
      }

      public static class Serializer implements JsonDeserializer, JsonSerializer {
         public ServerStatus.Version deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            JsonObject var4 = GsonHelper.convertToJsonObject(var1, "version");
            return new ServerStatus.Version(GsonHelper.getAsString(var4, "name"), GsonHelper.getAsInt(var4, "protocol"));
         }

         public JsonElement serialize(ServerStatus.Version var1, Type var2, JsonSerializationContext var3) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("name", var1.getName());
            var4.addProperty("protocol", var1.getProtocol());
            return var4;
         }

         // $FF: synthetic method
         public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
            return this.serialize((ServerStatus.Version)var1, var2, var3);
         }

         // $FF: synthetic method
         public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            return this.deserialize(var1, var2, var3);
         }
      }
   }

   public static class Players {
      private final int maxPlayers;
      private final int numPlayers;
      private GameProfile[] sample;

      public Players(int var1, int var2) {
         this.maxPlayers = var1;
         this.numPlayers = var2;
      }

      public int getMaxPlayers() {
         return this.maxPlayers;
      }

      public int getNumPlayers() {
         return this.numPlayers;
      }

      public GameProfile[] getSample() {
         return this.sample;
      }

      public void setSample(GameProfile[] var1) {
         this.sample = var1;
      }

      public static class Serializer implements JsonDeserializer, JsonSerializer {
         public ServerStatus.Players deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            JsonObject var4 = GsonHelper.convertToJsonObject(var1, "players");
            ServerStatus.Players var5 = new ServerStatus.Players(GsonHelper.getAsInt(var4, "max"), GsonHelper.getAsInt(var4, "online"));
            if (GsonHelper.isArrayNode(var4, "sample")) {
               JsonArray var6 = GsonHelper.getAsJsonArray(var4, "sample");
               if (var6.size() > 0) {
                  GameProfile[] var7 = new GameProfile[var6.size()];

                  for(int var8 = 0; var8 < var7.length; ++var8) {
                     JsonObject var9 = GsonHelper.convertToJsonObject(var6.get(var8), "player[" + var8 + "]");
                     String var10 = GsonHelper.getAsString(var9, "id");
                     var7[var8] = new GameProfile(UUID.fromString(var10), GsonHelper.getAsString(var9, "name"));
                  }

                  var5.setSample(var7);
               }
            }

            return var5;
         }

         public JsonElement serialize(ServerStatus.Players var1, Type var2, JsonSerializationContext var3) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("max", var1.getMaxPlayers());
            var4.addProperty("online", var1.getNumPlayers());
            if (var1.getSample() != null && var1.getSample().length > 0) {
               JsonArray var5 = new JsonArray();

               for(int var6 = 0; var6 < var1.getSample().length; ++var6) {
                  JsonObject var7 = new JsonObject();
                  UUID var8 = var1.getSample()[var6].getId();
                  var7.addProperty("id", var8 == null ? "" : var8.toString());
                  var7.addProperty("name", var1.getSample()[var6].getName());
                  var5.add(var7);
               }

               var4.add("sample", var5);
            }

            return var4;
         }

         // $FF: synthetic method
         public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
            return this.serialize((ServerStatus.Players)var1, var2, var3);
         }

         // $FF: synthetic method
         public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            return this.deserialize(var1, var2, var3);
         }
      }
   }
}
