package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelBakery;

public class RenderBuffers {
   private final ChunkBufferBuilderPack fixedBufferPack = new ChunkBufferBuilderPack();
   private final SortedMap fixedBuffers = (SortedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), (var1) -> {
      var1.put(Sheets.solidBlockSheet(), this.fixedBufferPack.builder(RenderType.solid()));
      var1.put(Sheets.cutoutBlockSheet(), this.fixedBufferPack.builder(RenderType.cutout()));
      var1.put(Sheets.bannerSheet(), this.fixedBufferPack.builder(RenderType.cutoutMipped()));
      var1.put(Sheets.translucentBlockSheet(), this.fixedBufferPack.builder(RenderType.translucent()));
      put(var1, Sheets.shieldSheet());
      put(var1, Sheets.bedSheet());
      put(var1, Sheets.shulkerBoxSheet());
      put(var1, Sheets.signSheet());
      put(var1, Sheets.chestSheet());
      put(var1, RenderType.translucentNoCrumbling());
      put(var1, RenderType.glint());
      put(var1, RenderType.entityGlint());
      put(var1, RenderType.waterMask());
      ModelBakery.DESTROY_TYPES.forEach((var1x) -> {
         put(var1, var1x);
      });
   });
   private final MultiBufferSource.BufferSource bufferSource;
   private final MultiBufferSource.BufferSource crumblingBufferSource;
   private final OutlineBufferSource outlineBufferSource;

   public RenderBuffers() {
      this.bufferSource = MultiBufferSource.immediateWithBuffers(this.fixedBuffers, new BufferBuilder(256));
      this.crumblingBufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
      this.outlineBufferSource = new OutlineBufferSource(this.bufferSource);
   }

   private static void put(Object2ObjectLinkedOpenHashMap var0, RenderType var1) {
      var0.put(var1, new BufferBuilder(var1.bufferSize()));
   }

   public ChunkBufferBuilderPack fixedBufferPack() {
      return this.fixedBufferPack;
   }

   public MultiBufferSource.BufferSource bufferSource() {
      return this.bufferSource;
   }

   public MultiBufferSource.BufferSource crumblingBufferSource() {
      return this.crumblingBufferSource;
   }

   public OutlineBufferSource outlineBufferSource() {
      return this.outlineBufferSource;
   }
}
