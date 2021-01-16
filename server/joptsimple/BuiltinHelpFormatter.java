package joptsimple;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import joptsimple.internal.Classes;
import joptsimple.internal.Messages;
import joptsimple.internal.Rows;
import joptsimple.internal.Strings;

public class BuiltinHelpFormatter implements HelpFormatter {
   private final Rows nonOptionRows;
   private final Rows optionRows;

   BuiltinHelpFormatter() {
      this(80, 2);
   }

   public BuiltinHelpFormatter(int var1, int var2) {
      super();
      this.nonOptionRows = new Rows(var1 * 2, 0);
      this.optionRows = new Rows(var1, var2);
   }

   public String format(Map<String, ? extends OptionDescriptor> var1) {
      this.optionRows.reset();
      this.nonOptionRows.reset();
      Comparator var2 = new Comparator<OptionDescriptor>() {
         public int compare(OptionDescriptor var1, OptionDescriptor var2) {
            return ((String)var1.options().iterator().next()).compareTo((String)var2.options().iterator().next());
         }
      };
      TreeSet var3 = new TreeSet(var2);
      var3.addAll(var1.values());
      this.addRows(var3);
      return this.formattedHelpOutput();
   }

   protected void addOptionRow(String var1) {
      this.addOptionRow(var1, "");
   }

   protected void addOptionRow(String var1, String var2) {
      this.optionRows.add(var1, var2);
   }

   protected void addNonOptionRow(String var1) {
      this.nonOptionRows.add(var1, "");
   }

   protected void fitRowsToWidth() {
      this.nonOptionRows.fitToWidth();
      this.optionRows.fitToWidth();
   }

   protected String nonOptionOutput() {
      return this.nonOptionRows.render();
   }

   protected String optionOutput() {
      return this.optionRows.render();
   }

   protected String formattedHelpOutput() {
      StringBuilder var1 = new StringBuilder();
      String var2 = this.nonOptionOutput();
      if (!Strings.isNullOrEmpty(var2)) {
         var1.append(var2).append(Strings.LINE_SEPARATOR);
      }

      var1.append(this.optionOutput());
      return var1.toString();
   }

   protected void addRows(Collection<? extends OptionDescriptor> var1) {
      this.addNonOptionsDescription(var1);
      if (var1.isEmpty()) {
         this.addOptionRow(this.message("no.options.specified"));
      } else {
         this.addHeaders(var1);
         this.addOptions(var1);
      }

      this.fitRowsToWidth();
   }

   protected void addNonOptionsDescription(Collection<? extends OptionDescriptor> var1) {
      OptionDescriptor var2 = this.findAndRemoveNonOptionsSpec(var1);
      if (this.shouldShowNonOptionArgumentDisplay(var2)) {
         this.addNonOptionRow(this.message("non.option.arguments.header"));
         this.addNonOptionRow(this.createNonOptionArgumentsDisplay(var2));
      }

   }

   protected boolean shouldShowNonOptionArgumentDisplay(OptionDescriptor var1) {
      return !Strings.isNullOrEmpty(var1.description()) || !Strings.isNullOrEmpty(var1.argumentTypeIndicator()) || !Strings.isNullOrEmpty(var1.argumentDescription());
   }

   protected String createNonOptionArgumentsDisplay(OptionDescriptor var1) {
      StringBuilder var2 = new StringBuilder();
      this.maybeAppendOptionInfo(var2, var1);
      this.maybeAppendNonOptionsDescription(var2, var1);
      return var2.toString();
   }

   protected void maybeAppendNonOptionsDescription(StringBuilder var1, OptionDescriptor var2) {
      var1.append(var1.length() > 0 && !Strings.isNullOrEmpty(var2.description()) ? " -- " : "").append(var2.description());
   }

   protected OptionDescriptor findAndRemoveNonOptionsSpec(Collection<? extends OptionDescriptor> var1) {
      Iterator var2 = var1.iterator();

      OptionDescriptor var3;
      do {
         if (!var2.hasNext()) {
            throw new AssertionError("no non-options argument spec");
         }

         var3 = (OptionDescriptor)var2.next();
      } while(!var3.representsNonOptions());

      var2.remove();
      return var3;
   }

   protected void addHeaders(Collection<? extends OptionDescriptor> var1) {
      if (this.hasRequiredOption(var1)) {
         this.addOptionRow(this.message("option.header.with.required.indicator"), this.message("description.header"));
         this.addOptionRow(this.message("option.divider.with.required.indicator"), this.message("description.divider"));
      } else {
         this.addOptionRow(this.message("option.header"), this.message("description.header"));
         this.addOptionRow(this.message("option.divider"), this.message("description.divider"));
      }

   }

   protected final boolean hasRequiredOption(Collection<? extends OptionDescriptor> var1) {
      Iterator var2 = var1.iterator();

      OptionDescriptor var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (OptionDescriptor)var2.next();
      } while(!var3.isRequired());

      return true;
   }

   protected void addOptions(Collection<? extends OptionDescriptor> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         OptionDescriptor var3 = (OptionDescriptor)var2.next();
         if (!var3.representsNonOptions()) {
            this.addOptionRow(this.createOptionDisplay(var3), this.createDescriptionDisplay(var3));
         }
      }

   }

   protected String createOptionDisplay(OptionDescriptor var1) {
      StringBuilder var2 = new StringBuilder(var1.isRequired() ? "* " : "");
      Iterator var3 = var1.options().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.append(this.optionLeader(var4));
         var2.append(var4);
         if (var3.hasNext()) {
            var2.append(", ");
         }
      }

      this.maybeAppendOptionInfo(var2, var1);
      return var2.toString();
   }

   protected String optionLeader(String var1) {
      return var1.length() > 1 ? "--" : ParserRules.HYPHEN;
   }

   protected void maybeAppendOptionInfo(StringBuilder var1, OptionDescriptor var2) {
      String var3 = this.extractTypeIndicator(var2);
      String var4 = var2.argumentDescription();
      if (var2.acceptsArguments() || !Strings.isNullOrEmpty(var4) || var2.representsNonOptions()) {
         this.appendOptionHelp(var1, var3, var4, var2.requiresArgument());
      }

   }

   protected String extractTypeIndicator(OptionDescriptor var1) {
      String var2 = var1.argumentTypeIndicator();
      return !Strings.isNullOrEmpty(var2) && !String.class.getName().equals(var2) ? Classes.shortNameOf(var2) : "String";
   }

   protected void appendOptionHelp(StringBuilder var1, String var2, String var3, boolean var4) {
      if (var4) {
         this.appendTypeIndicator(var1, var2, var3, '<', '>');
      } else {
         this.appendTypeIndicator(var1, var2, var3, '[', ']');
      }

   }

   protected void appendTypeIndicator(StringBuilder var1, String var2, String var3, char var4, char var5) {
      var1.append(' ').append(var4);
      if (var2 != null) {
         var1.append(var2);
      }

      if (!Strings.isNullOrEmpty(var3)) {
         if (var2 != null) {
            var1.append(": ");
         }

         var1.append(var3);
      }

      var1.append(var5);
   }

   protected String createDescriptionDisplay(OptionDescriptor var1) {
      List var2 = var1.defaultValues();
      if (var2.isEmpty()) {
         return var1.description();
      } else {
         String var3 = this.createDefaultValuesDisplay(var2);
         return (var1.description() + ' ' + Strings.surround(this.message("default.value.header") + ' ' + var3, '(', ')')).trim();
      }
   }

   protected String createDefaultValuesDisplay(List<?> var1) {
      return var1.size() == 1 ? var1.get(0).toString() : var1.toString();
   }

   protected String message(String var1, Object... var2) {
      return Messages.message(Locale.getDefault(), "joptsimple.HelpFormatterMessages", BuiltinHelpFormatter.class, var1, var2);
   }
}
