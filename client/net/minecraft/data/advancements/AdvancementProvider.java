package net.minecraft.data.advancements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementProvider implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;
   private final List<Consumer<Consumer<Advancement>>> tabs = ImmutableList.of(new TheEndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());

   public AdvancementProvider(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public void run(HashCache var1) {
      Path var2 = this.generator.getOutputFolder();
      HashSet var3 = Sets.newHashSet();
      Consumer var4 = (var3x) -> {
         if (!var3.add(var3x.getId())) {
            throw new IllegalStateException("Duplicate advancement " + var3x.getId());
         } else {
            Path var4 = createPath(var2, var3x);

            try {
               DataProvider.save(GSON, var1, var3x.deconstruct().serializeToJson(), var4);
            } catch (IOException var6) {
               LOGGER.error("Couldn't save advancement {}", var4, var6);
            }

         }
      };
      Iterator var5 = this.tabs.iterator();

      while(var5.hasNext()) {
         Consumer var6 = (Consumer)var5.next();
         var6.accept(var4);
      }

   }

   private static Path createPath(Path var0, Advancement var1) {
      return var0.resolve("data/" + var1.getId().getNamespace() + "/advancements/" + var1.getId().getPath() + ".json");
   }

   public String getName() {
      return "Advancements";
   }
}
