package net.minecraft.network.chat.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.ComponentContents;

public interface PlainTextContents extends ComponentContents {
   MapCodec<PlainTextContents> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.STRING.fieldOf("text").forGetter(PlainTextContents::text)).apply(var0, PlainTextContents::create)
   );
   ComponentContents.Type<PlainTextContents> TYPE = new ComponentContents.Type<>(CODEC, "text");
   PlainTextContents EMPTY = new PlainTextContents() {
      @Override
      public String toString() {
         return "empty";
      }

      @Override
      public String text() {
         return "";
      }
   };

   static PlainTextContents create(String var0) {
      return (PlainTextContents)(var0.isEmpty() ? EMPTY : new PlainTextContents.LiteralContents(var0));
   }

   String text();

   @Override
   default ComponentContents.Type<?> type() {
      return TYPE;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
