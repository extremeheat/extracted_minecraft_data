package net.minecraft.data.worldgen.material;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

public class VanillaMaterialRules {
   public static final ResourceKey<MaterialRule> BEDROCK_FLOOR = createKey("bedrock_floor");
   public static final ResourceKey<MaterialRule> BEDROCK_ROOF = createKey("bedrock_roof");
   private static final MaterialRule BEDROCK;

   public VanillaMaterialRules() {
      super();
   }

   private static ResourceKey<MaterialRule> createKey(final String name) {
      return ResourceKey.create(Registries.MATERIAL_RULE, Identifier.withDefaultNamespace(name));
   }

   private static MaterialRule makeStateRule(final Block block) {
      return MaterialRules.state(block.defaultBlockState());
   }

   public static void bootstrap(final BootstrapContext<MaterialRule> context) {
      context.register(BEDROCK_FLOOR, MaterialRules.ifTrue(MaterialRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
      context.register(BEDROCK_ROOF, MaterialRules.ifTrue(MaterialRules.not(MaterialRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK));
      OverworldMaterialRules.bootstrap(context);
      NetherMaterialRules.bootstrap(context);
      EndMaterialRules.bootstrap(context);
   }

   public static MaterialRule air() {
      return MaterialRules.state(Blocks.AIR.defaultBlockState());
   }

   static {
      BEDROCK = makeStateRule(Blocks.BEDROCK);
   }
}
