package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryLookingAtEntityTags implements DebugScreenEntry {
   public DebugEntryLookingAtEntityTags() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.crosshairPickEntity;
      List<String> tags = new ArrayList();
      if (entity != null) {
         DebugEntryLookingAt.addTagEntries(tags, entity);
      }

      if (!tags.isEmpty()) {
         displayer.addToGroup(DebugGroups.LOOKING_AT_ENTITY, tags);
      }

   }
}
