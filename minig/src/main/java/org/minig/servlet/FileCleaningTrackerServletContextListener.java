package org.minig.servlet;

import org.apache.commons.io.FileCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Kamill Sokol
 */
public class FileCleaningTrackerServletContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(FileCleaningTrackerServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {}

    /**
     * Call this method to cause the file cleaner thread to terminate when
     * there are no more objects being tracked for deletion.
     * <p>
     * In a simple environment, you don't need this method as the file cleaner
     * thread will simply exit when the JVM exits. In a more complex environment,
     * with multiple class loaders (such as an application server), you should be
     * aware that the file cleaner thread will continue running even if the class
     * loader it was started from terminates. This can consitute a memory leak.
     * <p>
     * For example, suppose that you have developed a web application, which
     * contains the commons-io jar file in your WEB-INF/lib directory. In other
     * words, the FileCleaner class is loaded through the class loader of your
     * web application. If the web application is terminated, but the servlet
     * container is still running, then the file cleaner thread will still exist,
     * posing a memory leak.
     * <p>
     * This method allows the thread to be terminated. Simply call this method
     * in the resource cleanup code, such as {@link javax.servlet.ServletContextListener#contextDestroyed}.
     * One called, no new objects can be tracked by the file cleaner.
     * @deprecated Use {@link org.apache.commons.io.FileCleaningTracker#exitWhenFinished()}.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("call FileCleaningTracker#exitWhenFinished");
        FileCleaner.exitWhenFinished();
    }
}
