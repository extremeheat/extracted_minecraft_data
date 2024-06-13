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
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Type;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.GsonHelper;

public final class ResourceLocation implements Comparable<ResourceLocation> {
   public static final Codec<ResourceLocation> CODEC = Codec.STRING.comapFlatMap(ResourceLocation::read, ResourceLocation::toString).stable();
   public static final StreamCodec<ByteBuf, ResourceLocation> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(ResourceLocation::parse, ResourceLocation::toString);
   public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));
   public static final char NAMESPACE_SEPARATOR = ':';
   public static final String DEFAULT_NAMESPACE = "minecraft";
   public static final String REALMS_NAMESPACE = "realms";
   private final String namespace;
   private final String path;

   private ResourceLocation(String var1, String var2) {
      super();

      assert isValidNamespace(var1);

      assert isValidPath(var2);

      this.namespace = var1;
      this.path = var2;
   }

   private static ResourceLocation createUntrusted(String var0, String var1) {
      return new ResourceLocation(assertValidNamespace(var0, var1), assertValidPath(var0, var1));
   }

   public static ResourceLocation fromNamespaceAndPath(String var0, String var1) {
      return createUntrusted(var0, var1);
   }

   public static ResourceLocation parse(String var0) {
      return bySeparator(var0, ':');
   }

   public static ResourceLocation withDefaultNamespace(String var0) {
      return new ResourceLocation("minecraft", assertValidPath("minecraft", var0));
   }

   @Nullable
   public static ResourceLocation tryParse(String var0) {
      return tryBySeparator(var0, ':');
   }

   @Nullable
   public static ResourceLocation tryBuild(String var0, String var1) {
      return isValidNamespace(var0) && isValidPath(var1) ? new ResourceLocation(var0, var1) : null;
   }

   public static ResourceLocation bySeparator(String var0, char var1) {
      int var2 = var0.indexOf(var1);
      if (var2 >= 0) {
         String var3 = var0.substring(var2 + 1);
         if (var2 != 0) {
            String var4 = var0.substring(0, var2);
            return createUntrusted(var4, var3);
         } else {
            return withDefaultNamespace(var3);
         }
      } else {
         return withDefaultNamespace(var0);
      }
   }

   @Nullable
   public static ResourceLocation tryBySeparator(String var0, char var1) {
      int var2 = var0.indexOf(var1);
      if (var2 >= 0) {
         String var3 = var0.substring(var2 + 1);
         if (!isValidPath(var3)) {
            return null;
         } else if (var2 != 0) {
            String var4 = var0.substring(0, var2);
            return isValidNamespace(var4) ? new ResourceLocation(var4, var3) : null;
         } else {
            return new ResourceLocation("minecraft", var3);
         }
      } else {
         return isValidPath(var0) ? new ResourceLocation("minecraft", var0) : null;
      }
   }

   public static DataResult<ResourceLocation> read(String var0) {
      try {
         return DataResult.success(parse(var0));
      } catch (ResourceLocationException var2) {
         return DataResult.error(() -> "Not a valid resource location: " + var0 + " " + var2.getMessage());
      }
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public ResourceLocation withPath(String var1) {
      return new ResourceLocation(this.namespace, assertValidPath(this.namespace, var1));
   }

   public ResourceLocation withPath(UnaryOperator<String> var1) {
      return this.withPath(var1.apply(this.path));
   }

   public ResourceLocation withPrefix(String var1) {
      return this.withPath(var1 + this.path);
   }

   public ResourceLocation withSuffix(String var1) {
      return this.withPath(this.path + var1);
   }

   @Override
   public String toString() {
      return this.namespace + ":" + this.path;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof ResourceLocation var2) ? false : this.namespace.equals(var2.namespace) && this.path.equals(var2.path);
      }
   }

   @Override
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

   public String toDebugFileName() {
      return this.toString().replace('/', '_').replace(':', '_');
   }

   public String toLanguageKey() {
      return this.namespace + "." + this.path;
   }

   public String toShortLanguageKey() {
      return this.namespace.equals("minecraft") ? this.path : this.toLanguageKey();
   }

   public String toLanguageKey(String var1) {
      return var1 + "." + this.toLanguageKey();
   }

   public String toLanguageKey(String var1, String var2) {
      return var1 + "." + this.toLanguageKey() + "." + var2;
   }

   private static String readGreedy(StringReader var0) {
      int var1 = var0.getCursor();

      while (var0.canRead() && isAllowedInResourceLocation(var0.peek())) {
         var0.skip();
      }

      return var0.getString().substring(var1, var0.getCursor());
   }

   public static ResourceLocation read(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      String var2 = readGreedy(var0);

      try {
         return parse(var2);
      } catch (ResourceLocationException var4) {
         var0.setCursor(var1);
         throw ERROR_INVALID.createWithContext(var0);
      }
   }

   public static ResourceLocation readNonEmpty(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      String var2 = readGreedy(var0);
      if (var2.isEmpty()) {
         throw ERROR_INVALID.createWithContext(var0);
      } else {
         try {
            return parse(var2);
         } catch (ResourceLocationException var4) {
            var0.setCursor(var1);
            throw ERROR_INVALID.createWithContext(var0);
         }
      }
   }

   public static boolean isAllowedInResourceLocation(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'a' && var0 <= 'z' || var0 == '_' || var0 == ':' || var0 == '/' || var0 == '.' || var0 == '-';
   }

   public static boolean isValidPath(String var0) {
      for (int var1 = 0; var1 < var0.length(); var1++) {
         if (!validPathChar(var0.charAt(var1))) {
            return false;
         }
      }

      return true;
   }

   public static boolean isValidNamespace(String var0) {
      for (int var1 = 0; var1 < var0.length(); var1++) {
         if (!validNamespaceChar(var0.charAt(var1))) {
            return false;
         }
      }

      return true;
   }

   private static String assertValidNamespace(String var0, String var1) {
      if (!isValidNamespace(var0)) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + var0 + ":" + var1);
      } else {
         return var0;
      }
   }

   public static boolean validPathChar(char var0) {
      return var0 == '_' || var0 == '-' || var0 >= 'a' && var0 <= 'z' || var0 >= '0' && var0 <= '9' || var0 == '/' || var0 == '.';
   }

   private static boolean validNamespaceChar(char var0) {
      return var0 == '_' || var0 == '-' || var0 >= 'a' && var0 <= 'z' || var0 >= '0' && var0 <= '9' || var0 == '.';
   }

   private static String assertValidPath(String var0, String var1) {
      if (!isValidPath(var1)) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + var0 + ":" + var1);
      } else {
         return var1;
      }
   }

   public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
      public Serializer() {
         super();
      }

      public ResourceLocation deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return ResourceLocation.parse(GsonHelper.convertToString(var1, "location"));
      }

      public JsonElement serialize(ResourceLocation var1, Type var2, JsonSerializationContext var3) {
         return new JsonPrimitive(var1.toString());
      }
   }
}
