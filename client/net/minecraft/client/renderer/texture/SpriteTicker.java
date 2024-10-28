package net.minecraft.client.renderer.texture;

public interface SpriteTicker extends AutoCloseable {
   void tickAndUpload(int var1, int var2);

   void close();
}
