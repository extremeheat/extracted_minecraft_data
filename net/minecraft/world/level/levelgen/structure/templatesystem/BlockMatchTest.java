package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMatchTest extends RuleTest {
   private final Block block;

   public BlockMatchTest(Block var1) {
      this.block = var1;
   }

   public BlockMatchTest(Dynamic var1) {
      this((Block)Registry.BLOCK.get(new ResourceLocation(var1.get("block").asString(""))));
   }

   public boolean test(BlockState var1, Random var2) {
      return var1.getBlock() == this.block;
   }

   protected RuleTestType getType() {
      return RuleTestType.BLOCK_TEST;
   }

   protected Dynamic getDynamic(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("block"), var1.createString(Registry.BLOCK.getKey(this.block).toString()))));
   }
}
