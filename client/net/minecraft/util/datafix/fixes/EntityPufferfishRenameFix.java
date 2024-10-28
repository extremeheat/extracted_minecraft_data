package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class EntityPufferfishRenameFix extends SimplestEntityRenameFix {
   public static final Map<String, String> RENAMED_IDS = ImmutableMap.builder().put("minecraft:puffer_fish_spawn_egg", "minecraft:pufferfish_spawn_egg").build();

   public EntityPufferfishRenameFix(Schema var1, boolean var2) {
      super("EntityPufferfishRenameFix", var1, var2);
   }

   protected String rename(String var1) {
      return Objects.equals("minecraft:puffer_fish", var1) ? "minecraft:pufferfish" : var1;
   }
}
