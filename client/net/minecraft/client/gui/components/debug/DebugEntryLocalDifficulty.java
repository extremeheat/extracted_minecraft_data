package net.minecraft.client.gui.components.debug;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class DebugEntryLocalDifficulty implements DebugScreenEntry {
   public DebugEntryLocalDifficulty() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.getCameraEntity();
      if (var6 != null && var5.level != null && var4 != null && var2 != null) {
         BlockPos var7 = var6.blockPosition();
         if (var5.level.isInsideBuildHeight(var7.getY())) {
            float var8 = var2.getMoonBrightness();
            long var9 = var4.getInhabitedTime();
            DifficultyInstance var11 = new DifficultyInstance(var2.getDifficulty(), var2.getDayTime(), var9, var8);
            var1.addLine(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", var11.getEffectiveDifficulty(), var11.getSpecialMultiplier(), var5.level.getDayTime() / 24000L));
         }

      }
   }
}
