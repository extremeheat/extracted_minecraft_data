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

public class EntityRidingToPassengersFix extends DataFix {
   public EntityRidingToPassengersFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      Schema var2 = this.getOutputSchema();
      Type var3 = var1.getTypeRaw(References.ENTITY_TREE);
      Type var4 = var2.getTypeRaw(References.ENTITY_TREE);
      Type var5 = var1.getTypeRaw(References.ENTITY);
      return this.cap(var1, var2, var3, var4, var5);
   }

   private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule cap(
      Schema var1, Schema var2, Type<OldEntityTree> var3, Type<NewEntityTree> var4, Type<Entity> var5
   ) {
      Type var6 = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Riding", var3)), var5));
      Type var7 = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list(var4))), var5));
      Type var8 = var1.getType(References.ENTITY_TREE);
      Type var9 = var2.getType(References.ENTITY_TREE);
      if (!Objects.equals(var8, var6)) {
         throw new IllegalStateException("Old entity type is not what was expected.");
      } else if (!var9.equals(var7, true, true)) {
         throw new IllegalStateException("New entity type is not what was expected.");
      } else {
         OpticFinder var10 = DSL.typeFinder(var6);
         OpticFinder var11 = DSL.typeFinder(var7);
         OpticFinder var12 = DSL.typeFinder(var4);
         Type var13 = var1.getType(References.PLAYER);
         Type var14 = var2.getType(References.PLAYER);
         return TypeRewriteRule.seq(
            this.fixTypeEverywhere(
               "EntityRidingToPassengerFix",
               var6,
               var7,
               var5x -> var6x -> {
                     Optional var7x = Optional.empty();
                     Pair var8x = var6x;

                     while (true) {
                        Either var9x = (Either)DataFixUtils.orElse(
                           var7x.map(
                              var4xxx -> {
                                 Typed var5xxx = (Typed)var4.pointTyped(var5x).orElseThrow(() -> new IllegalStateException("Could not create new entity tree"));
                                 Object var6xx = var5xxx.set(var11, var4xxx)
                                    .getOptional(var12)
                                    .orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
                                 return Either.left(ImmutableList.of(var6xx));
                              }
                           ),
                           Either.right(DSL.unit())
                        );
                        var7x = Optional.of(Pair.of(References.ENTITY_TREE.typeName(), Pair.of(var9x, ((Pair)var8x.getSecond()).getSecond())));
                        Optional var10x = ((Either)((Pair)var8x.getSecond()).getFirst()).left();
                        if (var10x.isEmpty()) {
                           return (Pair)var7x.orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
                        }

                        var8x = (Pair)new Typed(var3, var5x, var10x.get())
                           .getOptional(var10)
                           .orElseThrow(() -> new IllegalStateException("Should always have an entity here"));
                     }
                  }
            ),
            this.writeAndRead("player RootVehicle injecter", var13, var14)
         );
      }
   }
}