package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4307 extends NamespacedSchema {
   public V4307(int var1, Schema var2) {
      super(var1, var2);
   }

   public static SequencedMap<String, Supplier<TypeTemplate>> components(Schema var0) {
      SequencedMap var1 = V4059.components(var0);
      var1.put("minecraft:can_place_on", (Supplier)() -> adventureModePredicate(var0));
      var1.put("minecraft:can_break", (Supplier)() -> adventureModePredicate(var0));
      return var1;
   }

   private static TypeTemplate adventureModePredicate(Schema var0) {
      TypeTemplate var1 = DSL.optionalFields("blocks", DSL.or(References.BLOCK_NAME.in(var0), DSL.list(References.BLOCK_NAME.in(var0))));
      return DSL.or(var1, DSL.list(var1));
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, References.DATA_COMPONENTS, () -> DSL.optionalFieldsLazy(components(var1)));
   }
}
