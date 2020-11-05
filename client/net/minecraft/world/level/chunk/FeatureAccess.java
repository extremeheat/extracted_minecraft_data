package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface FeatureAccess {
   @Nullable
   StructureStart<?> getStartForFeature(StructureFeature<?> var1);

   void setStartForFeature(StructureFeature<?> var1, StructureStart<?> var2);

   LongSet getReferencesForFeature(StructureFeature<?> var1);

   void addReferenceForFeature(StructureFeature<?> var1, long var2);

   Map<StructureFeature<?>, LongSet> getAllReferences();

   void setAllReferences(Map<StructureFeature<?>, LongSet> var1);
}
