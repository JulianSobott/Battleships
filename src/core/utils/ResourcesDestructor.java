package core.utils;

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
        for(Thread thread : instance.threads) {
            thread.interrupt();
        }
        instance.threads = new ArrayList<>();
    }

    public static void stopSingleThread(Thread thread) {
        if(thread  == null) return;
        instance.threads.remove(thread);
        if (thread.isAlive()) {
            thread.interrupt();
        }
    }

    public static void shutdownAll() {
        stopThreads();
        shutdownServer();
    }
}
