package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public abstract class EntityRenameFix extends DataFix {
   protected final String name;

   public EntityRenameFix(String var1, Schema var2, boolean var3) {
      super(var2, var3);
      this.name = var1;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoice.TaggedChoiceType var1 = this.getInputSchema().findChoiceType(References.ENTITY);
      TaggedChoice.TaggedChoiceType var2 = this.getOutputSchema().findChoiceType(References.ENTITY);
      Function var3 = Util.memoize((var2x) -> {
         Type var3 = (Type)var1.types().get(var2x);
         return ExtraDataFixUtils.patchSubType(var3, var1, var2);
      });
      return this.fixTypeEverywhere(this.name, var1, var2, (var3x) -> {
         return (var4) -> {
            String var5 = (String)var4.getFirst();
            Type var6 = (Type)var3.apply(var5);
            Pair var7 = this.fix(var5, this.getEntity(var4.getSecond(), var3x, var6));
            Type var8 = (Type)var2.types().get(var7.getFirst());
            if (!var8.equals(((Typed)var7.getSecond()).getType(), true, true)) {
               throw new IllegalStateException(String.format(Locale.ROOT, "Dynamic type check failed: %s not equal to %s", var8, ((Typed)var7.getSecond()).getType()));
            } else {
               return Pair.of((String)var7.getFirst(), ((Typed)var7.getSecond()).getValue());
            }
         };
      });
   }

   private <A> Typed<A> getEntity(Object var1, DynamicOps<?> var2, Type<A> var3) {
      return new Typed(var3, var2, var1);
   }

   protected abstract Pair<String, Typed<?>> fix(String var1, Typed<?> var2);
}
