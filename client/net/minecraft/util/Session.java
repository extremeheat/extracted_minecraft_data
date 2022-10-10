package net.minecraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class Session {
   private final String field_74286_b;
   private final String field_148257_b;
   private final String field_148258_c;
   private final Session.Type field_152429_d;

   public Session(String var1, String var2, String var3, String var4) {
      super();
      this.field_74286_b = var1;
      this.field_148257_b = var2;
      this.field_148258_c = var3;
      this.field_152429_d = Session.Type.func_152421_a(var4);
   }

   public String func_111286_b() {
      return "token:" + this.field_148258_c + ":" + this.field_148257_b;
   }

   public String func_148255_b() {
      return this.field_148257_b;
   }

   public String func_111285_a() {
      return this.field_74286_b;
   }

   public String func_148254_d() {
      return this.field_148258_c;
   }

   public GameProfile func_148256_e() {
      try {
         UUID var1 = UUIDTypeAdapter.fromString(this.func_148255_b());
         return new GameProfile(var1, this.func_111285_a());
      } catch (IllegalArgumentException var2) {
         return new GameProfile((UUID)null, this.func_111285_a());
      }
   }

   public static enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map<String, Session.Type> field_152425_c = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
         return var0.field_152426_d;
      }, Function.identity()));
      private final String field_152426_d;

      private Type(String var3) {
         this.field_152426_d = var3;
      }

      @Nullable
      public static Session.Type func_152421_a(String var0) {
         return (Session.Type)field_152425_c.get(var0.toLowerCase(Locale.ROOT));
      }
   }
}
