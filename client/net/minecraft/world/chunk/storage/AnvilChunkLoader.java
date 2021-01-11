package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
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
   private Map<ChunkCoordIntPair, NBTTagCompound> field_75828_a = new ConcurrentHashMap();
   private Set<ChunkCoordIntPair> field_75826_b = Collections.newSetFromMap(new ConcurrentHashMap());
   private final File field_75825_d;
   private boolean field_183014_e = false;

   public AnvilChunkLoader(File var1) {
      super();
      this.field_75825_d = var1;
   }

   public Chunk func_75815_a(World var1, int var2, int var3) throws IOException {
      ChunkCoordIntPair var4 = new ChunkCoordIntPair(var2, var3);
      NBTTagCompound var5 = (NBTTagCompound)this.field_75828_a.get(var4);
      if (var5 == null) {
         DataInputStream var6 = RegionFileCache.func_76549_c(this.field_75825_d, var2, var3);
         if (var6 == null) {
            return null;
         }

         var5 = CompressedStreamTools.func_74794_a(var6);
      }

      return this.func_75822_a(var1, var2, var3, var5);
   }

   protected Chunk func_75822_a(World var1, int var2, int var3, NBTTagCompound var4) {
      if (!var4.func_150297_b("Level", 10)) {
         field_151505_a.error("Chunk file at " + var2 + "," + var3 + " is missing level data, skipping");
         return null;
      } else {
         NBTTagCompound var5 = var4.func_74775_l("Level");
         if (!var5.func_150297_b("Sections", 9)) {
            field_151505_a.error("Chunk file at " + var2 + "," + var3 + " is missing block data, skipping");
            return null;
         } else {
            Chunk var6 = this.func_75823_a(var1, var5);
            if (!var6.func_76600_a(var2, var3)) {
               field_151505_a.error("Chunk file at " + var2 + "," + var3 + " is in the wrong location; relocating. (Expected " + var2 + ", " + var3 + ", got " + var6.field_76635_g + ", " + var6.field_76647_h + ")");
               var5.func_74768_a("xPos", var2);
               var5.func_74768_a("zPos", var3);
               var6 = this.func_75823_a(var1, var5);
            }

            return var6;
         }
      }
   }

   public void func_75816_a(World var1, Chunk var2) throws IOException, MinecraftException {
      var1.func_72906_B();

      try {
         NBTTagCompound var3 = new NBTTagCompound();
         NBTTagCompound var4 = new NBTTagCompound();
         var3.func_74782_a("Level", var4);
         this.func_75820_a(var2, var1, var4);
         this.func_75824_a(var2.func_76632_l(), var3);
      } catch (Exception var5) {
         field_151505_a.error("Failed to save chunk", var5);
      }

   }

   protected void func_75824_a(ChunkCoordIntPair var1, NBTTagCompound var2) {
      if (!this.field_75826_b.contains(var1)) {
         this.field_75828_a.put(var1, var2);
      }

      ThreadedFileIOBase.func_178779_a().func_75735_a(this);
   }

   public boolean func_75814_c() {
      if (this.field_75828_a.isEmpty()) {
         if (this.field_183014_e) {
            field_151505_a.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[]{this.field_75825_d.getName()});
         }

         return false;
      } else {
         ChunkCoordIntPair var1 = (ChunkCoordIntPair)this.field_75828_a.keySet().iterator().next();

         boolean var3;
         try {
            this.field_75826_b.add(var1);
            NBTTagCompound var2 = (NBTTagCompound)this.field_75828_a.remove(var1);
            if (var2 != null) {
               try {
                  this.func_183013_b(var1, var2);
               } catch (Exception var7) {
                  field_151505_a.error("Failed to save chunk", var7);
               }
            }

            var3 = true;
         } finally {
            this.field_75826_b.remove(var1);
         }

         return var3;
      }
   }

   private void func_183013_b(ChunkCoordIntPair var1, NBTTagCompound var2) throws IOException {
      DataOutputStream var3 = RegionFileCache.func_76552_d(this.field_75825_d, var1.field_77276_a, var1.field_77275_b);
      CompressedStreamTools.func_74800_a(var2, var3);
      var3.close();
   }

   public void func_75819_b(World var1, Chunk var2) throws IOException {
   }

   public void func_75817_a() {
   }

   public void func_75818_b() {
      try {
         this.field_183014_e = true;

         while(true) {
            if (this.func_75814_c()) {
               continue;
            }
         }
      } finally {
         this.field_183014_e = false;
      }

   }

   private void func_75820_a(Chunk var1, World var2, NBTTagCompound var3) {
      var3.func_74774_a("V", (byte)1);
      var3.func_74768_a("xPos", var1.field_76635_g);
      var3.func_74768_a("zPos", var1.field_76647_h);
      var3.func_74772_a("LastUpdate", var2.func_82737_E());
      var3.func_74783_a("HeightMap", var1.func_177445_q());
      var3.func_74757_a("TerrainPopulated", var1.func_177419_t());
      var3.func_74757_a("LightPopulated", var1.func_177423_u());
      var3.func_74772_a("InhabitedTime", var1.func_177416_w());
      ExtendedBlockStorage[] var4 = var1.func_76587_i();
      NBTTagList var5 = new NBTTagList();
      boolean var6 = !var2.field_73011_w.func_177495_o();
      ExtendedBlockStorage[] var7 = var4;
      int var8 = var4.length;

      NBTTagCompound var11;
      for(int var9 = 0; var9 < var8; ++var9) {
         ExtendedBlockStorage var10 = var7[var9];
         if (var10 != null) {
            var11 = new NBTTagCompound();
            var11.func_74774_a("Y", (byte)(var10.func_76662_d() >> 4 & 255));
            byte[] var12 = new byte[var10.func_177487_g().length];
            NibbleArray var13 = new NibbleArray();
            NibbleArray var14 = null;

            for(int var15 = 0; var15 < var10.func_177487_g().length; ++var15) {
               char var16 = var10.func_177487_g()[var15];
               int var17 = var15 & 15;
               int var18 = var15 >> 8 & 15;
               int var19 = var15 >> 4 & 15;
               if (var16 >> 12 != 0) {
                  if (var14 == null) {
                     var14 = new NibbleArray();
                  }

                  var14.func_76581_a(var17, var18, var19, var16 >> 12);
               }

               var12[var15] = (byte)(var16 >> 4 & 255);
               var13.func_76581_a(var17, var18, var19, var16 & 15);
            }

            var11.func_74773_a("Blocks", var12);
            var11.func_74773_a("Data", var13.func_177481_a());
            if (var14 != null) {
               var11.func_74773_a("Add", var14.func_177481_a());
            }

            var11.func_74773_a("BlockLight", var10.func_76661_k().func_177481_a());
            if (var6) {
               var11.func_74773_a("SkyLight", var10.func_76671_l().func_177481_a());
            } else {
               var11.func_74773_a("SkyLight", new byte[var10.func_76661_k().func_177481_a().length]);
            }

            var5.func_74742_a(var11);
         }
      }

      var3.func_74782_a("Sections", var5);
      var3.func_74773_a("Biomes", var1.func_76605_m());
      var1.func_177409_g(false);
      NBTTagList var20 = new NBTTagList();

      Iterator var22;
      for(var8 = 0; var8 < var1.func_177429_s().length; ++var8) {
         var22 = var1.func_177429_s()[var8].iterator();

         while(var22.hasNext()) {
            Entity var24 = (Entity)var22.next();
            var11 = new NBTTagCompound();
            if (var24.func_70039_c(var11)) {
               var1.func_177409_g(true);
               var20.func_74742_a(var11);
            }
         }
      }

      var3.func_74782_a("Entities", var20);
      NBTTagList var21 = new NBTTagList();
      var22 = var1.func_177434_r().values().iterator();

      while(var22.hasNext()) {
         TileEntity var25 = (TileEntity)var22.next();
         var11 = new NBTTagCompound();
         var25.func_145841_b(var11);
         var21.func_74742_a(var11);
      }

      var3.func_74782_a("TileEntities", var21);
      List var23 = var2.func_72920_a(var1, false);
      if (var23 != null) {
         long var26 = var2.func_82737_E();
         NBTTagList var27 = new NBTTagList();
         Iterator var28 = var23.iterator();

         while(var28.hasNext()) {
            NextTickListEntry var29 = (NextTickListEntry)var28.next();
            NBTTagCompound var30 = new NBTTagCompound();
            ResourceLocation var31 = (ResourceLocation)Block.field_149771_c.func_177774_c(var29.func_151351_a());
            var30.func_74778_a("i", var31 == null ? "" : var31.toString());
            var30.func_74768_a("x", var29.field_180282_a.func_177958_n());
            var30.func_74768_a("y", var29.field_180282_a.func_177956_o());
            var30.func_74768_a("z", var29.field_180282_a.func_177952_p());
            var30.func_74768_a("t", (int)(var29.field_77180_e - var26));
            var30.func_74768_a("p", var29.field_82754_f);
            var27.func_74742_a(var30);
         }

         var3.func_74782_a("TileTicks", var27);
      }

   }

   private Chunk func_75823_a(World var1, NBTTagCompound var2) {
      int var3 = var2.func_74762_e("xPos");
      int var4 = var2.func_74762_e("zPos");
      Chunk var5 = new Chunk(var1, var3, var4);
      var5.func_177420_a(var2.func_74759_k("HeightMap"));
      var5.func_177446_d(var2.func_74767_n("TerrainPopulated"));
      var5.func_177421_e(var2.func_74767_n("LightPopulated"));
      var5.func_177415_c(var2.func_74763_f("InhabitedTime"));
      NBTTagList var6 = var2.func_150295_c("Sections", 10);
      byte var7 = 16;
      ExtendedBlockStorage[] var8 = new ExtendedBlockStorage[var7];
      boolean var9 = !var1.field_73011_w.func_177495_o();

      for(int var10 = 0; var10 < var6.func_74745_c(); ++var10) {
         NBTTagCompound var11 = var6.func_150305_b(var10);
         byte var12 = var11.func_74771_c("Y");
         ExtendedBlockStorage var13 = new ExtendedBlockStorage(var12 << 4, var9);
         byte[] var14 = var11.func_74770_j("Blocks");
         NibbleArray var15 = new NibbleArray(var11.func_74770_j("Data"));
         NibbleArray var16 = var11.func_150297_b("Add", 7) ? new NibbleArray(var11.func_74770_j("Add")) : null;
         char[] var17 = new char[var14.length];

         for(int var18 = 0; var18 < var17.length; ++var18) {
            int var19 = var18 & 15;
            int var20 = var18 >> 8 & 15;
            int var21 = var18 >> 4 & 15;
            int var22 = var16 != null ? var16.func_76582_a(var19, var20, var21) : 0;
            var17[var18] = (char)(var22 << 12 | (var14[var18] & 255) << 4 | var15.func_76582_a(var19, var20, var21));
         }

         var13.func_177486_a(var17);
         var13.func_76659_c(new NibbleArray(var11.func_74770_j("BlockLight")));
         if (var9) {
            var13.func_76666_d(new NibbleArray(var11.func_74770_j("SkyLight")));
         }

         var13.func_76672_e();
         var8[var12] = var13;
      }

      var5.func_76602_a(var8);
      if (var2.func_150297_b("Biomes", 7)) {
         var5.func_76616_a(var2.func_74770_j("Biomes"));
      }

      NBTTagList var23 = var2.func_150295_c("Entities", 10);
      if (var23 != null) {
         for(int var24 = 0; var24 < var23.func_74745_c(); ++var24) {
            NBTTagCompound var26 = var23.func_150305_b(var24);
            Entity var28 = EntityList.func_75615_a(var26, var1);
            var5.func_177409_g(true);
            if (var28 != null) {
               var5.func_76612_a(var28);
               Entity var32 = var28;

               for(NBTTagCompound var34 = var26; var34.func_150297_b("Riding", 10); var34 = var34.func_74775_l("Riding")) {
                  Entity var37 = EntityList.func_75615_a(var34.func_74775_l("Riding"), var1);
                  if (var37 != null) {
                     var5.func_76612_a(var37);
                     var32.func_70078_a(var37);
                  }

                  var32 = var37;
               }
            }
         }
      }

      NBTTagList var25 = var2.func_150295_c("TileEntities", 10);
      if (var25 != null) {
         for(int var27 = 0; var27 < var25.func_74745_c(); ++var27) {
            NBTTagCompound var30 = var25.func_150305_b(var27);
            TileEntity var33 = TileEntity.func_145827_c(var30);
            if (var33 != null) {
               var5.func_150813_a(var33);
            }
         }
      }

      if (var2.func_150297_b("TileTicks", 9)) {
         NBTTagList var29 = var2.func_150295_c("TileTicks", 10);
         if (var29 != null) {
            for(int var31 = 0; var31 < var29.func_74745_c(); ++var31) {
               NBTTagCompound var35 = var29.func_150305_b(var31);
               Block var36;
               if (var35.func_150297_b("i", 8)) {
                  var36 = Block.func_149684_b(var35.func_74779_i("i"));
               } else {
                  var36 = Block.func_149729_e(var35.func_74762_e("i"));
               }

               var1.func_180497_b(new BlockPos(var35.func_74762_e("x"), var35.func_74762_e("y"), var35.func_74762_e("z")), var36, var35.func_74762_e("t"), var35.func_74762_e("p"));
            }
         }
      }

      return var5;
   }
}
