package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   private static final Logger field_192828_d = LogManager.getLogger();
   private final RecipeManager field_199641_f;

   public ServerRecipeBook(RecipeManager var1) {
      super();
      this.field_199641_f = var1;
   }

   public int func_197926_a(Collection<IRecipe> var1, EntityPlayerMP var2) {
      ArrayList var3 = Lists.newArrayList();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         IRecipe var6 = (IRecipe)var5.next();
         ResourceLocation var7 = var6.func_199560_c();
         if (!this.field_194077_a.contains(var7) && !var6.func_192399_d()) {
            this.func_209118_a(var7);
            this.func_209120_c(var7);
            var3.add(var7);
            CriteriaTriggers.field_192126_f.func_192225_a(var2, var6);
            ++var4;
         }
      }

      this.func_194081_a(SPacketRecipeBook.State.ADD, var2, var3);
      return var4;
   }

   public int func_197925_b(Collection<IRecipe> var1, EntityPlayerMP var2) {
      ArrayList var3 = Lists.newArrayList();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         IRecipe var6 = (IRecipe)var5.next();
         ResourceLocation var7 = var6.func_199560_c();
         if (this.field_194077_a.contains(var7)) {
            this.func_209119_b(var7);
            var3.add(var7);
            ++var4;
         }
      }

      this.func_194081_a(SPacketRecipeBook.State.REMOVE, var2, var3);
      return var4;
   }

   private void func_194081_a(SPacketRecipeBook.State var1, EntityPlayerMP var2, List<ResourceLocation> var3) {
      var2.field_71135_a.func_147359_a(new SPacketRecipeBook(var1, var3, Collections.emptyList(), this.field_192818_b, this.field_192819_c, this.field_202885_e, this.field_202886_f));
   }

   public NBTTagCompound func_192824_e() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74757_a("isGuiOpen", this.field_192818_b);
      var1.func_74757_a("isFilteringCraftable", this.field_192819_c);
      var1.func_74757_a("isFurnaceGuiOpen", this.field_202885_e);
      var1.func_74757_a("isFurnaceFilteringCraftable", this.field_202886_f);
      NBTTagList var2 = new NBTTagList();
      Iterator var3 = this.field_194077_a.iterator();

      while(var3.hasNext()) {
         ResourceLocation var4 = (ResourceLocation)var3.next();
         var2.add((INBTBase)(new NBTTagString(var4.toString())));
      }

      var1.func_74782_a("recipes", var2);
      NBTTagList var6 = new NBTTagList();
      Iterator var7 = this.field_194078_b.iterator();

      while(var7.hasNext()) {
         ResourceLocation var5 = (ResourceLocation)var7.next();
         var6.add((INBTBase)(new NBTTagString(var5.toString())));
      }

      var1.func_74782_a("toBeDisplayed", var6);
      return var1;
   }

   public void func_192825_a(NBTTagCompound var1) {
      this.field_192818_b = var1.func_74767_n("isGuiOpen");
      this.field_192819_c = var1.func_74767_n("isFilteringCraftable");
      this.field_202885_e = var1.func_74767_n("isFurnaceGuiOpen");
      this.field_202886_f = var1.func_74767_n("isFurnaceFilteringCraftable");
      NBTTagList var2 = var1.func_150295_c("recipes", 8);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ResourceLocation var4 = new ResourceLocation(var2.func_150307_f(var3));
         IRecipe var5 = this.field_199641_f.func_199517_a(var4);
         if (var5 == null) {
            field_192828_d.error("Tried to load unrecognized recipe: {} removed now.", var4);
         } else {
            this.func_194073_a(var5);
         }
      }

      NBTTagList var7 = var1.func_150295_c("toBeDisplayed", 8);

      for(int var8 = 0; var8 < var7.size(); ++var8) {
         ResourceLocation var9 = new ResourceLocation(var7.func_150307_f(var8));
         IRecipe var6 = this.field_199641_f.func_199517_a(var9);
         if (var6 == null) {
            field_192828_d.error("Tried to load unrecognized recipe: {} removed now.", var9);
         } else {
            this.func_193825_e(var6);
         }
      }

   }

   public void func_192826_c(EntityPlayerMP var1) {
      var1.field_71135_a.func_147359_a(new SPacketRecipeBook(SPacketRecipeBook.State.INIT, this.field_194077_a, this.field_194078_b, this.field_192818_b, this.field_192819_c, this.field_202885_e, this.field_202886_f));
   }
}
