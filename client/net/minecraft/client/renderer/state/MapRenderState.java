package net.minecraft.client.renderer.state;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MapRenderState {
   @Nullable
   public ResourceLocation texture;
   public final List<MapDecorationRenderState> decorations = new ArrayList();

   public MapRenderState() {
      super();
   }

   public static class MapDecorationRenderState {
      @Nullable
      public TextureAtlasSprite atlasSprite;
      public byte x;
      public byte y;
      public byte rot;
      public boolean renderOnFrame;
      @Nullable
      public Component name;

      public MapDecorationRenderState() {
         super();
      }
   }
}
