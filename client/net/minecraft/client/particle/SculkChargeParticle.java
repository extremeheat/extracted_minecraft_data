package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;

public class SculkChargeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   SculkChargeParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.friction = 0.96F;
      this.sprites = var14;
      this.scale(1.5F);
      this.hasPhysics = false;
      this.setSpriteFromAge(var14);
   }

   @Override
   public int getLightColor(float var1) {
      return 240;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @Override
   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
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
