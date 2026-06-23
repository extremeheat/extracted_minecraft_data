package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class PotDecorationsComponentUnflatteningFix extends DataFix {
   public PotDecorationsComponentUnflatteningFix(final Schema outputSchema) {
      super(outputSchema, true);
   }

   protected TypeRewriteRule makeRule() {
      return this.writeFixAndRead("Pot decoration structure fix", this.getInputSchema().getType(References.DATA_COMPONENTS), this.getOutputSchema().getType(References.DATA_COMPONENTS), (components) -> components.update("minecraft:pot_decorations", PotDecorationsComponentUnflatteningFix::unpackList));
   }

   public static <T> Dynamic<T> unpackList(final Dynamic<T> original) {
      Optional<Stream<Dynamic<T>>> decorationIds = original.asStreamOpt().result();
      if (decorationIds.isEmpty()) {
         return original;
      } else {
         List<Optional<String>> decorationIdList = ((Stream)decorationIds.get()).map((s) -> s.asString().result()).toList();
         Map<Dynamic<T>, Dynamic<T>> result = new HashMap(4);

         for(int i = 0; i < decorationIdList.size(); ++i) {
            Optional<String> decorationId = (Optional)decorationIdList.get(i);
            if (decorationId.isEmpty()) {
               return original;
            }

            String var10000;
            switch (i) {
               case 0 -> var10000 = "back";
               case 1 -> var10000 = "left";
               case 2 -> var10000 = "right";
               case 3 -> var10000 = "front";
               default -> var10000 = null;
            }

            String sideName = var10000;
            if (sideName != null) {
               Map<Dynamic<T>, Dynamic<T>> newStack = Map.of(original.createString("id"), original.createString((String)decorationId.get()));
               result.put(original.createString(sideName), original.createMap(newStack));
            }
         }

         return original.createMap(result);
      }
   }
}
