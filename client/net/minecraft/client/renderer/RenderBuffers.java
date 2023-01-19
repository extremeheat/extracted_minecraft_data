package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelBakery;

public class RenderBuffers {
   private final ChunkBufferBuilderPack fixedBufferPack = new ChunkBufferBuilderPack();
   private final SortedMap<RenderType, BufferBuilder> fixedBuffers = Util.make(new Object2ObjectLinkedOpenHashMap(), var1 -> {
      var1.put(Sheets.solidBlockSheet(), this.fixedBufferPack.builder(RenderType.solid()));
      var1.put(Sheets.cutoutBlockSheet(), this.fixedBufferPack.builder(RenderType.cutout()));
      var1.put(Sheets.bannerSheet(), this.fixedBufferPack.builder(RenderType.cutoutMipped()));
      var1.put(Sheets.translucentCullBlockSheet(), this.fixedBufferPack.builder(RenderType.translucent()));
      put(var1, Sheets.shieldSheet());
      put(var1, Sheets.bedSheet());
      put(var1, Sheets.shulkerBoxSheet());
      put(var1, Sheets.signSheet());
      put(var1, Sheets.hangingSignSheet());
      put(var1, Sheets.chestSheet());
      put(var1, RenderType.translucentNoCrumbling());
      put(var1, RenderType.armorGlint());
      put(var1, RenderType.armorEntityGlint());
      put(var1, RenderType.glint());
      put(var1, RenderType.glintDirect());
      put(var1, RenderType.glintTranslucent());
      put(var1, RenderType.entityGlint());
      put(var1, RenderType.entityGlintDirect());
      put(var1, RenderType.waterMask());
      ModelBakery.DESTROY_TYPES.forEach(var1x -> put(var1, var1x));
   });
   private final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediateWithBuffers(this.fixedBuffers, new BufferBuilder(256));
   private final MultiBufferSource.BufferSource crumblingBufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
   private final OutlineBufferSource outlineBufferSource = new OutlineBufferSource(this.bufferSource);

   public RenderBuffers() {
      super();
   }

   private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> var0, RenderType var1) {
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
