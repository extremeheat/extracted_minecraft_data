package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;

public class RealmsAnvilLevelStorageSource {
   private final LevelStorageSource levelStorageSource;

   public RealmsAnvilLevelStorageSource(LevelStorageSource var1) {
      this.levelStorageSource = var1;
   }

   public String getName() {
      return this.levelStorageSource.getName();
   }

   public boolean levelExists(String var1) {
      return this.levelStorageSource.levelExists(var1);
   }

   public boolean convertLevel(String var1, ProgressListener var2) {
      return this.levelStorageSource.convertLevel(var1, var2);
   }

   public boolean requiresConversion(String var1) {
      return this.levelStorageSource.requiresConversion(var1);
   }

   public boolean isNewLevelIdAcceptable(String var1) {
      return this.levelStorageSource.isNewLevelIdAcceptable(var1);
   }

   public boolean deleteLevel(String var1) {
      return this.levelStorageSource.deleteLevel(var1);
   }

   public void renameLevel(String var1, String var2) {
      this.levelStorageSource.renameLevel(var1, var2);
   }

   public List getLevelList() throws LevelStorageException {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.levelStorageSource.getLevelList().iterator();

      while(var2.hasNext()) {
         LevelSummary var3 = (LevelSummary)var2.next();
         var1.add(new RealmsLevelSummary(var3));
      }

      return var1;
   }
}
