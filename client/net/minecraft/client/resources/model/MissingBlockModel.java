package net.minecraft.client.resources.model;

import com.mojang.datafixers.util.Either;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class MissingBlockModel {
   public static final String NAME = "missing";
   public static final ResourceLocation LOCATION = SpecialModels.builtinModelId("missing");
   public static final ModelResourceLocation VARIANT = new ModelResourceLocation(LOCATION, "missing");

   public MissingBlockModel() {
      super();
   }

   public static UnbakedModel missingModel() {
      BlockFaceUV var0 = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
      EnumMap var1 = new EnumMap<>(Direction.class);

      for (Direction var5 : Direction.values()) {
         var1.put(var5, new BlockElementFace(var5, 0, MissingTextureAtlasSprite.getLocation().getPath(), var0));
      }

      BlockElement var6 = new BlockElement(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), var1);
      BlockModel var7 = new BlockModel(
         null, List.of(var6), Map.of("particle", Either.left(BlockModel.MISSING_MATERIAL)), null, null, ItemTransforms.NO_TRANSFORMS, List.of()
      );
      var7.name = "missingno";
      return var7;
   }
}
