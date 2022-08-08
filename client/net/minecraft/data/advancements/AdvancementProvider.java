package net.minecraft.data.advancements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import org.slf4j.Logger;

public class AdvancementProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DataGenerator.PathProvider pathProvider;
   private final List<Consumer<Consumer<Advancement>>> tabs = ImmutableList.of(new TheEndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());

   public AdvancementProvider(DataGenerator var1) {
      super();
      this.pathProvider = var1.createPathProvider(DataGenerator.Target.DATA_PACK, "advancements");
   }

   public void run(CachedOutput var1) {
      HashSet var2 = Sets.newHashSet();
      Consumer var3 = (var3x) -> {
         if (!var2.add(var3x.getId())) {
            throw new IllegalStateException("Duplicate advancement " + var3x.getId());
         } else {
            Path var4 = this.pathProvider.json(var3x.getId());

            try {
               DataProvider.saveStable(var1, var3x.deconstruct().serializeToJson(), var4);
            } catch (IOException var6) {
               LOGGER.error("Couldn't save advancement {}", var4, var6);
            }

         }
      };
      Iterator var4 = this.tabs.iterator();

      while(var4.hasNext()) {
         Consumer var5 = (Consumer)var4.next();
         var5.accept(var3);
      }

   }

   public String getName() {
      return "Advancements";
   }
}
