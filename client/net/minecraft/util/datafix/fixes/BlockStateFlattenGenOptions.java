package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;

public class BlockStateFlattenGenOptions extends DataFix {
   private static final Splitter field_199181_a = Splitter.on(';').limit(5);
   private static final Splitter field_199182_b = Splitter.on(',');
   private static final Splitter field_199183_c = Splitter.on('x').limit(2);
   private static final Splitter field_199184_d = Splitter.on('*').limit(2);
   private static final Splitter field_199185_e = Splitter.on(':').limit(3);

   public BlockStateFlattenGenOptions(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(TypeReferences.field_211285_a), (var1) -> {
         return var1.update(DSL.remainderFinder(), this::func_209636_a);
      });
   }

   private Dynamic<?> func_209636_a(Dynamic<?> var1) {
      return var1.getString("generatorName").equalsIgnoreCase("flat") ? var1.update("generatorOptions", (var1x) -> {
         Optional var10000 = var1x.getStringValue().map(this::func_199180_a);
         var1x.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(var1x::createString), var1x);
      }) : var1;
   }

   @VisibleForTesting
   String func_199180_a(String var1) {
      if (var1.isEmpty()) {
         return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
      } else {
         Iterator var2 = field_199181_a.split(var1).iterator();
         String var3 = (String)var2.next();
         int var4;
         String var5;
         if (var2.hasNext()) {
            var4 = NumberUtils.toInt(var3, 0);
            var5 = (String)var2.next();
         } else {
            var4 = 0;
            var5 = var3;
         }

         if (var4 >= 0 && var4 <= 3) {
            StringBuilder var6 = new StringBuilder();
            Splitter var7 = var4 < 3 ? field_199183_c : field_199184_d;
            var6.append((String)StreamSupport.stream(field_199182_b.split(var5).spliterator(), false).map((var2x) -> {
               List var5 = var7.splitToList(var2x);
               int var3;
               String var4x;
               if (var5.size() == 2) {
                  var3 = NumberUtils.toInt((String)var5.get(0));
                  var4x = (String)var5.get(1);
               } else {
                  var3 = 1;
                  var4x = (String)var5.get(0);
               }

               List var6 = field_199185_e.splitToList(var4x);
               int var7x = ((String)var6.get(0)).equals("minecraft") ? 1 : 0;
               String var8 = (String)var6.get(var7x);
               int var9 = var4 == 3 ? BlockStateFlatternEntities.func_199171_a("minecraft:" + var8) : NumberUtils.toInt(var8, 0);
               int var10 = var7x + 1;
               int var11 = var6.size() > var10 ? NumberUtils.toInt((String)var6.get(var10), 0) : 0;
               return (var3 == 1 ? "" : var3 + "*") + BlockStateFlatteningMap.func_210049_b(var9 << 4 | var11).getString("Name");
            }).collect(Collectors.joining(",")));

            while(var2.hasNext()) {
               var6.append(';').append((String)var2.next());
            }

            return var6.toString();
         } else {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
         }
      }
   }
}
