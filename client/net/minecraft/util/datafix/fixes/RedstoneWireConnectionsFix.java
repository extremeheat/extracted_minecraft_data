package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class RedstoneWireConnectionsFix extends DataFix {
   public RedstoneWireConnectionsFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      return this.fixTypeEverywhereTyped("RedstoneConnectionsFix", var1.getType(References.BLOCK_STATE), (var1x) -> {
         return var1x.update(DSL.remainderFinder(), this::updateRedstoneConnections);
      });
   }

   private <T> Dynamic<T> updateRedstoneConnections(Dynamic<T> var1) {
      boolean var2 = var1.get("Name").asString().result().filter("minecraft:redstone_wire"::equals).isPresent();
      return !var2 ? var1 : var1.update("Properties", (var0) -> {
         String var1 = var0.get("east").asString("none");
         String var2 = var0.get("west").asString("none");
         String var3 = var0.get("north").asString("none");
         String var4 = var0.get("south").asString("none");
         boolean var5 = isConnected(var1) || isConnected(var2);
         boolean var6 = isConnected(var3) || isConnected(var4);
         String var7 = !isConnected(var1) && !var6 ? "side" : var1;
         String var8 = !isConnected(var2) && !var6 ? "side" : var2;
         String var9 = !isConnected(var3) && !var5 ? "side" : var3;
         String var10 = !isConnected(var4) && !var5 ? "side" : var4;
         return var0.update("east", (var1x) -> {
            return var1x.createString(var7);
         }).update("west", (var1x) -> {
            return var1x.createString(var8);
         }).update("north", (var1x) -> {
            return var1x.createString(var9);
         }).update("south", (var1x) -> {
            return var1x.createString(var10);
         });
      });
   }

   private static boolean isConnected(String var0) {
      return !"none".equals(var0);
   }
}
