package net.minecraft.client.stream;

import net.minecraft.stats.Achievement;

public class MetadataAchievement extends Metadata {
   public MetadataAchievement(Achievement var1) {
      super("achievement");
      this.func_152808_a("achievement_id", var1.field_75975_e);
      this.func_152808_a("achievement_name", var1.func_150951_e().func_150260_c());
      this.func_152808_a("achievement_description", var1.func_75989_e());
      this.func_152807_a("Achievement '" + var1.func_150951_e().func_150260_c() + "' obtained!");
   }
}
