package net.minecraft.world.level.storage;

public class DataVersion {
   private final int version;
   private final String series;
   public static String MAIN_SERIES = "main";

   public DataVersion(int var1) {
      this(var1, MAIN_SERIES);
   }

   public DataVersion(int var1, String var2) {
      super();
      this.version = var1;
      this.series = var2;
   }

   public boolean isSideSeries() {
      return !this.series.equals(MAIN_SERIES);
   }

   public String getSeries() {
      return this.series;
   }

   public int getVersion() {
      return this.version;
   }

   public boolean isCompatible(DataVersion var1) {
      return this.getSeries().equals(var1.getSeries());
   }
}
