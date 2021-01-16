package org.apache.commons.lang3.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class DiffBuilder implements Builder<DiffResult> {
   private final List<Diff<?>> diffs;
   private final boolean objectsTriviallyEqual;
   private final Object left;
   private final Object right;
   private final ToStringStyle style;

   public DiffBuilder(Object var1, Object var2, ToStringStyle var3, boolean var4) {
      super();
      if (var1 == null) {
         throw new IllegalArgumentException("lhs cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("rhs cannot be null");
      } else {
         this.diffs = new ArrayList();
         this.left = var1;
         this.right = var2;
         this.style = var3;
         this.objectsTriviallyEqual = var4 && (var1 == var2 || var1.equals(var2));
      }
   }

   public DiffBuilder(Object var1, Object var2, ToStringStyle var3) {
      this(var1, var2, var3, true);
   }

   public DiffBuilder append(String var1, final boolean var2, final boolean var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (var2 != var3) {
            this.diffs.add(new Diff<Boolean>(var1) {
               private static final long serialVersionUID = 1L;

               public Boolean getLeft() {
                  return var2;
               }

               public Boolean getRight() {
                  return var3;
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final boolean[] var2, final boolean[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (!Arrays.equals(var2, var3)) {
            this.diffs.add(new Diff<Boolean[]>(var1) {
               private static final long serialVersionUID = 1L;

               public Boolean[] getLeft() {
                  return ArrayUtils.toObject(var2);
               }

               public Boolean[] getRight() {
                  return ArrayUtils.toObject(var3);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final byte var2, final byte var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (var2 != var3) {
            this.diffs.add(new Diff<Byte>(var1) {
               private static final long serialVersionUID = 1L;

               public Byte getLeft() {
                  return var2;
               }

               public Byte getRight() {
                  return var3;
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final byte[] var2, final byte[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (!Arrays.equals(var2, var3)) {
            this.diffs.add(new Diff<Byte[]>(var1) {
               private static final long serialVersionUID = 1L;

               public Byte[] getLeft() {
                  return ArrayUtils.toObject(var2);
               }

               public Byte[] getRight() {
                  return ArrayUtils.toObject(var3);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final char var2, final char var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (var2 != var3) {
            this.diffs.add(new Diff<Character>(var1) {
               private static final long serialVersionUID = 1L;

               public Character getLeft() {
                  return var2;
               }

               public Character getRight() {
                  return var3;
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final char[] var2, final char[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (!Arrays.equals(var2, var3)) {
            this.diffs.add(new Diff<Character[]>(var1) {
               private static final long serialVersionUID = 1L;

               public Character[] getLeft() {
                  return ArrayUtils.toObject(var2);
               }

               public Character[] getRight() {
                  return ArrayUtils.toObject(var3);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final double var2, final double var4) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (Double.doubleToLongBits(var2) != Double.doubleToLongBits(var4)) {
            this.diffs.add(new Diff<Double>(var1) {
               private static final long serialVersionUID = 1L;

               public Double getLeft() {
                  return var2;
               }

               public Double getRight() {
                  return var4;
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final double[] var2, final double[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (!Arrays.equals(var2, var3)) {
            this.diffs.add(new Diff<Double[]>(var1) {
               private static final long serialVersionUID = 1L;

               public Double[] getLeft() {
                  return ArrayUtils.toObject(var2);
               }

               public Double[] getRight() {
                  return ArrayUtils.toObject(var3);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final float var2, final float var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (Float.floatToIntBits(var2) != Float.floatToIntBits(var3)) {
            this.diffs.add(new Diff<Float>(var1) {
               private static final long serialVersionUID = 1L;

               public Float getLeft() {
                  return var2;
               }

               public Float getRight() {
                  return var3;
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final float[] var2, final float[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (!Arrays.equals(var2, var3)) {
            this.diffs.add(new Diff<Float[]>(var1) {
               private static final long serialVersionUID = 1L;

               public Float[] getLeft() {
                  return ArrayUtils.toObject(var2);
               }

               public Float[] getRight() {
                  return ArrayUtils.toObject(var3);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final int var2, final int var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (var2 != var3) {
            this.diffs.add(new Diff<Integer>(var1) {
               private static final long serialVersionUID = 1L;

               public Integer getLeft() {
                  return var2;
               }

               public Integer getRight() {
                  return var3;
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final int[] var2, final int[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (!Arrays.equals(var2, var3)) {
            this.diffs.add(new Diff<Integer[]>(var1) {
               private static final long serialVersionUID = 1L;

               public Integer[] getLeft() {
                  return ArrayUtils.toObject(var2);
               }

               public Integer[] getRight() {
                  return ArrayUtils.toObject(var3);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final long var2, final long var4) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (var2 != var4) {
            this.diffs.add(new Diff<Long>(var1) {
               private static final long serialVersionUID = 1L;

               public Long getLeft() {
                  return var2;
               }

               public Long getRight() {
                  return var4;
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final long[] var2, final long[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (!Arrays.equals(var2, var3)) {
            this.diffs.add(new Diff<Long[]>(var1) {
               private static final long serialVersionUID = 1L;

               public Long[] getLeft() {
                  return ArrayUtils.toObject(var2);
               }

               public Long[] getRight() {
                  return ArrayUtils.toObject(var3);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final short var2, final short var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (var2 != var3) {
            this.diffs.add(new Diff<Short>(var1) {
               private static final long serialVersionUID = 1L;

               public Short getLeft() {
                  return var2;
               }

               public Short getRight() {
                  return var3;
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final short[] var2, final short[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (!Arrays.equals(var2, var3)) {
            this.diffs.add(new Diff<Short[]>(var1) {
               private static final long serialVersionUID = 1L;

               public Short[] getLeft() {
                  return ArrayUtils.toObject(var2);
               }

               public Short[] getRight() {
                  return ArrayUtils.toObject(var3);
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, final Object var2, final Object var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else if (var2 == var3) {
         return this;
      } else {
         Object var4;
         if (var2 != null) {
            var4 = var2;
         } else {
            var4 = var3;
         }

         if (var4.getClass().isArray()) {
            if (var4 instanceof boolean[]) {
               return this.append(var1, (boolean[])((boolean[])var2), (boolean[])((boolean[])var3));
            } else if (var4 instanceof byte[]) {
               return this.append(var1, (byte[])((byte[])var2), (byte[])((byte[])var3));
            } else if (var4 instanceof char[]) {
               return this.append(var1, (char[])((char[])var2), (char[])((char[])var3));
            } else if (var4 instanceof double[]) {
               return this.append(var1, (double[])((double[])var2), (double[])((double[])var3));
            } else if (var4 instanceof float[]) {
               return this.append(var1, (float[])((float[])var2), (float[])((float[])var3));
            } else if (var4 instanceof int[]) {
               return this.append(var1, (int[])((int[])var2), (int[])((int[])var3));
            } else if (var4 instanceof long[]) {
               return this.append(var1, (long[])((long[])var2), (long[])((long[])var3));
            } else {
               return var4 instanceof short[] ? this.append(var1, (short[])((short[])var2), (short[])((short[])var3)) : this.append(var1, (Object[])((Object[])var2), (Object[])((Object[])var3));
            }
         } else if (var2 != null && var2.equals(var3)) {
            return this;
         } else {
            this.diffs.add(new Diff<Object>(var1) {
               private static final long serialVersionUID = 1L;

               public Object getLeft() {
                  return var2;
               }

               public Object getRight() {
                  return var3;
               }
            });
            return this;
         }
      }
   }

   public DiffBuilder append(String var1, final Object[] var2, final Object[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         if (!Arrays.equals(var2, var3)) {
            this.diffs.add(new Diff<Object[]>(var1) {
               private static final long serialVersionUID = 1L;

               public Object[] getLeft() {
                  return var2;
               }

               public Object[] getRight() {
                  return var3;
               }
            });
         }

         return this;
      }
   }

   public DiffBuilder append(String var1, DiffResult var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("Field name cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("Diff result cannot be null");
      } else if (this.objectsTriviallyEqual) {
         return this;
      } else {
         Iterator var3 = var2.getDiffs().iterator();

         while(var3.hasNext()) {
            Diff var4 = (Diff)var3.next();
            this.append(var1 + "." + var4.getFieldName(), var4.getLeft(), var4.getRight());
         }

         return this;
      }
   }

   public DiffResult build() {
      return new DiffResult(this.left, this.right, this.diffs, this.style);
   }
}
