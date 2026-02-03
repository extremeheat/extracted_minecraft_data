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
import net.minecraft.resources.Identifier;
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
   private static final Identifier SPLASHES_LOCATION;
   private static final RandomSource RANDOM;
   private List<Component> splashes = List.of();
   private final User user;

   public SplashManager(final User user) {
      super();
      this.user = user;
   }

   private static Component literalSplash(final String text) {
      return Component.literal(text).setStyle(DEFAULT_STYLE);
   }

   protected List<Component> prepare(final ResourceManager manager, final ProfilerFiller profiler) {
      try {
         BufferedReader reader = Minecraft.getInstance().getResourceManager().openAsReader(SPLASHES_LOCATION);

         List var4;
         try {
            var4 = reader.lines().map(String::trim).filter((line) -> line.hashCode() != 125780783).map(SplashManager::literalSplash).toList();
         } catch (Throwable var7) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (reader != null) {
            reader.close();
         }

         return var4;
      } catch (IOException var8) {
         return List.of();
      }
   }

   protected void apply(final List<Component> preparations, final ResourceManager manager, final ProfilerFiller profiler) {
      this.splashes = List.copyOf(preparations);
   }

   public @Nullable SplashRenderer getSplash() {
      MonthDay monthDay = SpecialDates.dayNow();
      if (monthDay.equals(SpecialDates.CHRISTMAS)) {
         return SplashRenderer.CHRISTMAS;
      } else if (monthDay.equals(SpecialDates.NEW_YEAR)) {
         return SplashRenderer.NEW_YEAR;
      } else if (monthDay.equals(SpecialDates.HALLOWEEN)) {
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

   static {
      DEFAULT_STYLE = Style.EMPTY.withColor(-256);
      CHRISTMAS = literalSplash("Merry X-mas!");
      NEW_YEAR = literalSplash("Happy new year!");
      HALLOWEEN = literalSplash("OOoooOOOoooo! Spooky!");
      SPLASHES_LOCATION = Identifier.withDefaultNamespace("texts/splashes.txt");
      RANDOM = RandomSource.create();
   }
}
