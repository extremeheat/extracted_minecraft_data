package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TickableTextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class TextureAtlasHolder extends SimplePreparableReloadListener<TextureAtlas.Preparations> implements AutoCloseable {
   private final TextureAtlas textureAtlas;

   public TextureAtlasHolder(TextureManager var1, ResourceLocation var2, String var3) {
      super();
      this.textureAtlas = new TextureAtlas(var3);
      var1.register((ResourceLocation)var2, (TickableTextureObject)this.textureAtlas);
   }

   protected abstract Iterable<ResourceLocation> getResourcesToLoad();

   protected TextureAtlasSprite getSprite(ResourceLocation var1) {
      return this.textureAtlas.getSprite(var1);
   }

   protected TextureAtlas.Preparations prepare(ResourceManager var1, ProfilerFiller var2) {
      var2.startTick();
      var2.push("stitching");
      TextureAtlas.Preparations var3 = this.textureAtlas.prepareToStitch(var1, this.getResourcesToLoad(), var2);
      var2.pop();
      var2.endTick();
      return var3;
   }

   protected void apply(TextureAtlas.Preparations var1, ResourceManager var2, ProfilerFiller var3) {
      var3.startTick();
      var3.push("upload");
      this.textureAtlas.reload(var1);
      var3.pop();
      var3.endTick();
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
