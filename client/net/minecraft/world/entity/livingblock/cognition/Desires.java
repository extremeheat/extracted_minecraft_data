package net.minecraft.world.entity.livingblock.cognition;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.phys.Vec3;

public interface Desires {
   Desire<Targetable> ATTACK = Desire.<Targetable>of();
   Desire<Vec3> PROTECT = Desire.<Vec3>of();
   Desire<Target> APPROACH = Desire.<Target>of();
   Desire<BlockPos> MINE = Desire.<BlockPos>of();
   Desire<BuildTarget> BUILD = Desire.<BuildTarget>of();
}
