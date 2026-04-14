package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.util.SequencedSet;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.Util;

public class RenderBuffers implements AutoCloseable {
   private final SectionBufferBuilderPack fixedBufferPack = new SectionBufferBuilderPack();
   private final SectionBufferBuilderPool sectionBufferPool;
   private final MultiBufferSource.BufferSource bufferSource;
   private final MultiBufferSource.BufferSource crumblingBufferSource;
   private final OutlineBufferSource outlineBufferSource;

   public RenderBuffers(final int maxSectionBuilders) {
      super();
      this.sectionBufferPool = SectionBufferBuilderPool.allocate(maxSectionBuilders);
      SequencedSet<RenderType> fixedTypes = (SequencedSet)Util.make(new ObjectLinkedOpenHashSet(), (types) -> {
         types.add(Sheets.cutoutBlockItemSheet());
         types.add(Sheets.translucentBlockItemSheet());
         types.add(Sheets.cutoutItemSheet());
         types.add(Sheets.translucentItemSheet());
         types.add(RenderTypes.glint());
         types.add(RenderTypes.glintTranslucent());
         types.add(RenderTypes.waterMask());
      });
      this.bufferSource = MultiBufferSource.create(786432, fixedTypes);
      this.outlineBufferSource = new OutlineBufferSource(MultiBufferSource.create(1536, ObjectSortedSets.emptySet()));
      this.crumblingBufferSource = MultiBufferSource.create(1536, new ObjectLinkedOpenHashSet(ModelBakery.DESTROY_TYPES));
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

   public void endFrame() {
      this.bufferSource.endFrame();
      this.outlineBufferSource.endFrame();
      this.crumblingBufferSource.endFrame();
   }

   public void close() {
      this.bufferSource.close();
      this.outlineBufferSource.close();
      this.crumblingBufferSource.close();
   }
}
