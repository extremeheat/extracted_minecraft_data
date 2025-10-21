package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class ChestSpecialRenderer implements NoDataSpecialModelRenderer {
   public static final ResourceLocation GIFT_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("christmas");
   public static final ResourceLocation NORMAL_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("normal");
   public static final ResourceLocation TRAPPED_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("trapped");
   public static final ResourceLocation ENDER_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("ender");
   public static final ResourceLocation COPPER_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("copper");
   public static final ResourceLocation EXPOSED_COPPER_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("copper_exposed");
   public static final ResourceLocation WEATHERED_COPPER_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("copper_weathered");
   public static final ResourceLocation OXIDIZED_COPPER_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("copper_oxidized");
   private final MaterialSet materials;
   private final ChestModel model;
   private final Material material;
   private final float openness;

   public ChestSpecialRenderer(MaterialSet var1, ChestModel var2, Material var3, float var4) {
      super();
      this.materials = var1;
      this.model = var2;
      this.material = var3;
      this.openness = var4;
   }

   public void submit(ItemDisplayContext var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, boolean var6, int var7) {
      var3.submitModel(this.model, this.openness, var2, this.material.renderType(RenderTypes::entitySolid), var4, var5, -1, this.materials.get(this.material), var7, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      this.model.setupAnim(this.openness);
      this.model.root().getExtentsForGui(var2, var1);
   }

   public static record Unbaked(ResourceLocation texture, float openness) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture), Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness)).apply(var0, Unbaked::new));

      public Unbaked(ResourceLocation var1) {
         this(var1, 0.0F);
      }

      public Unbaked(ResourceLocation var1, float var2) {
         super();
         this.texture = var1;
         this.openness = var2;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext var1) {
         ChestModel var2 = new ChestModel(var1.entityModelSet().bakeLayer(ModelLayers.CHEST));
         Material var3 = Sheets.CHEST_MAPPER.apply(this.texture);
         return new ChestSpecialRenderer(var1.materials(), var2, var3, this.openness);
      }
   }
}
