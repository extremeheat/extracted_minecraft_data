package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class ProjectileStoredWeaponFix extends DataFix {
   public ProjectileStoredWeaponFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ENTITY);
      Type var2 = this.getOutputSchema().getType(References.ENTITY);
      return this.fixTypeEverywhereTyped("Fix Arrow stored weapon", var1, var2, ExtraDataFixUtils.chainAllFilters(this.fixChoice("minecraft:arrow"), this.fixChoice("minecraft:spectral_arrow")));
   }

   private Function<Typed<?>, Typed<?>> fixChoice(String var1) {
      Type var2 = this.getInputSchema().getChoiceType(References.ENTITY, var1);
      Type var3 = this.getOutputSchema().getChoiceType(References.ENTITY, var1);
      return fixChoiceCap(var1, var2, var3);
   }

   private static <T> Function<Typed<?>, Typed<?>> fixChoiceCap(String var0, Type<?> var1, Type<T> var2) {
      OpticFinder var3 = DSL.namedChoice(var0, var1);
      return (var2x) -> {
         return var2x.updateTyped(var3, var2, (var1) -> {
            return Util.writeAndReadTypedOrThrow(var1, var2, UnaryOperator.identity());
         });
      };
   }
}
