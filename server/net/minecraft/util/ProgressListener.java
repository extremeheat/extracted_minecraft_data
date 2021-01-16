package net.minecraft.util;

import net.minecraft.network.chat.Component;

public interface ProgressListener {
   void progressStartNoAbort(Component var1);

   void progressStage(Component var1);

   void progressStagePercentage(int var1);
}
