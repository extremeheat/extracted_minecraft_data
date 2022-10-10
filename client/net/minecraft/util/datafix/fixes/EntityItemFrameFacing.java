package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class EntityItemFrameFacing extends NamedEntityFix {
   public EntityItemFrameFacing(Schema var1, boolean var2) {
      super(var1, var2, "EntityItemFrameDirectionFix", TypeReferences.field_211299_o, "minecraft:item_frame");
   }

   public Dynamic<?> func_209651_a(Dynamic<?> var1) {
      return var1.set("Facing", var1.createByte(func_210567_a(var1.getByte("Facing"))));
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::func_209651_a);
   }

   private static byte func_210567_a(byte var0) {
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
