package net.minecraft.gametest.framework;

import java.util.stream.Stream;
import net.minecraft.core.GlobalPos;

@FunctionalInterface
public interface TestPosFinder {
   Stream<GlobalPos> findTestPos();
}
