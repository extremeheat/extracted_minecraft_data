package net.minecraft.client.resources.model;

import com.mojang.math.Quadrant;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.SimpleUnbakedGeometry;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
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
      BlockElementFace.UVs var0 = new BlockElementFace.UVs(0.0F, 0.0F, 16.0F, 16.0F);
      Map var1 = Util.makeEnumMap(Direction.class, (var1x) -> new BlockElementFace(var1x, -1, "missingno", var0, Quadrant.R0));
      BlockElement var2 = new BlockElement(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), var1);
      return new BlockModel(new SimpleUnbakedGeometry(List.of(var2)), (UnbakedModel.GuiLight)null, (Boolean)null, ItemTransforms.NO_TRANSFORMS, (new TextureSlots.Data.Builder()).addReference("particle", "missingno").addTexture("missingno", new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation())).build(), (Identifier)null);
   }
}
