package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

public class MapStorage {
   private ISaveHandler field_75751_a;
   protected Map<String, WorldSavedData> field_75749_b = Maps.newHashMap();
   private List<WorldSavedData> field_75750_c = Lists.newArrayList();
   private Map<String, Short> field_75748_d = Maps.newHashMap();

   public MapStorage(ISaveHandler var1) {
      super();
      this.field_75751_a = var1;
      this.func_75746_b();
   }

   public WorldSavedData func_75742_a(Class<? extends WorldSavedData> var1, String var2) {
      WorldSavedData var3 = (WorldSavedData)this.field_75749_b.get(var2);
      if (var3 != null) {
         return var3;
      } else {
         if (this.field_75751_a != null) {
            try {
               File var4 = this.field_75751_a.func_75758_b(var2);
               if (var4 != null && var4.exists()) {
                  try {
                     var3 = (WorldSavedData)var1.getConstructor(String.class).newInstance(var2);
                  } catch (Exception var7) {
                     throw new RuntimeException("Failed to instantiate " + var1.toString(), var7);
                  }

                  FileInputStream var5 = new FileInputStream(var4);
                  NBTTagCompound var6 = CompressedStreamTools.func_74796_a(var5);
                  var5.close();
                  var3.func_76184_a(var6.func_74775_l("data"));
               }
            } catch (Exception var8) {
               var8.printStackTrace();
            }
         }

         if (var3 != null) {
            this.field_75749_b.put(var2, var3);
            this.field_75750_c.add(var3);
         }

         return var3;
      }
   }

   public void func_75745_a(String var1, WorldSavedData var2) {
      if (this.field_75749_b.containsKey(var1)) {
         this.field_75750_c.remove(this.field_75749_b.remove(var1));
      }

      this.field_75749_b.put(var1, var2);
      this.field_75750_c.add(var2);
   }

   public void func_75744_a() {
      for(int var1 = 0; var1 < this.field_75750_c.size(); ++var1) {
         WorldSavedData var2 = (WorldSavedData)this.field_75750_c.get(var1);
         if (var2.func_76188_b()) {
            this.func_75747_a(var2);
            var2.func_76186_a(false);
         }
      }

   }

   private void func_75747_a(WorldSavedData var1) {
      if (this.field_75751_a != null) {
         try {
            File var2 = this.field_75751_a.func_75758_b(var1.field_76190_i);
            if (var2 != null) {
               NBTTagCompound var3 = new NBTTagCompound();
               var1.func_76187_b(var3);
               NBTTagCompound var4 = new NBTTagCompound();
               var4.func_74782_a("data", var3);
               FileOutputStream var5 = new FileOutputStream(var2);
               CompressedStreamTools.func_74799_a(var4, var5);
               var5.close();
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }

      }
   }

   private void func_75746_b() {
      try {
         this.field_75748_d.clear();
         if (this.field_75751_a == null) {
            return;
         }

         File var1 = this.field_75751_a.func_75758_b("idcounts");
         if (var1 != null && var1.exists()) {
            DataInputStream var2 = new DataInputStream(new FileInputStream(var1));
            NBTTagCompound var3 = CompressedStreamTools.func_74794_a(var2);
            var2.close();
            Iterator var4 = var3.func_150296_c().iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               NBTBase var6 = var3.func_74781_a(var5);
               if (var6 instanceof NBTTagShort) {
                  NBTTagShort var7 = (NBTTagShort)var6;
                  short var9 = var7.func_150289_e();
                  this.field_75748_d.put(var5, var9);
               }
            }
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }

   }

   public int func_75743_a(String var1) {
      Short var2 = (Short)this.field_75748_d.get(var1);
      if (var2 == null) {
         var2 = Short.valueOf((short)0);
      } else {
         var2 = (short)(var2 + 1);
      }

      this.field_75748_d.put(var1, var2);
      if (this.field_75751_a == null) {
         return var2;
      } else {
         try {
            File var3 = this.field_75751_a.func_75758_b("idcounts");
            if (var3 != null) {
               NBTTagCompound var4 = new NBTTagCompound();
               Iterator var5 = this.field_75748_d.keySet().iterator();

               while(var5.hasNext()) {
                  String var6 = (String)var5.next();
                  short var7 = (Short)this.field_75748_d.get(var6);
                  var4.func_74777_a(var6, var7);
               }

               DataOutputStream var9 = new DataOutputStream(new FileOutputStream(var3));
               CompressedStreamTools.func_74800_a(var4, var9);
               var9.close();
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         return var2;
      }
   }
}
