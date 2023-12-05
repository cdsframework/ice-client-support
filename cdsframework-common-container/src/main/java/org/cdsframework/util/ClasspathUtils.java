package org.cdsframework.util;

/**
 * The MTS core support EJB project is the base framework for the CDS Framework Middle Tier Service.
 *
 * Copyright (C) 2016 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see https://www.hln.com/services/open-source/ or send
 * correspondence to ice@hln.com.
 */


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;

/**
 *
 * @author HLN Consulting, LLC
 */
public class ClasspathUtils {
    
    private final static LogUtils logger = LogUtils.getLogger(ClasspathUtils.class);
    
    public static Class[] getClassesFromClasspath() throws IOException, ClassNotFoundException, URISyntaxException {
        return getClassesFromClasspath("org.cdsframework.dto");
    }
    
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static Class[] getClassesFromClasspath(String packageName) throws IOException, ClassNotFoundException, URISyntaxException {
        final String METHODNAME = "getClassesFromClasspath ";
        String path = packageName.replace('.', '/');
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = cloader.getResources(path);
        ArrayList<Class> classes = new ArrayList<Class>();
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource != null) {
                String protocol = resource.getProtocol();                
                String resourcePath;
//                logger.info(METHODNAME, "resource.getPath()=", resource.getPath());
//                logger.info(METHODNAME, "protocol=", protocol);

                if (!protocol.equalsIgnoreCase("vfs")) {
                    resourcePath = resource.getPath().split("!")[0].split("\\.jar")[0] + ".jar";
                    if (resourcePath.startsWith("file:")) {
                        resourcePath = resourcePath.split("file:")[1];
                    }
                }
                else {
                    resourcePath = resource.toString();
//                    logger.info(METHODNAME, "resourcePath=", resourcePath);
                    if (resourcePath.toLowerCase().indexOf(".jar") >=0) {
                        URLConnection conn = resource.openConnection();
                        VirtualFile vf = (VirtualFile) conn.getContent();
                        String jarVFSPath = VFSUtils.getPhysicalURL(vf.getParent().getParent()).getFile();
//                        logger.info(METHODNAME, "jarVFSPath=" + jarVFSPath);
                        int idxJarExt = jarVFSPath.toLowerCase().lastIndexOf(".jar");
//                        logger.info(METHODNAME, "idxJarExt=" + idxJarExt);
                        int idxSlashAfterJar = jarVFSPath.indexOf("/", idxJarExt);
//                        logger.info(METHODNAME, "idxSlashAfterJar=" + idxSlashAfterJar);
                        int idxSlashBeforeJar = jarVFSPath.substring(0, idxJarExt).lastIndexOf("/");
//                        logger.info(METHODNAME, "idxSlashBeforeJar=" + idxSlashBeforeJar);
                        String jarFileName2 = jarVFSPath.substring(idxSlashBeforeJar + 1, idxJarExt + 4);
                        String jarFolder = jarVFSPath.substring(0, idxSlashAfterJar);
                        String jarFullPath = jarFolder + "/" + jarFileName2;
//                        logger.info(METHODNAME, "jarFileName2=" + jarFileName2);
//                        logger.info(METHODNAME, "jarFolder=" + jarFolder);
//                        logger.info(METHODNAME, "jarFullPath=" + jarFullPath);
                        resourcePath = jarFullPath;
                    }
                }
                
                logger.info(METHODNAME, "resourcePath=", resourcePath);
                File file = new File(resourcePath);
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry nextJarEntry = entries.nextElement();
                    String name = nextJarEntry.getName();
                    if (name.startsWith(path) && name.endsWith(".class") && !name.endsWith("package-info.class") && !name.contains("$")) {
                        name = name.replace("/", ".").substring(0, name.length() - 6);
//                        logger.info(METHODNAME, "name=", name);
                        classes.add(Class.forName(name));
                    }
                }
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }    
}
