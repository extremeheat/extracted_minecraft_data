package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JUnitLikeTestReporter implements TestReporter {
   private final Document document;
   private final Element testSuite;
   private final Stopwatch stopwatch;
   private final File destination;

   public JUnitLikeTestReporter(File var1) throws ParserConfigurationException {
      super();
      this.destination = var1;
      this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      this.testSuite = this.document.createElement("testsuite");
      Element var2 = this.document.createElement("testsuite");
      var2.appendChild(this.testSuite);
      this.document.appendChild(var2);
      this.testSuite.setAttribute("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
      this.stopwatch = Stopwatch.createStarted();
   }

   private Element createTestCase(GameTestInfo var1, String var2) {
      Element var3 = this.document.createElement("testcase");
      var3.setAttribute("name", var2);
      var3.setAttribute("classname", var1.getStructureName());
      var3.setAttribute("time", String.valueOf((double)var1.getRunTime() / 1000.0));
      this.testSuite.appendChild(var3);
      return var3;
   }

   @Override
   public void onTestFailed(GameTestInfo var1) {
      String var2 = var1.getTestName();
      String var3 = var1.getError().getMessage();
      Element var4;
      if (var1.isRequired()) {
         var4 = this.document.createElement("failure");
         var4.setAttribute("message", var3);
      } else {
         var4 = this.document.createElement("skipped");
         var4.setAttribute("message", var3);
      }

      Element var5 = this.createTestCase(var1, var2);
      var5.appendChild(var4);
   }

   @Override
   public void onTestSuccess(GameTestInfo var1) {
      String var2 = var1.getTestName();
      this.createTestCase(var1, var2);
   }

   @Override
   public void finish() {
      this.stopwatch.stop();
      this.testSuite.setAttribute("time", String.valueOf((double)this.stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0));

      try {
         this.save(this.destination);
      } catch (TransformerException var2) {
         throw new Error("Couldn't save test report", var2);
      }
   }

   public void save(File var1) throws TransformerException {
      TransformerFactory var2 = TransformerFactory.newInstance();
      Transformer var3 = var2.newTransformer();
      DOMSource var4 = new DOMSource(this.document);
      StreamResult var5 = new StreamResult(var1);
      var3.transform(var4, var5);
   }
}
