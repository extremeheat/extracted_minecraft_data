package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.Util;

public class FixProjectileStoredItem extends DataFix {
   private static final String EMPTY_POTION = "minecraft:empty";

   public FixProjectileStoredItem(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ENTITY);
      Type var2 = this.getOutputSchema().getType(References.ENTITY);
      return this.fixTypeEverywhereTyped("Fix AbstractArrow item type", var1, var2, this.chainAllFilters(this.fixChoice("minecraft:trident", FixProjectileStoredItem::castUnchecked), this.fixChoice("minecraft:arrow", FixProjectileStoredItem::fixArrow), this.fixChoice("minecraft:spectral_arrow", FixProjectileStoredItem::fixSpectralArrow)));
   }

   @SafeVarargs
   private <T> Function<Typed<?>, Typed<?>> chainAllFilters(Function<Typed<?>, Typed<?>>... var1) {
      return (var1x) -> {
         Function[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Function var5 = var2[var4];
            var1x = (Typed)var5.apply(var1x);
         }

         return var1x;
      };
   }

   private Function<Typed<?>, Typed<?>> fixChoice(String var1, SubFixer<?> var2) {
      Type var3 = this.getInputSchema().getChoiceType(References.ENTITY, var1);
      Type var4 = this.getOutputSchema().getChoiceType(References.ENTITY, var1);
      return fixChoiceCap(var1, var2, var3, var4);
   }

   private static <T> Function<Typed<?>, Typed<?>> fixChoiceCap(String var0, SubFixer<?> var1, Type<?> var2, Type<T> var3) {
      OpticFinder var4 = DSL.namedChoice(var0, var2);
      return (var3x) -> {
         return var3x.updateTyped(var4, var3, (var2) -> {
            return var1.fix(var2, var3);
         });
      };
   }

   private static <T> Typed<T> fixArrow(Typed<?> var0, Type<T> var1) {
      return Util.writeAndReadTypedOrThrow(var0, var1, (var0x) -> {
         return var0x.set("item", createItemStack(var0x, getArrowType(var0x)));
      });
   }

   private static String getArrowType(Dynamic<?> var0) {
      return var0.get("Potion").asString("minecraft:empty").equals("minecraft:empty") ? "minecraft:arrow" : "minecraft:tipped_arrow";
   }

   private static <T> Typed<T> fixSpectralArrow(Typed<?> var0, Type<T> var1) {
      return Util.writeAndReadTypedOrThrow(var0, var1, (var0x) -> {
         return var0x.set("item", createItemStack(var0x, "minecraft:spectral_arrow"));
      });
   }

   private static Dynamic<?> createItemStack(Dynamic<?> var0, String var1) {
      return var0.createMap(ImmutableMap.of(var0.createString("id"), var0.createString(var1), var0.createString("Count"), var0.createInt(1)));
   }

   private static <T> Typed<T> castUnchecked(Typed<?> var0, Type<T> var1) {
      return new Typed(var1, var0.getOps(), var0.getValue());
   }

   private interface SubFixer<F> {
      Typed<F> fix(Typed<?> var1, Type<F> var2);
   }
}
