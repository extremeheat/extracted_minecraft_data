package net.minecraft.world.level;

import net.minecraft.server.level.ServerLevel;

public interface CustomSpawner {
   int tick(ServerLevel var1, boolean var2, boolean var3);
}
