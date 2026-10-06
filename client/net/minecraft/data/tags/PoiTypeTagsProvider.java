package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypeIds;

public class PoiTypeTagsProvider extends TagsProvider<PoiType> {
   public PoiTypeTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider) {
      super(output, Registries.POINT_OF_INTEREST_TYPE, lookupProvider);
   }

   protected void addTags(final HolderLookup.Provider registries) {
      this.tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(PoiTypeIds.ARMORER, PoiTypeIds.BUTCHER, PoiTypeIds.CARTOGRAPHER, PoiTypeIds.CLERIC, PoiTypeIds.FARMER, PoiTypeIds.FISHERMAN, PoiTypeIds.FLETCHER, PoiTypeIds.LEATHERWORKER, PoiTypeIds.LIBRARIAN, PoiTypeIds.MASON, PoiTypeIds.SHEPHERD, PoiTypeIds.TOOLSMITH, PoiTypeIds.WEAPONSMITH);
      this.tag(PoiTypeTags.VILLAGE).addTag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(PoiTypeIds.HOME, PoiTypeIds.MEETING);
      this.tag(PoiTypeTags.BEE_HOME).add(PoiTypeIds.BEEHIVE, PoiTypeIds.BEE_NEST);
   }
}
