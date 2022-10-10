package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class RidingToPassengers extends DataFix {
   public RidingToPassengers(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      Schema var2 = this.getOutputSchema();
      Type var3 = var1.getTypeRaw(TypeReferences.field_211298_n);
      Type var4 = var2.getTypeRaw(TypeReferences.field_211298_n);
      Type var5 = var1.getTypeRaw(TypeReferences.field_211299_o);
      return this.func_206340_a(var1, var2, var3, var4, var5);
   }

   private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule func_206340_a(Schema var1, Schema var2, Type<OldEntityTree> var3, Type<NewEntityTree> var4, Type<Entity> var5) {
      Type var6 = DSL.named(TypeReferences.field_211298_n.typeName(), DSL.and(DSL.optional(DSL.field("Riding", var3)), var5));
      Type var7 = DSL.named(TypeReferences.field_211298_n.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list(var4))), var5));
      Type var8 = var1.getType(TypeReferences.field_211298_n);
      Type var9 = var2.getType(TypeReferences.field_211298_n);
      if (!Objects.equals(var8, var6)) {
         throw new IllegalStateException("Old entity type is not what was expected.");
      } else if (!var9.equals(var7, true, true)) {
         throw new IllegalStateException("New entity type is not what was expected.");
      } else {
         OpticFinder var10 = DSL.typeFinder(var6);
         OpticFinder var11 = DSL.typeFinder(var7);
         OpticFinder var12 = DSL.typeFinder(var4);
         Type var13 = var1.getType(TypeReferences.field_211286_b);
         Type var14 = var2.getType(TypeReferences.field_211286_b);
         return TypeRewriteRule.seq(this.fixTypeEverywhere("EntityRidingToPassengerFix", var6, var7, (var5x) -> {
            return (var6) -> {
               Optional var7 = Optional.empty();
               Pair var8 = var6;

               while(true) {
                  Either var9 = (Either)DataFixUtils.orElse(var7.map((var4x) -> {
                     Typed var5 = (Typed)var4.pointTyped(var5x).orElseThrow(() -> {
                        return new IllegalStateException("Could not create new entity tree");
                     });
                     Object var6 = var5.set(var11, var4x).getOptional(var12).orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                     return Either.left(ImmutableList.of(var6));
                  }), Either.right(DSL.unit()));
                  var7 = Optional.of(Pair.of(TypeReferences.field_211298_n.typeName(), Pair.of(var9, ((Pair)var8.getSecond()).getSecond())));
                  Optional var10x = ((Either)((Pair)var8.getSecond()).getFirst()).left();
                  if (!var10x.isPresent()) {
                     return (Pair)var7.orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                  }

                  var8 = (Pair)(new Typed(var3, var5x, var10x.get())).getOptional(var10).orElseThrow(() -> {
                     return new IllegalStateException("Should always have an entity here");
                  });
               }
            };
         }), this.writeAndRead("player RootVehicle injecter", var13, var14));
      }
   }
}
