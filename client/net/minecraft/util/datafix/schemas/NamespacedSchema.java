package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.resources.ResourceLocation;

public class NamespacedSchema extends Schema {
   public NamespacedSchema(int var1, Schema var2) {
      super(var1, var2);
   }

   public static String ensureNamespaced(String var0) {
      ResourceLocation var1 = ResourceLocation.tryParse(var0);
      return var1 != null ? var1.toString() : var0;
   }

   public Type<?> getChoiceType(TypeReference var1, String var2) {
      return super.getChoiceType(var1, ensureNamespaced(var2));
   }
}
