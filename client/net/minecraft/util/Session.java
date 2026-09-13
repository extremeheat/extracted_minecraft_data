package net.minecraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;

public class Session {
   private final String field_74286_b;
   private final String field_148257_b;
   private final String field_148258_c;
   private final Session$Type field_152429_d;

   public Session(String var1, String var2, String var3, String var4) {
      super();
      this.field_74286_b = var1;
      this.field_148257_b = var2;
      this.field_148258_c = var3;
      this.field_152429_d = Session$Type.func_152421_a(var4);
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
         return new GameProfile(null, this.func_111285_a());
      }
   }

   public Session$Type func_152428_f() {
      return this.field_152429_d;
   }
}
