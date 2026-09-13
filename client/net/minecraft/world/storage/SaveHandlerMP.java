package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public class SaveHandlerMP implements ISaveHandler {
   public SaveHandlerMP() {
      super();
   }

   @Override
   public WorldInfo func_75757_d() {
      return null;
   }

   @Override
   public void func_75762_c() {
   }

   @Override
   public IChunkLoader func_75763_a(WorldProvider var1) {
      return null;
   }

   @Override
   public void func_75755_a(WorldInfo var1, NBTTagCompound var2) {
   }

   @Override
   public void func_75761_a(WorldInfo var1) {
   }

   @Override
   public IPlayerFileData func_75756_e() {
      return null;
   }

   @Override
   public void func_75759_a() {
   }

   @Override
   public File func_75758_b(String var1) {
      return null;
   }

   @Override
   public String func_75760_g() {
      return "none";
   }

   @Override
   public File func_75765_b() {
      return null;
   }
}
