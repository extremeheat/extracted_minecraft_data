package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class AddBedTileEntity extends DataFix {
   public AddBedTileEntity(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(TypeReferences.field_211287_c);
      Type var2 = var1.findFieldType("Level");
      Type var3 = var2.findFieldType("TileEntities");
      if (!(var3 instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType var4 = (ListType)var3;
         return this.func_206296_a(var2, var4);
      }
   }

   private <TE> TypeRewriteRule func_206296_a(Type<?> var1, ListType<TE> var2) {
      Type var3 = var2.getElement();
      OpticFinder var4 = DSL.fieldFinder("Level", var1);
      OpticFinder var5 = DSL.fieldFinder("TileEntities", var2);
      boolean var6 = true;
      return TypeRewriteRule.seq(this.fixTypeEverywhere("InjectBedBlockEntityType", this.getInputSchema().findChoiceType(TypeReferences.field_211294_j), this.getOutputSchema().findChoiceType(TypeReferences.field_211294_j), (var0) -> {
         return (var0x) -> {
            return var0x;
         };
      }), this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(TypeReferences.field_211287_c), (var3x) -> {
         Typed var4x = var3x.getTyped(var4);
         Dynamic var5x = (Dynamic)var4x.get(DSL.remainderFinder());
         int var6 = var5x.getInt("xPos");
         int var7 = var5x.getInt("zPos");
         ArrayList var8 = Lists.newArrayList((Iterable)var4x.getOrCreate(var5));
         List var9 = (List)((Stream)var5x.get("Sections").flatMap(Dynamic::getStream).orElse(Stream.empty())).collect(Collectors.toList());

         for(int var10 = 0; var10 < var9.size(); ++var10) {
            Dynamic var11 = (Dynamic)var9.get(var10);
            int var12 = ((Number)var11.get("Y").flatMap(Dynamic::getNumberValue).orElse(0)).intValue();
            Stream var13 = ((Stream)var11.get("Blocks").flatMap(Dynamic::getStream).orElse(Stream.empty())).map((var0) -> {
               return ((Number)var0.getNumberValue().orElse(0)).intValue();
            });
            int var14 = 0;
            var13.getClass();

            for(Iterator var15 = (var13::iterator).iterator(); var15.hasNext(); ++var14) {
               int var16 = (Integer)var15.next();
               if (416 == (var16 & 255) << 4) {
                  int var17 = var14 & 15;
                  int var18 = var14 >> 8 & 15;
                  int var19 = var14 >> 4 & 15;
                  HashMap var20 = Maps.newHashMap();
                  var20.put(var11.createString("id"), var11.createString("minecraft:bed"));
                  var20.put(var11.createString("x"), var11.createInt(var17 + (var6 << 4)));
                  var20.put(var11.createString("y"), var11.createInt(var18 + (var12 << 4)));
                  var20.put(var11.createString("z"), var11.createInt(var19 + (var7 << 4)));
                  var20.put(var11.createString("color"), var11.createShort((short)14));
                  var8.add(((Optional)var3.read(var11.createMap(var20)).getSecond()).orElseThrow(() -> {
                     return new IllegalStateException("Could not parse newly created bed block entity.");
                  }));
               }
            }
         }

         if (!var8.isEmpty()) {
            return var3x.set(var4, var4x.set(var5, var8));
         } else {
            return var3x;
         }
      }));
   }
}
