package net.minecraft.realms.pluginapi;

import com.mojang.datafixers.util.Either;

public interface RealmsPlugin {
   Either tryLoad(String var1);
}
