package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FurnaceRecipeFix extends DataFix {
   public FurnaceRecipeFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      return this.cap(this.getOutputSchema().getTypeRaw(References.RECIPE));
   }

   private <R> TypeRewriteRule cap(Type<R> var1) {
      Type var2 = DSL.and(DSL.optional(DSL.field("RecipesUsed", DSL.and(DSL.compoundList(var1, DSL.intType()), DSL.remainderType()))), DSL.remainderType());
      OpticFinder var3 = DSL.namedChoice("minecraft:furnace", this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:furnace"));
      OpticFinder var4 = DSL.namedChoice("minecraft:blast_furnace", this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:blast_furnace"));
      OpticFinder var5 = DSL.namedChoice("minecraft:smoker", this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:smoker"));
      Type var6 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:furnace");
      Type var7 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:blast_furnace");
      Type var8 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:smoker");
      Type var9 = this.getInputSchema().getType(References.BLOCK_ENTITY);
      Type var10 = this.getOutputSchema().getType(References.BLOCK_ENTITY);
      return this.fixTypeEverywhereTyped("FurnaceRecipesFix", var9, var10, (var9x) -> {
         return var9x.updateTyped(var3, var6, (var3x) -> {
            return this.updateFurnaceContents(var1, var2, var3x);
         }).updateTyped(var4, var7, (var3x) -> {
            return this.updateFurnaceContents(var1, var2, var3x);
         }).updateTyped(var5, var8, (var3x) -> {
            return this.updateFurnaceContents(var1, var2, var3x);
         });
      });
   }

   private <R> Typed<?> updateFurnaceContents(Type<R> var1, Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>> var2, Typed<?> var3) {
      Dynamic var4 = (Dynamic)var3.getOrCreate(DSL.remainderFinder());
      int var5 = var4.get("RecipesUsedSize").asInt(0);
      var4 = var4.remove("RecipesUsedSize");
      ArrayList var6 = Lists.newArrayList();

      for(int var7 = 0; var7 < var5; ++var7) {
         String var8 = "RecipeLocation" + var7;
         String var9 = "RecipeAmount" + var7;
         Optional var10 = var4.get(var8).result();
         int var11 = var4.get(var9).asInt(0);
         if (var11 > 0) {
            var10.ifPresent((var3x) -> {
               Optional var4 = var1.read(var3x).result();
               var4.ifPresent((var2) -> {
                  var6.add(Pair.of(var2.getFirst(), var11));
               });
            });
         }

         var4 = var4.remove(var8).remove(var9);
      }

      return var3.set(DSL.remainderFinder(), var2, Pair.of(Either.left(Pair.of(var6, var4.emptyMap())), var4));
   }
}
