package net.minecraft.client.renderer.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
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
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      this.doRender(var3, var5, var7);
      RenderSystem.disableBlend();
   }

   private void doRender(double var1, double var3, double var5) {
      BlockPos var7 = new BlockPos(var1, var3, var5);
      this.villageSections.forEach(var1x -> {
         if (var7.closerThan(var1x.center(), 60.0)) {
            highlightVillageSection(var1x);
         }
      });
   }

   private static void highlightVillageSection(SectionPos var0) {
      float var1 = 1.0F;
      BlockPos var2 = var0.center();
      BlockPos var3 = var2.offset(-1.0, -1.0, -1.0);
      BlockPos var4 = var2.offset(1.0, 1.0, 1.0);
      DebugRenderer.renderFilledBox(var3, var4, 0.2F, 1.0F, 0.2F, 0.15F);
   }
}
