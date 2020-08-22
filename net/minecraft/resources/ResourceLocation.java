package net.minecraft.resources;

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
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ResourceLocation implements Comparable {
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new TranslatableComponent("argument.id.invalid", new Object[0]));
   protected final String namespace;
   protected final String path;

   protected ResourceLocation(String[] var1) {
      this.namespace = StringUtils.isEmpty(var1[0]) ? "minecraft" : var1[0];
      this.path = var1[1];
      if (!isValidNamespace(this.namespace)) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
      } else if (!isValidPath(this.path)) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
      }
   }

   public ResourceLocation(String var1) {
      this(decompose(var1, ':'));
   }

   public ResourceLocation(String var1, String var2) {
      this(new String[]{var1, var2});
   }

   public static ResourceLocation of(String var0, char var1) {
      return new ResourceLocation(decompose(var0, var1));
   }

   @Nullable
   public static ResourceLocation tryParse(String var0) {
      try {
         return new ResourceLocation(var0);
      } catch (ResourceLocationException var2) {
         return null;
      }
   }

   protected static String[] decompose(String var0, char var1) {
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

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public String toString() {
      return this.namespace + ':' + this.path;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation var2 = (ResourceLocation)var1;
         return this.namespace.equals(var2.namespace) && this.path.equals(var2.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(ResourceLocation var1) {
      int var2 = this.path.compareTo(var1.path);
      if (var2 == 0) {
         var2 = this.namespace.compareTo(var1.namespace);
      }

      return var2;
   }

   public static ResourceLocation read(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();

      while(var0.canRead() && isAllowedInResourceLocation(var0.peek())) {
         var0.skip();
      }

      String var2 = var0.getString().substring(var1, var0.getCursor());

      try {
         return new ResourceLocation(var2);
      } catch (ResourceLocationException var4) {
         var0.setCursor(var1);
         throw ERROR_INVALID.createWithContext(var0);
      }
   }

   public static boolean isAllowedInResourceLocation(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'a' && var0 <= 'z' || var0 == '_' || var0 == ':' || var0 == '/' || var0 == '.' || var0 == '-';
   }

   private static boolean isValidPath(String var0) {
      return var0.chars().allMatch((var0x) -> {
         return var0x == 95 || var0x == 45 || var0x >= 97 && var0x <= 122 || var0x >= 48 && var0x <= 57 || var0x == 47 || var0x == 46;
      });
   }

   private static boolean isValidNamespace(String var0) {
      return var0.chars().allMatch((var0x) -> {
         return var0x == 95 || var0x == 45 || var0x >= 97 && var0x <= 122 || var0x >= 48 && var0x <= 57 || var0x == 46;
      });
   }

   public static boolean isValidResourceLocation(String var0) {
      String[] var1 = decompose(var0, ':');
      return isValidNamespace(StringUtils.isEmpty(var1[0]) ? "minecraft" : var1[0]) && isValidPath(var1[1]);
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((ResourceLocation)var1);
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public ResourceLocation deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return new ResourceLocation(GsonHelper.convertToString(var1, "location"));
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
