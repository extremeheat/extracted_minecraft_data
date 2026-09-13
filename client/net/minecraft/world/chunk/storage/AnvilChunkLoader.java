package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO {
   private static final Logger field_151505_a = LogManager.getLogger();
   private List field_75828_a = new ArrayList();
   private Set field_75826_b = new HashSet();
   private Object field_75827_c = new Object();
   private final File field_75825_d;

   public AnvilChunkLoader(File var1) {
      super();
      this.field_75825_d = var1;
   }

   @Override
   public Chunk func_75815_a(World var1, int var2, int var3) {
      NBTTagCompound var4 = null;
      ChunkCoordIntPair var5 = new ChunkCoordIntPair(var2, var3);
      synchronized(this.field_75827_c) {
         if (this.field_75826_b.contains(var5)) {
            for(int var7 = 0; var7 < this.field_75828_a.size(); ++var7) {
               if (((AnvilChunkLoader$PendingChunk)this.field_75828_a.get(var7)).field_76548_a.equals(var5)) {
                  var4 = ((AnvilChunkLoader$PendingChunk)this.field_75828_a.get(var7)).field_76547_b;
                  break;
               }
            }
         }
      }

      if (var4 == null) {
         DataInputStream var10 = RegionFileCache.func_76549_c(this.field_75825_d, var2, var3);
         if (var10 == null) {
            return null;
         }

         var4 = CompressedStreamTools.func_74794_a(var10);
      }

      return this.func_75822_a(var1, var2, var3, var4);
   }

   protected Chunk func_75822_a(World var1, int var2, int var3, NBTTagCompound var4) {
      if (!var4.func_150297_b("Level", 10)) {
         field_151505_a.error("Chunk file at " + var2 + "," + var3 + " is missing level data, skipping");
         return null;
      } else if (!var4.func_74775_l("Level").func_150297_b("Sections", 9)) {
         field_151505_a.error("Chunk file at " + var2 + "," + var3 + " is missing block data, skipping");
         return null;
      } else {
         Chunk var5 = this.func_75823_a(var1, var4.func_74775_l("Level"));
         if (!var5.func_76600_a(var2, var3)) {
            field_151505_a.error(
               "Chunk file at "
                  + var2
                  + ","
                  + var3
                  + " is in the wrong location; relocating. (Expected "
                  + var2
                  + ", "
                  + var3
                  + ", got "
                  + var5.field_76635_g
                  + ", "
                  + var5.field_76647_h
                  + ")"
            );
            var4.func_74768_a("xPos", var2);
            var4.func_74768_a("zPos", var3);
            var5 = this.func_75823_a(var1, var4.func_74775_l("Level"));
         }

         return var5;
      }
   }

   @Override
   public void func_75816_a(World var1, Chunk var2) {
      var1.func_72906_B();

      try {
         NBTTagCompound var3 = new NBTTagCompound();
         NBTTagCompound var4 = new NBTTagCompound();
         var3.func_74782_a("Level", var4);
         this.func_75820_a(var2, var1, var4);
         this.func_75824_a(var2.func_76632_l(), var3);
      } catch (Exception var5) {
         var5.printStackTrace();
      }
   }

   protected void func_75824_a(ChunkCoordIntPair var1, NBTTagCompound var2) {
      synchronized(this.field_75827_c) {
         if (this.field_75826_b.contains(var1)) {
            for(int var4 = 0; var4 < this.field_75828_a.size(); ++var4) {
               if (((AnvilChunkLoader$PendingChunk)this.field_75828_a.get(var4)).field_76548_a.equals(var1)) {
                  this.field_75828_a.set(var4, new AnvilChunkLoader$PendingChunk(var1, var2));
                  return;
               }
            }
         }

         this.field_75828_a.add(new AnvilChunkLoader$PendingChunk(var1, var2));
         this.field_75826_b.add(var1);
         ThreadedFileIOBase.field_75741_a.func_75735_a(this);
      }
   }

   @Override
   public boolean func_75814_c() {
      Object var1 = null;
      AnvilChunkLoader$PendingChunk var6;
      synchronized(this.field_75827_c) {
         if (this.field_75828_a.isEmpty()) {
            return false;
         }

         var6 = (AnvilChunkLoader$PendingChunk)this.field_75828_a.remove(0);
         this.field_75826_b.remove(var6.field_76548_a);
      }

      if (var6 != null) {
         try {
            this.func_75821_a(var6);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

      return true;
   }

   private void func_75821_a(AnvilChunkLoader$PendingChunk var1) {
      DataOutputStream var2 = RegionFileCache.func_76552_d(this.field_75825_d, var1.field_76548_a.field_77276_a, var1.field_76548_a.field_77275_b);
      CompressedStreamTools.func_74800_a(var1.field_76547_b, var2);
      var2.close();
   }

   @Override
   public void func_75819_b(World var1, Chunk var2) {
   }

   @Override
   public void func_75817_a() {
   }

   @Override
   public void func_75818_b() {
      while(this.func_75814_c()) {
      }
   }

   private void func_75820_a(Chunk var1, World var2, NBTTagCompound var3) {
      var3.func_74774_a("V", (byte)1);
      var3.func_74768_a("xPos", var1.field_76635_g);
      var3.func_74768_a("zPos", var1.field_76647_h);
      var3.func_74772_a("LastUpdate", var2.func_82737_E());
      var3.func_74783_a("HeightMap", var1.field_76634_f);
      var3.func_74757_a("TerrainPopulated", var1.field_76646_k);
      var3.func_74757_a("LightPopulated", var1.field_150814_l);
      var3.func_74772_a("InhabitedTime", var1.field_111204_q);
      ExtendedBlockStorage[] var4 = var1.func_76587_i();
      NBTTagList var5 = new NBTTagList();
      boolean var6 = !var2.field_73011_w.field_76576_e;

      for(ExtendedBlockStorage var10 : var4) {
         if (var10 != null) {
            NBTTagCompound var11 = new NBTTagCompound();
            var11.func_74774_a("Y", (byte)(var10.func_76662_d() >> 4 & 0xFF));
            var11.func_74773_a("Blocks", var10.func_76658_g());
            if (var10.func_76660_i() != null) {
               var11.func_74773_a("Add", var10.func_76660_i().field_76585_a);
            }

            var11.func_74773_a("Data", var10.func_76669_j().field_76585_a);
            var11.func_74773_a("BlockLight", var10.func_76661_k().field_76585_a);
            if (var6) {
               var11.func_74773_a("SkyLight", var10.func_76671_l().field_76585_a);
            } else {
               var11.func_74773_a("SkyLight", new byte[var10.func_76661_k().field_76585_a.length]);
            }

            var5.func_74742_a(var11);
         }
      }

      var3.func_74782_a("Sections", var5);
      var3.func_74773_a("Biomes", var1.func_76605_m());
      var1.field_76644_m = false;
      NBTTagList var16 = new NBTTagList();

      for(int var17 = 0; var17 < var1.field_76645_j.length; ++var17) {
         for(Entity var22 : var1.field_76645_j[var17]) {
            NBTTagCompound var25 = new NBTTagCompound();
            if (var22.func_70039_c(var25)) {
               var1.field_76644_m = true;
               var16.func_74742_a(var25);
            }
         }
      }

      var3.func_74782_a("Entities", var16);
      NBTTagList var18 = new NBTTagList();

      for(TileEntity var23 : var1.field_150816_i.values()) {
         NBTTagCompound var26 = new NBTTagCompound();
         var23.func_145841_b(var26);
         var18.func_74742_a(var26);
      }

      var3.func_74782_a("TileEntities", var18);
      List var21 = var2.func_72920_a(var1, false);
      if (var21 != null) {
         long var24 = var2.func_82737_E();
         NBTTagList var12 = new NBTTagList();

         for(NextTickListEntry var14 : var21) {
            NBTTagCompound var15 = new NBTTagCompound();
            var15.func_74768_a("i", Block.func_149682_b(var14.func_151351_a()));
            var15.func_74768_a("x", var14.field_77183_a);
            var15.func_74768_a("y", var14.field_77181_b);
            var15.func_74768_a("z", var14.field_77182_c);
            var15.func_74768_a("t", (int)(var14.field_77180_e - var24));
            var15.func_74768_a("p", var14.field_82754_f);
            var12.func_74742_a(var15);
         }

         var3.func_74782_a("TileTicks", var12);
      }
   }

   private Chunk func_75823_a(World var1, NBTTagCompound var2) {
      int var3 = var2.func_74762_e("xPos");
      int var4 = var2.func_74762_e("zPos");
      Chunk var5 = new Chunk(var1, var3, var4);
      var5.field_76634_f = var2.func_74759_k("HeightMap");
      var5.field_76646_k = var2.func_74767_n("TerrainPopulated");
      var5.field_150814_l = var2.func_74767_n("LightPopulated");
      var5.field_111204_q = var2.func_74763_f("InhabitedTime");
      NBTTagList var6 = var2.func_150295_c("Sections", 10);
      byte var7 = 16;
      ExtendedBlockStorage[] var8 = new ExtendedBlockStorage[var7];
      boolean var9 = !var1.field_73011_w.field_76576_e;

      for(int var10 = 0; var10 < var6.func_74745_c(); ++var10) {
         NBTTagCompound var11 = var6.func_150305_b(var10);
         byte var12 = var11.func_74771_c("Y");
         ExtendedBlockStorage var13 = new ExtendedBlockStorage(var12 << 4, var9);
         var13.func_76664_a(var11.func_74770_j("Blocks"));
         if (var11.func_150297_b("Add", 7)) {
            var13.func_76673_a(new NibbleArray(var11.func_74770_j("Add"), 4));
         }

         var13.func_76668_b(new NibbleArray(var11.func_74770_j("Data"), 4));
         var13.func_76659_c(new NibbleArray(var11.func_74770_j("BlockLight"), 4));
         if (var9) {
            var13.func_76666_d(new NibbleArray(var11.func_74770_j("SkyLight"), 4));
         }

         var13.func_76672_e();
         var8[var12] = var13;
      }

      var5.func_76602_a(var8);
      if (var2.func_150297_b("Biomes", 7)) {
         var5.func_76616_a(var2.func_74770_j("Biomes"));
      }

      NBTTagList var17 = var2.func_150295_c("Entities", 10);
      if (var17 != null) {
         for(int var18 = 0; var18 < var17.func_74745_c(); ++var18) {
            NBTTagCompound var20 = var17.func_150305_b(var18);
            Entity var23 = EntityList.func_75615_a(var20, var1);
            var5.field_76644_m = true;
            if (var23 != null) {
               var5.func_76612_a(var23);
               Entity var14 = var23;

               for(NBTTagCompound var15 = var20; var15.func_150297_b("Riding", 10); var15 = var15.func_74775_l("Riding")) {
                  Entity var16 = EntityList.func_75615_a(var15.func_74775_l("Riding"), var1);
                  if (var16 != null) {
                     var5.func_76612_a(var16);
                     var14.func_70078_a(var16);
                  }

                  var14 = var16;
               }
            }
         }
      }

      NBTTagList var19 = var2.func_150295_c("TileEntities", 10);
      if (var19 != null) {
         for(int var21 = 0; var21 < var19.func_74745_c(); ++var21) {
            NBTTagCompound var24 = var19.func_150305_b(var21);
            TileEntity var26 = TileEntity.func_145827_c(var24);
            if (var26 != null) {
               var5.func_150813_a(var26);
            }
         }
      }

      if (var2.func_150297_b("TileTicks", 9)) {
         NBTTagList var22 = var2.func_150295_c("TileTicks", 10);
         if (var22 != null) {
            for(int var25 = 0; var25 < var22.func_74745_c(); ++var25) {
               NBTTagCompound var27 = var22.func_150305_b(var25);
               var1.func_147446_b(
                  var27.func_74762_e("x"),
                  var27.func_74762_e("y"),
                  var27.func_74762_e("z"),
                  Block.func_149729_e(var27.func_74762_e("i")),
                  var27.func_74762_e("t"),
                  var27.func_74762_e("p")
               );
            }
         }
      }

      return var5;
   }
}
