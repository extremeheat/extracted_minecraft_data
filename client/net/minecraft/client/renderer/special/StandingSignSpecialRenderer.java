package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector3f;

public class StandingSignSpecialRenderer implements NoDataSpecialModelRenderer {
   private final MaterialSet materials;
   private final Model.Simple model;
   private final Material material;

   public StandingSignSpecialRenderer(MaterialSet var1, Model.Simple var2, Material var3) {
      super();
      this.materials = var1;
      this.model = var2;
      this.material = var3;
   }

   public void submit(ItemDisplayContext var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, boolean var6, int var7) {
      SignRenderer.submitSpecial(this.materials, var2, var3, var4, var5, this.model, this.material);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      SignRenderer.applyInHandTransforms(var2);
      this.model.root().getExtentsForGui(var2, var1);
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

      public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext var1) {
         Model.Simple var2 = SignRenderer.createSignModel(var1.entityModelSet(), this.woodType, true);
         Optional var10000 = this.texture;
         MaterialMapper var10001 = Sheets.SIGN_MAPPER;
         Objects.requireNonNull(var10001);
         Material var3 = (Material)var10000.map(var10001::apply).orElseGet(() -> Sheets.getSignMaterial(this.woodType));
         return new StandingSignSpecialRenderer(var1.materials(), var2, var3);
      }
   }
}
