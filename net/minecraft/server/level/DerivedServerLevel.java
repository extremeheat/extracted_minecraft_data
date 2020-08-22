package net.minecraft.server.level;

import java.util.concurrent.Executor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorage;

public class DerivedServerLevel extends ServerLevel {
   public DerivedServerLevel(ServerLevel var1, MinecraftServer var2, Executor var3, LevelStorage var4, DimensionType var5, ProfilerFiller var6, ChunkProgressListener var7) {
      super(var2, var3, var4, new DerivedLevelData(var1.getLevelData()), var5, var6, var7);
      var1.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(this.getWorldBorder()));
   }

   protected void tickTime() {
   }
}
