package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class V1909 extends NamespacedSchema {
   public V1909(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.registerSimple(var2, "minecraft:jigsaw");
      return var2;
   }
}
