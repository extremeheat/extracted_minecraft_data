package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class SplashManager extends SimplePreparableReloadListener<List<String>> {
   private static final ResourceLocation SPLASHES_LOCATION = new ResourceLocation("texts/splashes.txt");
   private static final Random RANDOM = new Random();
   private final List<String> splashes = Lists.newArrayList();
   private final User user;

   public SplashManager(User var1) {
      super();
      this.user = var1;
   }

   protected List<String> prepare(ResourceManager var1, ProfilerFiller var2) {
      try {
         Resource var3 = Minecraft.getInstance().getResourceManager().getResource(SPLASHES_LOCATION);

         List var5;
         try {
            BufferedReader var4 = new BufferedReader(new InputStreamReader(var3.getInputStream(), StandardCharsets.UTF_8));

            try {
               var5 = (List)var4.lines().map(String::trim).filter((var0) -> {
                  return var0.hashCode() != 125780783;
               }).collect(Collectors.toList());
            } catch (Throwable var9) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            var4.close();
         } catch (Throwable var10) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw var10;
         }

         if (var3 != null) {
            var3.close();
         }

         return var5;
      } catch (IOException var11) {
         return Collections.emptyList();
      }
   }

   protected void apply(List<String> var1, ResourceManager var2, ProfilerFiller var3) {
      this.splashes.clear();
      this.splashes.addAll(var1);
   }

   @Nullable
   public String getSplash() {
      Calendar var1 = Calendar.getInstance();
      var1.setTime(new Date());
      if (var1.get(2) + 1 == 12 && var1.get(5) == 24) {
         return "Merry X-mas!";
      } else if (var1.get(2) + 1 == 1 && var1.get(5) == 1) {
         return "Happy new year!";
      } else if (var1.get(2) + 1 == 10 && var1.get(5) == 31) {
         return "OOoooOOOoooo! Spooky!";
      } else if (this.splashes.isEmpty()) {
         return null;
      } else if (this.user != null && RANDOM.nextInt(this.splashes.size()) == 42) {
         String var10000 = this.user.getName();
         return var10000.toUpperCase(Locale.ROOT) + " IS YOU";
      } else {
         return (String)this.splashes.get(RANDOM.nextInt(this.splashes.size()));
      }
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
