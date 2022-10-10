package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class EntityCodSalmonFix extends TypedEntityRenameHelper {
   public static final Map<String, String> field_207460_a = ImmutableMap.builder().put("minecraft:salmon_mob", "minecraft:salmon").put("minecraft:cod_mob", "minecraft:cod").build();
   public static final Map<String, String> field_209759_b = ImmutableMap.builder().put("minecraft:salmon_mob_spawn_egg", "minecraft:salmon_spawn_egg").put("minecraft:cod_mob_spawn_egg", "minecraft:cod_spawn_egg").build();

   public EntityCodSalmonFix(Schema var1, boolean var2) {
      super("EntityCodSalmonFix", var1, var2);
   }

   protected String func_211311_a(String var1) {
      return (String)field_207460_a.getOrDefault(var1, var1);
   }
}
