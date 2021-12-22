package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.Mth;

public class VillagerRebuildLevelAndXpFix extends DataFix {
   private static final int TRADES_PER_LEVEL = 2;
   private static final int[] LEVEL_XP_THRESHOLDS = new int[]{0, 10, 50, 100, 150};

   public static int getMinXpPerLevel(int var0) {
      return LEVEL_XP_THRESHOLDS[Mth.clamp((int)(var0 - 1), (int)0, (int)(LEVEL_XP_THRESHOLDS.length - 1))];
   }

   public VillagerRebuildLevelAndXpFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getChoiceType(References.ENTITY, "minecraft:villager");
      OpticFinder var2 = DSL.namedChoice("minecraft:villager", var1);
      OpticFinder var3 = var1.findField("Offers");
      Type var4 = var3.type();
      OpticFinder var5 = var4.findField("Recipes");
      ListType var6 = (ListType)var5.type();
      OpticFinder var7 = var6.getElement().finder();
      return this.fixTypeEverywhereTyped("Villager level and xp rebuild", this.getInputSchema().getType(References.ENTITY), (var5x) -> {
         return var5x.updateTyped(var2, var1, (var3x) -> {
            Dynamic var4 = (Dynamic)var3x.get(DSL.remainderFinder());
            int var5x = var4.get("VillagerData").get("level").asInt(0);
            Typed var6 = var3x;
            if (var5x == 0 || var5x == 1) {
               int var7x = (Integer)var3x.getOptionalTyped(var3).flatMap((var1) -> {
                  return var1.getOptionalTyped(var5);
               }).map((var1) -> {
                  return var1.getAllTyped(var7).size();
               }).orElse(0);
               var5x = Mth.clamp((int)(var7x / 2), (int)1, (int)5);
               if (var5x > 1) {
                  var6 = addLevel(var3x, var5x);
               }
            }

            Optional var8 = var4.get("Xp").asNumber().result();
            if (!var8.isPresent()) {
               var6 = addXpFromLevel(var6, var5x);
            }

            return var6;
         });
      });
   }

   private static Typed<?> addLevel(Typed<?> var0, int var1) {
      return var0.update(DSL.remainderFinder(), (var1x) -> {
         return var1x.update("VillagerData", (var1xx) -> {
            return var1xx.set("level", var1xx.createInt(var1));
         });
      });
   }

   private static Typed<?> addXpFromLevel(Typed<?> var0, int var1) {
      int var2 = getMinXpPerLevel(var1);
      return var0.update(DSL.remainderFinder(), (var1x) -> {
         return var1x.set("Xp", var1x.createInt(var2));
      });
   }
}
