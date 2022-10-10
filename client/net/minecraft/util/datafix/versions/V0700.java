package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V0700 extends Schema {
   public V0700(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void func_206627_a(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return V0100.func_206605_a(var0);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      func_206627_a(var1, var2, "ElderGuardian");
      return var2;
   }
}
