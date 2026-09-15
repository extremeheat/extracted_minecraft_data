package net.minecraft.world.level.levelgen.material.rule;

import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface RuleEvaluator {
   @Nullable BlockState tryApply(int blockX, int blockY, int blockZ);
}
