package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import java.util.Optional;

public class OutlineBufferSource implements MultiBufferSource {
   private final MultiBufferSource.BufferSource bufferSource;
   private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
   private int teamR = 255;
   private int teamG = 255;
   private int teamB = 255;
   private int teamA = 255;

   public OutlineBufferSource(MultiBufferSource.BufferSource var1) {
      super();
      this.bufferSource = var1;
   }

   @Override
   public VertexConsumer getBuffer(RenderType var1) {
      if (var1.isOutline()) {
         VertexConsumer var6 = this.outlineBufferSource.getBuffer(var1);
         return new OutlineBufferSource.EntityOutlineGenerator(var6, this.teamR, this.teamG, this.teamB, this.teamA);
      } else {
         VertexConsumer var2 = this.bufferSource.getBuffer(var1);
         Optional var3 = var1.outline();
         if (var3.isPresent()) {
            VertexConsumer var4 = this.outlineBufferSource.getBuffer((RenderType)var3.get());
            OutlineBufferSource.EntityOutlineGenerator var5 = new OutlineBufferSource.EntityOutlineGenerator(
               var4, this.teamR, this.teamG, this.teamB, this.teamA
            );
            return VertexMultiConsumer.create(var5, var2);
         } else {
            return var2;
         }
      }
   }

   public void setColor(int var1, int var2, int var3, int var4) {
      this.teamR = var1;
      this.teamG = var2;
      this.teamB = var3;
      this.teamA = var4;
   }

   public void endOutlineBatch() {
      this.outlineBufferSource.endBatch();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
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
