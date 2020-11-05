package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class EntityItemFrameDirectionFix extends NamedEntityFix {
   public EntityItemFrameDirectionFix(Schema var1, boolean var2) {
      super(var1, var2, "EntityItemFrameDirectionFix", References.ENTITY, "minecraft:item_frame");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return var1.set("Facing", var1.createByte(direction2dTo3d(var1.get("Facing").asByte((byte)0))));
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }

   private static byte direction2dTo3d(byte var0) {
      switch(var0) {
      case 0:
         return 3;
      case 1:
         return 4;
      case 2:
      default:
         return 2;
      case 3:
         return 5;
      }
   }
}
