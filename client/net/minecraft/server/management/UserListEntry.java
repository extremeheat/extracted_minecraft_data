package net.minecraft.server.management;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class UserListEntry<T> {
   @Nullable
   private final T field_152642_a;

   public UserListEntry(T var1) {
      super();
      this.field_152642_a = var1;
   }

   protected UserListEntry(@Nullable T var1, JsonObject var2) {
      super();
      this.field_152642_a = var1;
   }

   @Nullable
   T func_152640_f() {
      return this.field_152642_a;
   }

   boolean func_73682_e() {
      return false;
   }

   protected void func_152641_a(JsonObject var1) {
   }
}
