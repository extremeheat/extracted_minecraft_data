package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;

public class JigsawRotationFix extends AbstractBlockPropertyFix {
   private static final Map<String, String> RENAMES = ImmutableMap.builder().put("down", "down_south").put("up", "up_north").put("north", "north_up").put("south", "south_up").put("west", "west_up").put("east", "east_up").build();

   public JigsawRotationFix(Schema var1) {
      super(var1, "jigsaw_rotation_fix");
   }

   protected boolean shouldFix(String var1) {
      return var1.equals("minecraft:jigsaw");
   }

   protected <T> Dynamic<T> fixProperties(String var1, Dynamic<T> var2) {
      String var3 = var2.get("facing").asString("north");
      return var2.remove("facing").set("orientation", var2.createString((String)RENAMES.getOrDefault(var3, var3)));
   }
}
