package net.minecraft.entity.monster;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.passive.IAnimals;

public interface IMob extends IAnimals {
   IEntitySelector field_82192_a = new IMob$1();
}
