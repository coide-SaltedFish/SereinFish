package sereinfish.bot.entity;

import com.icecreamqaq.yuq.annotation.GroupController;
import com.icecreamqaq.yuq.annotation.PrivateController;
import sereinfish.bot.mlog.SfLog;
import sereinfish.bot.myYuq.MyYuQ;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类管理器
 * 管理各类注解
 */
public class ClassManager {
    private ArrayList<Class> classArrayList;//类列表

    private ArrayList<Class> controllerClassList = new ArrayList<>();//控制器类列表

    private static ClassManager manager;
    private ClassManager() throws Exception {
        classArrayList = getClassName(MyYuQ.BASE_PACK);//查找所有类
        filterClass();//查找控制器类
    }

    public static ClassManager init() throws Exception {
        manager = new ClassManager();
        return manager;
    }

    public static ClassManager getInstance(){
        if (manager == null){
            throw new NullPointerException("类管理器尚未初始化");
        }
        return manager;
    }

    /**
     * 对类进行筛选分类
     */
    private void filterClass() {
        for(Class cls:classArrayList){
            if (cls.isAnnotationPresent(GroupController.class) || cls.isAnnotationPresent(PrivateController.class)){//是否是控制器类
                controllerClassList.add(cls);
            }
        }
    }

    /**
     * 获取某包下（包括该包的所有子包）所有类
     * @param packageName 包名
     * @return 类的完整名称
     */
    public ArrayList<Class> getClassName(String packageName) throws ClassNotFoundException {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     * @param packageName 包名
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    public ArrayList<Class> getClassName(String packageName, boolean childPackage) throws ClassNotFoundException {
        ArrayList<Class> fileNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if (type.equals("file")) {
                fileNames = getClassNameByFile(url.getPath(), null, childPackage);
            } else if (type.equals("jar")) {
                fileNames = getClassNameByJar(url.getPath(), childPackage);
            }
        } else {
            fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
        }
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     * @param filePath 文件路径
     * @param className 类名集合
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private ArrayList<Class> getClassNameByFile(String filePath, ArrayList<Class> className, boolean childPackage) throws ClassNotFoundException {
        ArrayList<Class> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    Class<?> c = this.getClass().getClassLoader().loadClass(childFilePath);
                    myClassName.add(c);
                }
            }
        }

        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     * @param jarPath jar文件路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private ArrayList<Class> getClassNameByJar(String jarPath, boolean childPackage) {
        ArrayList<Class> myClassName = new ArrayList<>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try {
            jarFilePath = URLDecoder.decode(jarFilePath,"utf-8");
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    if (childPackage) {
                        if (entryName.startsWith(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            Class<?> c = this.getClass().getClassLoader().loadClass(entryName);
                            myClassName.add(c);
                        }
                    } else {
                        int index = entryName.lastIndexOf("/");
                        String myPackagePath;
                        if (index != -1) {
                            myPackagePath = entryName.substring(0, index);
                        } else {
                            myPackagePath = entryName;
                        }
                        if (myPackagePath.equals(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            Class<?> c = this.getClass().getClassLoader().loadClass(entryName);
                            myClassName.add(c);
                        }
                    }
                }
            }
        } catch (Exception e) {
            SfLog.getInstance().e(this.getClass(), e);
        }
        return myClassName;
    }

    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     * @param urls URL集合
     * @param packagePath 包路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private ArrayList<Class> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) {
        ArrayList<Class> myClassName = new ArrayList<>();
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + packagePath;
                myClassName.addAll(getClassNameByJar(jarPath, childPackage));
            }
        }
        return myClassName;
    }

    public ArrayList<Class> getControllerClassList() {
        return controllerClassList;
    }
}
