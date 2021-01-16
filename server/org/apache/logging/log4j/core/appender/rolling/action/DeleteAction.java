package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

@Plugin(
   name = "Delete",
   category = "Core",
   printObject = true
)
public class DeleteAction extends AbstractPathAction {
   private final PathSorter pathSorter;
   private final boolean testMode;
   private final ScriptCondition scriptCondition;

   DeleteAction(String var1, boolean var2, int var3, boolean var4, PathSorter var5, PathCondition[] var6, ScriptCondition var7, StrSubstitutor var8) {
      super(var1, var2, var3, var6, var8);
      this.testMode = var4;
      this.pathSorter = (PathSorter)Objects.requireNonNull(var5, "sorter");
      this.scriptCondition = var7;
      if (var7 == null && (var6 == null || var6.length == 0)) {
         LOGGER.error("Missing Delete conditions: unconditional Delete not supported");
         throw new IllegalArgumentException("Unconditional Delete not supported");
      }
   }

   public boolean execute() throws IOException {
      return this.scriptCondition != null ? this.executeScript() : super.execute();
   }

   private boolean executeScript() throws IOException {
      List var1 = this.callScript();
      if (var1 == null) {
         LOGGER.trace("Script returned null list (no files to delete)");
         return true;
      } else {
         this.deleteSelectedFiles(var1);
         return true;
      }
   }

   private List<PathWithAttributes> callScript() throws IOException {
      List var1 = this.getSortedPaths();
      this.trace("Sorted paths:", var1);
      List var2 = this.scriptCondition.selectFilesToDelete(this.getBasePath(), var1);
      return var2;
   }

   private void deleteSelectedFiles(List<PathWithAttributes> var1) throws IOException {
      this.trace("Paths the script selected for deletion:", var1);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         PathWithAttributes var3 = (PathWithAttributes)var2.next();
         Path var4 = var3 == null ? null : var3.getPath();
         if (this.isTestMode()) {
            LOGGER.info((String)"Deleting {} (TEST MODE: file not actually deleted)", (Object)var4);
         } else {
            this.delete(var4);
         }
      }

   }

   protected void delete(Path var1) throws IOException {
      LOGGER.trace((String)"Deleting {}", (Object)var1);
      Files.deleteIfExists(var1);
   }

   public boolean execute(FileVisitor<Path> var1) throws IOException {
      List var2 = this.getSortedPaths();
      this.trace("Sorted paths:", var2);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         PathWithAttributes var4 = (PathWithAttributes)var3.next();

         try {
            var1.visitFile(var4.getPath(), var4.getAttributes());
         } catch (IOException var6) {
            LOGGER.error((String)"Error in post-rollover Delete when visiting {}", (Object)var4.getPath(), (Object)var6);
            var1.visitFileFailed(var4.getPath(), var6);
         }
      }

      return true;
   }

   private void trace(String var1, List<PathWithAttributes> var2) {
      LOGGER.trace(var1);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         PathWithAttributes var4 = (PathWithAttributes)var3.next();
         LOGGER.trace((Object)var4);
      }

   }

   List<PathWithAttributes> getSortedPaths() throws IOException {
      SortingVisitor var1 = new SortingVisitor(this.pathSorter);
      super.execute(var1);
      List var2 = var1.getSortedPaths();
      return var2;
   }

   public boolean isTestMode() {
      return this.testMode;
   }

   protected FileVisitor<Path> createFileVisitor(Path var1, List<PathCondition> var2) {
      return new DeletingVisitor(var1, var2, this.testMode);
   }

   @PluginFactory
   public static DeleteAction createDeleteAction(@PluginAttribute("basePath") String var0, @PluginAttribute("followLinks") boolean var1, @PluginAttribute(value = "maxDepth",defaultInt = 1) int var2, @PluginAttribute("testMode") boolean var3, @PluginElement("PathSorter") PathSorter var4, @PluginElement("PathConditions") PathCondition[] var5, @PluginElement("ScriptCondition") ScriptCondition var6, @PluginConfiguration Configuration var7) {
      Object var8 = var4 == null ? new PathSortByModificationTime(true) : var4;
      return new DeleteAction(var0, var1, var2, var3, (PathSorter)var8, var5, var6, var7.getStrSubstitutor());
   }
}
