package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.advancements.AdventureAdvancements;
import net.minecraft.data.advancements.EndAdvancements;
import net.minecraft.data.advancements.HusbandryAdvancements;
import net.minecraft.data.advancements.NetherAdvancements;
import net.minecraft.data.advancements.StoryAdvancements;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementProvider implements IDataProvider {
   private static final Logger field_204023_a = LogManager.getLogger();
   private static final Gson field_204024_b = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator field_204025_c;
   private final List<Consumer<Consumer<Advancement>>> field_204283_d = ImmutableList.of(new EndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());

   public AdvancementProvider(DataGenerator var1) {
      super();
      this.field_204025_c = var1;
   }

   public void func_200398_a(DirectoryCache var1) throws IOException {
      Path var2 = this.field_204025_c.func_200391_b();
      HashSet var3 = Sets.newHashSet();
      Consumer var4 = (var4x) -> {
         if (!var3.add(var4x.func_192067_g())) {
            throw new IllegalStateException("Duplicate advancement " + var4x.func_192067_g());
         } else {
            this.func_208309_a(var1, var4x.func_192075_a().func_200273_b(), var2.resolve("data/" + var4x.func_192067_g().func_110624_b() + "/advancements/" + var4x.func_192067_g().func_110623_a() + ".json"));
         }
      };
      Iterator var5 = this.field_204283_d.iterator();

      while(var5.hasNext()) {
         Consumer var6 = (Consumer)var5.next();
         var6.accept(var4);
      }

   }

   private void func_208309_a(DirectoryCache var1, JsonObject var2, Path var3) {
      try {
         String var4 = field_204024_b.toJson(var2);
         String var5 = field_208307_a.hashUnencodedChars(var4).toString();
         if (!Objects.equals(var1.func_208323_a(var3), var5) || !Files.exists(var3, new LinkOption[0])) {
            Files.createDirectories(var3.getParent());
            BufferedWriter var6 = Files.newBufferedWriter(var3);
            Throwable var7 = null;

            try {
               var6.write(var4);
            } catch (Throwable var17) {
               var7 = var17;
               throw var17;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var16) {
                        var7.addSuppressed(var16);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         }

         var1.func_208316_a(var3, var5);
      } catch (IOException var19) {
         field_204023_a.error("Couldn't save advancement {}", var3, var19);
      }

   }

   public String func_200397_b() {
      return "Advancements";
   }
}
