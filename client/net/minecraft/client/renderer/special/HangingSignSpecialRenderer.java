package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.WoodType;

public class HangingSignSpecialRenderer implements NoDataSpecialModelRenderer {
   private final Model model;
   private final Material material;

   public HangingSignSpecialRenderer(Model var1, Material var2) {
      super();
      this.model = var1;
      this.material = var2;
   }

   public void render(ItemDisplayContext var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6) {
      HangingSignRenderer.renderInHand(var2, var3, var4, var5, this.model, this.material);
   }

   public static record Unbaked(WoodType woodType, Optional<ResourceLocation> texture) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(WoodType.CODEC.fieldOf("wood_type").forGetter(Unbaked::woodType), ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(Unbaked::texture)).apply(var0, Unbaked::new));

      public Unbaked(WoodType var1) {
         this(var1, Optional.empty());
      }

      public Unbaked(WoodType var1, Optional<ResourceLocation> var2) {
         super();
         this.woodType = var1;
         this.texture = var2;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         Model var2 = HangingSignRenderer.createSignModel(var1, this.woodType, HangingSignRenderer.AttachmentType.CEILING_MIDDLE);
         Material var3 = (Material)this.texture.map(Sheets::createHangingSignMaterial).orElseGet(() -> Sheets.getHangingSignMaterial(this.woodType));
         return new HangingSignSpecialRenderer(var2, var3);
      }
   }
}
