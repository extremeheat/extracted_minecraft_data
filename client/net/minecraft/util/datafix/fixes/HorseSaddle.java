package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class HorseSaddle extends NamedEntityFix {
   public HorseSaddle(Schema var1, boolean var2) {
      super(var1, var2, "EntityHorseSaddleFix", TypeReferences.field_211299_o, "EntityHorse");
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(TypeReferences.field_211301_q.typeName(), DSL.namespacedString()));
      Type var3 = this.getInputSchema().getTypeRaw(TypeReferences.field_211295_k);
      OpticFinder var4 = DSL.fieldFinder("SaddleItem", var3);
      Optional var5 = var1.getOptionalTyped(var4);
      Dynamic var6 = (Dynamic)var1.get(DSL.remainderFinder());
      if (!var5.isPresent() && var6.getBoolean("Saddle")) {
         Typed var7 = (Typed)var3.pointTyped(var1.getOps()).orElseThrow(IllegalStateException::new);
         var7 = var7.set(var2, Pair.of(TypeReferences.field_211301_q.typeName(), "minecraft:saddle"));
         Dynamic var8 = var6.emptyMap();
         var8 = var8.set("Count", var8.createByte((byte)1));
         var8 = var8.set("Damage", var8.createShort((short)0));
         var7 = var7.set(DSL.remainderFinder(), var8);
         var6.remove("Saddle");
         return var1.set(var4, var7).set(DSL.remainderFinder(), var6);
      } else {
         return var1;
      }
   }
}
