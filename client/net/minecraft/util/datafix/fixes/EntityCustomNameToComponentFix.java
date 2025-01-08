package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.Util;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntityCustomNameToComponentFix extends DataFix {
   public EntityCustomNameToComponentFix(Schema var1) {
      super(var1, true);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ENTITY);
      Type var2 = this.getOutputSchema().getType(References.ENTITY);
      OpticFinder var3 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      OpticFinder var4 = var1.findField("CustomName");
      Type var5 = var2.findFieldType("CustomName");
      return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", var1, var2, (var3x) -> fixEntity(var3x, var3, var4, var5));
   }

   private static <T> Typed<?> fixEntity(Typed<?> var0, OpticFinder<String> var1, OpticFinder<String> var2, Type<T> var3) {
      return var0.update(var2, var3, (var3x) -> {
         String var4 = (String)var0.getOptional(var1).orElse("");
         Dynamic var5 = fixCustomName(var0.getOps(), var3x, var4);
         return Util.readTypedOrThrow(var3, var5).getValue();
      });
   }

   private static <T> Dynamic<T> fixCustomName(DynamicOps<T> var0, String var1, String var2) {
      return "minecraft:commandblock_minecart".equals(var2) ? new Dynamic(var0, var0.createString(var1)) : LegacyComponentDataFixUtils.createPlainTextComponent(var0, var1);
   }
}
