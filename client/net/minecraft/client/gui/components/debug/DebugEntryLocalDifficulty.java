package net.minecraft.client.gui.components.debug;

import java.util.Locale;
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

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.getCameraEntity();
      if (var6 != null && var4 != null && var2 instanceof ServerLevel var7) {
         BlockPos var8 = var6.blockPosition();
         if (var7.isInsideBuildHeight(var8.getY())) {
            float var9 = var7.getMoonBrightness();
            long var10 = var4.getInhabitedTime();
            DifficultyInstance var12 = new DifficultyInstance(var7.getDifficulty(), var7.getDayTime(), var10, var9);
            var1.addLine(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", var12.getEffectiveDifficulty(), var12.getSpecialMultiplier(), var7.getDayCount()));
         }

      }
   }
}
