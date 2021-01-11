package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;

public class RealmsAnvilLevelStorageSource {
   private ISaveFormat levelStorageSource;

   public RealmsAnvilLevelStorageSource(ISaveFormat var1) {
      super();
      this.levelStorageSource = var1;
   }

   public String getName() {
      return this.levelStorageSource.func_154333_a();
   }

   public boolean levelExists(String var1) {
      return this.levelStorageSource.func_90033_f(var1);
   }

   public boolean convertLevel(String var1, IProgressUpdate var2) {
      return this.levelStorageSource.func_75805_a(var1, var2);
   }

   public boolean requiresConversion(String var1) {
      return this.levelStorageSource.func_75801_b(var1);
   }

   public boolean isNewLevelIdAcceptable(String var1) {
      return this.levelStorageSource.func_154335_d(var1);
   }

   public boolean deleteLevel(String var1) {
      return this.levelStorageSource.func_75802_e(var1);
   }

   public boolean isConvertible(String var1) {
      return this.levelStorageSource.func_154334_a(var1);
   }

   public void renameLevel(String var1, String var2) {
      this.levelStorageSource.func_75806_a(var1, var2);
   }

   public void clearAll() {
      this.levelStorageSource.func_75800_d();
   }

   public List<RealmsLevelSummary> getLevelList() throws AnvilConverterException {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.levelStorageSource.func_75799_b().iterator();

      while(var2.hasNext()) {
         SaveFormatComparator var3 = (SaveFormatComparator)var2.next();
         var1.add(new RealmsLevelSummary(var3));
      }

      return var1;
   }
}
