package net.minecraft.world.level.block.state.properties;

import java.util.Collection;
import java.util.Optional;

public interface Property {
   String getName();

   Collection getPossibleValues();

   Class getValueClass();

   Optional getValue(String var1);

   String getName(Comparable var1);
}
