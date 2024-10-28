package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;

public class SplashManager extends SimplePreparableReloadListener<List<String>> {
   private static final ResourceLocation SPLASHES_LOCATION = new ResourceLocation("texts/splashes.txt");
   private static final RandomSource RANDOM = RandomSource.create();
   private final List<String> splashes = Lists.newArrayList();
   private final User user;

   public SplashManager(User var1) {
      super();
      this.user = var1;
   }

   protected List<String> prepare(ResourceManager var1, ProfilerFiller var2) {
      try {
         BufferedReader var3 = Minecraft.getInstance().getResourceManager().openAsReader(SPLASHES_LOCATION);

         List var4;
         try {
            var4 = (List)var3.lines().map(String::trim).filter((var0) -> {
               return var0.hashCode() != 125780783;
            }).collect(Collectors.toList());
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }

         return var4;
      } catch (IOException var8) {
         return Collections.emptyList();
      }
   }

   protected void apply(List<String> var1, ResourceManager var2, ProfilerFiller var3) {
      this.splashes.clear();
      this.splashes.addAll(var1);
   }

   @Nullable
   public SplashRenderer getSplash() {
      Calendar var1 = Calendar.getInstance();
      var1.setTime(new Date());
      if (var1.get(2) + 1 == 12 && var1.get(5) == 24) {
         return SplashRenderer.CHRISTMAS;
      } else if (var1.get(2) + 1 == 1 && var1.get(5) == 1) {
         return SplashRenderer.NEW_YEAR;
      } else if (var1.get(2) + 1 == 10 && var1.get(5) == 31) {
         return SplashRenderer.HALLOWEEN;
      } else if (this.splashes.isEmpty()) {
         return null;
      } else if (this.user != null && RANDOM.nextInt(this.splashes.size()) == 42) {
         String var10002 = this.user.getName();
         return new SplashRenderer(var10002.toUpperCase(Locale.ROOT) + " IS YOU");
      } else {
         return new SplashRenderer((String)this.splashes.get(RANDOM.nextInt(this.splashes.size())));
      }
   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
