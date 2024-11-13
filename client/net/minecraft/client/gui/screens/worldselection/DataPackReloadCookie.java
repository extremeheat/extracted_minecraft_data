package net.minecraft.client.gui.screens.worldselection;

import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldGenSettings;

public record DataPackReloadCookie(WorldGenSettings worldGenSettings, WorldDataConfiguration dataConfiguration) {
   public DataPackReloadCookie(WorldGenSettings var1, WorldDataConfiguration var2) {
      super();
      this.worldGenSettings = var1;
      this.dataConfiguration = var2;
   }
}
