package net.minecraft.client.multiplayer.chat.report;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public enum ReportReason {
   FALSE_REPORTING(2, "false_reporting", false),
   HATE_SPEECH(5, "hate_speech", true),
   TERRORISM_OR_VIOLENT_EXTREMISM(16, "terrorism_or_violent_extremism", true),
   CHILD_SEXUAL_EXPLOITATION_OR_ABUSE(17, "child_sexual_exploitation_or_abuse", true),
   IMMINENT_HARM(18, "imminent_harm", true),
   NON_CONSENSUAL_INTIMATE_IMAGERY(19, "non_consensual_intimate_imagery", true),
   HARASSMENT_OR_BULLYING(21, "harassment_or_bullying", true),
   DEFAMATION_IMPERSONATION_FALSE_INFORMATION(27, "defamation_impersonation_false_information", true),
   SELF_HARM_OR_SUICIDE(31, "self_harm_or_suicide", true),
   ALCOHOL_TOBACCO_DRUGS(39, "alcohol_tobacco_drugs", true);

   private final int id;
   private final String backendName;
   private final boolean reportable;
   private final Component title;
   private final Component description;

   private ReportReason(int var3, String var4, boolean var5) {
      this.id = var3;
      this.backendName = var4.toUpperCase(Locale.ROOT);
      this.reportable = var5;
      String var6 = "gui.abuseReport.reason." + var4;
      this.title = Component.translatable(var6);
      this.description = Component.translatable(var6 + ".description");
   }

   public String backendName() {
      return this.backendName;
   }

   public Component title() {
      return this.title;
   }

   public Component description() {
      return this.description;
   }

   public boolean reportable() {
      return this.reportable;
   }

   @Nullable
   public static Component getTranslationById(int var0) {
      ReportReason[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ReportReason var4 = var1[var3];
         if (var4.id == var0) {
            return var4.title;
         }
      }

      return null;
   }

   // $FF: synthetic method
   private static ReportReason[] $values() {
      return new ReportReason[]{FALSE_REPORTING, HATE_SPEECH, TERRORISM_OR_VIOLENT_EXTREMISM, CHILD_SEXUAL_EXPLOITATION_OR_ABUSE, IMMINENT_HARM, NON_CONSENSUAL_INTIMATE_IMAGERY, HARASSMENT_OR_BULLYING, DEFAMATION_IMPERSONATION_FALSE_INFORMATION, SELF_HARM_OR_SUICIDE, ALCOHOL_TOBACCO_DRUGS};
   }
}
