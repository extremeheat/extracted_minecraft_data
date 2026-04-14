package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public interface MultiBufferSource {
   static BufferSource create(final int initialBufferSize, final SequencedSet<RenderType> fixedTypes) {
      return new BufferSource(initialBufferSize, fixedTypes);
   }

   VertexConsumer getBuffer(final RenderType renderType);

   public static class BufferSource implements MultiBufferSource, AutoCloseable {
      private static final int NO_FIXED_DRAW = -1;
      private final StagedVertexBuffer stagedBuffer;
      private final SequencedSet<RenderType> fixedTypes;
      private final List<StagedVertexBuffer.Draw> draws = new ArrayList();
      private final List<RenderType> drawTypes = new ArrayList();
      private final Reference2IntMap<RenderType> fixedDraws = new Reference2IntOpenHashMap();

      protected BufferSource(final int initialBufferSize, final SequencedSet<RenderType> fixedTypes) {
         super();
         this.stagedBuffer = new StagedVertexBuffer(() -> "MultiBufferSource", initialBufferSize);
         this.fixedTypes = fixedTypes;
      }

      public VertexConsumer getBuffer(final RenderType renderType) {
         if (!this.drawTypes.isEmpty() && this.drawTypes.getLast() == renderType && renderType.canConsolidateConsecutiveGeometry()) {
            return this.stagedBuffer.getVertexBuilder((StagedVertexBuffer.Draw)this.draws.getLast());
         } else {
            int fixedDrawIndex = this.fixedDraws.getOrDefault(renderType, -1);
            StagedVertexBuffer.Draw draw;
            if (fixedDrawIndex != -1) {
               draw = (StagedVertexBuffer.Draw)this.draws.get(fixedDrawIndex);
            } else {
               draw = this.appendDraw(renderType);
            }

            return this.stagedBuffer.getVertexBuilder(draw);
         }
      }

      private StagedVertexBuffer.Draw appendDraw(final RenderType renderType) {
         StagedVertexBuffer.Draw draw = this.stagedBuffer.appendDraw(renderType.format(), renderType.mode(), renderType.sortOnUpload() ? RenderSystem.getProjectionType().vertexSorting() : null);
         this.draws.add(draw);
         this.drawTypes.add(renderType);
         if (this.fixedTypes.contains(renderType)) {
            this.fixedDraws.put(renderType, this.draws.size() - 1);
         }

         return draw;
      }

      public void uploadAndDraw() {
         if (!this.draws.isEmpty()) {
            ProfilerFiller profiler = Profiler.get();
            profiler.push("uploadMultiBufferSource");
            this.stagedBuffer.upload();
            profiler.popPush("drawMultiBufferSource");

            for(int i = 0; i < this.draws.size(); ++i) {
               StagedVertexBuffer.Draw draw = (StagedVertexBuffer.Draw)this.draws.get(i);
               RenderType type = (RenderType)this.drawTypes.get(i);
               if (!this.fixedDraws.containsKey(type)) {
                  StagedVertexBuffer.ExecuteInfo info = this.stagedBuffer.getExecuteInfo(draw);
                  if (info != null) {
                     type.drawFromBuffer(info);
                  }
               }
            }

            for(RenderType fixedType : this.fixedTypes) {
               int index = this.fixedDraws.getOrDefault(fixedType, -1);
               if (index != -1) {
                  StagedVertexBuffer.Draw draw = (StagedVertexBuffer.Draw)this.draws.get(index);
                  StagedVertexBuffer.ExecuteInfo info = this.stagedBuffer.getExecuteInfo(draw);
                  if (info != null) {
                     fixedType.drawFromBuffer(info);
                  }
               }
            }

            this.stagedBuffer.endDraw();
            profiler.pop();
            this.draws.clear();
            this.drawTypes.clear();
            this.fixedDraws.clear();
         }
      }

      public void endFrame() {
         this.stagedBuffer.endFrame();
      }

      public void close() {
         this.stagedBuffer.close();
      }
   }
}
