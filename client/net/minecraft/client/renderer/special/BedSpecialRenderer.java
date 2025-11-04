package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class BedSpecialRenderer implements NoDataSpecialModelRenderer {
   private final BedRenderer bedRenderer;
   private final Material material;

   public BedSpecialRenderer(BedRenderer var1, Material var2) {
      super();
      this.bedRenderer = var1;
      this.material = var2;
   }

   public void submit(ItemDisplayContext var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, boolean var6, int var7) {
      this.bedRenderer.submitSpecial(var2, var3, var4, var5, this.material, var7);
   }

   public void getExtents(Set<Vector3f> var1) {
      this.bedRenderer.getExtents(var1);
   }

   public static record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture)).apply(var0, Unbaked::new));

      public Unbaked(DyeColor var1) {
         this(Sheets.colorToResourceMaterial(var1));
      }

      public Unbaked(Identifier var1) {
         super();
         this.texture = var1;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext var1) {
         return new BedSpecialRenderer(new BedRenderer(var1), Sheets.BED_MAPPER.apply(this.texture));
      }
   }
}
