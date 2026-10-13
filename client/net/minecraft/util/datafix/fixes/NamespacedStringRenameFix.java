package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class NamespacedStringRenameFix extends DataFix {
   private final String name;
   private final DSL.TypeReference namespacedStringType;

   public NamespacedStringRenameFix(final Schema outputSchema, final DSL.TypeReference namespacedStringType, final String name) {
      super(outputSchema, false);
      this.name = name;
      this.namespacedStringType = namespacedStringType;
   }

   public TypeRewriteRule makeRule() {
      Type<Pair<String, String>> itemNameType = DSL.named(this.namespacedStringType.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(this.getInputSchema().getType(this.namespacedStringType), itemNameType)) {
         throw new IllegalStateException(this.namespacedStringType.typeName() + " name type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, itemNameType, (var1) -> (input) -> input.mapSecond(this::fixName));
      }
   }

   protected abstract String fixName(final String name);

   public static DataFix create(final Schema outputSchema, final DSL.TypeReference namespacedStringType, final String name, final Function<String, String> fixItem) {
      return new NamespacedStringRenameFix(outputSchema, namespacedStringType, name) {
         protected String fixName(final String name) {
            return (String)fixItem.apply(name);
         }
      };
   }
}
