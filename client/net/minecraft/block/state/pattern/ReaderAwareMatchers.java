package net.minecraft.block.state.pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public final class ReaderAwareMatchers {
   public static <T> IBlockMatcherReaderAware<T> func_202084_a(IBlockMatcherReaderAware<T> var0) {
      return new ReaderAwareMatchers.NotMatcher(var0);
   }

   public static <T> IBlockMatcherReaderAware<T> func_202083_b(IBlockMatcherReaderAware<? super T>... var0) {
      return new ReaderAwareMatchers.OrMatcher(func_202086_a(var0));
   }

   private static <T> List<T> func_202086_a(T... var0) {
      return func_202085_c(Arrays.asList(var0));
   }

   private static <T> List<T> func_202085_c(Iterable<T> var0) {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.add(Preconditions.checkNotNull(var3));
      }

      return var1;
   }

   static class OrMatcher<T> implements IBlockMatcherReaderAware<T> {
      private final List<? extends IBlockMatcherReaderAware<? super T>> field_202076_a;

      private OrMatcher(List<? extends IBlockMatcherReaderAware<? super T>> var1) {
         super();
         this.field_202076_a = var1;
      }

      public boolean test(@Nullable T var1, IBlockReader var2, BlockPos var3) {
         for(int var4 = 0; var4 < this.field_202076_a.size(); ++var4) {
            if (((IBlockMatcherReaderAware)this.field_202076_a.get(var4)).test(var1, var2, var3)) {
               return true;
            }
         }

         return false;
      }

      // $FF: synthetic method
      OrMatcher(List var1, Object var2) {
         this(var1);
      }
   }

   static class NotMatcher<T> implements IBlockMatcherReaderAware<T> {
      private final IBlockMatcherReaderAware<T> field_202075_a;

      NotMatcher(IBlockMatcherReaderAware<T> var1) {
         super();
         this.field_202075_a = (IBlockMatcherReaderAware)Preconditions.checkNotNull(var1);
      }

      public boolean test(@Nullable T var1, IBlockReader var2, BlockPos var3) {
         return !this.field_202075_a.test(var1, var2, var3);
      }
   }
}
