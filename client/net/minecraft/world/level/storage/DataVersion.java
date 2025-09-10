package net.minecraft.world.level.storage;

import net.minecraft.SharedConstants;

public record DataVersion(int version, String series) {
   public static final String MAIN_SERIES = "main";

   public DataVersion(int var1, String var2) {
      super();
      this.version = var1;
      this.series = var2;
   }

   public boolean isSideSeries() {
      return !this.series.equals("main");
   }

   public boolean isCompatible(DataVersion var1) {
      return SharedConstants.DEBUG_OPEN_INCOMPATIBLE_WORLDS ? true : this.series().equals(var1.series());
   }
}
