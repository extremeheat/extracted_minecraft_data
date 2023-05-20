package net.minecraft.client.multiplayer.chat.report;

import java.util.Locale;
import net.minecraft.network.chat.Component;

public enum ReportReason {
   HATE_SPEECH("hate_speech"),
   TERRORISM_OR_VIOLENT_EXTREMISM("terrorism_or_violent_extremism"),
   CHILD_SEXUAL_EXPLOITATION_OR_ABUSE("child_sexual_exploitation_or_abuse"),
   IMMINENT_HARM("imminent_harm"),
   NON_CONSENSUAL_INTIMATE_IMAGERY("non_consensual_intimate_imagery"),
   HARASSMENT_OR_BULLYING("harassment_or_bullying"),
   DEFAMATION_IMPERSONATION_FALSE_INFORMATION("defamation_impersonation_false_information"),
   SELF_HARM_OR_SUICIDE("self_harm_or_suicide"),
   ALCOHOL_TOBACCO_DRUGS("alcohol_tobacco_drugs");

   private final String backendName;
   private final Component title;
   private final Component description;

   private ReportReason(String var3) {
      this.backendName = var3.toUpperCase(Locale.ROOT);
      String var4 = "gui.abuseReport.reason." + var3;
      this.title = Component.translatable(var4);
      this.description = Component.translatable(var4 + ".description");
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
}
