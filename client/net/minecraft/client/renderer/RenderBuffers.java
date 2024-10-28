package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelBakery;

public class RenderBuffers {
   private final SectionBufferBuilderPack fixedBufferPack = new SectionBufferBuilderPack();
   private final SectionBufferBuilderPool sectionBufferPool;
   private final MultiBufferSource.BufferSource bufferSource;
   private final MultiBufferSource.BufferSource crumblingBufferSource;
   private final OutlineBufferSource outlineBufferSource;

   public RenderBuffers(int var1) {
      super();
      this.sectionBufferPool = SectionBufferBuilderPool.allocate(var1);
      SortedMap var2 = (SortedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), (var1x) -> {
         var1x.put(Sheets.solidBlockSheet(), this.fixedBufferPack.builder(RenderType.solid()));
         var1x.put(Sheets.cutoutBlockSheet(), this.fixedBufferPack.builder(RenderType.cutout()));
         var1x.put(Sheets.bannerSheet(), this.fixedBufferPack.builder(RenderType.cutoutMipped()));
         var1x.put(Sheets.translucentCullBlockSheet(), this.fixedBufferPack.builder(RenderType.translucent()));
         put(var1x, Sheets.shieldSheet());
         put(var1x, Sheets.bedSheet());
         put(var1x, Sheets.shulkerBoxSheet());
         put(var1x, Sheets.signSheet());
         put(var1x, Sheets.hangingSignSheet());
         var1x.put(Sheets.chestSheet(), new BufferBuilder(786432));
         put(var1x, RenderType.armorGlint());
         put(var1x, RenderType.armorEntityGlint());
         put(var1x, RenderType.glint());
         put(var1x, RenderType.glintDirect());
         put(var1x, RenderType.glintTranslucent());
         put(var1x, RenderType.entityGlint());
         put(var1x, RenderType.entityGlintDirect());
         put(var1x, RenderType.waterMask());
         ModelBakery.DESTROY_TYPES.forEach((var1) -> {
            put(var1x, var1);
         });
      });
      this.crumblingBufferSource = MultiBufferSource.immediate(new BufferBuilder(1536));
      this.bufferSource = MultiBufferSource.immediateWithBuffers(var2, new BufferBuilder(786432));
      this.outlineBufferSource = new OutlineBufferSource(this.bufferSource);
   }

   private static void put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> var0, RenderType var1) {
      var0.put(var1, new BufferBuilder(var1.bufferSize()));
   }

   public SectionBufferBuilderPack fixedBufferPack() {
      return this.fixedBufferPack;
   }

   public SectionBufferBuilderPool sectionBufferPool() {
      return this.sectionBufferPool;
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
