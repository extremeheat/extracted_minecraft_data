package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;

public class ChunkRenamesFix extends DataFix {
   public ChunkRenamesFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder var2 = var1.findField("Level");
      OpticFinder var3 = var2.type().findField("Structures");
      Type var4 = this.getOutputSchema().getType(References.CHUNK);
      Type var5 = var4.findFieldType("structures");
      return this.fixTypeEverywhereTyped("Chunk Renames; purge Level-tag", var1, var4, (var3x) -> {
         Typed var4 = var3x.getTyped(var2);
         Typed var5x = appendChunkName(var4);
         var5x = var5x.set(DSL.remainderFinder(), mergeRemainders(var3x, (Dynamic)var4.get(DSL.remainderFinder())));
         var5x = renameField(var5x, "TileEntities", "block_entities");
         var5x = renameField(var5x, "TileTicks", "block_ticks");
         var5x = renameField(var5x, "Entities", "entities");
         var5x = renameField(var5x, "Sections", "sections");
         var5x = var5x.updateTyped(var3, var5, (var0) -> {
            return renameField(var0, "Starts", "starts");
         });
         var5x = renameField(var5x, "Structures", "structures");
         return var5x.update(DSL.remainderFinder(), (var0) -> {
            return var0.remove("Level");
         });
      });
   }

   private static Typed<?> renameField(Typed<?> var0, String var1, String var2) {
      return renameFieldHelper(var0, var1, var2, var0.getType().findFieldType(var1)).update(DSL.remainderFinder(), (var1x) -> {
         return var1x.remove(var1);
      });
   }

   private static <A> Typed<?> renameFieldHelper(Typed<?> var0, String var1, String var2, Type<A> var3) {
      Type var4 = DSL.optional(DSL.field(var1, var3));
      Type var5 = DSL.optional(DSL.field(var2, var3));
      return var0.update(var4.finder(), var5, Function.identity());
   }

   private static <A> Typed<Pair<String, A>> appendChunkName(Typed<A> var0) {
      return new Typed(DSL.named("chunk", var0.getType()), var0.getOps(), Pair.of("chunk", var0.getValue()));
   }

   private static <T> Dynamic<T> mergeRemainders(Typed<?> var0, Dynamic<T> var1) {
      DynamicOps var2 = var1.getOps();
      Dynamic var3 = ((Dynamic)var0.get(DSL.remainderFinder())).convert(var2);
      DataResult var4 = var2.getMap(var1.getValue()).flatMap((var2x) -> {
         return var2.mergeToMap(var3.getValue(), var2x);
      });
      return (Dynamic)var4.result().map((var1x) -> {
         return new Dynamic(var2, var1x);
      }).orElse(var1);
   }
}
