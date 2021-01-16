package org.apache.commons.lang3;

import java.util.UUID;

public class Conversion {
   private static final boolean[] TTTT = new boolean[]{true, true, true, true};
   private static final boolean[] FTTT = new boolean[]{false, true, true, true};
   private static final boolean[] TFTT = new boolean[]{true, false, true, true};
   private static final boolean[] FFTT = new boolean[]{false, false, true, true};
   private static final boolean[] TTFT = new boolean[]{true, true, false, true};
   private static final boolean[] FTFT = new boolean[]{false, true, false, true};
   private static final boolean[] TFFT = new boolean[]{true, false, false, true};
   private static final boolean[] FFFT = new boolean[]{false, false, false, true};
   private static final boolean[] TTTF = new boolean[]{true, true, true, false};
   private static final boolean[] FTTF = new boolean[]{false, true, true, false};
   private static final boolean[] TFTF = new boolean[]{true, false, true, false};
   private static final boolean[] FFTF = new boolean[]{false, false, true, false};
   private static final boolean[] TTFF = new boolean[]{true, true, false, false};
   private static final boolean[] FTFF = new boolean[]{false, true, false, false};
   private static final boolean[] TFFF = new boolean[]{true, false, false, false};
   private static final boolean[] FFFF = new boolean[]{false, false, false, false};

   public Conversion() {
      super();
   }

   public static int hexDigitToInt(char var0) {
      int var1 = Character.digit(var0, 16);
      if (var1 < 0) {
         throw new IllegalArgumentException("Cannot interpret '" + var0 + "' as a hexadecimal digit");
      } else {
         return var1;
      }
   }

   public static int hexDigitMsb0ToInt(char var0) {
      switch(var0) {
      case '0':
         return 0;
      case '1':
         return 8;
      case '2':
         return 4;
      case '3':
         return 12;
      case '4':
         return 2;
      case '5':
         return 10;
      case '6':
         return 6;
      case '7':
         return 14;
      case '8':
         return 1;
      case '9':
         return 9;
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      default:
         throw new IllegalArgumentException("Cannot interpret '" + var0 + "' as a hexadecimal digit");
      case 'A':
      case 'a':
         return 5;
      case 'B':
      case 'b':
         return 13;
      case 'C':
      case 'c':
         return 3;
      case 'D':
      case 'd':
         return 11;
      case 'E':
      case 'e':
         return 7;
      case 'F':
      case 'f':
         return 15;
      }
   }

   public static boolean[] hexDigitToBinary(char var0) {
      switch(var0) {
      case '0':
         return (boolean[])FFFF.clone();
      case '1':
         return (boolean[])TFFF.clone();
      case '2':
         return (boolean[])FTFF.clone();
      case '3':
         return (boolean[])TTFF.clone();
      case '4':
         return (boolean[])FFTF.clone();
      case '5':
         return (boolean[])TFTF.clone();
      case '6':
         return (boolean[])FTTF.clone();
      case '7':
         return (boolean[])TTTF.clone();
      case '8':
         return (boolean[])FFFT.clone();
      case '9':
         return (boolean[])TFFT.clone();
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      default:
         throw new IllegalArgumentException("Cannot interpret '" + var0 + "' as a hexadecimal digit");
      case 'A':
      case 'a':
         return (boolean[])FTFT.clone();
      case 'B':
      case 'b':
         return (boolean[])TTFT.clone();
      case 'C':
      case 'c':
         return (boolean[])FFTT.clone();
      case 'D':
      case 'd':
         return (boolean[])TFTT.clone();
      case 'E':
      case 'e':
         return (boolean[])FTTT.clone();
      case 'F':
      case 'f':
         return (boolean[])TTTT.clone();
      }
   }

   public static boolean[] hexDigitMsb0ToBinary(char var0) {
      switch(var0) {
      case '0':
         return (boolean[])FFFF.clone();
      case '1':
         return (boolean[])FFFT.clone();
      case '2':
         return (boolean[])FFTF.clone();
      case '3':
         return (boolean[])FFTT.clone();
      case '4':
         return (boolean[])FTFF.clone();
      case '5':
         return (boolean[])FTFT.clone();
      case '6':
         return (boolean[])FTTF.clone();
      case '7':
         return (boolean[])FTTT.clone();
      case '8':
         return (boolean[])TFFF.clone();
      case '9':
         return (boolean[])TFFT.clone();
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      default:
         throw new IllegalArgumentException("Cannot interpret '" + var0 + "' as a hexadecimal digit");
      case 'A':
      case 'a':
         return (boolean[])TFTF.clone();
      case 'B':
      case 'b':
         return (boolean[])TFTT.clone();
      case 'C':
      case 'c':
         return (boolean[])TTFF.clone();
      case 'D':
      case 'd':
         return (boolean[])TTFT.clone();
      case 'E':
      case 'e':
         return (boolean[])TTTF.clone();
      case 'F':
      case 'f':
         return (boolean[])TTTT.clone();
      }
   }

   public static char binaryToHexDigit(boolean[] var0) {
      return binaryToHexDigit(var0, 0);
   }

   public static char binaryToHexDigit(boolean[] var0, int var1) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Cannot convert an empty array.");
      } else if (var0.length > var1 + 3 && var0[var1 + 3]) {
         if (var0.length > var1 + 2 && var0[var1 + 2]) {
            if (var0.length > var1 + 1 && var0[var1 + 1]) {
               return (char)(var0[var1] ? 'f' : 'e');
            } else {
               return (char)(var0[var1] ? 'd' : 'c');
            }
         } else if (var0.length > var1 + 1 && var0[var1 + 1]) {
            return (char)(var0[var1] ? 'b' : 'a');
         } else {
            return (char)(var0[var1] ? '9' : '8');
         }
      } else if (var0.length > var1 + 2 && var0[var1 + 2]) {
         if (var0.length > var1 + 1 && var0[var1 + 1]) {
            return (char)(var0[var1] ? '7' : '6');
         } else {
            return (char)(var0[var1] ? '5' : '4');
         }
      } else if (var0.length > var1 + 1 && var0[var1 + 1]) {
         return (char)(var0[var1] ? '3' : '2');
      } else {
         return (char)(var0[var1] ? '1' : '0');
      }
   }

   public static char binaryToHexDigitMsb0_4bits(boolean[] var0) {
      return binaryToHexDigitMsb0_4bits(var0, 0);
   }

   public static char binaryToHexDigitMsb0_4bits(boolean[] var0, int var1) {
      if (var0.length > 8) {
         throw new IllegalArgumentException("src.length>8: src.length=" + var0.length);
      } else if (var0.length - var1 < 4) {
         throw new IllegalArgumentException("src.length-srcPos<4: src.length=" + var0.length + ", srcPos=" + var1);
      } else if (var0[var1 + 3]) {
         if (var0[var1 + 2]) {
            if (var0[var1 + 1]) {
               return (char)(var0[var1] ? 'f' : '7');
            } else {
               return (char)(var0[var1] ? 'b' : '3');
            }
         } else if (var0[var1 + 1]) {
            return (char)(var0[var1] ? 'd' : '5');
         } else {
            return (char)(var0[var1] ? '9' : '1');
         }
      } else if (var0[var1 + 2]) {
         if (var0[var1 + 1]) {
            return (char)(var0[var1] ? 'e' : '6');
         } else {
            return (char)(var0[var1] ? 'a' : '2');
         }
      } else if (var0[var1 + 1]) {
         return (char)(var0[var1] ? 'c' : '4');
      } else {
         return (char)(var0[var1] ? '8' : '0');
      }
   }

   public static char binaryBeMsb0ToHexDigit(boolean[] var0) {
      return binaryBeMsb0ToHexDigit(var0, 0);
   }

   public static char binaryBeMsb0ToHexDigit(boolean[] var0, int var1) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Cannot convert an empty array.");
      } else {
         int var2 = var0.length - 1 - var1;
         int var3 = Math.min(4, var2 + 1);
         boolean[] var4 = new boolean[4];
         System.arraycopy(var0, var2 + 1 - var3, var4, 4 - var3, var3);
         byte var5 = 0;
         if (var4[var5]) {
            if (var4.length > var5 + 1 && var4[var5 + 1]) {
               if (var4.length > var5 + 2 && var4[var5 + 2]) {
                  return (char)(var4.length > var5 + 3 && var4[var5 + 3] ? 'f' : 'e');
               } else {
                  return (char)(var4.length > var5 + 3 && var4[var5 + 3] ? 'd' : 'c');
               }
            } else if (var4.length > var5 + 2 && var4[var5 + 2]) {
               return (char)(var4.length > var5 + 3 && var4[var5 + 3] ? 'b' : 'a');
            } else {
               return (char)(var4.length > var5 + 3 && var4[var5 + 3] ? '9' : '8');
            }
         } else if (var4.length > var5 + 1 && var4[var5 + 1]) {
            if (var4.length > var5 + 2 && var4[var5 + 2]) {
               return (char)(var4.length > var5 + 3 && var4[var5 + 3] ? '7' : '6');
            } else {
               return (char)(var4.length > var5 + 3 && var4[var5 + 3] ? '5' : '4');
            }
         } else if (var4.length > var5 + 2 && var4[var5 + 2]) {
            return (char)(var4.length > var5 + 3 && var4[var5 + 3] ? '3' : '2');
         } else {
            return (char)(var4.length > var5 + 3 && var4[var5 + 3] ? '1' : '0');
         }
      }
   }

   public static char intToHexDigit(int var0) {
      char var1 = Character.forDigit(var0, 16);
      if (var1 == 0) {
         throw new IllegalArgumentException("nibble value not between 0 and 15: " + var0);
      } else {
         return var1;
      }
   }

   public static char intToHexDigitMsb0(int var0) {
      switch(var0) {
      case 0:
         return '0';
      case 1:
         return '8';
      case 2:
         return '4';
      case 3:
         return 'c';
      case 4:
         return '2';
      case 5:
         return 'a';
      case 6:
         return '6';
      case 7:
         return 'e';
      case 8:
         return '1';
      case 9:
         return '9';
      case 10:
         return '5';
      case 11:
         return 'd';
      case 12:
         return '3';
      case 13:
         return 'b';
      case 14:
         return '7';
      case 15:
         return 'f';
      default:
         throw new IllegalArgumentException("nibble value not between 0 and 15: " + var0);
      }
   }

   public static long intArrayToLong(int[] var0, int var1, long var2, int var4, int var5) {
      if ((var0.length != 0 || var1 != 0) && 0 != var5) {
         if ((var5 - 1) * 32 + var4 >= 64) {
            throw new IllegalArgumentException("(nInts-1)*32+dstPos is greather or equal to than 64");
         } else {
            long var6 = var2;

            for(int var8 = 0; var8 < var5; ++var8) {
               int var9 = var8 * 32 + var4;
               long var10 = (4294967295L & (long)var0[var8 + var1]) << var9;
               long var12 = 4294967295L << var9;
               var6 = var6 & ~var12 | var10;
            }

            return var6;
         }
      } else {
         return var2;
      }
   }

   public static long shortArrayToLong(short[] var0, int var1, long var2, int var4, int var5) {
      if ((var0.length != 0 || var1 != 0) && 0 != var5) {
         if ((var5 - 1) * 16 + var4 >= 64) {
            throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greather or equal to than 64");
         } else {
            long var6 = var2;

            for(int var8 = 0; var8 < var5; ++var8) {
               int var9 = var8 * 16 + var4;
               long var10 = (65535L & (long)var0[var8 + var1]) << var9;
               long var12 = 65535L << var9;
               var6 = var6 & ~var12 | var10;
            }

            return var6;
         }
      } else {
         return var2;
      }
   }

   public static int shortArrayToInt(short[] var0, int var1, int var2, int var3, int var4) {
      if ((var0.length != 0 || var1 != 0) && 0 != var4) {
         if ((var4 - 1) * 16 + var3 >= 32) {
            throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greather or equal to than 32");
         } else {
            int var5 = var2;

            for(int var6 = 0; var6 < var4; ++var6) {
               int var7 = var6 * 16 + var3;
               int var8 = ('\uffff' & var0[var6 + var1]) << var7;
               int var9 = '\uffff' << var7;
               var5 = var5 & ~var9 | var8;
            }

            return var5;
         }
      } else {
         return var2;
      }
   }

   public static long byteArrayToLong(byte[] var0, int var1, long var2, int var4, int var5) {
      if ((var0.length != 0 || var1 != 0) && 0 != var5) {
         if ((var5 - 1) * 8 + var4 >= 64) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greather or equal to than 64");
         } else {
            long var6 = var2;

            for(int var8 = 0; var8 < var5; ++var8) {
               int var9 = var8 * 8 + var4;
               long var10 = (255L & (long)var0[var8 + var1]) << var9;
               long var12 = 255L << var9;
               var6 = var6 & ~var12 | var10;
            }

            return var6;
         }
      } else {
         return var2;
      }
   }

   public static int byteArrayToInt(byte[] var0, int var1, int var2, int var3, int var4) {
      if ((var0.length != 0 || var1 != 0) && 0 != var4) {
         if ((var4 - 1) * 8 + var3 >= 32) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greather or equal to than 32");
         } else {
            int var5 = var2;

            for(int var6 = 0; var6 < var4; ++var6) {
               int var7 = var6 * 8 + var3;
               int var8 = (255 & var0[var6 + var1]) << var7;
               int var9 = 255 << var7;
               var5 = var5 & ~var9 | var8;
            }

            return var5;
         }
      } else {
         return var2;
      }
   }

   public static short byteArrayToShort(byte[] var0, int var1, short var2, int var3, int var4) {
      if ((var0.length != 0 || var1 != 0) && 0 != var4) {
         if ((var4 - 1) * 8 + var3 >= 16) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greather or equal to than 16");
         } else {
            short var5 = var2;

            for(int var6 = 0; var6 < var4; ++var6) {
               int var7 = var6 * 8 + var3;
               int var8 = (255 & var0[var6 + var1]) << var7;
               int var9 = 255 << var7;
               var5 = (short)(var5 & ~var9 | var8);
            }

            return var5;
         }
      } else {
         return var2;
      }
   }

   public static long hexToLong(String var0, int var1, long var2, int var4, int var5) {
      if (0 == var5) {
         return var2;
      } else if ((var5 - 1) * 4 + var4 >= 64) {
         throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 64");
      } else {
         long var6 = var2;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = var8 * 4 + var4;
            long var10 = (15L & (long)hexDigitToInt(var0.charAt(var8 + var1))) << var9;
            long var12 = 15L << var9;
            var6 = var6 & ~var12 | var10;
         }

         return var6;
      }
   }

   public static int hexToInt(String var0, int var1, int var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if ((var4 - 1) * 4 + var3 >= 32) {
         throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 32");
      } else {
         int var5 = var2;

         for(int var6 = 0; var6 < var4; ++var6) {
            int var7 = var6 * 4 + var3;
            int var8 = (15 & hexDigitToInt(var0.charAt(var6 + var1))) << var7;
            int var9 = 15 << var7;
            var5 = var5 & ~var9 | var8;
         }

         return var5;
      }
   }

   public static short hexToShort(String var0, int var1, short var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if ((var4 - 1) * 4 + var3 >= 16) {
         throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 16");
      } else {
         short var5 = var2;

         for(int var6 = 0; var6 < var4; ++var6) {
            int var7 = var6 * 4 + var3;
            int var8 = (15 & hexDigitToInt(var0.charAt(var6 + var1))) << var7;
            int var9 = 15 << var7;
            var5 = (short)(var5 & ~var9 | var8);
         }

         return var5;
      }
   }

   public static byte hexToByte(String var0, int var1, byte var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if ((var4 - 1) * 4 + var3 >= 8) {
         throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 8");
      } else {
         byte var5 = var2;

         for(int var6 = 0; var6 < var4; ++var6) {
            int var7 = var6 * 4 + var3;
            int var8 = (15 & hexDigitToInt(var0.charAt(var6 + var1))) << var7;
            int var9 = 15 << var7;
            var5 = (byte)(var5 & ~var9 | var8);
         }

         return var5;
      }
   }

   public static long binaryToLong(boolean[] var0, int var1, long var2, int var4, int var5) {
      if ((var0.length != 0 || var1 != 0) && 0 != var5) {
         if (var5 - 1 + var4 >= 64) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 64");
         } else {
            long var6 = var2;

            for(int var8 = 0; var8 < var5; ++var8) {
               int var9 = var8 + var4;
               long var10 = (var0[var8 + var1] ? 1L : 0L) << var9;
               long var12 = 1L << var9;
               var6 = var6 & ~var12 | var10;
            }

            return var6;
         }
      } else {
         return var2;
      }
   }

   public static int binaryToInt(boolean[] var0, int var1, int var2, int var3, int var4) {
      if ((var0.length != 0 || var1 != 0) && 0 != var4) {
         if (var4 - 1 + var3 >= 32) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 32");
         } else {
            int var5 = var2;

            for(int var6 = 0; var6 < var4; ++var6) {
               int var7 = var6 + var3;
               int var8 = (var0[var6 + var1] ? 1 : 0) << var7;
               int var9 = 1 << var7;
               var5 = var5 & ~var9 | var8;
            }

            return var5;
         }
      } else {
         return var2;
      }
   }

   public static short binaryToShort(boolean[] var0, int var1, short var2, int var3, int var4) {
      if ((var0.length != 0 || var1 != 0) && 0 != var4) {
         if (var4 - 1 + var3 >= 16) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 16");
         } else {
            short var5 = var2;

            for(int var6 = 0; var6 < var4; ++var6) {
               int var7 = var6 + var3;
               int var8 = (var0[var6 + var1] ? 1 : 0) << var7;
               int var9 = 1 << var7;
               var5 = (short)(var5 & ~var9 | var8);
            }

            return var5;
         }
      } else {
         return var2;
      }
   }

   public static byte binaryToByte(boolean[] var0, int var1, byte var2, int var3, int var4) {
      if ((var0.length != 0 || var1 != 0) && 0 != var4) {
         if (var4 - 1 + var3 >= 8) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 8");
         } else {
            byte var5 = var2;

            for(int var6 = 0; var6 < var4; ++var6) {
               int var7 = var6 + var3;
               int var8 = (var0[var6 + var1] ? 1 : 0) << var7;
               int var9 = 1 << var7;
               var5 = (byte)(var5 & ~var9 | var8);
            }

            return var5;
         }
      } else {
         return var2;
      }
   }

   public static int[] longToIntArray(long var0, int var2, int[] var3, int var4, int var5) {
      if (0 == var5) {
         return var3;
      } else if ((var5 - 1) * 32 + var2 >= 64) {
         throw new IllegalArgumentException("(nInts-1)*32+srcPos is greather or equal to than 64");
      } else {
         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var6 * 32 + var2;
            var3[var4 + var6] = (int)(-1L & var0 >> var7);
         }

         return var3;
      }
   }

   public static short[] longToShortArray(long var0, int var2, short[] var3, int var4, int var5) {
      if (0 == var5) {
         return var3;
      } else if ((var5 - 1) * 16 + var2 >= 64) {
         throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greather or equal to than 64");
      } else {
         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var6 * 16 + var2;
            var3[var4 + var6] = (short)((int)(65535L & var0 >> var7));
         }

         return var3;
      }
   }

   public static short[] intToShortArray(int var0, int var1, short[] var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if ((var4 - 1) * 16 + var1 >= 32) {
         throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greather or equal to than 32");
      } else {
         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var5 * 16 + var1;
            var2[var3 + var5] = (short)('\uffff' & var0 >> var6);
         }

         return var2;
      }
   }

   public static byte[] longToByteArray(long var0, int var2, byte[] var3, int var4, int var5) {
      if (0 == var5) {
         return var3;
      } else if ((var5 - 1) * 8 + var2 >= 64) {
         throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greather or equal to than 64");
      } else {
         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var6 * 8 + var2;
            var3[var4 + var6] = (byte)((int)(255L & var0 >> var7));
         }

         return var3;
      }
   }

   public static byte[] intToByteArray(int var0, int var1, byte[] var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if ((var4 - 1) * 8 + var1 >= 32) {
         throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greather or equal to than 32");
      } else {
         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var5 * 8 + var1;
            var2[var3 + var5] = (byte)(255 & var0 >> var6);
         }

         return var2;
      }
   }

   public static byte[] shortToByteArray(short var0, int var1, byte[] var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if ((var4 - 1) * 8 + var1 >= 16) {
         throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greather or equal to than 16");
      } else {
         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var5 * 8 + var1;
            var2[var3 + var5] = (byte)(255 & var0 >> var6);
         }

         return var2;
      }
   }

   public static String longToHex(long var0, int var2, String var3, int var4, int var5) {
      if (0 == var5) {
         return var3;
      } else if ((var5 - 1) * 4 + var2 >= 64) {
         throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 64");
      } else {
         StringBuilder var6 = new StringBuilder(var3);
         int var7 = var6.length();

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = var8 * 4 + var2;
            int var10 = (int)(15L & var0 >> var9);
            if (var4 + var8 == var7) {
               ++var7;
               var6.append(intToHexDigit(var10));
            } else {
               var6.setCharAt(var4 + var8, intToHexDigit(var10));
            }
         }

         return var6.toString();
      }
   }

   public static String intToHex(int var0, int var1, String var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if ((var4 - 1) * 4 + var1 >= 32) {
         throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 32");
      } else {
         StringBuilder var5 = new StringBuilder(var2);
         int var6 = var5.length();

         for(int var7 = 0; var7 < var4; ++var7) {
            int var8 = var7 * 4 + var1;
            int var9 = 15 & var0 >> var8;
            if (var3 + var7 == var6) {
               ++var6;
               var5.append(intToHexDigit(var9));
            } else {
               var5.setCharAt(var3 + var7, intToHexDigit(var9));
            }
         }

         return var5.toString();
      }
   }

   public static String shortToHex(short var0, int var1, String var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if ((var4 - 1) * 4 + var1 >= 16) {
         throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 16");
      } else {
         StringBuilder var5 = new StringBuilder(var2);
         int var6 = var5.length();

         for(int var7 = 0; var7 < var4; ++var7) {
            int var8 = var7 * 4 + var1;
            int var9 = 15 & var0 >> var8;
            if (var3 + var7 == var6) {
               ++var6;
               var5.append(intToHexDigit(var9));
            } else {
               var5.setCharAt(var3 + var7, intToHexDigit(var9));
            }
         }

         return var5.toString();
      }
   }

   public static String byteToHex(byte var0, int var1, String var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if ((var4 - 1) * 4 + var1 >= 8) {
         throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 8");
      } else {
         StringBuilder var5 = new StringBuilder(var2);
         int var6 = var5.length();

         for(int var7 = 0; var7 < var4; ++var7) {
            int var8 = var7 * 4 + var1;
            int var9 = 15 & var0 >> var8;
            if (var3 + var7 == var6) {
               ++var6;
               var5.append(intToHexDigit(var9));
            } else {
               var5.setCharAt(var3 + var7, intToHexDigit(var9));
            }
         }

         return var5.toString();
      }
   }

   public static boolean[] longToBinary(long var0, int var2, boolean[] var3, int var4, int var5) {
      if (0 == var5) {
         return var3;
      } else if (var5 - 1 + var2 >= 64) {
         throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 64");
      } else {
         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var6 + var2;
            var3[var4 + var6] = (1L & var0 >> var7) != 0L;
         }

         return var3;
      }
   }

   public static boolean[] intToBinary(int var0, int var1, boolean[] var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if (var4 - 1 + var1 >= 32) {
         throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 32");
      } else {
         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var5 + var1;
            var2[var3 + var5] = (1 & var0 >> var6) != 0;
         }

         return var2;
      }
   }

   public static boolean[] shortToBinary(short var0, int var1, boolean[] var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if (var4 - 1 + var1 >= 16) {
         throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 16");
      } else {
         assert var4 - 1 < 16 - var1;

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var5 + var1;
            var2[var3 + var5] = (1 & var0 >> var6) != 0;
         }

         return var2;
      }
   }

   public static boolean[] byteToBinary(byte var0, int var1, boolean[] var2, int var3, int var4) {
      if (0 == var4) {
         return var2;
      } else if (var4 - 1 + var1 >= 8) {
         throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 8");
      } else {
         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var5 + var1;
            var2[var3 + var5] = (1 & var0 >> var6) != 0;
         }

         return var2;
      }
   }

   public static byte[] uuidToByteArray(UUID var0, byte[] var1, int var2, int var3) {
      if (0 == var3) {
         return var1;
      } else if (var3 > 16) {
         throw new IllegalArgumentException("nBytes is greather than 16");
      } else {
         longToByteArray(var0.getMostSignificantBits(), 0, var1, var2, var3 > 8 ? 8 : var3);
         if (var3 >= 8) {
            longToByteArray(var0.getLeastSignificantBits(), 0, var1, var2 + 8, var3 - 8);
         }

         return var1;
      }
   }

   public static UUID byteArrayToUuid(byte[] var0, int var1) {
      if (var0.length - var1 < 16) {
         throw new IllegalArgumentException("Need at least 16 bytes for UUID");
      } else {
         return new UUID(byteArrayToLong(var0, var1, 0L, 0, 8), byteArrayToLong(var0, var1 + 8, 0L, 0, 8));
      }
   }
}
