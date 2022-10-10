package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class PufferfishRename extends TypedEntityRenameHelper {
   public static final Map<String, String> field_207461_a = ImmutableMap.builder().put("minecraft:puffer_fish_spawn_egg", "minecraft:pufferfish_spawn_egg").build();

   public PufferfishRename(Schema var1, boolean var2) {
      super("EntityPufferfishRenameFix", var1, var2);
   }

   protected String func_211311_a(String var1) {
      return Objects.equals("minecraft:puffer_fish", var1) ? "minecraft:pufferfish" : var1;
   }
}
