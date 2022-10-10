package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class JsonException extends IOException {
   private final List<JsonException.Entry> field_151383_a = Lists.newArrayList();
   private final String field_151382_b;

   public JsonException(String var1) {
      super();
      this.field_151383_a.add(new JsonException.Entry());
      this.field_151382_b = var1;
   }

   public JsonException(String var1, Throwable var2) {
      super(var2);
      this.field_151383_a.add(new JsonException.Entry());
      this.field_151382_b = var1;
   }

   public void func_151380_a(String var1) {
      ((JsonException.Entry)this.field_151383_a.get(0)).func_151373_a(var1);
   }

   public void func_151381_b(String var1) {
      ((JsonException.Entry)this.field_151383_a.get(0)).field_151376_a = var1;
      this.field_151383_a.add(0, new JsonException.Entry());
   }

   public String getMessage() {
      return "Invalid " + this.field_151383_a.get(this.field_151383_a.size() - 1) + ": " + this.field_151382_b;
   }

   public static JsonException func_151379_a(Exception var0) {
      if (var0 instanceof JsonException) {
         return (JsonException)var0;
      } else {
         String var1 = var0.getMessage();
         if (var0 instanceof FileNotFoundException) {
            var1 = "File not found";
         }

         return new JsonException(var1, var0);
      }
   }

   public static class Entry {
      private String field_151376_a;
      private final List<String> field_151375_b;

      private Entry() {
         super();
         this.field_151375_b = Lists.newArrayList();
      }

      private void func_151373_a(String var1) {
         this.field_151375_b.add(0, var1);
      }

      public String func_151372_b() {
         return StringUtils.join(this.field_151375_b, "->");
      }

      public String toString() {
         if (this.field_151376_a != null) {
            return this.field_151375_b.isEmpty() ? this.field_151376_a : this.field_151376_a + " " + this.func_151372_b();
         } else {
            return this.field_151375_b.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.func_151372_b();
         }
      }

      // $FF: synthetic method
      Entry(Object var1) {
         this();
      }
   }
}
