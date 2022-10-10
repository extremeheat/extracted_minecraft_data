package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionSavedDataManager {
   private static final Logger field_212776_a = LogManager.getLogger();
   private final DimensionType field_212777_b;
   private Map<String, WorldSavedData> field_212778_c = Maps.newHashMap();
   private final Object2IntMap<String> field_212779_d = new Object2IntOpenHashMap();
   @Nullable
   private final ISaveHandler field_212780_e;

   public DimensionSavedDataManager(DimensionType var1, @Nullable ISaveHandler var2) {
      super();
      this.field_212777_b = var1;
      this.field_212780_e = var2;
      this.field_212779_d.defaultReturnValue(-1);
   }

   @Nullable
   public <T extends WorldSavedData> T func_201067_a(Function<String, T> var1, String var2) {
      WorldSavedData var3 = (WorldSavedData)this.field_212778_c.get(var2);
      if (var3 == null && this.field_212780_e != null) {
         try {
            File var4 = this.field_212780_e.func_212423_a(this.field_212777_b, var2);
            if (var4 != null && var4.exists()) {
               var3 = (WorldSavedData)var1.apply(var2);
               var3.func_76184_a(func_212774_a(this.field_212780_e, this.field_212777_b, var2, 1631).func_74775_l("data"));
               this.field_212778_c.put(var2, var3);
            }
         } catch (Exception var5) {
            field_212776_a.error("Error loading saved data: {}", var2, var5);
         }
      }

      return var3;
   }

   public void func_75745_a(String var1, WorldSavedData var2) {
      this.field_212778_c.put(var1, var2);
   }

   public void func_75746_b() {
      try {
         this.field_212779_d.clear();
         if (this.field_212780_e == null) {
            return;
         }

         File var1 = this.field_212780_e.func_212423_a(this.field_212777_b, "idcounts");
         if (var1 != null && var1.exists()) {
            DataInputStream var2 = new DataInputStream(new FileInputStream(var1));
            NBTTagCompound var3 = CompressedStreamTools.func_74794_a(var2);
            var2.close();
            Iterator var4 = var3.func_150296_c().iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               if (var3.func_150297_b(var5, 99)) {
                  this.field_212779_d.put(var5, var3.func_74762_e(var5));
               }
            }
         }
      } catch (Exception var6) {
         field_212776_a.error("Could not load aux values", var6);
      }

   }

   public int func_75743_a(String var1) {
      int var2 = this.field_212779_d.getInt(var1) + 1;
      this.field_212779_d.put(var1, var2);
      if (this.field_212780_e == null) {
         return var2;
      } else {
         try {
            File var3 = this.field_212780_e.func_212423_a(this.field_212777_b, "idcounts");
            if (var3 != null) {
               NBTTagCompound var4 = new NBTTagCompound();
               ObjectIterator var5 = this.field_212779_d.object2IntEntrySet().iterator();

               while(var5.hasNext()) {
                  Entry var6 = (Entry)var5.next();
                  var4.func_74768_a((String)var6.getKey(), var6.getIntValue());
               }

               DataOutputStream var8 = new DataOutputStream(new FileOutputStream(var3));
               CompressedStreamTools.func_74800_a(var4, var8);
               var8.close();
            }
         } catch (Exception var7) {
            field_212776_a.error("Could not get free aux value {}", var1, var7);
         }

         return var2;
      }
   }

   public static NBTTagCompound func_212774_a(ISaveHandler var0, DimensionType var1, String var2, int var3) throws IOException {
      File var4 = var0.func_212423_a(var1, var2);
      FileInputStream var5 = new FileInputStream(var4);
      Throwable var6 = null;

      NBTTagCompound var9;
      try {
         NBTTagCompound var7 = CompressedStreamTools.func_74796_a(var5);
         int var8 = var7.func_150297_b("DataVersion", 99) ? var7.func_74762_e("DataVersion") : 1343;
         var9 = NBTUtil.func_210821_a(var0.func_197718_i(), DataFixTypes.SAVED_DATA, var7, var8, var3);
      } catch (Throwable var18) {
         var6 = var18;
         throw var18;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var17) {
                  var6.addSuppressed(var17);
               }
            } else {
               var5.close();
            }
         }

      }

      return var9;
   }

   public void func_212775_b() {
      if (this.field_212780_e != null) {
         Iterator var1 = this.field_212778_c.values().iterator();

         while(var1.hasNext()) {
            WorldSavedData var2 = (WorldSavedData)var1.next();
            if (var2.func_76188_b()) {
               this.func_75747_a(var2);
               var2.func_76186_a(false);
            }
         }

      }
   }

   private void func_75747_a(WorldSavedData var1) {
      if (this.field_212780_e != null) {
         try {
            File var2 = this.field_212780_e.func_212423_a(this.field_212777_b, var1.func_195925_e());
            if (var2 != null) {
               NBTTagCompound var3 = new NBTTagCompound();
               var3.func_74782_a("data", var1.func_189551_b(new NBTTagCompound()));
               var3.func_74768_a("DataVersion", 1631);
               FileOutputStream var4 = new FileOutputStream(var2);
               CompressedStreamTools.func_74799_a(var3, var4);
               var4.close();
            }
         } catch (Exception var5) {
            field_212776_a.error("Could not save data {}", var1, var5);
         }

      }
   }
}
