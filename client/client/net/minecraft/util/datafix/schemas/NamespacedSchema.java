package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Const.PrimitiveType;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.resources.ResourceLocation;

public class NamespacedSchema extends Schema {
   public static final PrimitiveCodec<String> NAMESPACED_STRING_CODEC = new PrimitiveCodec<String>() {
      public <T> DataResult<String> read(DynamicOps<T> var1, T var2) {
         return var1.getStringValue(var2).map(NamespacedSchema::ensureNamespaced);
      }

      public <T> T write(DynamicOps<T> var1, String var2) {
         return (T)var1.createString(var2);
      }

      @Override
      public String toString() {
         return "NamespacedString";
      }
   };
   private static final Type<String> NAMESPACED_STRING = new PrimitiveType(NAMESPACED_STRING_CODEC);

   public NamespacedSchema(int var1, Schema var2) {
      super(var1, var2);
   }

   public static String ensureNamespaced(String var0) {
      ResourceLocation var1 = ResourceLocation.tryParse(var0);
      return var1 != null ? var1.toString() : var0;
   }

   public static Type<String> namespacedString() {
      return NAMESPACED_STRING;
   }

   public Type<?> getChoiceType(TypeReference var1, String var2) {
      return super.getChoiceType(var1, ensureNamespaced(var2));
   }
}
