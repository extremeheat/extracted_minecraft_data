package net.minecraft.data.worldgen;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class UpdateOneTwentyOneProcessorLists {
   public static final ResourceKey<StructureProcessorList> TRIAL_CHAMBERS_COPPER_BULB_DEGRADATION;

   public UpdateOneTwentyOneProcessorLists() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureProcessorList> var0) {
      register(var0, TRIAL_CHAMBERS_COPPER_BULB_DEGRADATION, List.of(new RuleProcessor(List.of(new ProcessorRule(new RandomBlockMatchTest(Blocks.WAXED_COPPER_BULB, 0.1F), AlwaysTrueTest.INSTANCE, (BlockState)Blocks.WAXED_OXIDIZED_COPPER_BULB.defaultBlockState().setValue(CopperBulbBlock.LIT, true)), new ProcessorRule(new RandomBlockMatchTest(Blocks.WAXED_COPPER_BULB, 0.33333334F), AlwaysTrueTest.INSTANCE, (BlockState)Blocks.WAXED_WEATHERED_COPPER_BULB.defaultBlockState().setValue(CopperBulbBlock.LIT, true)), new ProcessorRule(new RandomBlockMatchTest(Blocks.WAXED_COPPER_BULB, 0.5F), AlwaysTrueTest.INSTANCE, (BlockState)Blocks.WAXED_EXPOSED_COPPER_BULB.defaultBlockState().setValue(CopperBulbBlock.LIT, true)))), new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE)));
   }

   private static void register(BootstrapContext<StructureProcessorList> var0, ResourceKey<StructureProcessorList> var1, List<StructureProcessor> var2) {
      var0.register(var1, new StructureProcessorList(var2));
   }

   static {
      TRIAL_CHAMBERS_COPPER_BULB_DEGRADATION = ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("trial_chambers_copper_bulb_degradation"));
   }
}
