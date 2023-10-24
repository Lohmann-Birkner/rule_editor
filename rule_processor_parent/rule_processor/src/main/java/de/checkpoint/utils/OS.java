package de.checkpoint.utils;

import java.io.File;

/**
 *
 * @author peter
 */
public class OS
{
    private static boolean inited = false;
    private static boolean isMacOS = false;
    private static boolean isWin32 = false;
    private static boolean isWin64 = false;
    private static boolean isLinux = false;
    
    public static String getSharedLibraryLoaderPath()
    {
        if(! inited) {
            init();
        }
        String currentDir = System.getProperty("user.dir");
        if(! currentDir.endsWith(File.separator)) {
            currentDir += File.separator;
        }
        if(isWin32) {
            return currentDir + "lib" + File.separator + "Win32" + File.separator;
        }
        if(isWin64) {
            return currentDir + "lib" + File.separator + "Win64" + File.separator;
        }
        if(isLinux) {
            return currentDir + "lib" + File.separator + "Linux" + File.separator;
        }
        if(isMacOS) {
            return currentDir + "lib" + File.separator + "MacOSX" + File.separator;
        }
        return null;
    }
    
    public static String getSharedLibraryExt()
    {
        if(! inited) {
            init();
        }
        if(isWin32) {
            return ".dll";
        }
        if(isWin64) {
            return ".dll";
        }
        if(isLinux) {
            return ".jnilib";
        }
        if(isMacOS) {
            return ".jnilib";
        }
        return null;
    }
    
    public static boolean isWindows()
    {
        if(! inited) {
            init();
        }
        return isWin32 || isWin64;
    }
    
    public static boolean isMacOS()
    {
        if(! inited) {
            init();
        }
        return isMacOS;
    }
    
    public static boolean isLinux()
    {
        if(! inited) {
            init();
        }
        return isLinux;
    }

    private static void init()
    {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.indexOf("win") >= 0) {
            if("x86".equals(System.getProperty("os.arch"))) {
                isWin32 = true;
            } else {
                isWin64 = true;
            }
        } else if(os.indexOf("mac") >= 0) {
            isMacOS = true;
        } else if(os.indexOf("nux") >= 0) {
            isLinux = true;
        }
        inited = true;
    }
}
