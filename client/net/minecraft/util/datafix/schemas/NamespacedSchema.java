package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Const;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class NamespacedSchema extends Schema {
   private static final String DEFAULT_NAMESPACE = "minecraft";
   public static final PrimitiveCodec<String> NAMESPACED_STRING_CODEC = new PrimitiveCodec<String>() {
      public <T> DataResult<String> read(final DynamicOps<T> ops, final T input) {
         return ops.getStringValue(input).map(NamespacedSchema::ensureNamespaced);
      }

      public <T> T write(final DynamicOps<T> ops, final String value) {
         return (T)ops.createString(value);
      }

      public String toString() {
         return "NamespacedString";
      }
   };
   private static final Type<String> NAMESPACED_STRING;

   public NamespacedSchema(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public static String ensureNamespaced(final String input) {
      if (input.equals("DUMMY")) {
         return input;
      } else {
         int separatorIndex = input.indexOf(":");
         if (separatorIndex == 0) {
            return "minecraft" + input;
         } else {
            return separatorIndex > 0 ? input : "minecraft:" + input;
         }
      }
   }

   public static String packNamespacedWithDot(final String location) {
      return replaceNamespaceSeparator(location, ':', '.');
   }

   public static String namespacedStringBySeparator(final String value, final char separator) {
      return replaceNamespaceSeparator(value, separator, ':');
   }

   private static String replaceNamespaceSeparator(final String value, final char oldSeparator, final char newSeparator) {
      int separatorIndex = value.indexOf(oldSeparator);
      String namespace;
      String path;
      if (separatorIndex > 0) {
         namespace = value.substring(0, separatorIndex);
         path = value.substring(separatorIndex + 1);
      } else if (separatorIndex == 0) {
         namespace = "minecraft";
         path = value.substring(1);
      } else {
         namespace = "minecraft";
         path = value;
      }

      return namespace + newSeparator + path;
   }

   public static Type<String> namespacedString() {
      return NAMESPACED_STRING;
   }

   public Type<?> getChoiceType(final DSL.TypeReference type, final String choiceName) {
      return super.getChoiceType(type, ensureNamespaced(choiceName));
   }

   static {
      NAMESPACED_STRING = new Const.PrimitiveType(NAMESPACED_STRING_CODEC);
   }
}
