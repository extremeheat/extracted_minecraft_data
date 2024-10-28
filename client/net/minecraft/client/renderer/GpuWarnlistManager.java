package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class GpuWarnlistManager extends SimplePreparableReloadListener<Preparations> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation GPU_WARNLIST_LOCATION = ResourceLocation.withDefaultNamespace("gpu_warnlist.json");
   private ImmutableMap<String, String> warnings = ImmutableMap.of();
   private boolean showWarning;
   private boolean warningDismissed;
   private boolean skipFabulous;

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

   public void dismissWarningAndSkipFabulous() {
      this.warningDismissed = true;
      this.skipFabulous = true;
   }

   public boolean isShowingWarning() {
      return this.showWarning && !this.warningDismissed;
   }

   public boolean isSkippingFabulous() {
      return this.skipFabulous;
   }

   public void resetWarnings() {
      this.showWarning = false;
      this.warningDismissed = false;
      this.skipFabulous = false;
   }

   @Nullable
   public String getRendererWarnings() {
      return (String)this.warnings.get("renderer");
   }

   @Nullable
   public String getVersionWarnings() {
      return (String)this.warnings.get("version");
   }

   @Nullable
   public String getVendorWarnings() {
      return (String)this.warnings.get("vendor");
   }

   @Nullable
   public String getAllWarnings() {
      StringBuilder var1 = new StringBuilder();
      this.warnings.forEach((var1x, var2) -> {
         var1.append(var1x).append(": ").append(var2);
      });
      return var1.length() == 0 ? null : var1.toString();
   }

   protected Preparations prepare(ResourceManager var1, ProfilerFiller var2) {
      ArrayList var3 = Lists.newArrayList();
      ArrayList var4 = Lists.newArrayList();
      ArrayList var5 = Lists.newArrayList();
      var2.startTick();
      JsonObject var6 = parseJson(var1, var2);
      if (var6 != null) {
         var2.push("compile_regex");
         compilePatterns(var6.getAsJsonArray("renderer"), var3);
         compilePatterns(var6.getAsJsonArray("version"), var4);
         compilePatterns(var6.getAsJsonArray("vendor"), var5);
         var2.pop();
      }

      var2.endTick();
      return new Preparations(var3, var4, var5);
   }

   protected void apply(Preparations var1, ResourceManager var2, ProfilerFiller var3) {
      this.warnings = var1.apply();
   }

   private static void compilePatterns(JsonArray var0, List<Pattern> var1) {
      var0.forEach((var1x) -> {
         var1.add(Pattern.compile(var1x.getAsString(), 2));
      });
   }

   @Nullable
   private static JsonObject parseJson(ResourceManager var0, ProfilerFiller var1) {
      var1.push("parse_json");
      JsonObject var2 = null;

      try {
         BufferedReader var3 = var0.openAsReader(GPU_WARNLIST_LOCATION);

         try {
            var2 = JsonParser.parseReader(var3).getAsJsonObject();
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  ((Reader)var3).close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            ((Reader)var3).close();
         }
      } catch (JsonSyntaxException | IOException var8) {
         LOGGER.warn("Failed to load GPU warnlist");
      }

      var1.pop();
      return var2;
   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }

   protected static final class Preparations {
      private final List<Pattern> rendererPatterns;
      private final List<Pattern> versionPatterns;
      private final List<Pattern> vendorPatterns;

      Preparations(List<Pattern> var1, List<Pattern> var2, List<Pattern> var3) {
         super();
         this.rendererPatterns = var1;
         this.versionPatterns = var2;
         this.vendorPatterns = var3;
      }

      private static String matchAny(List<Pattern> var0, String var1) {
         ArrayList var2 = Lists.newArrayList();
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            Pattern var4 = (Pattern)var3.next();
            Matcher var5 = var4.matcher(var1);

            while(var5.find()) {
               var2.add(var5.group());
            }
         }

         return String.join(", ", var2);
      }

      ImmutableMap<String, String> apply() {
         ImmutableMap.Builder var1 = new ImmutableMap.Builder();
         String var2 = matchAny(this.rendererPatterns, GlUtil.getRenderer());
         if (!var2.isEmpty()) {
            var1.put("renderer", var2);
         }

         String var3 = matchAny(this.versionPatterns, GlUtil.getOpenGLVersion());
         if (!var3.isEmpty()) {
            var1.put("version", var3);
         }

         String var4 = matchAny(this.vendorPatterns, GlUtil.getVendor());
         if (!var4.isEmpty()) {
            var1.put("vendor", var4);
         }

         return var1.build();
      }
   }
}
