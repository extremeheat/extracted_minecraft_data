package net.minecraft.client.renderer.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public class VillageSectionsDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST_FOR_VILLAGE_SECTIONS = 60;
   private final Set<SectionPos> villageSections = Sets.newHashSet();

   VillageSectionsDebugRenderer() {
      super();
   }

   @Override
   public void clear() {
      this.villageSections.clear();
   }

   public void setVillageSection(SectionPos var1) {
      this.villageSections.add(var1);
   }

   public void setNotVillageSection(SectionPos var1) {
      this.villageSections.remove(var1);
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      BlockPos var9 = BlockPos.containing(var3, var5, var7);
      this.villageSections.forEach(var3x -> {
         if (var9.closerThan(var3x.center(), 60.0)) {
            highlightVillageSection(var1, var2, var3x);
         }
      });
   }

   private static void highlightVillageSection(PoseStack var0, MultiBufferSource var1, SectionPos var2) {
      DebugRenderer.renderFilledUnitCube(var0, var1, var2.center(), 0.2F, 1.0F, 0.2F, 0.15F);
   }
}
