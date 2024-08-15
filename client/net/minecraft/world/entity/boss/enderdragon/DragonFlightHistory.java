package net.minecraft.world.entity.boss.enderdragon;

import java.util.Arrays;
import net.minecraft.util.Mth;

public class DragonFlightHistory {
   public static final int LENGTH = 64;
   private static final int MASK = 63;
   private final DragonFlightHistory.Sample[] samples = new DragonFlightHistory.Sample[64];
   private int head = -1;

   public DragonFlightHistory() {
      super();
      Arrays.fill(this.samples, new DragonFlightHistory.Sample(0.0, 0.0F));
   }

   public void copyFrom(DragonFlightHistory var1) {
      System.arraycopy(var1.samples, 0, this.samples, 0, 64);
      this.head = var1.head;
   }

   public void record(double var1, float var3) {
      DragonFlightHistory.Sample var4 = new DragonFlightHistory.Sample(var1, var3);
      if (this.head < 0) {
         Arrays.fill(this.samples, var4);
      }

      if (++this.head == 64) {
         this.head = 0;
      }

      this.samples[this.head] = var4;
   }

   public DragonFlightHistory.Sample get(int var1) {
      return this.samples[this.head - var1 & 63];
   }

   public DragonFlightHistory.Sample get(int var1, float var2) {
      DragonFlightHistory.Sample var3 = this.get(var1);
      DragonFlightHistory.Sample var4 = this.get(var1 + 1);
      return new DragonFlightHistory.Sample(Mth.lerp((double)var2, var4.y, var3.y), Mth.rotLerp(var2, var4.yRot, var3.yRot));
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
