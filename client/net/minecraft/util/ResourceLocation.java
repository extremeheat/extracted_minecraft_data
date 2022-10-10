package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextComponentTranslation;

public class ResourceLocation implements Comparable<ResourceLocation> {
   private static final SimpleCommandExceptionType field_200118_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.id.invalid", new Object[0]));
   protected final String field_110626_a;
   protected final String field_110625_b;

   protected ResourceLocation(String[] var1) {
      super();
      this.field_110626_a = org.apache.commons.lang3.StringUtils.isEmpty(var1[0]) ? "minecraft" : var1[0];
      this.field_110625_b = var1[1];
      if (!this.field_110626_a.chars().allMatch((var0) -> {
         return var0 == 95 || var0 == 45 || var0 >= 97 && var0 <= 122 || var0 >= 48 && var0 <= 57 || var0 == 46;
      })) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.field_110626_a + ':' + this.field_110625_b);
      } else if (!this.field_110625_b.chars().allMatch((var0) -> {
         return var0 == 95 || var0 == 45 || var0 >= 97 && var0 <= 122 || var0 >= 48 && var0 <= 57 || var0 == 47 || var0 == 46;
      })) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.field_110626_a + ':' + this.field_110625_b);
      }
   }

   public ResourceLocation(String var1) {
      this(func_195823_b(var1, ':'));
   }

   public ResourceLocation(String var1, String var2) {
      this(new String[]{var1, var2});
   }

   public static ResourceLocation func_195828_a(String var0, char var1) {
      return new ResourceLocation(func_195823_b(var0, var1));
   }

   @Nullable
   public static ResourceLocation func_208304_a(String var0) {
      try {
         return new ResourceLocation(var0);
      } catch (ResourceLocationException var2) {
         return null;
      }
   }

   protected static String[] func_195823_b(String var0, char var1) {
      String[] var2 = new String[]{"minecraft", var0};
      int var3 = var0.indexOf(var1);
      if (var3 >= 0) {
         var2[1] = var0.substring(var3 + 1, var0.length());
         if (var3 >= 1) {
            var2[0] = var0.substring(0, var3);
         }
      }

      return var2;
   }

   public String func_110623_a() {
      return this.field_110625_b;
   }

   public String func_110624_b() {
      return this.field_110626_a;
   }

   public String toString() {
      return this.field_110626_a + ':' + this.field_110625_b;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation var2 = (ResourceLocation)var1;
         return this.field_110626_a.equals(var2.field_110626_a) && this.field_110625_b.equals(var2.field_110625_b);
      }
   }

   public int hashCode() {
      return 31 * this.field_110626_a.hashCode() + this.field_110625_b.hashCode();
   }

   public int compareTo(ResourceLocation var1) {
      int var2 = this.field_110625_b.compareTo(var1.field_110625_b);
      if (var2 == 0) {
         var2 = this.field_110626_a.compareTo(var1.field_110626_a);
      }

      return var2;
   }

   public static ResourceLocation func_195826_a(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();

      while(var0.canRead() && func_195824_a(var0.peek())) {
         var0.skip();
      }

      String var2 = var0.getString().substring(var1, var0.getCursor());

      try {
         return new ResourceLocation(var2);
      } catch (ResourceLocationException var4) {
         var0.setCursor(var1);
         throw field_200118_c.createWithContext(var0);
      }
   }

   public static boolean func_195824_a(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'a' && var0 <= 'z' || var0 == '_' || var0 == ':' || var0 == '/' || var0 == '.' || var0 == '-';
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((ResourceLocation)var1);
   }

   public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
      public Serializer() {
         super();
      }

      public ResourceLocation deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return new ResourceLocation(JsonUtils.func_151206_a(var1, "location"));
      }

      public JsonElement serialize(ResourceLocation var1, Type var2, JsonSerializationContext var3) {
         return new JsonPrimitive(var1.toString());
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((ResourceLocation)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
