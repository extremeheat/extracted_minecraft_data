package net.minecraft.command;

import net.minecraft.entity.Entity;

public interface IEntitySelector {
   IEntitySelector field_94557_a = new IEntitySelector$1();
   IEntitySelector field_152785_b = new IEntitySelector$2();
   IEntitySelector field_96566_b = new IEntitySelector$3();

   boolean func_82704_a(Entity var1);
}
