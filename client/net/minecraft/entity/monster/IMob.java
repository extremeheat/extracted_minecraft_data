package net.minecraft.entity.monster;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimal;

public interface IMob extends IAnimal {
   Predicate<Entity> field_82192_a = (var0) -> {
      return var0 instanceof IMob;
   };
   Predicate<Entity> field_175450_e = (var0) -> {
      return var0 instanceof IMob && !var0.func_82150_aj();
   };
}
