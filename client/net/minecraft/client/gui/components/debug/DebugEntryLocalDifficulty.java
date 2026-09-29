package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryLocalDifficulty implements DebugScreenEntry {
   public DebugEntryLocalDifficulty() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.getCameraEntity();
      if (entity != null && serverChunk != null && serverOrClientLevel instanceof ServerLevel serverLevel) {
         BlockPos feetPos = entity.blockPosition();
         if (serverLevel.isInsideBuildHeight(feetPos.getY())) {
            float moonBrightness = serverLevel.getMoonBrightness(feetPos);
            long localTime = serverChunk.getInhabitedTime();
            DifficultyInstance localDifficulty = new DifficultyInstance(serverLevel.getDifficulty(), serverLevel.getOverworldClockTime(), localTime, moonBrightness);
            displayer.addFactToGroup(DebugGroups.MISC, "Local Difficulty", (fact) -> fact.formattedValue("%.2f", localDifficulty.getEffectiveDifficulty()).text(" // ").formattedValue("%.2f", localDifficulty.getSpecialMultiplier()));
         }

      }
   }
}
