package net.minecraft.client.resources.model;

import com.mojang.math.Quadrant;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.Material;
import net.minecraft.client.renderer.block.model.SimpleUnbakedGeometry;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.joml.Vector3f;

public class MissingBlockModel {
   private static final String TEXTURE_SLOT = "missingno";
   public static final Identifier LOCATION = Identifier.withDefaultNamespace("builtin/missing");

   public MissingBlockModel() {
      super();
   }

   public static UnbakedModel missingModel() {
      BlockElementFace.UVs fullFaceUv = new BlockElementFace.UVs(0.0F, 0.0F, 16.0F, 16.0F);
      Map<Direction, BlockElementFace> faces = Util.<Direction, BlockElementFace>makeEnumMap(Direction.class, (direction) -> new BlockElementFace(direction, -1, "missingno", fullFaceUv, Quadrant.R0));
      BlockElement cube = new BlockElement(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), faces);
      return new BlockModel(new SimpleUnbakedGeometry(List.of(cube)), (UnbakedModel.GuiLight)null, (Boolean)null, ItemTransforms.NO_TRANSFORMS, (new TextureSlots.Data.Builder()).addReference("particle", "missingno").addTexture("missingno", new Material(MissingTextureAtlasSprite.getLocation())).build(), (Identifier)null);
   }
}
