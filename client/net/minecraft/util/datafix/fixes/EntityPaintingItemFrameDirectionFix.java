package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class EntityPaintingItemFrameDirectionFix extends DataFix {
   private static final int[][] DIRECTIONS = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

   public EntityPaintingItemFrameDirectionFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private Dynamic<?> doFix(Dynamic<?> var1, boolean var2, boolean var3) {
      if ((var2 || var3) && var1.get("Facing").asNumber().result().isEmpty()) {
         int var4;
         if (var1.get("Direction").asNumber().result().isPresent()) {
            var4 = var1.get("Direction").asByte((byte)0) % DIRECTIONS.length;
            int[] var5 = DIRECTIONS[var4];
            var1 = var1.set("TileX", var1.createInt(var1.get("TileX").asInt(0) + var5[0]));
            var1 = var1.set("TileY", var1.createInt(var1.get("TileY").asInt(0) + var5[1]));
            var1 = var1.set("TileZ", var1.createInt(var1.get("TileZ").asInt(0) + var5[2]));
            var1 = var1.remove("Direction");
            if (var3 && var1.get("ItemRotation").asNumber().result().isPresent()) {
               var1 = var1.set("ItemRotation", var1.createByte((byte)(var1.get("ItemRotation").asByte((byte)0) * 2)));
            }
         } else {
            var4 = var1.get("Dir").asByte((byte)0) % DIRECTIONS.length;
            var1 = var1.remove("Dir");
         }

         var1 = var1.set("Facing", var1.createByte((byte)var4));
      }

      return var1;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getChoiceType(References.ENTITY, "Painting");
      OpticFinder var2 = DSL.namedChoice("Painting", var1);
      Type var3 = this.getInputSchema().getChoiceType(References.ENTITY, "ItemFrame");
      OpticFinder var4 = DSL.namedChoice("ItemFrame", var3);
      Type var5 = this.getInputSchema().getType(References.ENTITY);
      TypeRewriteRule var6 = this.fixTypeEverywhereTyped("EntityPaintingFix", var5, (var3x) -> {
         return var3x.updateTyped(var2, var1, (var1x) -> {
            return var1x.update(DSL.remainderFinder(), (var1) -> {
               return this.doFix(var1, true, false);
            });
         });
      });
      TypeRewriteRule var7 = this.fixTypeEverywhereTyped("EntityItemFrameFix", var5, (var3x) -> {
         return var3x.updateTyped(var4, var3, (var1) -> {
            return var1.update(DSL.remainderFinder(), (var1x) -> {
               return this.doFix(var1x, false, true);
            });
         });
      });
      return TypeRewriteRule.seq(var6, var7);
   }
}
