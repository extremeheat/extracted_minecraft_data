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
import java.util.Optional;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;
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
      return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", var1, var2, (var4x) -> fixEntity(var4x, var2, var3, var4, var5));
   }

   private static <T> Typed<?> fixEntity(Typed<?> var0, Type<?> var1, OpticFinder<String> var2, OpticFinder<String> var3, Type<T> var4) {
      Optional var5 = var0.getOptional(var3);
      if (var5.isEmpty()) {
         return ExtraDataFixUtils.cast(var1, var0);
      } else if (((String)var5.get()).isEmpty()) {
         return Util.writeAndReadTypedOrThrow(var0, var1, (var0x) -> var0x.remove("CustomName"));
      } else {
         String var6 = (String)var0.getOptional(var2).orElse("");
         Dynamic var7 = fixCustomName(var0.getOps(), (String)var5.get(), var6);
         return var0.set(var3, Util.readTypedOrThrow(var4, var7));
      }
   }

   private static <T> Dynamic<T> fixCustomName(DynamicOps<T> var0, String var1, String var2) {
      return "minecraft:commandblock_minecart".equals(var2) ? new Dynamic(var0, var0.createString(var1)) : LegacyComponentDataFixUtils.createPlainTextComponent(var0, var1);
   }
}
