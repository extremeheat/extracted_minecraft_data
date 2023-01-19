package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class EntityPaintingFieldsRenameFix extends NamedEntityFix {
   public EntityPaintingFieldsRenameFix(Schema var1) {
      super(var1, false, "EntityPaintingFieldsRenameFix", References.ENTITY, "minecraft:painting");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return this.renameField(this.renameField(var1, "Motive", "variant"), "Facing", "facing");
   }

   private Dynamic<?> renameField(Dynamic<?> var1, String var2, String var3) {
      Optional var4 = var1.get(var2).result();
      Optional var5 = var4.map(var3x -> var1.remove(var2).set(var3, var3x));
      return (Dynamic<?>)DataFixUtils.orElse(var5, var1);
   }

   @Override
   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
