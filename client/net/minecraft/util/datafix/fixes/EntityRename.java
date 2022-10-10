package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.datafix.TypeReferences;

public abstract class EntityRename extends DataFix {
   protected final String field_211313_a;

   public EntityRename(String var1, Schema var2, boolean var3) {
      super(var2, var3);
      this.field_211313_a = var1;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType var1 = this.getInputSchema().findChoiceType(TypeReferences.field_211299_o);
      TaggedChoiceType var2 = this.getOutputSchema().findChoiceType(TypeReferences.field_211299_o);
      return this.fixTypeEverywhere(this.field_211313_a, var1, var2, (var3) -> {
         return (var4) -> {
            String var5 = (String)var4.getFirst();
            Type var6 = (Type)var1.types().get(var5);
            Pair var7 = this.func_209149_a(var5, this.func_209757_a(var4.getSecond(), var3, var6));
            Type var8 = (Type)var2.types().get(var7.getFirst());
            if (!var8.equals(((Typed)var7.getSecond()).getType(), true, true)) {
               throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", var8, ((Typed)var7.getSecond()).getType()));
            } else {
               return Pair.of(var7.getFirst(), ((Typed)var7.getSecond()).getValue());
            }
         };
      });
   }

   private <A> Typed<A> func_209757_a(Object var1, DynamicOps<?> var2, Type<A> var3) {
      return new Typed(var3, var2, var1);
   }

   protected abstract Pair<String, Typed<?>> func_209149_a(String var1, Typed<?> var2);
}
