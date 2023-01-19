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
      return LEVEL_XP_THRESHOLDS[Mth.clamp(var0 - 1, 0, LEVEL_XP_THRESHOLDS.length - 1)];
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
      return this.fixTypeEverywhereTyped(
         "Villager level and xp rebuild",
         this.getInputSchema().getType(References.ENTITY),
         var5x -> var5x.updateTyped(
               var2,
               var1,
               var3xx -> {
                  Dynamic var4xx = (Dynamic)var3xx.get(DSL.remainderFinder());
                  int var5xx = var4xx.get("VillagerData").get("level").asInt(0);
                  Typed var6x = var3xx;
                  if (var5xx == 0 || var5xx == 1) {
                     int var7x = var3xx.getOptionalTyped(var3)
                        .flatMap(var1xxx -> var1xxx.getOptionalTyped(var5))
                        .map(var1xxx -> var1xxx.getAllTyped(var7).size())
                        .orElse(0);
                     var5xx = Mth.clamp(var7x / 2, 1, 5);
                     if (var5xx > 1) {
                        var6x = addLevel(var3xx, var5xx);
                     }
                  }
      
                  Optional var8 = var4xx.get("Xp").asNumber().result();
                  if (!var8.isPresent()) {
                     var6x = addXpFromLevel(var6x, var5xx);
                  }
      
                  return var6x;
               }
            )
      );
   }

   private static Typed<?> addLevel(Typed<?> var0, int var1) {
      return var0.update(DSL.remainderFinder(), var1x -> var1x.update("VillagerData", var1xx -> var1xx.set("level", var1xx.createInt(var1))));
   }

   private static Typed<?> addXpFromLevel(Typed<?> var0, int var1) {
      int var2 = getMinXpPerLevel(var1);
      return var0.update(DSL.remainderFinder(), var1x -> var1x.set("Xp", var1x.createInt(var2)));
   }
}
