package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class V143 extends Schema {
   public V143(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var2.remove("TippedArrow");
      return var2;
   }
}
