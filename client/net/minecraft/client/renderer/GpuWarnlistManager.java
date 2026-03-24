package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class GpuWarnlistManager extends SimplePreparableReloadListener<Preparations> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Identifier GPU_WARNLIST_LOCATION = Identifier.withDefaultNamespace("gpu_warnlist.json");
   private ImmutableMap<String, String> warnings = ImmutableMap.of();
   private boolean showWarning;
   private boolean warningDismissed;

   public GpuWarnlistManager() {
      super();
   }

   public boolean hasWarnings() {
      return !this.warnings.isEmpty();
   }

   public boolean willShowWarning() {
      return this.hasWarnings() && !this.warningDismissed;
   }

   public void showWarning() {
      this.showWarning = true;
   }

   public void dismissWarning() {
      this.warningDismissed = true;
   }

   public boolean isShowingWarning() {
      return this.showWarning && !this.warningDismissed;
   }

   public void resetWarnings() {
      this.showWarning = false;
      this.warningDismissed = false;
   }

   public @Nullable String getRendererWarnings() {
      return (String)this.warnings.get("renderer");
   }

   public @Nullable String getVersionWarnings() {
      return (String)this.warnings.get("version");
   }

   public @Nullable String getVendorWarnings() {
      return (String)this.warnings.get("vendor");
   }

   public @Nullable String getAllWarnings() {
      StringBuilder sb = new StringBuilder();
      this.warnings.forEach((k, v) -> sb.append(k).append(": ").append(v));
      return sb.isEmpty() ? null : sb.toString();
   }

   protected Preparations prepare(final ResourceManager manager, final ProfilerFiller profiler) {
      List<Pattern> rendererPatterns = Lists.newArrayList();
      List<Pattern> versionPatterns = Lists.newArrayList();
      List<Pattern> vendorPatterns = Lists.newArrayList();
      JsonObject root = parseJson(manager, profiler);
      if (root != null) {
         try (Zone ignored = profiler.zone("compile_regex")) {
            compilePatterns(root.getAsJsonArray("renderer"), rendererPatterns);
            compilePatterns(root.getAsJsonArray("version"), versionPatterns);
            compilePatterns(root.getAsJsonArray("vendor"), vendorPatterns);
         }
      }

      return new Preparations(rendererPatterns, versionPatterns, vendorPatterns);
   }

   protected void apply(final Preparations preparations, final ResourceManager manager, final ProfilerFiller profiler) {
      this.warnings = preparations.apply();
   }

   private static void compilePatterns(final JsonArray jsonArray, final List<Pattern> patternList) {
      jsonArray.forEach((e) -> patternList.add(Pattern.compile(e.getAsString(), 2)));
   }

   private static @Nullable JsonObject parseJson(final ResourceManager manager, final ProfilerFiller profiler) {
      try {
         JsonObject var4;
         try (Zone ignored = profiler.zone("parse_json")) {
            Reader resource = manager.openAsReader(GPU_WARNLIST_LOCATION);

            try {
               var4 = StrictJsonParser.parse(resource).getAsJsonObject();
            } catch (Throwable var8) {
               if (resource != null) {
                  try {
                     resource.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (resource != null) {
               resource.close();
            }
         }

         return var4;
      } catch (JsonSyntaxException | IOException e) {
         LOGGER.warn("Failed to load GPU warnlist", e);
         return null;
      }
   }

   protected static final class Preparations {
      private final List<Pattern> rendererPatterns;
      private final List<Pattern> versionPatterns;
      private final List<Pattern> vendorPatterns;

      private Preparations(final List<Pattern> rendererPatterns, final List<Pattern> versionPatterns, final List<Pattern> vendorPatterns) {
         super();
         this.rendererPatterns = rendererPatterns;
         this.versionPatterns = versionPatterns;
         this.vendorPatterns = vendorPatterns;
      }

      private static String matchAny(final List<Pattern> patterns, final String input) {
         List<String> allMatches = Lists.newArrayList();

         for(Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(input);

            while(matcher.find()) {
               allMatches.add(matcher.group());
            }
         }

         return String.join(", ", allMatches);
      }

      private ImmutableMap<String, String> apply() {
         ImmutableMap.Builder<String, String> map = new ImmutableMap.Builder();
         GpuDevice device = RenderSystem.getDevice();
         if (device.getBackendName().equals("OpenGL")) {
            String rendererFails = matchAny(this.rendererPatterns, device.getRenderer());
            if (!rendererFails.isEmpty()) {
               map.put("renderer", rendererFails);
            }

            String versionFails = matchAny(this.versionPatterns, device.getVersion());
            if (!versionFails.isEmpty()) {
               map.put("version", versionFails);
            }

            String vendorFails = matchAny(this.vendorPatterns, device.getVendor());
            if (!vendorFails.isEmpty()) {
               map.put("vendor", vendorFails);
            }
         }

         return map.build();
      }
   }
}
