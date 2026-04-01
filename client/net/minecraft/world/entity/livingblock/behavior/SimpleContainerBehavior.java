package net.minecraft.world.entity.livingblock.behavior;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.livingblock.LivingBlock;

public interface SimpleContainerBehavior extends LivingBlockBehavior {
   SimpleContainer getContainer();

   Component displayName(final LivingBlock entity);
}
