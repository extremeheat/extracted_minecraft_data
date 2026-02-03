package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SequencedMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.Util;

public class RenderBuffers {
   private final SectionBufferBuilderPack fixedBufferPack = new SectionBufferBuilderPack();
   private final SectionBufferBuilderPool sectionBufferPool;
   private final MultiBufferSource.BufferSource bufferSource;
   private final MultiBufferSource.BufferSource crumblingBufferSource;
   private final OutlineBufferSource outlineBufferSource;

   public RenderBuffers(final int maxSectionBuilders) {
      super();
      this.sectionBufferPool = SectionBufferBuilderPool.allocate(maxSectionBuilders);
      SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers = (SequencedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), (map) -> {
         map.put(Sheets.cutoutBlockItemSheet(), this.fixedBufferPack.buffer(ChunkSectionLayer.CUTOUT));
         map.put(Sheets.translucentItemSheet(), this.fixedBufferPack.buffer(ChunkSectionLayer.TRANSLUCENT));
         put(map, Sheets.translucentBlockItemSheet());
         put(map, RenderTypes.armorEntityGlint());
         put(map, RenderTypes.glint());
         put(map, RenderTypes.glintTranslucent());
         put(map, RenderTypes.entityGlint());
         put(map, RenderTypes.waterMask());
      });
      this.bufferSource = MultiBufferSource.immediateWithBuffers(fixedBuffers, new ByteBufferBuilder(786432));
      this.outlineBufferSource = new OutlineBufferSource();
      SequencedMap<RenderType, ByteBufferBuilder> crumblingBuffers = (SequencedMap)Util.make(new Object2ObjectLinkedOpenHashMap(), (map) -> ModelBakery.DESTROY_TYPES.forEach((type) -> put(map, type)));
      this.crumblingBufferSource = MultiBufferSource.immediateWithBuffers(crumblingBuffers, new ByteBufferBuilder(0));
   }

   private static void put(final Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> map, final RenderType type) {
      map.put(type, new ByteBufferBuilder(type.bufferSize()));
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
