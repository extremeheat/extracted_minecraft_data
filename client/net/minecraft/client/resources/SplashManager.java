package net.minecraft.client.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.MonthDay;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpecialDates;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.Nullable;

public class SplashManager extends SimplePreparableReloadListener<List<Component>> {
   private static final Style DEFAULT_STYLE;
   public static final Component CHRISTMAS;
   public static final Component NEW_YEAR;
   public static final Component HALLOWEEN;
   private static final ResourceLocation SPLASHES_LOCATION;
   private static final RandomSource RANDOM;
   private List<Component> splashes = List.of();
   private final User user;

   public SplashManager(User var1) {
      super();
      this.user = var1;
   }

   private static Component literalSplash(String var0) {
      return Component.literal(var0).setStyle(DEFAULT_STYLE);
   }

   protected List<Component> prepare(ResourceManager var1, ProfilerFiller var2) {
      try {
         BufferedReader var3 = Minecraft.getInstance().getResourceManager().openAsReader(SPLASHES_LOCATION);

         List var4;
         try {
            var4 = var3.lines().map(String::trim).filter((var0) -> var0.hashCode() != 125780783).map(SplashManager::literalSplash).toList();
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
         return List.of();
      }
   }

   protected void apply(List<Component> var1, ResourceManager var2, ProfilerFiller var3) {
      this.splashes = List.copyOf(var1);
   }

   public @Nullable SplashRenderer getSplash() {
      MonthDay var1 = SpecialDates.dayNow();
      if (var1.equals(SpecialDates.CHRISTMAS)) {
         return SplashRenderer.CHRISTMAS;
      } else if (var1.equals(SpecialDates.NEW_YEAR)) {
         return SplashRenderer.NEW_YEAR;
      } else if (var1.equals(SpecialDates.HALLOWEEN)) {
         return SplashRenderer.HALLOWEEN;
      } else if (this.splashes.isEmpty()) {
         return null;
      } else if (this.user != null && RANDOM.nextInt(this.splashes.size()) == 42) {
         String var10002 = this.user.getName();
         return new SplashRenderer(literalSplash(var10002.toUpperCase(Locale.ROOT) + " IS YOU"));
      } else {
         return new SplashRenderer((Component)this.splashes.get(RANDOM.nextInt(this.splashes.size())));
      }
   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }

   static {
      DEFAULT_STYLE = Style.EMPTY.withColor(-256);
      CHRISTMAS = literalSplash("Merry X-mas!");
      NEW_YEAR = literalSplash("Happy new year!");
      HALLOWEEN = literalSplash("OOoooOOOoooo! Spooky!");
      SPLASHES_LOCATION = ResourceLocation.withDefaultNamespace("texts/splashes.txt");
      RANDOM = RandomSource.create();
   }
}
