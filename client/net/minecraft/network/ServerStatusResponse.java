package net.minecraft.network;

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
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.ITextComponent;

public class ServerStatusResponse {
   private ITextComponent field_151326_a;
   private ServerStatusResponse.Players field_151324_b;
   private ServerStatusResponse.Version field_151325_c;
   private String field_151323_d;

   public ServerStatusResponse() {
      super();
   }

   public ITextComponent func_151317_a() {
      return this.field_151326_a;
   }

   public void func_151315_a(ITextComponent var1) {
      this.field_151326_a = var1;
   }

   public ServerStatusResponse.Players func_151318_b() {
      return this.field_151324_b;
   }

   public void func_151319_a(ServerStatusResponse.Players var1) {
      this.field_151324_b = var1;
   }

   public ServerStatusResponse.Version func_151322_c() {
      return this.field_151325_c;
   }

   public void func_151321_a(ServerStatusResponse.Version var1) {
      this.field_151325_c = var1;
   }

   public void func_151320_a(String var1) {
      this.field_151323_d = var1;
   }

   public String func_151316_d() {
      return this.field_151323_d;
   }

   public static class Serializer implements JsonDeserializer<ServerStatusResponse>, JsonSerializer<ServerStatusResponse> {
      public Serializer() {
         super();
      }

      public ServerStatusResponse deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = JsonUtils.func_151210_l(var1, "status");
         ServerStatusResponse var5 = new ServerStatusResponse();
         if (var4.has("description")) {
            var5.func_151315_a((ITextComponent)var3.deserialize(var4.get("description"), ITextComponent.class));
         }

         if (var4.has("players")) {
            var5.func_151319_a((ServerStatusResponse.Players)var3.deserialize(var4.get("players"), ServerStatusResponse.Players.class));
         }

         if (var4.has("version")) {
            var5.func_151321_a((ServerStatusResponse.Version)var3.deserialize(var4.get("version"), ServerStatusResponse.Version.class));
         }

         if (var4.has("favicon")) {
            var5.func_151320_a(JsonUtils.func_151200_h(var4, "favicon"));
         }

         return var5;
      }

      public JsonElement serialize(ServerStatusResponse var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         if (var1.func_151317_a() != null) {
            var4.add("description", var3.serialize(var1.func_151317_a()));
         }

         if (var1.func_151318_b() != null) {
            var4.add("players", var3.serialize(var1.func_151318_b()));
         }

         if (var1.func_151322_c() != null) {
            var4.add("version", var3.serialize(var1.func_151322_c()));
         }

         if (var1.func_151316_d() != null) {
            var4.addProperty("favicon", var1.func_151316_d());
         }

         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((ServerStatusResponse)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static class Version {
      private final String field_151306_a;
      private final int field_151305_b;

      public Version(String var1, int var2) {
         super();
         this.field_151306_a = var1;
         this.field_151305_b = var2;
      }

      public String func_151303_a() {
         return this.field_151306_a;
      }

      public int func_151304_b() {
         return this.field_151305_b;
      }

      public static class Serializer implements JsonDeserializer<ServerStatusResponse.Version>, JsonSerializer<ServerStatusResponse.Version> {
         public Serializer() {
            super();
         }

         public ServerStatusResponse.Version deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            JsonObject var4 = JsonUtils.func_151210_l(var1, "version");
            return new ServerStatusResponse.Version(JsonUtils.func_151200_h(var4, "name"), JsonUtils.func_151203_m(var4, "protocol"));
         }

         public JsonElement serialize(ServerStatusResponse.Version var1, Type var2, JsonSerializationContext var3) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("name", var1.func_151303_a());
            var4.addProperty("protocol", var1.func_151304_b());
            return var4;
         }

         // $FF: synthetic method
         public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
            return this.serialize((ServerStatusResponse.Version)var1, var2, var3);
         }

         // $FF: synthetic method
         public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            return this.deserialize(var1, var2, var3);
         }
      }
   }

   public static class Players {
      private final int field_151336_a;
      private final int field_151334_b;
      private GameProfile[] field_151335_c;

      public Players(int var1, int var2) {
         super();
         this.field_151336_a = var1;
         this.field_151334_b = var2;
      }

      public int func_151332_a() {
         return this.field_151336_a;
      }

      public int func_151333_b() {
         return this.field_151334_b;
      }

      public GameProfile[] func_151331_c() {
         return this.field_151335_c;
      }

      public void func_151330_a(GameProfile[] var1) {
         this.field_151335_c = var1;
      }

      public static class Serializer implements JsonDeserializer<ServerStatusResponse.Players>, JsonSerializer<ServerStatusResponse.Players> {
         public Serializer() {
            super();
         }

         public ServerStatusResponse.Players deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            JsonObject var4 = JsonUtils.func_151210_l(var1, "players");
            ServerStatusResponse.Players var5 = new ServerStatusResponse.Players(JsonUtils.func_151203_m(var4, "max"), JsonUtils.func_151203_m(var4, "online"));
            if (JsonUtils.func_151202_d(var4, "sample")) {
               JsonArray var6 = JsonUtils.func_151214_t(var4, "sample");
               if (var6.size() > 0) {
                  GameProfile[] var7 = new GameProfile[var6.size()];

                  for(int var8 = 0; var8 < var7.length; ++var8) {
                     JsonObject var9 = JsonUtils.func_151210_l(var6.get(var8), "player[" + var8 + "]");
                     String var10 = JsonUtils.func_151200_h(var9, "id");
                     var7[var8] = new GameProfile(UUID.fromString(var10), JsonUtils.func_151200_h(var9, "name"));
                  }

                  var5.func_151330_a(var7);
               }
            }

            return var5;
         }

         public JsonElement serialize(ServerStatusResponse.Players var1, Type var2, JsonSerializationContext var3) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("max", var1.func_151332_a());
            var4.addProperty("online", var1.func_151333_b());
            if (var1.func_151331_c() != null && var1.func_151331_c().length > 0) {
               JsonArray var5 = new JsonArray();

               for(int var6 = 0; var6 < var1.func_151331_c().length; ++var6) {
                  JsonObject var7 = new JsonObject();
                  UUID var8 = var1.func_151331_c()[var6].getId();
                  var7.addProperty("id", var8 == null ? "" : var8.toString());
                  var7.addProperty("name", var1.func_151331_c()[var6].getName());
                  var5.add(var7);
               }

               var4.add("sample", var5);
            }

            return var4;
         }

         // $FF: synthetic method
         public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
            return this.serialize((ServerStatusResponse.Players)var1, var2, var3);
         }

         // $FF: synthetic method
         public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            return this.deserialize(var1, var2, var3);
         }
      }
   }
}
