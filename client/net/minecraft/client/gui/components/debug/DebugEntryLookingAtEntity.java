package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryLookingAtEntity implements DebugScreenEntry {
   public DebugEntryLookingAtEntity() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.crosshairPickEntity;
      if (entity != null) {
         displayer.addFactToGroup(DebugGroups.LOOKING_AT_ENTITY, "Coordinates", (fact) -> fact.formattedValue("%.2f", entity.getX()).text(", ").formattedValue("%.2f", entity.getY()).text(", ").formattedValue("%.2f", entity.getZ()));
         displayer.addFactToGroup(DebugGroups.LOOKING_AT_ENTITY, "Type", (fact) -> fact.value(entity.typeHolder().getRegisteredName()));
      }

   }
}
