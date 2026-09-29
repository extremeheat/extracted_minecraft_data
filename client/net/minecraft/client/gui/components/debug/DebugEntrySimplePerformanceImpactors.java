package net.minecraft.client.gui.components.debug;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.TextureFilteringMethod;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntrySimplePerformanceImpactors implements DebugScreenEntry {
   public DebugEntrySimplePerformanceImpactors() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Options options = minecraft.options;
      displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "OIT", (fact) -> fact.value((Boolean)options.improvedTransparency().get() ? "On" : "Off"));
      displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "Clouds", (fact) -> fact.value(options.cloudStatus().get() == CloudStatus.OFF ? "Off" : (options.cloudStatus().get() == CloudStatus.FAST ? "Fast" : "Fancy")));
      displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "Biome Blend", (fact) -> fact.value((Integer)options.biomeBlendRadius().get()));
      TextureFilteringMethod filteringMethod = (TextureFilteringMethod)options.textureFiltering().get();
      if (filteringMethod == TextureFilteringMethod.ANISOTROPIC) {
         displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "Filtering", (fact) -> fact.value(filteringMethod.caption().getString()).text(" ").value(options.maxAnisotropyValue()).text("x"));
      } else {
         displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "Filtering", (fact) -> fact.value(filteringMethod.caption().getString()));
      }

      boolean isMultiDrawIndirect = minecraft.levelRenderer.isChunkRenderingUsingMultiDrawIndirect();
      displayer.addFactToGroup(DebugGroups.PERFORMANCE_IMPACTORS, "Multi-draw", (fact) -> fact.value(isMultiDrawIndirect ? "On" : "Off"));
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
