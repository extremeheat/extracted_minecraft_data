package net.minecraft.data.worldgen.material;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

public class EndMaterialRules {
   public static final ResourceKey<MaterialRule> END = createKey("end");

   public EndMaterialRules() {
      super();
   }

   private static ResourceKey<MaterialRule> createKey(final String name) {
      return ResourceKey.create(Registries.MATERIAL_RULE, Identifier.withDefaultNamespace(name));
   }

   public static void bootstrap(final BootstrapContext<MaterialRule> context) {
      context.register(END, MaterialRules.state(Blocks.END_STONE.defaultBlockState()));
   }
}
