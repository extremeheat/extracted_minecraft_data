package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface FeatureAccess {
   @Nullable
   StructureStart getStartForFeature(String var1);

   void setStartForFeature(String var1, StructureStart var2);

   LongSet getReferencesForFeature(String var1);

   void addReferenceForFeature(String var1, long var2);

   Map getAllReferences();

   void setAllReferences(Map var1);
}
