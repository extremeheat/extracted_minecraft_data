package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3fc;

public class ChestSpecialRenderer implements NoDataSpecialModelRenderer {
   public static final Identifier GIFT_CHEST_TEXTURE = Identifier.withDefaultNamespace("christmas");
   public static final Identifier NORMAL_CHEST_TEXTURE = Identifier.withDefaultNamespace("normal");
   public static final Identifier TRAPPED_CHEST_TEXTURE = Identifier.withDefaultNamespace("trapped");
   public static final Identifier ENDER_CHEST_TEXTURE = Identifier.withDefaultNamespace("ender");
   public static final Identifier COPPER_CHEST_TEXTURE = Identifier.withDefaultNamespace("copper");
   public static final Identifier EXPOSED_COPPER_CHEST_TEXTURE = Identifier.withDefaultNamespace("copper_exposed");
   public static final Identifier WEATHERED_COPPER_CHEST_TEXTURE = Identifier.withDefaultNamespace("copper_weathered");
   public static final Identifier OXIDIZED_COPPER_CHEST_TEXTURE = Identifier.withDefaultNamespace("copper_oxidized");
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

   public void getExtents(Consumer<Vector3fc> var1) {
      PoseStack var2 = new PoseStack();
      this.model.setupAnim(this.openness);
      this.model.root().getExtentsForGui(var2, var1);
   }

   public static record Unbaked(Identifier texture, float openness) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness)).apply(var0, Unbaked::new));

      public Unbaked(Identifier var1) {
         this(var1, 0.0F);
      }

      public Unbaked(Identifier var1, float var2) {
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
