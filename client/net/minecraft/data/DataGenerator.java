package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.init.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataGenerator {
   private static final Logger field_200393_a = LogManager.getLogger();
   private final Collection<Path> field_200394_b;
   private final Path field_200395_c;
   private final List<IDataProvider> field_200396_d = Lists.newArrayList();

   public DataGenerator(Path var1, Collection<Path> var2) {
      super();
      this.field_200395_c = var1;
      this.field_200394_b = var2;
   }

   public Collection<Path> func_200389_a() {
      return this.field_200394_b;
   }

   public Path func_200391_b() {
      return this.field_200395_c;
   }

   public void func_200392_c() throws IOException {
      DirectoryCache var1 = new DirectoryCache(this.field_200395_c, "cache");
      Stopwatch var2 = Stopwatch.createUnstarted();
      Iterator var3 = this.field_200396_d.iterator();

      while(var3.hasNext()) {
         IDataProvider var4 = (IDataProvider)var3.next();
         field_200393_a.info("Starting provider: {}", var4.func_200397_b());
         var2.start();
         var4.func_200398_a(var1);
         var2.stop();
         field_200393_a.info("{} finished after {} ms", var4.func_200397_b(), var2.elapsed(TimeUnit.MILLISECONDS));
         var2.reset();
      }

      var1.func_208317_a();
   }

   public void func_200390_a(IDataProvider var1) {
      this.field_200396_d.add(var1);
   }

   static {
      Bootstrap.func_151354_b();
   }
}
