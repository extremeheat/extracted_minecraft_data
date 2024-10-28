package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class PlayerHeadBlockProfileFix extends NamedEntityFix {
   public PlayerHeadBlockProfileFix(Schema var1) {
      super(var1, false, "PlayerHeadBlockProfileFix", References.BLOCK_ENTITY, "minecraft:skull");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fix);
   }

   private <T> Dynamic<T> fix(Dynamic<T> var1) {
      Optional var2 = var1.get("SkullOwner").result();
      Optional var3 = var1.get("ExtraType").result();
      Optional var4 = var2.or(() -> {
         return var3;
      });
      if (var4.isEmpty()) {
         return var1;
      } else {
         var1 = var1.remove("SkullOwner").remove("ExtraType");
         var1 = var1.set("profile", ItemStackComponentizationFix.fixProfile((Dynamic)var4.get()));
         return var1;
      }
   }
}
