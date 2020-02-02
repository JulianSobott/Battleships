package core.utils;

import core.utils.logging.LoggerLogic;
import network.Server;

import java.util.ArrayList;
import java.util.List;

public class ResourcesDestructor {

    private static ResourcesDestructor instance = new ResourcesDestructor();

    private List<Thread> threads = new ArrayList<>();
    private Server server;

    private ResourcesDestructor() {

    }

    public static void addServer(Server server) {
        instance.server = server;
    }

    public static void addThread(Thread thread) {
        instance.threads.add(thread);
    }

    public static void shutdownServer() {
        if (instance.server != null) {
            instance.server.shutdown();
            instance.server = null;
        }
    }

    public static void stopThreads() {
        while (instance.threads.size() > 0) {
            stopSingleThread(instance.threads.remove(0));
        }
        instance.threads = new ArrayList<>();
    }

    public static void stopSingleThread(Thread thread) {
        if (thread == null) return;
        LoggerLogic.debug("Stopping thread: name=" + thread.getName() + ", interrupted=" + thread.isInterrupted() +
                ", alive=" + thread.isAlive());
        thread.interrupt();
        instance.threads.remove(thread);
    }

    public static void shutdownAll() {
        stopThreads();
        shutdownServer();
    }
}
