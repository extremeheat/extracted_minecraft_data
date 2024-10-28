package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntityPaintingMotiveFix extends NamedEntityFix {
   private static final Map<String, String> MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
      var0.put("donkeykong", "donkey_kong");
      var0.put("burningskull", "burning_skull");
      var0.put("skullandroses", "skull_and_roses");
   });

   public EntityPaintingMotiveFix(Schema var1, boolean var2) {
      super(var1, var2, "EntityPaintingMotiveFix", References.ENTITY, "minecraft:painting");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      Optional var2 = var1.get("Motive").asString().result();
      if (var2.isPresent()) {
         String var3 = ((String)var2.get()).toLowerCase(Locale.ROOT);
         return var1.set("Motive", var1.createString(NamespacedSchema.ensureNamespaced((String)MAP.getOrDefault(var3, var3))));
      } else {
         return var1;
      }
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
